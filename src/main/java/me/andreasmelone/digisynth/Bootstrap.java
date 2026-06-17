package me.andreasmelone.digisynth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

    static void main() {
        try {
            DigiSynthMain main = new DigiSynthMain();
            main.run();
        } catch (Exception e) {
            LOGGER.error("The program encountered a critical exception!");
            LOGGER.error("Restoring is not possible.");
            LOGGER.error("Stacktrace:", e);
        }
    }
}
