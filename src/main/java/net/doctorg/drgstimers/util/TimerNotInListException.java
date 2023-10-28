package net.doctorg.drgstimers.util;

public class TimerNotInListException extends RuntimeException {

    public TimerNotInListException() {
        super();
    }

    public TimerNotInListException(String s) {
        super(s);
    }
}
