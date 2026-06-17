package me.andreasmelone.digisynth.audio;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FrequencyBufferQueue implements AutoCloseable {
    public static final int BUFFER_COUNT = 4;
    public static final int SAMPLE_RATE = 44100;
    public static final int CYCLE_COUNT = 8000;

    private final int source;
    private final BlockingQueue<Integer> alBuffers;
    private final ShortBuffer sampleBuffer;

    public FrequencyBufferQueue(int source, BlockingQueue<Integer> alBuffers, ShortBuffer sampleBuffer) {
        this.source = source;
        this.alBuffers = alBuffers;
        this.sampleBuffer = sampleBuffer;
    }

    public int getSource() {
        return this.source;
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

    public static FrequencyBufferQueue create(int source, int frequency) {
        BlockingQueue<Integer> alBuffers = new ArrayBlockingQueue<>(BUFFER_COUNT);
        int[] buffers = new int[BUFFER_COUNT];
        AL11.alGenBuffers(buffers);
        alBuffers.addAll(Arrays.stream(buffers).boxed().toList());

        int samplesPerCycle = Math.round((float) SAMPLE_RATE / frequency);
        int capacity = samplesPerCycle * CYCLE_COUNT;
        ShortBuffer sampleBuffer = BufferUtils.createShortBuffer(capacity);

        for (int i = 0; i < capacity; i++) {
            double time = (double) i / SAMPLE_RATE;
            double sine = Math.signum(Math.sin(2.0 * Math.PI * frequency * time)) * 0.4;

            sampleBuffer.put((short) (sine * Short.MAX_VALUE));
        }
        sampleBuffer.flip();

        for (int buffer : buffers) {
            sampleBuffer.rewind();
            AL11.alBufferData(buffer, AL10.AL_FORMAT_MONO16, sampleBuffer, SAMPLE_RATE);
        }

        return new FrequencyBufferQueue(source, alBuffers, sampleBuffer);
    }

    private static int perfectLoopSamples(int sampleRate, int frequency) {
        return sampleRate / gcd(sampleRate, frequency);
    }

    private static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
