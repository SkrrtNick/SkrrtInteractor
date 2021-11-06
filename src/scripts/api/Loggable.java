package scripts.api;

import java.awt.*;

public enum Loggable {
    ERROR(Color.RED,Color.ORANGE),
    FATAL_ERROR(Color.BLACK,Color.RED),
    MESSAGE(Color.BLACK,new Color(151,195,10)),
    ANTIBAN(Color.WHITE,new Color(31, 190, 214));
    private Color foregroundColor;
    private Color backgroundColor;

    Loggable(Color foregroundColor, Color backgroundColor) {
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }
}
