package net.doctorg.drgstimers.util;

public class NegativeDateTimeException extends RuntimeException {

    public NegativeDateTimeException() {
        super();
    }

    public NegativeDateTimeException(String s) {
        super(s);
    }
}
