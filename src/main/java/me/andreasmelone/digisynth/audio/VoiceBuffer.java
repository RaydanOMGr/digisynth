package me.andreasmelone.digisynth.audio;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.andreasmelone.digisynth.voice.Voice;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class VoiceBuffer implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(VoiceBuffer.class);

    public static final int BUFFER_COUNT = 4;
    public static final int SAMPLE_RATE = 44100;
    public static final int CYCLE_COUNT = 8000;

    private static final Int2ObjectMap<ShortBuffer> CACHE = new Int2ObjectOpenHashMap<>();

    private final BlockingQueue<Integer> alBuffers;
    private final ShortBuffer sampleBuffer;

    public VoiceBuffer(BlockingQueue<Integer> alBuffers, ShortBuffer sampleBuffer) {
        this.alBuffers = alBuffers;
        this.sampleBuffer = sampleBuffer;
    }

    public int takeBuffer() throws InterruptedException {
        return alBuffers.take();
    }

    public void returnBuffer(int buffer) throws InterruptedException {
        sampleBuffer.rewind();
        AL11.alBufferData(buffer, AL10.AL_FORMAT_MONO16, sampleBuffer, SAMPLE_RATE);
        alBuffers.put(buffer);
    }

    public boolean hasBuffer() {
        return !alBuffers.isEmpty();
    }

    @Override
    public void close() {
        for (int alBuffer : this.alBuffers) {
            AL10.alDeleteBuffers(alBuffer);
        }
        sampleBuffer.clear();
    }

    public static VoiceBuffer create(Voice voice, int frequency) {
        BlockingQueue<Integer> alBuffers = new ArrayBlockingQueue<>(BUFFER_COUNT);
        int[] buffers = new int[BUFFER_COUNT];
        AL11.alGenBuffers(buffers);
        alBuffers.addAll(Arrays.stream(buffers).boxed().toList());

        int samplesPerCycle = Math.round((float) SAMPLE_RATE / frequency);
        int capacity = samplesPerCycle * CYCLE_COUNT;
        ShortBuffer sampleBuffer = getWave(frequency);

        for (int i = 0; i < capacity; i++) {
            double time = (double) i / SAMPLE_RATE;
            sampleBuffer.put(voice.computeShortSample(frequency, time));
        }
        sampleBuffer.flip();

        for (int buffer : buffers) {
            sampleBuffer.rewind();
            AL11.alBufferData(buffer, AL10.AL_FORMAT_MONO16, sampleBuffer, SAMPLE_RATE);
        }

        return new VoiceBuffer(alBuffers, sampleBuffer);
    }

    private static ShortBuffer getWave(int frequency) {
        return CACHE.computeIfAbsent(frequency, f -> {
            int samplesPerCycle = Math.round((float) SAMPLE_RATE / f);
            int capacity = samplesPerCycle * CYCLE_COUNT;

            ShortBuffer buffer = BufferUtils.createShortBuffer(capacity);

            for (int i = 0; i < capacity; i++) {
                double sine = Math.signum(Math.sin(
                        2.0 * Math.PI * f * i / SAMPLE_RATE
                )) * 0.4;

                buffer.put((short)(sine * Short.MAX_VALUE));
            }

            buffer.flip();
            return buffer;
        });
    }
}
