package src.main.java;

public class VolumeLimitExceededException extends RuntimeException{
    public VolumeLimitExceededException(String message) {
        super(message);
    }
}
