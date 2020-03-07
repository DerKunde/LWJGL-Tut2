package engine;

import engine.io.Window;
import org.joml.Vector2d;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private final Vector2d prevoiusPos;

    private final Vector2d currentPos;

    private final Vector2f displayVec;

    private boolean inWindow = false;

    private boolean leftBtnPressed = false;

    private boolean rightBtnPressed = false;

    public MouseInput() {
        prevoiusPos = new Vector2d(-1,-1);
        currentPos = new Vector2d(0,0);
        displayVec = new Vector2f();
    }

    public void init(Window window) {
        glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, xpos, ypos) -> {
           currentPos.x = xpos;
           currentPos.y = ypos;
        });

        glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) -> {
           inWindow = entered;
        });

        glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) -> {
           leftBtnPressed = (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS);
           rightBtnPressed = (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS);
        });
    }

    public Vector2f getDisplayVec() {
        return displayVec;
    }

    public void input(Window window) {
        displayVec.x = 0;
        displayVec.y = 0;

        if(prevoiusPos.x > 0 && prevoiusPos.y > 0 && inWindow) {
            double deltaX = currentPos.x - prevoiusPos.x;
            double deltaY = currentPos.y - prevoiusPos.y;
            boolean rotateX = deltaX != 0;
            boolean rotateY = deltaY != 0;

            if(rotateX) {
                displayVec.y = (float) deltaX;
            }
            if(rotateY) {
                displayVec.x = (float) deltaY;
            }
        }

        prevoiusPos.x = currentPos.x;
        prevoiusPos.y = currentPos.y;
    }

    public boolean isLeftBtnPressed() {
        return leftBtnPressed;
    }

    public boolean isRightBtnPressed() {
        return rightBtnPressed;
    }
}
