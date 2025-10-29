package org.example.model;

import java.io.Serializable;

public class SettingData implements Serializable {
    public enum ScreenSize {
        SMALL, MEDIUM, LARGE
    }
    public enum ColorBlindMode {
        Default,
        PROTANOPIA, //적색맹
        DEUTERANOPIA, //녹색맹
        TRITANOPIA //청색맹
    }
    public ScreenSize screenSize = ScreenSize.MEDIUM;
    public ColorBlindMode colorBlindMode = ColorBlindMode.Default;
    public KeyData controlData = new KeyData();
}
