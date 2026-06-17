package me.andreasmelone.digisynth;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.andreasmelone.digisynth.audio.ContinuousSoundManager;
import me.andreasmelone.digisynth.audio.FrequencyBufferQueue;
import me.andreasmelone.digisynth.window.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.NativeType;

public class DigiSynthMain implements Runnable {
    private Window window;

    private ContinuousSoundManager soundManager;
    private Thread audioThread;
    private Int2ObjectMap<FrequencyBufferQueue> frequencyQueue = new Int2ObjectOpenHashMap<>();


    @Override
    public void run() {
        GLFW.glfwInit();

        this.window = Window.createWindow(800, 600, "DigiSynth");
        GLFW.glfwSwapInterval(1);

        this.window.makeCurrent();
        GL.createCapabilities();

        this.audioThread = Thread.startVirtualThread(() -> {
            long device = ALC10.alcOpenDevice((CharSequence)null);
            long ctx = ALC10.alcCreateContext(device, (int[])null);
            ALC10.alcMakeContextCurrent(ctx);

            AL.setCurrentThread(AL.createCapabilities(ALC.createCapabilities(device)));

            this.soundManager = ContinuousSoundManager.create(ContinuousSoundManager.DEFAULT_SOURCE_COUNT);
            while(!Thread.currentThread().isInterrupted()) {
                this.soundManager.loop();
//
//                try {
//                    Thread.sleep(1); // do not turn the cpu into a great computer of sorrow and sadness
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }
            }
        });

        GLFW.glfwSetKeyCallback(this.window.getWindowHandle(), (@NativeType("GLFWwindow *") long window, int key, int scancode, int action, int mods) -> {
            if(this.soundManager == null) return;

            SoundNote note = SoundNote.getByKey(key);
            if(note == null) return;

            int status = GLFW.glfwGetKey(window, key);

            if(status == GLFW.GLFW_PRESS && !this.frequencyQueue.containsKey(note.frequency)) {
                this.frequencyQueue.put(note.frequency, this.soundManager.addPlayingFrequency(note.frequency));
            } else if(status == GLFW.GLFW_RELEASE && this.frequencyQueue.containsKey(note.frequency)) {
                this.soundManager.removePlayingFrequency(this.frequencyQueue.get(note.frequency));
                this.frequencyQueue.remove(note.frequency);
            }
        });

        while (!window.shouldClose()) {
            loop();

            GLFW.glfwSwapBuffers(window.getWindowHandle());
            GLFW.glfwPollEvents();
        }

        this.audioThread.interrupt();
        window.close();
        GLFW.glfwTerminate();
    }
    private void loop() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
