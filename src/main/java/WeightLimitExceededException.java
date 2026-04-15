package src.main.java;

public class WeightLimitExceededException extends RuntimeException{
    WeightLimitExceededException(String message){
        super(message);
    }
}
