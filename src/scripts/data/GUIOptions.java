package scripts.data;

import lombok.Getter;
import org.tribot.script.sdk.query.Query;

import java.util.List;

@Getter
public class GUIOptions {

    public enum Stamina {
        USE_AT_BANK("Use at bank"),
        KEEP_IN_INVENTORY("Keep in inventory");

        private String name;

        Stamina(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
        public static boolean hasStamina() {
            return Query.inventory().nameContains("Stamina").findFirst().isPresent();
        }
        public static List<Integer> getStaminaIDs(){
            return List.of(ItemID.STAMINA_POTION1,ItemID.STAMINA_POTION2,ItemID.STAMINA_POTION3,ItemID.STAMINA_POTION4);
        }
    }
public enum Mode {
        PICKING("Looping"),
        SINGLE_INTERACTION("Single interaction");

        private String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

    }
public enum BuyMethods {
        FIVE_PERCENT("+5%"),
        TEN_PERCENT("+10%"),
        FIFTEEN_PERCENT("+15%");

        private String name;

        BuyMethods(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

    }public enum SellMethods {
        ONE_GP("1gp"),
        FIVE_PERCENT("-5%"),
        TEN_PERCENT("-10%"),
        FIFTEEN_PERCENT("-15%");

        private String name;

        SellMethods(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

    }

    public enum Actions {
        TALK_TO("Talk-to"),
        TELEPORT("Teleport");

        private String name;

        Actions(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum PK {
        HOP("World hop"),
        LOGOUT("Log out and wait a short time before logging back in"),
        BANK("Travel to the bank");

        private String name;

        PK(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

    }

    public enum InteractionType {
        GROUND_ITEM("GroundItem"),
        INTERACTIVE_OBJECT("GameObject"),
        CHATTER("Chatter"),
        TELEGRABBING("Telegrabbing");

        private String name;

        InteractionType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

    }
}