package scripts.tasks;

import lombok.Getter;
import lombok.Setter;
import org.tribot.script.sdk.Bank;
import org.tribot.script.sdk.Equipment;
import org.tribot.script.sdk.Inventory;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.query.Query;
import org.tribot.script.sdk.types.InventoryItem;
import scripts.SkrrtInteractor;
import scripts.api.Loggable;
import scripts.api.Logger;
import scripts.api.framework.Priority;
import scripts.api.framework.Task;
import scripts.api.functions.BankItem;
import scripts.api.functions.Walking;
import scripts.data.GUIOptions;
import scripts.data.InteractionTask;
import scripts.data.ItemID;
import scripts.tasks.object.GrandExchangeTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BankingTask implements Task {
    @Getter
    private final InteractionTask interactionTask;
    @Getter
    @Setter
    private static boolean shouldBank = false;
    private Logger logger = new Logger().setHeader("Banking").setLoggable(Loggable.ERROR);

    public BankingTask(InteractionTask interactionTask) {
        this.interactionTask = interactionTask;
    }

    @Override
    public Priority priority() {
        return Priority.MEDIUM;
    }

    @Override
    public boolean validate() {
        return shouldBank;
    }

    @Override
    public void execute() {
        SkrrtInteractor.setStatus("Banking...");
        List<BankItem> bankItems = new ArrayList<>() {{
            add(new BankItem(interactionTask.getTeleport(), 1, interactionTask.getTeleport().isEquippable(), () -> interactionTask.getTeleport() != null));
            add(new BankItem(GUIOptions.Stamina.getStaminaIDs(), 1, () -> interactionTask.getStamina() != null));
            add(new BankItem(List.of(ItemID.LAW_RUNE), interactionTask.getRunes(), () -> interactionTask.getType().equals(GUIOptions.InteractionType.TELEGRABBING)));
            add(new BankItem(List.of(ItemID.STAFF_OF_AIR), interactionTask.getRunes(), true, () -> interactionTask.getType().equals(GUIOptions.InteractionType.TELEGRABBING) && interactionTask.isUseStaff()));
            add(new BankItem(List.of(ItemID.AIR_RUNE), interactionTask.getRunes(), () -> interactionTask.getType().equals(GUIOptions.InteractionType.TELEGRABBING) && !interactionTask.isUseStaff()));
        }};
        if (!Bank.isNearby()) {
            Walking.walkToBank(interactionTask::walkState);
        }
        if (!Bank.isOpen() && Bank.open()) {
            List<InventoryItem> inventoryItems = Inventory.getAll();
            if (!inventoryItems.isEmpty()) {
                for (InventoryItem i : inventoryItems) {
                    if (!getAllValidIDs(bankItems).contains(i.getId())) {
                        if (Bank.depositAll(i.getId())) {
                            Waiting.waitNormal(600, 175);
                        }
                    }
                }
            }
            Collections.shuffle(bankItems);
            for (BankItem b : bankItems) {
                if (!b.getRequirement().getAsBoolean()) {
                    continue;
                }
                if (isWearing(b.getItemIDs()) || inventAmount(b.getItemIDs()) == b.getQuantity()) {
                    continue;
                }
                if (!withdraw(b.getItemIDs(), b.getQuantity() - inventAmount(b.getItemIDs()))) {
                    if(!wearItem(b)){
                        logger.setMessage("Withdrawing failed, switching to restocking").print();
                        GrandExchangeTask.setShouldRestock(true);
                        setShouldBank(false);
                        return;
                    }
                }
                if(wearItem(b)){

                }
            }
        }
        setShouldBank(false);
    }

    private boolean withdraw(List<Integer> itemIDs, int amount) {
        int[] array = itemIDs.stream().mapToInt(Integer::intValue).toArray();
        return Query.bank()
                .idEquals(array)
                .findFirst()
                .map(i -> Bank.withdraw(i.getId(), amount) && Waiting.waitUntil(() -> Inventory.contains(i.getId())))
                .isPresent();
    }

    private int inventAmount(List<Integer> itemIDs) {
        int[] array = itemIDs.stream().mapToInt(Integer::intValue).toArray();
        return Query.inventory()
                .idEquals(array)
                .findFirst()
                .map(InventoryItem::getStack)
                .orElse(0);
    }

    private int getCurrentID(List<Integer> itemIDs) {
        int[] array = itemIDs.stream().mapToInt(Integer::intValue).toArray();
        return Query.inventory()
                .idEquals(array)
                .findFirst()
                .map(InventoryItem::getId)
                .orElse(-1);
    }
    private boolean wearItem(BankItem bankItem){
        int currentID = getCurrentID(bankItem.getItemIDs());
        if (bankItem.isEquippable()) {
            List<InventoryItem> inventoryItem = Inventory.getAll();
            if (!Equipment.equip(currentID)) {
                return false;
            } else {
                Waiting.waitNormal(600,175);
            }
            if (depositUnequipped(inventoryItem)) {
                Waiting.waitNormal(600, 175);
                return true;
            }
        } return Equipment.contains(currentID);
    }


    public static boolean drinkStam() {
        return Query.inventory()
                .nameContains("Stamina")
                .findFirst()
                .map(i -> i.click("Drink"))
                .orElse(false);
    }

    private List<Integer> getAllValidIDs(List<BankItem> bankItems) {
        List<Integer> list = new ArrayList<>();
        for (BankItem b : bankItems) {
            if(b.getRequirement().getAsBoolean()){
                list.addAll(b.getItemIDs());
            }
        }
        return list;
    }

    private boolean depositUnequipped(List<InventoryItem> inventoryItems) {
        return Query.inventory()
                .filter(i -> !inventoryItems.contains(i))
                .findFirst()
                .map(i -> Bank.depositAll(i.getId()))
                .orElse(false);
    }

    private boolean isWearing(List<Integer> itemID) {
        int[] array = itemID.stream().mapToInt(Integer::intValue).toArray();
        return Query.equipment()
                .idEquals(array)
                .isAny();
    }
}
