package scripts.tasks.object;

import lombok.Getter;
import org.tribot.api2007.GroundItems;
import org.tribot.script.sdk.*;
import org.tribot.script.sdk.query.Query;
import org.tribot.script.sdk.types.GroundItem;
import org.tribot.script.sdk.types.WorldTile;
import scripts.SkrrtInteractor;
import scripts.api.Logger;
import scripts.api.framework.Priority;
import scripts.api.framework.Task;
import scripts.api.functions.AntiPK;
import scripts.api.functions.Walking;
import scripts.data.InteractionTask;
import scripts.data.ItemID;
import scripts.tasks.BankingTask;

public class TelegrabbingGroundItem implements Task {
    @Getter
    private final InteractionTask interactionTask;
    private Logger logger = new Logger().setHeader("Telegrabber");


    public TelegrabbingGroundItem(InteractionTask interactionTask) {
        this.interactionTask = interactionTask;
    }

    @Override
    public Priority priority() {
        return Priority.LOW;
    }

    @Override
    public boolean validate() {
        return !BankingTask.isShouldBank();
    }

    @Override
    public void execute() {
        if(interactionTask.isUseAntiPK() && Combat.isInWilderness()){
            if(!SkrrtInteractor.getAntiPK().isAlive()){
                SkrrtInteractor.getAntiPK().start();
                logger.setMessage("Here").print();
            }
        } else {
            SkrrtInteractor.getAntiPK().interrupt();
        }
        EvadePK.setShouldEvade(SkrrtInteractor.getAntiPK().isPlayerDetected());
        BankingTask.setShouldBank((interactionTask.needsTeleport() && getInteractionTask().getWorldTile().distance() > 50) || !canTelegrab());
        if (BankingTask.isShouldBank()) return;
        EvadePK.setShouldEvade( SkrrtInteractor.getAntiPK().isPlayerDetected());
        if(EvadePK.isShouldEvade()) return;
        if (!SkrrtInteractor.getAntiPK().isPlayerDetected() && (!shouldWalk() || Walking.walkTo(getInteractionTask().getWorldTile(), interactionTask::walkState))) {
            WorldHopperTask.setShouldHop(interactionTask.isWorldHop() && GroundItems.find(interactionTask.getName()).length == 0);
            if (WorldHopperTask.isShouldHop()) return;
            logger.setMessage(Magic.getSelectedSpellName().orElse("")).print();
            var telegrab = Query.groundItems()
                    .nameEquals(interactionTask.getName())
                    .findBestInteractable()
                    .map(this::grabItem)
                    .orElse(false);
            if (telegrab) {
                if (interactionTask.getSafeTile() != null) {
                    Waiting.waitUntil(() -> Walking.walkTo(getSafeTile(), interactionTask::walkState));
                }
                Waiting.waitNormal(600, 175);
            }
        }
    }

    private boolean grabItem(GroundItem groundItem) {
        int inventCount = Inventory.getAll().size();
        if (!selectTelekineticGrab()) {
            return false;
        }
        return groundItem.interact("Cast") && Waiting.waitUntil(() -> Inventory.getAll().size() > inventCount);
    }

    private boolean selectTelekineticGrab() {
        return Magic.isAnySpellSelected() && Magic.getSelectedSpellName().map(s -> s.equals("Telekinetic Grab")).orElse(false) || Magic.selectSpell("Telekinetic Grab");
    }


    private boolean canTelegrab() {
        return !Inventory.isFull() && Skill.MAGIC.getActualLevel() >= 33 && (Inventory.contains(ItemID.AIR_RUNE) || Equipment.contains(ItemID.STAFF_OF_AIR)) && Inventory.contains(ItemID.LAW_RUNE);
    }

    private WorldTile getSafeTile() {
        String[] tiles = interactionTask.getSafeTile().split(",");
        return new WorldTile(Integer.parseInt(tiles[0]), Integer.parseInt(tiles[1]), Integer.parseInt(tiles[2]));
    }

    private boolean shouldWalk() {
        return interactionTask.getWorldTile().distance() > 10;
    }
}
