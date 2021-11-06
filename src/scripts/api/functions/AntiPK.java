package scripts.api.functions;

import lombok.Getter;
import lombok.Setter;
import org.tribot.script.sdk.Combat;
import org.tribot.script.sdk.MyPlayer;
import org.tribot.script.sdk.query.Query;
import scripts.api.Loggable;
import scripts.api.Logger;

public class AntiPK extends Thread {
    private Logger logger = new Logger().setHeader("PK Detection");
    private Thread thread;

    public AntiPK() {
    }

    public void run() {
        try {
            while (Combat.isInWilderness()) {
                setPlayerDetected(dangerousPlayerDetected());
                Thread.sleep(250);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean dangerousPlayerDetected() {
        if (!Combat.isInWilderness()) {
            logger.setMessage("Not in wilderness").print();
            return false;
        }
        return Query.players().maxDistance(20).maxLevel(getMaxLevel()).minLevel(getMinLevel()).isAny();
//       return Query.players().maxDistance(20).maxLevel(30).minLevel(3).isAny();
    }

    private int getMaxLevel() {
        return MyPlayer.getCombatLevel() + Combat.getWildernessLevel();

    }

    private int getMinLevel() {
        return MyPlayer.getCombatLevel() - Combat.getWildernessLevel();
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    @Setter @Getter
    public boolean playerDetected = false;

}
