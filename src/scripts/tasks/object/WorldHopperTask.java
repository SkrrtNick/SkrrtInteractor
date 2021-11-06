package scripts.tasks.object;

import lombok.Getter;
import lombok.Setter;
import org.tribot.api.General;
import org.tribot.script.sdk.*;
import org.tribot.script.sdk.query.Query;
import org.tribot.script.sdk.types.Player;
import org.tribot.script.sdk.types.World;
import org.tribot.script.sdk.walking.GlobalWalking;
import scripts.SkrrtInteractor;
import scripts.api.framework.Priority;
import scripts.api.framework.Task;
import scripts.data.InteractionTask;

public class WorldHopperTask implements Task {
    private InteractionTask interactionTask;
    @Getter
    @Setter
    private static boolean shouldHop = false;

    public WorldHopperTask(InteractionTask interactionTask) {
        this.interactionTask = interactionTask;
    }

    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public boolean validate() {
        return shouldHop;
    }

    @Override
    public void execute() {
        SkrrtInteractor.setStatus("Worldhopping");
        int currentWorld = WorldHopper.getCurrentWorld();
        var inCombat = Query.npcs()
                .isInteractingWithMe()
                .minDistance(1)
                .isAny();
        if(inCombat){
            if(!evadeCombat()){
                return;
            } else {
                Waiting.waitNormal(10000,250);
            }
        }
        if (WorldHopper.isInMembersWorld()) {
            Waiting.waitUntil(5000, () -> WorldHopper.hop(Worlds.getRandomMembers().map(World::getWorldNumber).orElseThrow()));
        } else {
            Waiting.waitUntil(5000, () -> WorldHopper.hop(Worlds.getRandomNonMembers().map(World::getWorldNumber).orElseThrow()));
        }
        setShouldHop(currentWorld == WorldHopper.getCurrentWorld());
    }

    private boolean evadeCombat(){
        if(GlobalWalking.walkTo(MyPlayer.getPosition().translate(General.randomSD(5,1),General.randomSD(5,1)))){
           return Waiting.waitUntil(()->MyPlayer.get().map(Player::isHealthBarVisible).orElse(false));
        } return false;
    }

}
