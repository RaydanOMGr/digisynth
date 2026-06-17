package me.andreasmelone.digisynth.audio;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lwjgl.openal.AL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ContinuousSoundManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContinuousSoundManager.class);

    public static final int DEFAULT_SOURCE_COUNT = 32;

    private final BlockingQueue<Integer> sources;
    private final int maxSourceCount;

    private final Set<FrequencyBufferQueue> playingFrequencies = new HashSet<>();
    private final BlockingQueue<FrequencyBufferQueue> removeQueue = new LinkedBlockingQueue<>();

    protected ContinuousSoundManager(BlockingQueue<Integer> sources, int maxSourceCount) {
        this.sources = sources;
        this.maxSourceCount = maxSourceCount;
    }

    public synchronized FrequencyBufferQueue addPlayingFrequency(int frequency) {
        if (!canAddNewFrequency()) {
            throw new IllegalStateException("Cannot add more than " + maxSourceCount + " frequencies at the same time!");
        }
        try {
            int source = this.sources.take();
            FrequencyBufferQueue freq = FrequencyBufferQueue.create(source, frequency);
            this.playingFrequencies.add(freq);
            return freq;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public synchronized void removePlayingFrequency(FrequencyBufferQueue frequency) {
        this.playingFrequencies.remove(frequency);
        this.removeQueue.add(frequency);
    }

    public synchronized boolean canAddNewFrequency() {
        return !this.sources.isEmpty();
    }

    public synchronized void loop() {
        this.playingFrequencies.forEach((freq) -> {
            try {
                int source = freq.getSource();

                int processed = AL11.alGetSourcei(source, AL11.AL_BUFFERS_PROCESSED);
                for (int i = 0; i < processed; i++) {
                    freq.returnBuffer(AL11.alSourceUnqueueBuffers(source));
                }

                if(this.removeQueue.contains(freq)) return;

                while(freq.hasBuffer()) {
                    AL11.alSourceQueueBuffers(source, freq.takeBuffer());
                }

                if(AL11.alGetSourcei(source, AL11.AL_SOURCE_STATE) != AL11.AL_PLAYING) {
                    AL11.alSourcePlay(source);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        FrequencyBufferQueue freq;
        while ((freq = this.removeQueue.poll()) != null) {
            int source = freq.getSource();

            AL11.alSourceStop(source);

            int queued = AL11.alGetSourcei(source, AL11.AL_BUFFERS_QUEUED);
            while (queued-- > 0) {
                AL11.alDeleteBuffers(AL11.alSourceUnqueueBuffers(source));
            }
            freq.close();

            if(!this.sources.offer(source)) {
                LOGGER.warn("Cannot reinsert source? {}", source);
            }
        }
    }

    public static ContinuousSoundManager create(int sourceCount) {
        BlockingQueue<Integer> sources = new ArrayBlockingQueue<>(sourceCount);
        int[] sourcesArray = new int[sourceCount];
        AL11.alGenSources(sourcesArray);
        sources.addAll(Arrays.stream(sourcesArray).boxed().toList());

        ContinuousSoundManager soundManager = new ContinuousSoundManager(sources, sourceCount);
        return soundManager;
    }
}
