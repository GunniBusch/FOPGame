package de.tum.cit.ase.editor.input;

import com.badlogic.gdx.Input;
import org.lwjgl.glfw.GLFW;

public class KeyMapper {
    public static LAYOUT layout;

    static {
        layout = GLFW.glfwGetKeyName(GLFW.GLFW_KEY_Y, GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_Y)).equals("y") ? LAYOUT.QWERTY : LAYOUT.QWERTZ;
    }

    public static int convertKey(int key) {
        if (layout == LAYOUT.QWERTZ) {
            switch (key) {
                case Input.Keys.Y -> {
                    return Input.Keys.Z;
                }
                case Input.Keys.Z -> {
                    return Input.Keys.Y;
                }
                default -> {
                    return key;
                }
            }
        } else {
            return key;
        }
    }

    public enum LAYOUT {
        QWERTZ, QWERTY
    }
}
