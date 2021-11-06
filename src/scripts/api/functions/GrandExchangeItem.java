package scripts.api.functions;

import lombok.Data;
import org.tribot.script.sdk.pricing.Pricing;
import org.tribot.script.sdk.types.definitions.ItemDefinition;
import scripts.data.Teleports;

import java.util.List;
import java.util.function.BooleanSupplier;

@Data
public class GrandExchangeItem {
    private int itemID;
    private int price;
    private String name;
    private int quantity;
    private BooleanSupplier requirement;
    private Teleports teleports;
    private boolean sell;

    public GrandExchangeItem(int itemID, int quantity, BooleanSupplier requirement, boolean sell) {
        this.itemID = itemID;
        this.price = Pricing.lookupPrice(itemID).orElse(-1);
        this.name = ItemDefinition.get(itemID).map(ItemDefinition::getName).orElse(null);
        this.quantity = quantity;
        this.requirement = requirement;
        this.teleports = null;
        this.sell = sell;
    }

    public GrandExchangeItem(Teleports teleports, int quantity, BooleanSupplier requirement, boolean sell) {
        this.itemID = teleports.getTradeableID();
        this.name = ItemDefinition.get(itemID).map(ItemDefinition::getName).orElse(null);
        this.price = Pricing.lookupPrice(itemID).orElse(-1);
        this.quantity = quantity;
        this.requirement = requirement;
        this.teleports = teleports;
        this.sell = sell;
    }
}
