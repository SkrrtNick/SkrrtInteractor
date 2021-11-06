package scripts.api.functions;

import org.tribot.api.util.abc.ABCUtil;
import org.tribot.script.sdk.Options;
import org.tribot.script.sdk.Waiting;
import scripts.api.Loggable;
import scripts.api.Logger;

public class Antiban {
    private Antiban() {
    }

    private static Antiban antiban = new Antiban();
    private Logger logger = new Logger().setLoggable(Loggable.ANTIBAN).setHeader("ABC2");

    public static Antiban get() {
        return antiban;
    }

    private ABCUtil abcUtil = new ABCUtil();
    private int activateRunAt = abcUtil.generateRunActivation();

    public void activateRun() {
        if (Options.isRunEnabled()) {
            return;
        }
        if (Options.getRunEnergy() >= activateRunAt) {
            Options.setRunEnabled(true);
            activateRunAt = abcUtil.generateRunActivation();
            logger.setMessage("Will next activate run at " + activateRunAt).print();
        }
    }

    public void idleActions() {
        if (abcUtil.shouldExamineEntity()) {
            Waiting.waitNormal(600, 175);
            abcUtil.examineEntity();
            logger.setMessage("Examining entity");
        }
        if (abcUtil.shouldRotateCamera()) {
            Waiting.waitNormal(600, 175);
            abcUtil.rotateCamera();
            logger.setMessage("Rotating camera");
        }
        if (abcUtil.shouldCheckXP()) {
            Waiting.waitNormal(600, 175);
            abcUtil.checkXP();
            logger.setMessage("Rotating camera");
        }
    }
}
