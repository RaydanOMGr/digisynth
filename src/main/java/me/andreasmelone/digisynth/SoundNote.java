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
    BB4      (GLFW.GLFW_KEY_BACKSLASH,  466.16);

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