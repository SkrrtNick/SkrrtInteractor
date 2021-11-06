package scripts.tasks.object;

import lombok.Getter;
import lombok.Setter;
import org.tribot.api.General;
import org.tribot.script.sdk.*;
import org.tribot.script.sdk.antiban.PlayerPreferences;
import org.tribot.script.sdk.cache.BankCache;
import org.tribot.script.sdk.query.Query;
import org.tribot.script.sdk.types.Area;
import org.tribot.script.sdk.types.GrandExchangeOffer;
import org.tribot.script.sdk.types.WorldTile;
import scripts.SkrrtInteractor;
import scripts.api.Loggable;
import scripts.api.Logger;
import scripts.api.framework.Priority;
import scripts.api.framework.Task;
import scripts.api.functions.GrandExchangeItem;
import scripts.api.functions.Walking;
import scripts.data.GUIOptions;
import scripts.data.InteractionTask;
import scripts.data.ItemID;
import scripts.data.Teleports;

import java.util.ArrayList;
import java.util.List;

public class GrandExchangeTask implements Task {
    @Getter
    private final InteractionTask interactionTask;
    @Getter
    @Setter
    private static boolean shouldRestock = false;
    private Logger logger = new Logger().setHeader("Banking").setLoggable(Loggable.ERROR);
    private Area area = Area.fromPolygon(
            new WorldTile[]{
                    new WorldTile(3158, 3487, 0),
                    new WorldTile(3172, 3487, 0),
                    new WorldTile(3169, 3483, 0),
                    new WorldTile(3162, 3483, 0)
            }
    );
    private static final int GRAND_EXCHANGE_MASTER = 465;

    public GrandExchangeTask(InteractionTask interactionTask) {
        this.interactionTask = interactionTask;
    }

    @Override
    public Priority priority() {
        return Priority.MEDIUM;
    }

    @Override
    public boolean validate() {
        return getInteractionTask().isUseRestocking() && shouldRestock;
    }

    @Override
    public void execute() {
        SkrrtInteractor.setStatus("Restocking...");
        int teleportQuantity = PlayerPreferences.preference("teleportQuantity", g -> g.uniform(2, 4));
        int staminaQuantity = PlayerPreferences.preference("staminaQuantity", g -> g.uniform(2, 4));
        int runes = PlayerPreferences.preference("Runes", g -> g.uniform(interactionTask.getRunes() * 5, interactionTask.getRunes() * 20));
        List<GrandExchangeItem> sellables = new ArrayList<>() {{
            add(new GrandExchangeItem(interactionTask.getItemID(), 0, () -> true, true));
            add(new GrandExchangeItem(interactionTask.getTeleport().getUnchargedID(), 0, () -> interactionTask.getTeleport().getUnchargedID() != -1, true));
            //TODO add uncharged teleport selling
        }};
        List<GrandExchangeItem> buyables = new ArrayList<>() {{
            add(new GrandExchangeItem(interactionTask.getTeleport(), teleportQuantity, () -> interactionTask.getTeleport() != null, false));
            add(new GrandExchangeItem(ItemID.STAMINA_POTION4, staminaQuantity, () -> interactionTask.getStamina() != null, false));
            add(new GrandExchangeItem(ItemID.LAW_RUNE, runes, () -> interactionTask.getType().equals(GUIOptions.InteractionType.TELEGRABBING), false));
            add(new GrandExchangeItem(ItemID.STAFF_OF_AIR, 1, () -> interactionTask.getType().equals(GUIOptions.InteractionType.TELEGRABBING) && interactionTask.isUseStaff(), false));
            add(new GrandExchangeItem(ItemID.AIR_RUNE, runes, () -> interactionTask.getType().equals(GUIOptions.InteractionType.TELEGRABBING) && !interactionTask.isUseStaff(), false));
            add(new GrandExchangeItem(Teleports.RING_OF_WEALTH, teleportQuantity, () -> true, false));
        }};
        if (area.getRandomTile().distance() > 50) {
            if (!Bank.isNearby()) {
                Walking.walkToBank(interactionTask::walkState);
            }
            if (Bank.open()) {
                if (Waiting.waitUntil(6000, Bank::isOpen) && Inventory.isFull() && Bank.depositInventory()) {
                    Waiting.waitNormal(600, 175);
                }
                if (withdraw(Teleports.RING_OF_WEALTH.getItemIDs(), 1)) {
                    Waiting.waitNormal(600, 175);
                }
                Bank.close();
            }
        }
        if (GrandExchange.isNearby() || Walking.walkTo(area.getRandomTile(), interactionTask::walkState)) {
            if (!Inventory.contains(ItemID.COINS_995) && Bank.open() && Waiting.waitUntil(6000, Bank::isOpen)) {
                if (Bank.withdrawAll(ItemID.COINS_995) && Waiting.waitUntil(() -> Inventory.contains(ItemID.COINS_995))) {
                    Bank.close();
                }
            }
            if (interactionTask.isSellItems()) {
                sellItems(sellables);
            }
            if (Inventory.contains(ItemID.COINS_995) && open()) {
                if (Waiting.waitUntil(GrandExchange::isOpen)) {
                    buyItems(buyables);
                }
                clickCollect();
                logger.setMessage("Has Required Items: " + hasRequiredItems(buyables)).print();
                setShouldRestock(!hasRequiredItems(buyables));
            }
        }

    }

    private boolean withdraw(List<Integer> itemIDs, int amount) {
        int[] array = itemIDs.stream().mapToInt(Integer::intValue).toArray();
        return Query.bank()
                .idEquals(array)
                .findFirst()
                .map(i -> Bank.withdraw(i.getId(), amount) && Waiting.waitUntil(() -> Inventory.contains(i.getId())))
                .isPresent();
    }

    private boolean hasFreeSlot() {
        return Query.grandExchangeOffers()
                .statusEquals(GrandExchangeOffer.Status.EMPTY)
                .isEnabled()
                .isAny();
    }

    private boolean isOfferPlacedButNotCompleted(GrandExchangeItem grandExchangeItem) {
        return Query.grandExchangeOffers()
                .itemNameEquals(grandExchangeItem.getName())
                .totalQuantityEquals(getRequiredAmount(grandExchangeItem))
                .statusEquals(GrandExchangeOffer.Status.IN_PROGRESS)
                .isAny();
    }

    private int incrementPrice(GrandExchangeItem grandExchangeItem) {
        return Query.grandExchangeOffers()
                .itemNameEquals(grandExchangeItem.getName())
                .totalQuantityEquals(getRequiredAmount(grandExchangeItem))
                .statusEquals(GrandExchangeOffer.Status.IN_PROGRESS)
                .findFirst()
                .map(GrandExchangeOffer::getPrice)
                .orElse(-1);
    }

    private int getBuyPrice(GrandExchangeItem grandExchangeItem) {
        int buyPrice;
        switch (interactionTask.getBuyMethod()) {
            case FIVE_PERCENT:
                buyPrice = (int) (grandExchangeItem.getPrice() * 1.05);
                break;
            case TEN_PERCENT:
                buyPrice = (int) (grandExchangeItem.getPrice() * 1.1);
                break;
            case FIFTEEN_PERCENT:
                buyPrice = (int) (grandExchangeItem.getPrice() * 1.15);
                break;
            default:
                buyPrice = grandExchangeItem.getPrice();
                break;
        }
        return buyPrice;
    }

    private int getSellPrice(GrandExchangeItem grandExchangeItem) {
        int sellPrice;
        switch (interactionTask.getSellMethod()) {
            case ONE_GP:
                sellPrice = 1;
                break;
            case FIVE_PERCENT:
                sellPrice = (int) (grandExchangeItem.getPrice() * .95);
                break;
            case TEN_PERCENT:
                sellPrice = (int) (grandExchangeItem.getPrice() * .9);
                break;
            case FIFTEEN_PERCENT:
                sellPrice = (int) (grandExchangeItem.getPrice() * .85);
                break;
            default:
                sellPrice = grandExchangeItem.getPrice();
                break;
        }
        return sellPrice;
    }

    private int getRequiredAmount(GrandExchangeItem grandExchangeItem) {
        return grandExchangeItem.getQuantity() - (BankCache.getStack(grandExchangeItem.getItemID()) + Inventory.getCount(grandExchangeItem.getItemID()));
    }

    private boolean clickCollect() {
        int inventoryCount = Inventory.getAll().size();
        return Query.widgets()
                .inRoots(GRAND_EXCHANGE_MASTER)
                .actionEquals("Collect to inventory")
                .findFirst()
                .map(w -> w.click("Collect to inventory") && Waiting.waitUntil(() -> Inventory.getAll().size() > inventoryCount))
                .orElse(false);
    }

    private boolean hasRequiredItems(List<GrandExchangeItem> grandExchangeItems) {
        for (GrandExchangeItem grandExchangeItem : grandExchangeItems) {
            if (grandExchangeItem.getName().contains("(")) {
                String name = grandExchangeItem.getName().split("^.*(?=([0-9]))")[0];
                if ((Inventory.getCount(grandExchangeItem.getItemID()) + Inventory.getCount(grandExchangeItem.getItemID() + 1) + Equipment.getCount(i -> i.getName().contains(name)) + getBankCacheStack(grandExchangeItem)) < grandExchangeItem.getQuantity()) {
                    logger.setMessage("We don't have " + grandExchangeItem.getName() + " x" + grandExchangeItem.getQuantity()).print();
                    return false;
                }
                continue;
            }
            if ((Inventory.getCount(grandExchangeItem.getItemID()) + Inventory.getCount(grandExchangeItem.getItemID() + 1) + getBankCacheStack(grandExchangeItem)) < grandExchangeItem.getQuantity()) {
                logger.setMessage("We don't have " + grandExchangeItem.getName() + " x" + grandExchangeItem.getQuantity()).print();
                return false;
            }
        }
        return true;
    }

    private int getBankCacheStack(GrandExchangeItem grandExchangeItem) {
        int stack = 0;
        if (grandExchangeItem.getTeleports() != null) {
            for (Integer i : grandExchangeItem.getTeleports().getItemIDs()) {
                stack += BankCache.getStack(i);
                General.sleep(20, 40);
            }
        } else {
            stack = BankCache.getStack(grandExchangeItem.getItemID());
        }
        logger.setMessage(grandExchangeItem.getName() + " stack is " + stack).print();
        return stack;
    }

    public static boolean open() {
        if (org.tribot.api2007.GrandExchange.getWindowState() != null) return true;
        int decision = PlayerPreferences.preference("Grand Exchange", g -> g.uniform(0, 100));
        if (General.randomSD(decision, 25) >= 50) {
            return Query
                    .npcs()
                    .nameEquals("Grand Exchange Clerk")
                    .findBestInteractable()
                    .map(g -> g.interact("Exchange") && Waiting.waitUntil(() -> org.tribot.api2007.GrandExchange.getWindowState() != null))
                    .orElse(false);
        } else {
            return Query
                    .gameObjects()
                    .nameEquals("Grand Exchange Booth")
                    .findBestInteractable()
                    .map(g -> g.interact("Exchange") && Waiting.waitUntil(() -> org.tribot.api2007.GrandExchange.getWindowState() != null))
                    .orElse(false);
        }
    }

    private void buyItems(List<GrandExchangeItem> grandExchangeItems) {
        for (GrandExchangeItem b : grandExchangeItems) {
            int coins = Inventory.getCount(ItemID.COINS_995);
            if (getRequiredAmount(b) <= 0) {
                continue;
            }
            if (isOfferPlacedButNotCompleted(b)) {
                b.setPrice(incrementPrice(b));
            }
            if (coins < getBuyPrice(b)) {
                logger.setMessage("We don't have enough coins to purchase " + b.getQuantity()).print();
                SkrrtInteractor.setRunning(false);
                return;
            }
            if (b.getPrice() == -1 || b.getName() == null) {
                logger.setMessage("Something went wrong please notify the scripter (" + b.getName() + ", " + b.getPrice() + ")").print();
                SkrrtInteractor.setRunning(false);
                return;
            } else {
                if (hasFreeSlot()) {
                    if (org.tribot.api2007.GrandExchange.offer(b.getName(), getBuyPrice(b), getRequiredAmount(b), b.isSell())) {
                        Waiting.waitNormal(600, 175);
                    }
                } else {
                    //TODO handle full slots
                }
            }
        }
    }

    private void sellItems(List<GrandExchangeItem> grandExchangeItems) {
        for (GrandExchangeItem b : grandExchangeItems) {
            int itemID = b.getItemID();
            if(!b.getRequirement().getAsBoolean()){
                return;
            }
            if (BankCache.getStack(b.getItemID()) + Inventory.getCount(b.getItemID() + 1) == 0) {
                continue;
            }
            if (Inventory.getCount(itemID + 1) == 0) {
                if (GrandExchange.isOpen()) {
                    org.tribot.api2007.GrandExchange.close();
                }
                if (Bank.open() && Waiting.waitUntil(Bank::isOpen)) {
                    if(Bank.contains(itemID)){
                        BankSettings.setNoteEnabled(!BankSettings.isNoteEnabled());
                    }
                }
                if (BankSettings.isNoteEnabled()) {
                    if (Bank.withdrawAll(itemID)) {
                        Waiting.waitUntil(() -> Inventory.contains(itemID + 1));
                    }
                }
            }
            if (b.getPrice() == -1 || b.getName() == null) {
                logger.setMessage("Something went wrong please notify the scripter (" + b.getName() + ", " + b.getPrice() + ")").print();
                SkrrtInteractor.setRunning(false);
                return;
            } else {
                if(Bank.close() && Waiting.waitUntil(()->!Bank.isOpen())){
                    open();
                }
                if (hasFreeSlot()) {
                    if (org.tribot.api2007.GrandExchange.offer(b.getName(), getSellPrice(b), Inventory.getCount(itemID + 1), b.isSell())) {
                        Waiting.waitNormal(600, 175);
                    }
                } else {
                    //TODO handle full slots
                }
            }
        }
    }
}
