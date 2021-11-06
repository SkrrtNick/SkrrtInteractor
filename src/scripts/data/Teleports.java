package scripts.data;

import lombok.Getter;
import org.tribot.script.sdk.Equipment;
import org.tribot.script.sdk.Inventory;
import org.tribot.script.sdk.types.definitions.ItemDefinition;

import java.util.List;

@Getter
public enum Teleports {
    GAMES_NECKLACE("Games necklace", true, ItemID.GAMES_NECKLACE8, List.of(ItemID.GAMES_NECKLACE1, ItemID.GAMES_NECKLACE2, ItemID.GAMES_NECKLACE3, ItemID.GAMES_NECKLACE4, ItemID.GAMES_NECKLACE5, ItemID.GAMES_NECKLACE6, ItemID.GAMES_NECKLACE7, ItemID.GAMES_NECKLACE8)),
    RING_OF_DUELING("Ring of Dueling", true, ItemID.RING_OF_DUELING8, List.of(ItemID.RING_OF_DUELING1, ItemID.RING_OF_DUELING2, ItemID.RING_OF_DUELING3, ItemID.RING_OF_DUELING4, ItemID.RING_OF_DUELING5, ItemID.RING_OF_DUELING6, ItemID.RING_OF_DUELING7, ItemID.RING_OF_DUELING8)),
    RING_OF_WEALTH("Ring of Wealth", true, ItemID.RING_OF_WEALTH_5,ItemID.RING_OF_WEALTH, List.of(ItemID.RING_OF_WEALTH_1, ItemID.RING_OF_WEALTH_2, ItemID.RING_OF_WEALTH_3, ItemID.RING_OF_WEALTH_4, ItemID.RING_OF_WEALTH_5)),
    SKILLS_NECKLACE("Skills Necklace", true, ItemID.SKILLS_NECKLACE6, List.of(ItemID.SKILLS_NECKLACE1, ItemID.SKILLS_NECKLACE2, ItemID.SKILLS_NECKLACE3, ItemID.SKILLS_NECKLACE4, ItemID.SKILLS_NECKLACE5, ItemID.SKILLS_NECKLACE6)),
    AMULET_OF_GLORY("Amulet of Glory", true, ItemID.AMULET_OF_GLORY6,ItemID.AMULET_OF_GLORY, List.of(ItemID.AMULET_OF_GLORY1, ItemID.AMULET_OF_GLORY2, ItemID.AMULET_OF_GLORY3, ItemID.AMULET_OF_GLORY4, ItemID.AMULET_OF_GLORY5, ItemID.AMULET_OF_GLORY6)),
    BURNING_AMULET("Burning amulet", true, ItemID.BURNING_AMULET5, List.of(ItemID.BURNING_AMULET1, ItemID.BURNING_AMULET2, ItemID.BURNING_AMULET3, ItemID.BURNING_AMULET4, ItemID.BURNING_AMULET5)),
    NECKLACE_OF_PASSAGE("Necklace of passage", true, ItemID.NECKLACE_OF_PASSAGE5, List.of(ItemID.NECKLACE_OF_PASSAGE1, ItemID.NECKLACE_OF_PASSAGE2, ItemID.NECKLACE_OF_PASSAGE3, ItemID.NECKLACE_OF_PASSAGE4, ItemID.NECKLACE_OF_PASSAGE5)),
    COMBAT_BRACELET("Combat bracelet", true, ItemID.COMBAT_BRACELET6,ItemID.COMBAT_BRACELET, List.of(ItemID.COMBAT_BRACELET1, ItemID.COMBAT_BRACELET2, ItemID.COMBAT_BRACELET3, ItemID.COMBAT_BRACELET4, ItemID.COMBAT_BRACELET5, ItemID.COMBAT_BRACELET6)),
    VARROCK_TAB("Varrock tab", false, ItemID.VARROCK_TELEPORT, List.of(ItemID.VARROCK_TELEPORT)),
    FALADOR_TAB("Falador tab", false, ItemID.FALADOR_TELEPORT, List.of(ItemID.FALADOR_TELEPORT)),
    LUMBRIDGE_TAB("Lumbridge tab", false, ItemID.LUMBRIDGE_TELEPORT, List.of(ItemID.LUMBRIDGE_TELEPORT)),
    CAMELOT_TAB("Camelot tab", false, ItemID.CAMELOT_TELEPORT, List.of(ItemID.CAMELOT_TELEPORT)),
    ARDOUGNE_TAB("Ardougne tab", false, ItemID.ARDOUGNE_TELEPORT, List.of(ItemID.ARDOUGNE_TELEPORT)),
    WATCHTOWER_TAB("Watchtower tab", false, ItemID.WATCHTOWER_TELEPORT, List.of(ItemID.WATCHTOWER_TELEPORT)),
    RIMMINGTON_TAB("Rimmington tab", false, ItemID.RIMMINGTON_TELEPORT, List.of(ItemID.RIMMINGTON_TELEPORT)),
    TAVERLEY_TAB("Taverley tab", false, ItemID.TAVERLEY_TELEPORT, List.of(ItemID.TAVERLEY_TELEPORT));

    private String name;
    private boolean equippable;
    private int tradeableID;
    private int unchargedID;
    private String tradeableName;
    private List<Integer> itemIDs;

    Teleports(String name, boolean equippable, int tradeableID, int unchargedID, List<Integer> itemIDs) {
        this.name = name;
        this.equippable = equippable;
        this.tradeableID = tradeableID;
        this.unchargedID = unchargedID;
        this.tradeableName = ItemDefinition.get(this.tradeableID)
                .map(ItemDefinition::getName)
                .orElse(null);
        this.itemIDs = itemIDs;
    }
    Teleports(String name, boolean equippable, int tradeableID, List<Integer> itemIDs) {
        this.name = name;
        this.equippable = equippable;
        this.tradeableID = tradeableID;
        this.unchargedID = -1;
        this.tradeableName = ItemDefinition.get(this.tradeableID)
                .map(ItemDefinition::getName)
                .orElse(null);
        this.itemIDs = itemIDs;
    }

    @Override
    public String toString() {
        return name;
    }

    public static boolean hasTeleport(List<Integer> itemIDs) {
        int[] array = itemIDs.stream().mapToInt(Integer::intValue).toArray();
        return Inventory.contains(array) || Equipment.contains(array);
    }
}
