package scripts.data;

import lombok.Data;
import org.tribot.api.General;
import org.tribot.api2007.Combat;
import org.tribot.script.sdk.Options;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.antiban.AntibanProperties;
import org.tribot.script.sdk.antiban.PlayerPreferences;
import org.tribot.script.sdk.types.WorldTile;
import org.tribot.script.sdk.walking.WalkState;
import scripts.api.Logger;
import scripts.api.functions.AntiPK;
import scripts.api.functions.Antiban;
import scripts.tasks.BankingTask;

import java.util.List;

@Data
public class InteractionTask {
    private Logger logger = new Logger().setHeader("InteractionTask");
    //Shared preferences
    String name;
    String tile;
    GUIOptions.InteractionType type;
    Teleports teleport;
    GUIOptions.Stamina stamina;
    boolean completed;

    //Chatter Settings
    String action;
    String dialogue;

    //Object Settings
    GUIOptions.Mode mode;
    boolean worldHop;
    String objectAction;
    int itemID = 0;

    //GE Settings
    boolean useRestocking;
    GUIOptions.BuyMethods buyMethod;
    boolean sellItems;
    GUIOptions.SellMethods sellMethod;

    //Telegrabbing settings
    String safeTile;
    boolean useStaff;
    int runes;

    //PK/Wildy related settings
    boolean useAntiPK;
    GUIOptions.PK antiPKMethod;

    public InteractionTask(String name, String tile, Teleports teleport, GUIOptions.Stamina stamina, String action, String dialogue) {
        this.type = GUIOptions.InteractionType.CHATTER;
        this.name = name;
        this.tile = tile;
        this.teleport = teleport;
        this.stamina = stamina;
        this.action = action;
        this.dialogue = dialogue;
    }

    public InteractionTask(String name, String tile, Teleports teleport, GUIOptions.Stamina stamina, GUIOptions.InteractionType type, GUIOptions.Mode mode, boolean useRestocking, GUIOptions.BuyMethods buyMethod, boolean sellItems, GUIOptions.SellMethods sellMethod, boolean useAntiPK, GUIOptions.PK antiPKMethod, String objectAction, boolean worldHop, String safeTile, boolean useStaff, int runes, int itemID) {
        this.name = name;
        this.tile = tile;
        this.teleport = teleport;
        this.stamina = stamina;
        this.type = type;
        this.mode = mode;
        this.useRestocking = useRestocking;
        this.buyMethod = buyMethod;
        this.sellItems = sellItems;
        this.sellMethod = sellMethod;
        this.useAntiPK = useAntiPK;
        this.antiPKMethod = antiPKMethod;
        this.objectAction = objectAction;
        this.worldHop = worldHop;
        this.safeTile = safeTile;
        this.useStaff = useStaff;
        this.runes = runes;
        this.itemID = itemID;
    }

    @Override
    public String toString() {
        String string = null;
        switch (this.type) {
            case CHATTER:
                string = "ChatterTask{" +
                        ", name='" + name + '\'' +
                        ", tile='" + tile + '\'' +
                        ", teleport='" + teleport + '\'' +
                        ", stamina='" + stamina + '\'' +
                        ", action='" + action + '\'' +
                        ", dialogue='" + dialogue + '\'' +
                        '}';
                break;
            case GROUND_ITEM:
                string = "GroundItemTask{" +
                        ", name='" + name + '\'' +
                        ", tile='" + tile + '\'' +
                        ", teleport='" + teleport + '\'' +
                        ", stamina='" + stamina + '\'' +
                        ", type='" + type + '\'' +
                        ", mode='" + mode + '\'' +
                        ", useRestocking=" + useRestocking +
                        ", buyMethod='" + buyMethod + '\'' +
                        ", sellItems=" + sellItems +
                        ", sellMethod='" + sellMethod + '\'' +
                        ", sellItemID='" + itemID + '\'' +
                        ", useAntiPK=" + useAntiPK +
                        ", antiPKMethod='" + antiPKMethod + '\'' +
                        '}';
                break;
            case TELEGRABBING:
                string = "TelegrabbingTask{" +
                        ", name='" + name + '\'' +
                        ", tile='" + tile + '\'' +
                        ", teleport='" + teleport + '\'' +
                        ", stamina='" + stamina + '\'' +
                        ", type='" + type + '\'' +
                        ", mode='" + mode + '\'' +
                        ", useRestocking=" + useRestocking +
                        ", buyMethod='" + buyMethod + '\'' +
                        ", sellItems=" + sellItems +
                        ", sellMethod='" + sellMethod + '\'' +
                        ", sellItemID='" + itemID + '\'' +
                        ", useAntiPK=" + useAntiPK +
                        ", antiPKMethod='" + antiPKMethod + '\'' +
                        ", safeTile='" + safeTile + '\'' +
                        '}';
                break;
            case INTERACTIVE_OBJECT:
                string = "GameObjectTask{" +
                        ", name='" + name + '\'' +
                        ", tile='" + tile + '\'' +
                        ", teleport='" + teleport + '\'' +
                        ", stamina='" + stamina + '\'' +
                        ", type='" + type + '\'' +
                        ", mode='" + mode + '\'' +
                        ", useRestocking=" + useRestocking +
                        ", buyMethod='" + buyMethod + '\'' +
                        ", sellItems=" + sellItems +
                        ", sellMethod='" + sellMethod + '\'' +
                        ", sellItemID='" + itemID + '\'' +
                        ", useAntiPK=" + useAntiPK +
                        ", antiPKMethod='" + antiPKMethod + '\'' +
                        '}';
                break;
        }
        return string;
    }

    public boolean needsTeleport() {
        return this.teleport != null && !Teleports.hasTeleport(this.teleport.getItemIDs());
    }

    public WorldTile getWorldTile() {
        String[] tiles = this.getTile().split(",");
        return new WorldTile(Integer.parseInt(tiles[0]), Integer.parseInt(tiles[1]), Integer.parseInt(tiles[2]));
    }

    public WalkState walkState() {
        Antiban.get().activateRun();
        int runEnergyPreference = PlayerPreferences.preference("Running distance", g -> g.normal(5, 30, 10, 5));
        int drinkStamAt = General.randomSD(runEnergyPreference, 5);
        if(this.isUseAntiPK() && Combat.isInWilderness()){
            AntiPK antiPK = new AntiPK();
            antiPK.start();
        }
        if (!Options.isRunEnabled() && Options.getRunEnergy() == 100 && Options.setRunEnabled(true)) {
            return WalkState.CONTINUE;
        }
        if (!GUIOptions.Stamina.hasStamina()) {
            return WalkState.CONTINUE;
        }
        if (Options.getRunEnergy() <= drinkStamAt) {
            if (Waiting.waitUntil(BankingTask::drinkStam) && Options.setRunEnabled(true)) {
                return WalkState.CONTINUE;
            }
        }
        return WalkState.CONTINUE;
    }
}
