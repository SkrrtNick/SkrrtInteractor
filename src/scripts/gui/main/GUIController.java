package scripts.gui.main;

import com.allatori.annotations.DoNotRename;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.tribot.script.sdk.Login;
import org.tribot.script.sdk.MyPlayer;
import org.tribot.script.sdk.Waiting;
import org.tribot.script.sdk.types.WorldTile;
import org.tribot.script.sdk.util.ScriptSettings;
import scripts.SkrrtInteractor;
import scripts.api.Loggable;
import scripts.api.Logger;
import scripts.data.GUIOptions;
import scripts.data.InteractionTask;
import scripts.data.Profile;
import scripts.data.Teleports;
import scripts.gui.AbstractGUIController;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@DoNotRename
public class GUIController extends AbstractGUIController {
    private Logger logger = new Logger().setHeader("GUI");

    @DoNotRename
    @FXML
        private Button btnAddNPCTask;

    @DoNotRename
    @FXML
        private Button btnAddObjectTask;

    @DoNotRename
    @FXML
        private Button btnAddTelegrabTask;

    @DoNotRename
    @FXML
        private Button btnGrabNPCTile;

    @DoNotRename
    @FXML
        private Button btnGroundItemIngameTile;

    @DoNotRename
    @FXML
        private Button btnLoad;

    @DoNotRename
    @FXML
        private Button btnObjectIngameTile;

    @DoNotRename
    @FXML
        private Button btnSafeTile;

    @DoNotRename
    @FXML
        private Button btnSave;

    @DoNotRename
    @FXML
        private Button btnStart;

    @DoNotRename
    @FXML
        private Button btnTelegrabtIngameTile;

    @DoNotRename
    @FXML
        private CheckBox checkGroundItemAntiPK;

    @DoNotRename
    @FXML
        private CheckBox checkGroundItemRestocking;

    @DoNotRename
    @FXML
        private CheckBox checkGroundItemSellLoot;

    @DoNotRename
    @FXML
        private CheckBox checkGroundItemWorldHop;

    @DoNotRename
    @FXML
        private CheckBox checkObjectAntiPK;

    @DoNotRename
    @FXML
        private CheckBox checkObjectRestocking;

    @DoNotRename
    @FXML
        private CheckBox checkObjectSellLoot;

    @DoNotRename
    @FXML
        private CheckBox checkStaff;

    @DoNotRename
    @FXML
        private CheckBox checkTelegrabAntiPK;

    @DoNotRename
    @FXML
        private CheckBox checkTelegrabRestocking;

    @DoNotRename
    @FXML
        private CheckBox checkTelegrabSellLoot;

    @DoNotRename
    @FXML
        private CheckBox checkTelegrabWorldHop;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.Actions> cmbAction;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.Mode> cmbGameObjectMode;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.BuyMethods> cmbGameObjectRestocking;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.SellMethods> cmbGameObjectSellItems;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.Mode> cmbGroundItemMode;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.PK> cmbGroundItemPK;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.BuyMethods> cmbGroundItemRestocking;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.SellMethods> cmbGroundItemSellItems;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.Stamina> cmbGroundItemStamina;

    @DoNotRename
    @FXML
        private ComboBox<Teleports> cmbGroundItemTeleport;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.Stamina> cmbNPCStamina;

    @DoNotRename
    @FXML
        private ComboBox<Teleports> cmbNPCTeleport;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.PK> cmbObjectPK;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.Stamina> cmbObjectStamina;

    @DoNotRename
    @FXML
        private ComboBox<Teleports> cmbObjectTeleport;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.PK> cmbTelegrabPK;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.BuyMethods> cmbTelegrabRestocking;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.SellMethods> cmbTelegrabSellItems;

    @DoNotRename
    @FXML
        private ComboBox<GUIOptions.Stamina> cmbTelegrabStamina;

    @DoNotRename
    @FXML
        private ComboBox<Teleports> cmbTelegrabTeleport;


    @DoNotRename
    @FXML
        private ListView<InteractionTask> listViewTasks;

    @DoNotRename
    @FXML
        private MenuItem mnuDelete;

    @DoNotRename
    @FXML
        private MenuItem mnuDuplicate;

    @DoNotRename
    @FXML
        private MenuItem mnuEdit;

    @DoNotRename
    @FXML
        private Spinner<Integer> spinRunes;

    @DoNotRename
    @FXML
        private Text titleAction;

    @DoNotRename
    @FXML
        private Text titleAction1;

    @DoNotRename
    @FXML
        private TextArea txtDialogue;

    @DoNotRename
    @FXML
        private TextField txtGameObjectAction;

    @DoNotRename
    @FXML
        private TextField txtGroundItemAction;

    @DoNotRename
    @FXML
        private TextField txtGroundItemName;

    @DoNotRename
    @FXML
        private TextField txtGroundItemTile;

    @DoNotRename
    @FXML
        private TextField txtNPCName;

    @DoNotRename
    @FXML
        private TextField txtNPCTile;

    @DoNotRename
    @FXML
        private TextField txtObjectName;

    @DoNotRename
    @FXML
        private TextField txtObjectTile;

    @DoNotRename
    @FXML
        private TextField txtSafeTile;

    @DoNotRename
    @FXML
        Tab txtTelegrabName;

    @DoNotRename
    @FXML
        TextField txtTelegrabbingName;

    @DoNotRename
    @FXML
        TextField txtTelegrabbingTile;


    @DoNotRename
    @FXML
        void btnAddTelegrabTaskPressed(ActionEvent event) {
            if (txtTelegrabbingName.getText().isBlank() || txtTelegrabbingTile.getText().isBlank()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Warning!");
                alert.setHeaderText("Warning!");
                alert.setContentText("Please ensure you populate the name and tile fields");
                alert.show();
                return;
            }
            interactionTasks.add(new InteractionTask(
                    txtTelegrabbingName.getText(),
                    txtTelegrabbingTile.getText(),
                    cmbTelegrabTeleport.getValue(),
                    cmbTelegrabStamina.getValue(),
                    GUIOptions.InteractionType.TELEGRABBING,
                    null,
                    checkTelegrabRestocking.isSelected(),
                    cmbTelegrabRestocking.getValue(),
                    checkTelegrabSellLoot.isSelected(),
                    cmbTelegrabSellItems.getValue(),
                    checkTelegrabAntiPK.isSelected(),
                    cmbTelegrabPK.getValue(),
                    null,
                    checkTelegrabWorldHop.isSelected(),
                    txtSafeTile.getText(),
                    checkStaff.isSelected(),
                    spinRunes.getValue(),
                    Integer.parseInt(txtGameObjectID.getText())));
            listViewTasks.refresh();
        }

        @DoNotRename
        @FXML
        void btnSafeTilePressed(ActionEvent event) {
            if (Login.login()) {
                if (Waiting.waitUntil(Login::isLoggedIn)) {
                    WorldTile tile = MyPlayer.getPosition();
                    if (tile != null) {
                        txtSafeTile.setText(tile.getX() + "," + tile.getY() + "," + tile.getPlane());
                    }
                }
            } else {
                logger.setLoggable(Loggable.ERROR).setMessage("We failed to login!").print();
            }
        }



        @DoNotRename
        @FXML
        void btnTelegrabIngameTilePressed(ActionEvent event) {
            getIngameTile(txtSafeTile);
        }


    @DoNotRename
        @FXML
    private ComboBox<String> cmbProfile;

    List<InteractionTask> interactionTasks = new ArrayList<>();

    @DoNotRename
    @FXML
    void mnuDeletePressed(ActionEvent event) {
        interactionTasks.remove(listViewTasks.getSelectionModel().getSelectedItem());
        listViewTasks.refresh();
        logger.setMessage(interactionTasks.toString()).print();
    }

    @DoNotRename
    @FXML
    void mnuDuplicatePressed(ActionEvent event) {
        InteractionTask interactionTask = listViewTasks.getSelectionModel().getSelectedItem();
        if (interactionTask != null) {
            listViewTasks.getItems().add(interactionTask);
        }
    }


    @DoNotRename
    @FXML
    void btnLoadPressed(ActionEvent event) {
        load(cmbProfile.getValue());
    }


    @DoNotRename
    @FXML
    void btnSavePressed(ActionEvent event) {
        save(cmbProfile.getValue());
    }


    @DoNotRename
    @FXML
    private TextField txtObjectAction;

    @DoNotRename
    @FXML
    private Button btnAddGroundItemTask;

    @DoNotRename
    @FXML
    private TextField txtGameObjectID;

    @DoNotRename
    @FXML
    private TextField txtTelegrabbingID;

    @DoNotRename
    @FXML
    private TextField txtGroundItemID;

    @DoNotRename
    @FXML
    void mnuEditClicked(ActionEvent event) {
        InteractionTask interactionTask = listViewTasks.getSelectionModel().getSelectedItem();
        if (interactionTask != null) {
            switch (interactionTask.getType()){
                case CHATTER:
                    txtNPCName.setText(interactionTask.getName());
                    txtNPCTile.setText(interactionTask.getTile());
                    txtDialogue.setText(interactionTask.getDialogue());
                    cmbNPCStamina.getSelectionModel().select(interactionTask.getStamina());
                    cmbAction.getEditor().setText(interactionTask.getAction());
                    cmbNPCTeleport.getSelectionModel().select(interactionTask.getTeleport());
                    break;
                case INTERACTIVE_OBJECT:
                    txtObjectName.setText(interactionTask.getName());
                    txtObjectTile.setText(interactionTask.getTile());
                    cmbObjectTeleport.getSelectionModel().select(interactionTask.getTeleport());
                    cmbObjectStamina.getSelectionModel().select(interactionTask.getStamina());
                    checkObjectRestocking.setSelected(interactionTask.isUseRestocking());
                    cmbGameObjectMode.getSelectionModel().select(interactionTask.getMode());
                    cmbGameObjectRestocking.getSelectionModel().select(interactionTask.getBuyMethod());
                    checkObjectSellLoot.setSelected(interactionTask.isSellItems());
                    cmbGameObjectSellItems.getSelectionModel().select(interactionTask.getSellMethod());
                    checkObjectAntiPK.setSelected(interactionTask.isUseAntiPK());
                    txtObjectAction.setText(interactionTask.getObjectAction());
                    cmbObjectPK.getSelectionModel().select(interactionTask.getAntiPKMethod());
                    txtGameObjectID.setText(String.valueOf(interactionTask.getItemID()));
                    break;
                case GROUND_ITEM:
                    txtGroundItemName.setText(interactionTask.getName());
                    txtGroundItemTile.setText(interactionTask.getTile());
                    cmbGroundItemTeleport.getSelectionModel().select(interactionTask.getTeleport());
                    cmbGroundItemStamina.getSelectionModel().select(interactionTask.getStamina());
                    cmbGroundItemMode.getSelectionModel().select(interactionTask.getMode());
                    checkGroundItemRestocking.setSelected(interactionTask.isUseRestocking());
                    cmbGroundItemRestocking.getSelectionModel().select(interactionTask.getBuyMethod());
                    checkGroundItemSellLoot.setSelected(interactionTask.isSellItems());
                    cmbGroundItemSellItems.getSelectionModel().select(interactionTask.getSellMethod());
                    checkGroundItemAntiPK.setSelected(interactionTask.isUseAntiPK());
                    txtGroundItemAction.setText(interactionTask.getObjectAction());
                    cmbGroundItemPK.getSelectionModel().select(interactionTask.getAntiPKMethod());
                    checkGroundItemWorldHop.setSelected(interactionTask.isWorldHop());
                    txtGroundItemID.setText(String.valueOf(interactionTask.getItemID()));
                    break;
                case TELEGRABBING:
                    txtTelegrabbingName.setText(interactionTask.getName());
                    txtTelegrabbingTile.setText(interactionTask.getTile());
                    cmbTelegrabTeleport.getSelectionModel().select(interactionTask.getTeleport());
                    cmbTelegrabStamina.getSelectionModel().select(interactionTask.getStamina());
                    checkTelegrabRestocking.setSelected(interactionTask.isUseRestocking());
                    cmbTelegrabRestocking.getSelectionModel().select(interactionTask.getBuyMethod());
                    checkTelegrabSellLoot.setSelected(interactionTask.isSellItems());
                    cmbTelegrabSellItems.getSelectionModel().select(interactionTask.getSellMethod());
                    checkTelegrabAntiPK.setSelected(interactionTask.isUseAntiPK());
                    cmbTelegrabPK.getSelectionModel().select(interactionTask.getAntiPKMethod());
                    checkTelegrabWorldHop.setSelected(interactionTask.isWorldHop());
                    checkStaff.setSelected(interactionTask.isUseStaff());
                    txtSafeTile.setText(interactionTask.getSafeTile());
                    spinRunes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100, interactionTask.getRunes()));
                    txtTelegrabbingID.setText(String.valueOf(interactionTask.getItemID()));
                    break;
            }
        }
        logger.setMessage(interactionTasks.toString()).print();
    }

    @DoNotRename
    @FXML
    void btnAddNPCTaskPressed(ActionEvent event) {
        if (txtNPCName.getText().isBlank() || txtNPCTile.getText().isBlank() || cmbAction.getEditor().getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Warning!");
            alert.setHeaderText("Warning!");
            alert.setContentText("Please ensure you populate the name, tile and action fields");
            alert.show();
            return;
        }
        interactionTasks.add(new InteractionTask(
                txtNPCName.getText(),
                txtNPCTile.getText(),
                cmbNPCTeleport.getValue(),
                cmbNPCStamina.getValue(),
                cmbAction.getEditor().getText(),
                txtDialogue.getText()));
        listViewTasks.setItems(FXCollections.observableList(interactionTasks));
        listViewTasks.refresh();
    }

    @DoNotRename
    @FXML
    void btnAddObjectTaskPressed(ActionEvent event) {
        if (txtObjectName.getText().isBlank() || txtObjectTile.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Warning!");
            alert.setHeaderText("Warning!");
            alert.setContentText("Please ensure you populate the name and tile fields");
            alert.show();
            return;
        }
        interactionTasks.add(new InteractionTask(
                txtObjectName.getText(),
                txtObjectTile.getText(),
                cmbObjectTeleport.getValue(),
                cmbObjectStamina.getValue(),
                GUIOptions.InteractionType.INTERACTIVE_OBJECT,
                cmbGameObjectMode.getValue(),
                checkObjectRestocking.isSelected(),
                cmbGameObjectRestocking.getValue(),
                checkObjectSellLoot.isSelected(),
                cmbGameObjectSellItems.getValue(),
                checkObjectAntiPK.isSelected(),
                cmbObjectPK.getValue(),
                txtObjectAction.getText(),
                false,
                null,
                false,
                0,
                Integer.parseInt(txtGameObjectID.getText())));
        listViewTasks.refresh();
    }

    @DoNotRename
    @FXML
    void btnAddGroundItemTaskPressed(ActionEvent event) {
        if (txtGroundItemName.getText().isBlank() || txtGroundItemTile.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Warning!");
            alert.setHeaderText("Warning!");
            alert.setContentText("Please ensure you populate the name and tile fields");
            alert.show();
            return;
        }
        interactionTasks.add(new InteractionTask(
                txtGroundItemName.getText(),
                txtGroundItemTile.getText(),
                cmbGroundItemTeleport.getValue(),
                cmbGroundItemStamina.getValue(),
                GUIOptions.InteractionType.GROUND_ITEM,
                cmbGroundItemMode.getValue(),
                checkGroundItemRestocking.isSelected(),
                cmbGroundItemRestocking.getValue(),
                checkGroundItemSellLoot.isSelected(),
                cmbGroundItemSellItems.getValue(),
                checkGroundItemAntiPK.isSelected(),
                cmbGroundItemPK.getValue(),
                txtGameObjectAction.getText(),
                checkGroundItemWorldHop.isSelected(),
                null,
                false,
                0,
                Integer.parseInt(txtGroundItemID.getText())));
        listViewTasks.refresh();
    }

    @DoNotRename
    @FXML
    void btnGrabNPCTilePressed(ActionEvent event) {
        getIngameTile(txtNPCTile);
    }

    @DoNotRename
    @FXML
    void btnObjectIngameTilePressed(ActionEvent event) {
        getIngameTile(txtObjectTile);
    }

    @DoNotRename
    @FXML
    void btnGroundItemIngameTilePressed(ActionEvent event) {
        getIngameTile(txtGroundItemTile);
    }

    @DoNotRename
    @FXML
    void btnStartPressed(ActionEvent event) {
        save("last");
        getGUI().close();
    }

    void save(String name) {
        ScriptSettings settings = ScriptSettings.getDefault();
        Profile profile = new Profile();
        profile.setInteractionTasks(interactionTasks);
        SkrrtInteractor.getRunningProfile().setInteractionTasks(interactionTasks);
        settings.save(name, profile);
        updateSaveNames();
    }

    void load(String name) {
        ScriptSettings settings = ScriptSettings.getDefault();
        settings.load(name, Profile.class).ifPresent(
                s -> {
                    if (s.getInteractionTasks() != null) {
                        interactionTasks.addAll(s.getInteractionTasks());
                    }
                });
        listViewTasks.setItems(FXCollections.observableList(interactionTasks));
    }

    void updateSaveNames() {
        ScriptSettings loadableProfiles = ScriptSettings.getDefault();
        List<String> profiles = loadableProfiles.getSaveNames();
        if (!profiles.isEmpty()) {
            cmbProfile.setItems(FXCollections.observableList(profiles));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewTasks.getItems().setAll(interactionTasks);
        cmbAction.getEditor().setText("Talk-to");
        txtGameObjectAction.setText("Take");
        cmbNPCTeleport.getItems().setAll(Teleports.values());
        cmbObjectTeleport.getItems().setAll(Teleports.values());
        cmbTelegrabTeleport.getItems().setAll(Teleports.values());
        cmbNPCStamina.getItems().setAll(GUIOptions.Stamina.values());
        cmbObjectStamina.getItems().setAll(GUIOptions.Stamina.values());
        cmbTelegrabStamina.getItems().setAll(GUIOptions.Stamina.values());
        cmbGroundItemStamina.getItems().setAll(GUIOptions.Stamina.values());
        cmbGroundItemTeleport.getItems().setAll(Teleports.values());
        cmbAction.getItems().setAll(GUIOptions.Actions.values());
        cmbGameObjectMode.getItems().setAll(GUIOptions.Mode.values());
        cmbGroundItemMode.getItems().setAll(GUIOptions.Mode.values());
        cmbGroundItemPK.getItems().setAll(GUIOptions.PK.values());
        cmbObjectPK.getItems().setAll(GUIOptions.PK.values());
        cmbTelegrabPK.getItems().setAll(GUIOptions.PK.values());
        spinRunes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100,28));
        cmbGroundItemSellItems.getItems().setAll(GUIOptions.SellMethods.values());
        cmbTelegrabSellItems.getItems().setAll(GUIOptions.SellMethods.values());
        cmbGameObjectSellItems.getItems().setAll(GUIOptions.SellMethods.values());
        cmbGroundItemRestocking.getItems().setAll(GUIOptions.BuyMethods.values());
        cmbTelegrabRestocking.getItems().setAll(GUIOptions.BuyMethods.values());
        cmbGameObjectRestocking.getItems().setAll(GUIOptions.BuyMethods.values());
        load("last");
        updateSaveNames();
    }

    private void getIngameTile(TextField textField){
        if (Login.login()) {
            if (Waiting.waitUntil(Login::isLoggedIn)) {
                WorldTile tile = MyPlayer.getPosition();
                if (tile != null) {
                    textField.setText(tile.getX() + "," + tile.getY() + "," + tile.getPlane());
                }
            }
        } else {
            logger.setLoggable(Loggable.ERROR).setMessage("We failed to login!").print();
        }
    }
}
