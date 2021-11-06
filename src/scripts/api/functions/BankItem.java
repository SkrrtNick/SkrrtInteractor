package scripts.api.functions;

import lombok.Data;
import scripts.data.Teleports;

import java.util.List;
import java.util.function.BooleanSupplier;
@Data
public class BankItem {
    private List<Integer> itemIDs;
    private int quantity;
    private BooleanSupplier requirement;
    private boolean isEquippable;
    private Teleports teleports;

    public BankItem(List<Integer> itemIDs, int quantity, BooleanSupplier requirement) {
        this.itemIDs = itemIDs;
        this.quantity = quantity;
        this.requirement = requirement;
        this.teleports = null;
    }
    public BankItem(List<Integer> itemIDs, int quantity, boolean isEquippable, BooleanSupplier requirement) {
        this.itemIDs = itemIDs;
        this.quantity = quantity;
        this.isEquippable = isEquippable;
        this.requirement = requirement;
    }
    public BankItem(Teleports teleports, int quantity, BooleanSupplier requirement) {
        this.itemIDs = teleports.getItemIDs();
        this.quantity = quantity;
        this.requirement = requirement;
        this.isEquippable = false;
        this.teleports = teleports;
    }

    public BankItem(Teleports teleports, int quantity, boolean isEquippable, BooleanSupplier requirement) {
        this.itemIDs = teleports.getItemIDs();
        this.quantity = quantity;
        this.isEquippable = isEquippable;
        this.requirement = requirement;
        this.teleports = teleports;
    }
}
