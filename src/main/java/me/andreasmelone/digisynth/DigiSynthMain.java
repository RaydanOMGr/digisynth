package me.andreasmelone.digisynth;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.andreasmelone.digisynth.audio.ContinuousSoundManager;
import me.andreasmelone.digisynth.audio.PlayingVoiceBuffer;
import me.andreasmelone.digisynth.audio.VoiceBuffer;
import me.andreasmelone.digisynth.voice.Voice;
import me.andreasmelone.digisynth.window.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.NativeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DigiSynthMain implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DigiSynthMain.class);

    private Window window;
    private volatile boolean renderDirty = true;

    private ContinuousSoundManager soundManager;
    private Int2ObjectMap<PlayingVoiceBuffer> frequencyQueue = new Int2ObjectOpenHashMap<>();
    private volatile boolean audioInit = false;

    private float volume = 0.5f;

    private Voice selectedVoice;
    public static final Map<SoundNote, VoiceBuffer> voiceBuffers = new HashMap<>();

    private static final Voice DEFAULT_VOICE = new Voice.OrganVoice();
    private static final Int2ObjectMap<Voice> keyToVoiceMapping = new Int2ObjectOpenHashMap<>(Map.of(
            GLFW.GLFW_KEY_KP_1, DEFAULT_VOICE,
            GLFW.GLFW_KEY_KP_2, new Voice.SynthVoice(),
            GLFW.GLFW_KEY_KP_3, new Voice.BellVoice(),
            GLFW.GLFW_KEY_KP_4, new Voice.SawVoice(),
            GLFW.GLFW_KEY_KP_5, new Voice.PadVoice(),
            GLFW.GLFW_KEY_KP_6, new Voice.PlucklessVoice(),
            GLFW.GLFW_KEY_KP_7, new Voice.BitVoice()
    ));


    @Override
    public void run() {
        this.selectedVoice = DEFAULT_VOICE;

        GLFW.glfwInit();

        this.window = Window.createWindow(800, 600, "DigiSynth");
        GLFW.glfwSwapInterval(0);

        this.window.makeCurrent();
        GL.createCapabilities();

        LOGGER.info("GL Version: {}", GL11.glGetString(GL11.GL_VERSION));
        LOGGER.info("Renderer: {}", GL11.glGetString(GL11.GL_RENDERER));

        Thread audioThread = new Thread(() -> {
            long device = ALC10.alcOpenDevice((CharSequence)null);
            long ctx = ALC10.alcCreateContext(device, (int[])null);
            ALC10.alcMakeContextCurrent(ctx);

            AL.setCurrentThread(AL.createCapabilities(ALC.createCapabilities(device)));

            LOGGER.info("Audio Device: {}", ALC10.alcGetString(device, ALC10.ALC_DEVICE_SPECIFIER));

            AL11.alListenerf(AL10.AL_GAIN, volume * 2f);

            for (SoundNote note : SoundNote.values()) {
                voiceBuffers.put(note, VoiceBuffer.create(this.selectedVoice, note.frequency));
            }

            this.soundManager = ContinuousSoundManager.create(ContinuousSoundManager.DEFAULT_SOURCE_COUNT);
            this.audioInit = true;
            this.renderDirty = true;
            while(!Thread.currentThread().isInterrupted()) {
                float volumeNow = volume;
                this.soundManager.loop();
                if(volumeNow != volume) {
                    AL11.alListenerf(AL10.AL_GAIN, volume * 2f);
                }
            }
        });
        audioThread.start();

        GLFW.glfwSetKeyCallback(this.window.getWindowHandle(), (@NativeType("GLFWwindow *") long window, int key, int scancode, int action, int mods) -> {
            if(this.soundManager == null || !audioInit) return;

            if(key == GLFW.GLFW_KEY_KP_SUBTRACT && action == GLFW.GLFW_PRESS) {
                volume -= 0.1f;
                this.renderDirty = true;
            }

            if(key == GLFW.GLFW_KEY_KP_ADD && action == GLFW.GLFW_PRESS) {
                volume += 0.1f;
                this.renderDirty = true;
            }

            if(keyToVoiceMapping.containsKey(key) && action == GLFW.GLFW_PRESS) {
                this.audioInit = false;
                this.renderDirty = true;
                this.selectedVoice = keyToVoiceMapping.get(key);

                Thread.startVirtualThread(() -> {
                    Map<SoundNote, VoiceBuffer> newVoiceBuffers = new HashMap<>();
                    for (SoundNote note : SoundNote.values()) {
                        newVoiceBuffers.put(note, VoiceBuffer.create(this.selectedVoice, note.frequency));
                    }
                    voiceBuffers.clear();
                    voiceBuffers.putAll(newVoiceBuffers);

                    this.audioInit = true;
                    this.renderDirty = true;
                });

                return;
            }

            SoundNote note = SoundNote.getByKey(key);
            if(note == null) return;

            if(action == GLFW.GLFW_PRESS && !this.frequencyQueue.containsKey(note.frequency)) {
                if(!this.soundManager.canAddNewFrequency()) return;
                this.frequencyQueue.put(note.frequency, this.soundManager.addPlayingFrequency(voiceBuffers.get(note)));
            } else if(action == GLFW.GLFW_RELEASE && this.frequencyQueue.containsKey(note.frequency)) {
                this.soundManager.removePlayingFrequency(this.frequencyQueue.get(note.frequency));
                this.frequencyQueue.remove(note.frequency);
            }
        });

        while (!window.shouldClose()) {
            GLFW.glfwPollEvents();

            if(renderDirty) {
                loop();
                GLFW.glfwSwapBuffers(window.getWindowHandle());
            }
        }

        audioThread.interrupt();
        window.close();
        GLFW.glfwTerminate();
    }

    private void loop() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        if(!audioInit) {
            GL11.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        } else {
            GL11.glClearColor(volume, volume, volume, 1.0f);
        }
    }
}
