package scripts.api.functions;

import java.util.concurrent.TimeUnit;

public class Numbers {
    public static String getHumanisedRuntime(long startTime) {
        long runtime = System.currentTimeMillis() - startTime;
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(runtime),
                TimeUnit.MILLISECONDS.toMinutes(runtime) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(runtime) % TimeUnit.MINUTES.toSeconds(1));
    }
}
