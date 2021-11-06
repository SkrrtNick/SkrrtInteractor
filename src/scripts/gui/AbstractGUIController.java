package scripts.gui;

import javafx.fxml.Initializable;

/**
 * @author Laniax
 */

public abstract class AbstractGUIController implements Initializable {

    private scripts.gui.GUI gui = null;

    public void setGUI(scripts.gui.GUI gui) {
        this.gui = gui;
    }

    public GUI getGUI() {
        return this.gui;
    }
}
