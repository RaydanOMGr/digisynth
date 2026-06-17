package me.andreasmelone.digisynth;

import org.lwjgl.glfw.GLFW;

// tune this to your liking
public enum SoundNote {
    C3       (GLFW.GLFW_KEY_A,          130.81),
    EB3      (GLFW.GLFW_KEY_S,          155.56),
    F3       (GLFW.GLFW_KEY_D,          174.61),
    F3_SHARP (GLFW.GLFW_KEY_F,          185.00),
    G3       (GLFW.GLFW_KEY_G,          196.00),
    BB3      (GLFW.GLFW_KEY_H,          233.08),
    C4       (GLFW.GLFW_KEY_J,          261.63),

    EB4      (GLFW.GLFW_KEY_K,          311.13),
    F4       (GLFW.GLFW_KEY_L,          349.23),
    F4_SHARP (GLFW.GLFW_KEY_SEMICOLON,  369.99),
    G4       (GLFW.GLFW_KEY_APOSTROPHE, 392.00),
    BB4      (GLFW.GLFW_KEY_BACKSLASH,  466.16),

    // upper row (QWERTZUIOPÜ+)
    C4_UPPER       (GLFW.GLFW_KEY_Q,          261.63),
    EB4_UPPER      (GLFW.GLFW_KEY_W,          311.13),
    F4_UPPER       (GLFW.GLFW_KEY_E,          349.23),
    F4_SHARP_UPPER (GLFW.GLFW_KEY_R,          369.99),
    G4_UPPER       (GLFW.GLFW_KEY_T,          392.00),
    BB4_UPPER      (GLFW.GLFW_KEY_Y,          466.16),
    C5             (GLFW.GLFW_KEY_U,          523.25),
    EB5            (GLFW.GLFW_KEY_I,          622.25),
    F5             (GLFW.GLFW_KEY_O,          698.46),
    F5_SHARP       (GLFW.GLFW_KEY_P,          739.99),
    G5             (GLFW.GLFW_KEY_LEFT_BRACKET,         783.99),
    BB5            (GLFW.GLFW_KEY_RIGHT_BRACKET,      932.33),

    C2        (GLFW.GLFW_KEY_WORLD_2,  65.41),
    C2_SHARP  (GLFW.GLFW_KEY_Z,           69.30),
    D2        (GLFW.GLFW_KEY_X,           73.42),
    EB2       (GLFW.GLFW_KEY_C,           77.78),
    E2        (GLFW.GLFW_KEY_V,           82.41),
    F2        (GLFW.GLFW_KEY_B,           87.31),
    F2_SHARP  (GLFW.GLFW_KEY_N,           92.50),
    G2        (GLFW.GLFW_KEY_M,           98.00),
    G2_SHARP  (GLFW.GLFW_KEY_COMMA,       103.83),
    A2        (GLFW.GLFW_KEY_PERIOD,      110.00),
    BB2       (GLFW.GLFW_KEY_SLASH,       116.54),
    B2        (GLFW.GLFW_KEY_RIGHT_SHIFT, 123.47);

    public final int key;
    public final int frequency;

    SoundNote(int key, double frequency) {
        this.key = key;
        this.frequency = (int)frequency;
    }

    public static SoundNote getByKey(int keyCode) {
        for (SoundNote value : values()) {
            if(value.key == keyCode) return value;
        }
        return null;
    }
}