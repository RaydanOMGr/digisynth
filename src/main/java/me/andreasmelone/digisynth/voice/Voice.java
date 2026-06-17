package me.andreasmelone.digisynth.voice;

public interface Voice {
    double computeSample(double frequency, double t);

    default short computeShortSample(double freq, double t) {
        return toShort(computeSample(freq, t));
    }

    static short toShort(double d) {
        return (short) (d * Short.MAX_VALUE);
    }

    record SynthVoice() implements Voice {
        @Override
        public double computeSample(double frequency, double t) {
            return Math.signum(Math.sin(2.0 * Math.PI * frequency * t));
        }
    }

    record OrganVoice() implements Voice {
        @Override
        public double computeSample(double frequency, double t) {
            double vibrato = Math.sin(2.0 * Math.PI * 5.0 * t) * 0.0002;
            double currentFreq = frequency * (1.0 + vibrato);

            double fundamental = Math.sin(2.0 * Math.PI * currentFreq * t);
            double octave = 0.5 * Math.sin(2.0 * Math.PI * currentFreq * 2.0 * t);
            double fifth = 0.3 * Math.sin(2.0 * Math.PI * currentFreq * 3.0 * t);
            double secondOctave = 0.2 * Math.sin(2.0 * Math.PI * currentFreq * 4.0 * t);

            double sample = fundamental + octave + fifth + secondOctave;

            return sample / 2.0;
        }
    }

    record SawVoice() implements Voice {
        @Override
        public double computeSample(double frequency, double t) {
            double phase = (frequency * t) % 1.0;
            return 2.0 * phase - 1.0;
        }
    }

    record BellVoice() implements Voice {
        @Override
        public double computeSample(double frequency, double t) {
            double base = Math.sin(2.0 * Math.PI * frequency * t);
            double harmonic2 = 0.4 * Math.sin(2.0 * Math.PI * frequency * 2.01 * t);
            double harmonic5 = 0.25 * Math.sin(2.0 * Math.PI * frequency * 5.02 * t);
            double harmonic7 = 0.15 * Math.sin(2.0 * Math.PI * frequency * 7.01 * t);

            return (base + harmonic2 + harmonic5 + harmonic7) / 1.8;
        }
    }

    record PadVoice() implements Voice {
        @Override
        public double computeSample(double frequency, double t) {
            double detune1 = Math.sin(2.0 * Math.PI * frequency * 0.997 * t);
            double detune2 = Math.sin(2.0 * Math.PI * frequency * 1.003 * t);

            double main = Math.sin(2.0 * Math.PI * frequency * t);

            return (main + detune1 + detune2) / 3.0;
        }
    }

    record PlucklessVoice() implements Voice {
        @Override
        public double computeSample(double frequency, double t) {
            double phase = 2.0 * Math.PI * frequency * t;

            double harmonic1 = Math.sin(phase);
            double harmonic2 = 0.5 * Math.sin(phase * 2.0);
            double harmonic3 = 0.25 * Math.sin(phase * 3.0);
            double harmonic4 = 0.125 * Math.sin(phase * 4.0);

            return harmonic1 + harmonic2 + harmonic3 + harmonic4;
        }
    }

    record BitVoice() implements Voice {
        @Override
        public double computeSample(double frequency, double t) {
            double wave = Math.sin(2.0 * Math.PI * frequency * t);
            return Math.round(wave * 8.0) / 8.0;
        }
    }
}
