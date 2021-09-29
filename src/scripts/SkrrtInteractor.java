package scripts;

import lombok.Getter;
import lombok.Setter;
import org.tribot.api.General;
import org.tribot.script.ScriptManifest;
import org.tribot.script.sdk.painting.Painting;
import org.tribot.script.sdk.script.ScriptConfig;
import org.tribot.script.sdk.script.TribotScript;
import org.tribot.script.sdk.util.ScriptSettings;
import org.tribot.script.sdk.walking.adapter.DaxWalkerAdapter;
import scripts.api.Logger;
import scripts.api.Timer;
import scripts.api.fluffee.FluffeesPaint;
import scripts.api.fluffee.PaintInfo;
import scripts.data.Profile;
import scripts.gui.GUI;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

@ScriptManifest(name = "SkrrtMonitor", authors = {"SkrrtNick"}, category = "Tools")
public class SkrrtInteractor implements TribotScript, PaintInfo {
    Logger logger = new Logger().setHeader("SkrrtScripts");
    DaxWalkerAdapter daxWalkerAdapter = new DaxWalkerAdapter("sub_JmRkbIB2XRYqmf", "7227dd88-8182-4cd9-a3d9-00b8fa6ff56e");
    @Setter
    @Getter
    private static String status;
    @Setter
    @Getter
    private static Profile runningProfile = new Profile();
    @Setter
    @Getter
    private static boolean isRunning = true;
    @Getter
    private static final long START_TIME = System.currentTimeMillis();
    private URL fxml;
    private GUI gui;
    @Setter
    @Getter
    private String mostProfitable;
    @Setter
    @Getter
    private int mostProfit = 0, combinedProfit = 0;
    Timer timer = new Timer(0);
    private double version = 2.01;

    @Override
    public String[] getPaintInfo() {
        if (getRunningProfile().getMethodMonitors() == null) {
            return new String[]{"SkrrtPicker v" + version, "Time ran: " + SkrrtPaint.getRuntimeString(), "Status: " + getStatus()};
        }
        return new String[]{"SkrrtMonitor v" + version, "Time ran: " + SkrrtPaint.getRuntimeString()};
    }

    private final FluffeesPaint SkrrtPaint = new FluffeesPaint(this, FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new Color[]{new Color(255, 251, 255)}, "Trebuchet MS", new Color[]{new Color(0, 0, 0, 124)},
            new Color[]{new Color(179, 0, 0)}, 1, false, 5, 3, 0);

    @Override
    public void configure(ScriptConfig config) {
        config.setRandomsAndLoginHandlerEnabled(false);
    }

    @Override
    public void execute(String args) {
        Painting.setPaint(SkrrtPaint::paint);
        if (!args.isBlank()) {
            ScriptSettings settings = ScriptSettings.getDefault();
            settings.load(args, Profile.class).ifPresent(SkrrtInteractor::setRunningProfile);
        }
//        if (getRunningProfile().getMethodMonitors()==null)
        try {
            fxml = new URL("https://raw.githubusercontent.com/SkrrtNick/SkrrtMonitor/master/src/scripts/gui/main/gui.fxml");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        gui = new GUI(fxml);
        gui.show();
        while (gui.isOpen()) {
            setStatus("Waiting on user input...");
            General.sleep(500);
        }
        logger.setHeader("Profile").setMessage(getRunningProfile().toString()).print();

        while (isRunning()) {

        }
    }
}
