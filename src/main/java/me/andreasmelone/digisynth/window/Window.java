package me.andreasmelone.digisynth.window;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;

import java.nio.IntBuffer;

public class Window implements AutoCloseable {
    private long windowHandle;

    protected Window(long windowHandle) {
        this.windowHandle = windowHandle;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public int getXPos() {
        IntBuffer x = IntBuffer.allocate(1);
        GLFW.glfwGetWindowPos(this.windowHandle, x, null);
        return x.get();
    }

    public int getYPos() {
        IntBuffer y = IntBuffer.allocate(1);
        GLFW.glfwGetWindowPos(this.windowHandle, null, y);
        return y.get();
    }

    public int getWidth() {
        IntBuffer x = IntBuffer.allocate(1);
        GLFW.glfwGetWindowSize(this.windowHandle, x, null);
        return x.get();
    }

    public int getHeight() {
        IntBuffer y = IntBuffer.allocate(1);
        GLFW.glfwGetWindowSize(this.windowHandle, null, y);
        return y.get();
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(this.windowHandle);
    }

    public void makeCurrent() {
        GLFW.glfwMakeContextCurrent(this.windowHandle);
    }

    @Override
    public void close() {
        GLFW.glfwDestroyWindow(this.windowHandle);
        this.windowHandle = 0;
    }

    public static Window createWindow(int width, int height, String title) {
        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_API);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE , GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE , GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        long handle = GLFW.glfwCreateWindow(width, height, title, 0, 0);

        if(handle == 0) {
            PointerBuffer buffer = PointerBuffer.allocateDirect(1);
            int error = GLFW.glfwGetError(buffer);
            if(error != GLFW.GLFW_NO_ERROR) {
                String errorString = buffer.getStringUTF8();
                throw new WindowCreationException("Failed to create window: " + errorString);
            }
            throw new WindowCreationException("Failed to create window with unknown error");
        }

        return new Window(handle);
    }
}
