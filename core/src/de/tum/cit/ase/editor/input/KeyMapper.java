package de.tum.cit.ase.editor.input;

import com.badlogic.gdx.Input;
import org.lwjgl.glfw.GLFW;

/**
 * The KeyMapper class is responsible for converting key codes based on the current layout.
 * It provides a static method to convert a key using the current layout.
 * The layout is determined based on the value of GLFW.GLFW_KEY_Y.
 */
public class KeyMapper {
    public static LAYOUT layout;

    static {
        layout = GLFW.glfwGetKeyName(GLFW.GLFW_KEY_Y, GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_Y)).equals("y") ? LAYOUT.QWERTY : LAYOUT.QWERTZ;
    }

    /**
     * Converts a key code based on the current layout.
     *
     * @param key the key code to be converted
     * @return the converted key code
     */
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
