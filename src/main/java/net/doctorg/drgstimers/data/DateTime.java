package net.doctorg.drgstimers.data;

import net.doctorg.drgstimers.util.NegativeDateTimeException;

public class DateTime {
    private float seconds;
    private int minuets;
    private int hours;

    public DateTime(float seconds, int minuets, int hours) {
        if (seconds < 0 || minuets < 0 || hours < 0) {
            throw new NegativeDateTimeException();
        }

        this.seconds = seconds;
        this.minuets = minuets;
        this.hours = hours;

        calcTime();
    }

    public DateTime(DateTime time) {
        if (time.seconds < 0 || time.minuets < 0 || time.hours < 0) {
            throw new NegativeDateTimeException();
        }

        this.seconds = time.seconds;
        this.minuets = time.minuets;
        this.hours = time.hours;

        calcTime();
    }

    public float getSeconds() {
        return seconds;
    }

    public int getMinuets() {
        return minuets;
    }

    public int getHours() {
        return hours;
    }

    public boolean smallerOrEquals(DateTime compareTime) {
        return this.hours <= compareTime.hours && this.minuets <= compareTime.minuets && this.seconds <= compareTime.seconds;
    }

    public boolean smallerOrEquals(float seconds, int minuets, int hours) {
        return this.hours <= hours && this.minuets <= minuets && this.seconds <= seconds;
    }

    public boolean biggerOrEquals(DateTime compareTime) {
        return this.hours <= compareTime.hours && this.minuets <= compareTime.minuets && this.seconds <= compareTime.seconds;
    }

    public boolean biggerOrEquals(float seconds, int minuets, int hours) {
        return this.hours >= hours && this.minuets >= minuets && this.seconds >= seconds;
    }

    private void calcTime() {
        int secondsToMinuets;
        int minuetsToHours;

        secondsToMinuets = (int)Math.floor(this.seconds / 60);
        this.seconds %= 60;

        this.minuets += secondsToMinuets;
        minuetsToHours = (int)Math.floor((float)this.minuets / 60);
        this.minuets %= 60;

        this.hours += minuetsToHours;
    }

    public void reduce(float timeToReduceInSec) {
        this.seconds -= timeToReduceInSec;

        if (smallerOrEquals(0, 0, 0)) {
            this.seconds = 0;
            return;
        }

        if (this.seconds < 0) {
            this.minuets--;
            this.seconds += 60;
        }
        if (this.minuets < 0) {
            this.hours--;
            this.minuets += 60;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DateTime compareTime)) {
            return false;
        }

        return this.seconds == compareTime.seconds && this.minuets == compareTime.minuets && this.hours == compareTime.hours;
    }

    public boolean equals(int seconds, int minuets, int hours) {
        return this.seconds == seconds && this.minuets == minuets && this.hours == hours;
    }

    @Override
    public String toString() {
        String secondsString;
        String minuetsString;

        if (seconds >= 10) {
            secondsString = ":" + (int) seconds;
        } else {
            secondsString = ":0" + (int) seconds;
        }

        if (minuets >= 10) {
            minuetsString = ":" + minuets;
        } else {
            minuetsString = ":0" + minuets;
        }

        return hours + minuetsString + secondsString;
    }
}
