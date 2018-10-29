package Exceptions;

public class LoopNumberTooSmallException extends Exception{
    public LoopNumberTooSmallException() {
        System.out.println("Loop number must be greater than 1.");
    }
}
