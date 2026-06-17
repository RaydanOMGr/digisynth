package me.andreasmelone.digisynth.window;

public class WindowCreationException extends RuntimeException {
    public WindowCreationException(String message) {
        super(message);
    }

    public WindowCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public WindowCreationException(Throwable cause) {
        super(cause);
    }

    public WindowCreationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public WindowCreationException() {
    }
}
