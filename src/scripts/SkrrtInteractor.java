package scripts;

import lombok.Getter;
import lombok.Setter;
import org.tribot.api.General;
import org.tribot.script.ScriptManifest;
import org.tribot.script.sdk.Login;
import org.tribot.script.sdk.Magic;
import org.tribot.script.sdk.Options;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.painting.Painting;
import org.tribot.script.sdk.script.ScriptConfig;
import org.tribot.script.sdk.script.TribotScript;
import org.tribot.script.sdk.util.ScriptSettings;
import org.tribot.script.sdk.walking.GlobalWalking;
import org.tribot.script.sdk.walking.WalkState;
import org.tribot.script.sdk.walking.adapter.DaxWalkerAdapter;
import scripts.api.Logger;
import scripts.api.fluffee.FluffeesPaint;
import scripts.api.fluffee.PaintInfo;
import scripts.api.functions.AntiPK;
import scripts.data.GUIOptions;
import scripts.data.InteractionTask;
import scripts.data.Profile;
import scripts.gui.GUI;
import scripts.tasks.BankingTask;
import scripts.tasks.chatter.ChatterTask;
import scripts.tasks.chatter.InteractingWithNPC;
import scripts.tasks.object.ObjectTask;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

@ScriptManifest(name = "SkrrtInteractor", authors = {"SkrrtNick"}, category = "Tools")
public class SkrrtInteractor implements TribotScript, PaintInfo {
    Logger logger = new Logger().setHeader("SkrrtScripts");
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
    private int mostProfit = 0, combinedProfit = 0;
    private double version = 2.08;
    @Setter @Getter
    private static AntiPK antiPK = new AntiPK();

    @Override
    public String[] getPaintInfo() {
        if (getRunningProfile().getInteractionTasks() == null) {
            return new String[]{"SkrrtInteractor v" + version, "Time ran: " + SkrrtPaint.getRuntimeString(), "Status: " + getStatus()};
        }
        return new String[]{"SkrrtInteractor v" + version, "Time ran: " + SkrrtPaint.getRuntimeString(), "Status: " + getStatus()};
    }

    private final FluffeesPaint SkrrtPaint = new FluffeesPaint(this, FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new Color[]{new Color(255, 251, 255)}, "Trebuchet MS", new Color[]{new Color(0, 0, 0, 124)},
            new Color[]{new Color(179, 0, 0)}, 1, false, 5, 3, 0);

    @Override
    public void configure(ScriptConfig config) {
        config.setRandomsAndLoginHandlerEnabled(true);
    }

    @Override
    public void execute(String args) {
        Painting.setPaint(SkrrtPaint::paint);
        if (!args.isBlank()) {
            ScriptSettings settings = ScriptSettings.getDefault();
            settings.load(args, Profile.class).ifPresent(SkrrtInteractor::setRunningProfile);
        }
        if (getRunningProfile().getInteractionTasks()==null) {
            try {
                fxml = new URL("https://raw.githubusercontent.com/SkrrtNick/SkrrtInteractor/master/src/scripts/gui/main/gui.fxml");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            gui = new GUI(fxml);
            gui.show();
            while (gui.isOpen()) {
                setStatus("Waiting on user input...");
                General.sleep(500);
            }
        }
        logger.setHeader("Profile").setMessage(getRunningProfile().toString()).print();
        while(!Login.isLoggedIn()){
            setStatus("Waiting for login");
            General.sleep(40,60);
        }
        while (isRunning()) {
            for(InteractionTask i : getRunningProfile().getInteractionTasks()){
                if(i.isCompleted()){
                    continue;
                }
                if(i.getType().equals(GUIOptions.InteractionType.CHATTER)){
                    ChatterTask chatterTask = new ChatterTask(i);
                    if(chatterTask.validate()){
                        chatterTask.execute();
                    }
                } else {
                    ObjectTask objectTask = new ObjectTask(i);
                    if(objectTask.validate()){
                        objectTask.execute();
                    }
                }
                if(!i.isCompleted()){
                    break;
                }
            }
            if(getRunningProfile().getInteractionTasks().stream().allMatch(InteractionTask::isCompleted)){
                break;
            }
            General.sleep(40,60);
        }
    }
}
