package scripts.tasks.object;

import lombok.Getter;
import org.tribot.api2007.GroundItems;
import org.tribot.script.sdk.Combat;
import org.tribot.script.sdk.Inventory;
import org.tribot.script.sdk.MyPlayer;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.query.Query;
import scripts.SkrrtInteractor;
import scripts.api.Logger;
import scripts.api.framework.Priority;
import scripts.api.framework.Task;
import scripts.api.functions.Walking;
import scripts.data.GUIOptions;
import scripts.data.InteractionTask;
import scripts.tasks.BankingTask;

public class InteractingWithObject implements Task {
    @Getter
    private final InteractionTask interactionTask;
    private Logger logger = new Logger().setHeader("Interactor");

    public InteractingWithObject(InteractionTask interactionTask) {
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
        if (interactionTask.isUseAntiPK() && !Combat.isInWilderness()) {
            SkrrtInteractor.getAntiPK().interrupt();

        } else if (interactionTask.isUseAntiPK() && Combat.isInWilderness()) {
            if (!SkrrtInteractor.getAntiPK().isAlive()) {
                SkrrtInteractor.getAntiPK().start();
            }
        }
        EvadePK.setShouldEvade(SkrrtInteractor.getAntiPK().isPlayerDetected());
        if (EvadePK.isShouldEvade()) return;
        GUIOptions.Mode mode = getInteractionTask().getMode();
        GUIOptions.InteractionType interactionType = getInteractionTask().getType();
        boolean interact = false;
        BankingTask.setShouldBank(Inventory.isFull() || (interactionTask.needsTeleport() && getInteractionTask().getWorldTile().distance() > 50));
        if (BankingTask.isShouldBank()) return;
        if (!SkrrtInteractor.getAntiPK().playerDetected && (objectExists() || Walking.walkTo(getInteractionTask().getWorldTile(), interactionTask::walkState))) {
            if (interactionType.equals(GUIOptions.InteractionType.GROUND_ITEM)) {
                WorldHopperTask.setShouldHop(interactionTask.isWorldHop() && GroundItems.find(interactionTask.getName()).length == 0);
                if (WorldHopperTask.isShouldHop()) return;
                int inventCount = Inventory.getAll().size();
                interact = Query.groundItems()
                        .nameEquals(interactionTask.getName())
                        .findBestInteractable()
                        .map(i -> i.interact(interactionTask.getObjectAction()) && Waiting.waitUntil(2000, () -> Inventory.getAll().size() > inventCount))
                        .orElse(false);
            }
            if (interactionType.equals(GUIOptions.InteractionType.INTERACTIVE_OBJECT)) {
                interact = Query.gameObjects()
                        .nameEquals(interactionTask.getName())
                        .findBestInteractable()
                        .map(i -> i.interact(interactionTask.getObjectAction()) && Waiting.waitUntil(2000, () -> !MyPlayer.isMoving()))
                        .orElse(false);
            }
        }
        if (interact) {
            if (mode.equals(GUIOptions.Mode.SINGLE_INTERACTION)) {
                getInteractionTask().setCompleted(true);
            }
        }
    }

    private boolean objectExists() {
        if (interactionTask.getType().equals(GUIOptions.InteractionType.GROUND_ITEM)) {
            return Query.groundItems()
                    .nameEquals(interactionTask.getName())
                    .isReachable()
                    .isAny();
        }
        return Query.gameObjects()
                .nameEquals(interactionTask.getName())
                .isReachable()
                .isAny();
    }

}
