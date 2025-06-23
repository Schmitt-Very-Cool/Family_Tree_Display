package com.nefariousmachine.generate;

import java.awt.*;

public class ColorManager {
    private static Color BACKGROUND_COLOR = new Color(230, 230, 230);
    private static Color DEFAULT_PRIMARY = new Color(0, 0, 0);
    private static Color DEFAULT_SECONDARY = new Color(255, 255, 255);

    public static Color getBGColor() {
        return BACKGROUND_COLOR;
    }

    public static Color getDefaultPrimary() {
        return DEFAULT_PRIMARY;
    }

    public static Color getDefaultSecondary() {
        return DEFAULT_SECONDARY;
    }
}
