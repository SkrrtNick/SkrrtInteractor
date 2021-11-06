package scripts.tasks.object;

import lombok.Getter;
import lombok.Setter;
import org.tribot.api.ScriptCache;
import org.tribot.script.sdk.Login;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.script.ScriptConfig;
import scripts.SkrrtInteractor;
import scripts.api.Logger;
import scripts.api.framework.Priority;
import scripts.api.framework.Task;
import scripts.api.functions.Walking;
import scripts.data.InteractionTask;

public class EvadePK implements Task {
    private InteractionTask interactionTask;
    private Logger logger = new Logger().setHeader("PK Detection");
    @Setter
    @Getter
    private static boolean shouldEvade = false;
    public EvadePK(InteractionTask interactionTask){
        this.interactionTask = interactionTask;
    }
    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public boolean validate() {
        return shouldEvade;
    }

    @Override
    public void execute() {
        SkrrtInteractor.setStatus("Evading PKer");
        logger.setMessage("Is Player detected: " + SkrrtInteractor.getAntiPK().isPlayerDetected()).print();
        switch (interactionTask.getAntiPKMethod()){
            case HOP:
                new WorldHopperTask(interactionTask).execute();
                setShouldEvade(false);
                break;
            case BANK:
                if(Walking.walkToBank(interactionTask::walkState)){
                    Waiting.waitNormal(30000,2500);
                }
                setShouldEvade(false);
                break;
            case LOGOUT:
                if(Login.logout()){
                    Waiting.waitNormal(30000,2500);
                }
                setShouldEvade(false);
                break;
        }
        logger.setMessage("Is Player detected: " + SkrrtInteractor.getAntiPK().isPlayerDetected()).print();
    }
}
