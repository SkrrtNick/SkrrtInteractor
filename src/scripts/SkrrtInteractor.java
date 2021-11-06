package scripts;

import lombok.Getter;
import lombok.Setter;
import org.tribot.api.General;
import org.tribot.script.ScriptManifest;
import org.tribot.script.sdk.Login;
import org.tribot.script.sdk.painting.Painting;
import org.tribot.script.sdk.script.ScriptConfig;
import org.tribot.script.sdk.script.TribotScript;
import org.tribot.script.sdk.util.ScriptSettings;
import scripts.api.Logger;
import scripts.api.functions.AntiPK;
import scripts.api.functions.Numbers;
import scripts.data.GUIOptions;
import scripts.data.InteractionTask;
import scripts.data.Profile;
import scripts.gui.GUI;
import scripts.tasks.chatter.ChatterTask;
import scripts.tasks.object.ObjectTask;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@ScriptManifest(name = "SkrrtInteractor", authors = {"SkrrtNick"}, category = "Tools")
public class SkrrtInteractor implements TribotScript {
    Logger logger = new Logger().setHeader("SkrrtScripts");
    private int ellipses = 0;
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
    private double version = 2.09;
    @Setter @Getter
    private static AntiPK antiPK = new AntiPK();

    @Override
    public void configure(ScriptConfig config) {
        config.setRandomsAndLoginHandlerEnabled(true);
    }

    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    final Image paintBg = getImage("https://imgur.com/vvn4em8.png");


    Font myMainFont = new Font("Calibri", 1, 12);
    Font versionFont = new Font("Calibri", 1, 11);


    private String getEllipses() {
        return ".".repeat(ellipses++);
    }

    @Override
    public void execute(String args) {

        Painting.addPaint(ui -> {
            ui.setFont(myMainFont);
            ui.setColor(new Color(1, 1, 1, 0.4f));
            ui.drawImage(paintBg, 275, 208, 240, 130, null);
            ui.setColor(Color.DARK_GRAY);
            ui.drawString(Numbers.getHumanisedRuntime(START_TIME), 334, 264);
            ui.drawString("Status: " + getStatus(), 334, 298);
            ui.setColor(Color.red);
            ui.setFont(versionFont);
            ui.drawString("V.", 480, 205);
            ui.setColor(Color.ORANGE);
            ui.drawString(String.valueOf(version), 490, 205);
        });
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
                setStatus("Waiting on user input" + getEllipses());
                if(ellipses==4) ellipses = 0;
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
