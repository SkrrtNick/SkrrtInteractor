package scripts.api.functions;

import org.tribot.api.General;
import org.tribot.api.ScriptCache;
import org.tribot.api2007.Game;
import org.tribot.script.sdk.Bank;
import org.tribot.script.sdk.Options;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.antiban.PlayerPreferences;
import org.tribot.script.sdk.query.Query;
import org.tribot.script.sdk.types.WorldTile;
import org.tribot.script.sdk.walking.GlobalWalking;
import org.tribot.script.sdk.walking.WalkState;
import scripts.SkrrtInteractor;
import scripts.api.Loggable;
import scripts.api.Logger;
import scripts.data.GUIOptions;
import scripts.tasks.BankingTask;

import java.util.function.Supplier;

public class Walking {
    private static Logger logger = new Logger().setHeader("Walker");
    private static final int distancePreference = PlayerPreferences.preference("Walking distance", g -> g.normal(2, 4, 3, 1));
    private static int attempt = 0;

    public static boolean walkTo(WorldTile worldTile, Supplier<WalkState> walkCondition) {
        SkrrtInteractor.setStatus("Walking to destination");
        int distance = General.randomSD(distancePreference, distancePreference / 3);
        if (worldTile.distance() <= distance) {
            return true;
        }
        if (GlobalWalking.walkTo(worldTile,walkCondition)) {
            if (Waiting.waitUntil(20000, () -> worldTile.distance() <= distance)) {
                logger.setMessage("We reached our destination.").print();
            }
        } else {
            logger.setMessage("Something went wrong trying to reach the destination.").print();
            attempt++;
            if (attempt == 5) {
                logger.setMessage("Something went really wrong").setLoggable(Loggable.FATAL_ERROR).print();
            }
        }
        return worldTile.distance() <= distance;
    }

    public static boolean walkToBank(Supplier<WalkState> walkCondition) {
        SkrrtInteractor.setStatus("Walking to Bank");
        if (GlobalWalking.walkToBank(walkCondition)) {
            if (Waiting.waitUntil(20000, Walking::closeToBank)) {
                logger.setMessage("We reached our destination.").print();
            }
        } else {
            logger.setMessage("Something went wrong trying to reach the destination.").print();
            attempt++;
            if (attempt == 5) {
                logger.setMessage("Something went really wrong").setLoggable(Loggable.FATAL_ERROR).print();
            }
        }
        return closeToBank();
    }

    private static boolean closeToBank(){
        return Query.gameObjects().nameContains("Bank").maxDistance(General.randomSD(distancePreference, distancePreference / 3)).isAny();
    }
}

