package net.doctorg.drgstimers.data;

import net.doctorg.drgstimers.util.NegativeDateTimeException;

public class DateTime {
    private float seconds;
    private int minutes;
    private int hours;

    public DateTime(float seconds, int minutes, int hours) {
        if (seconds < 0 || minutes < 0 || hours < 0) {
            throw new NegativeDateTimeException();
        }

        this.seconds = seconds;
        this.minutes = minutes;
        this.hours = hours;

        calcTime();
    }

    public DateTime(DateTime time) {
        if (time.seconds < 0 || time.minutes < 0 || time.hours < 0) {
            throw new NegativeDateTimeException();
        }

        this.seconds = time.seconds;
        this.minutes = time.minutes;
        this.hours = time.hours;

        calcTime();
    }

    public float getSeconds() {
        return seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getHours() {
        return hours;
    }

    public boolean smallerOrEquals(DateTime compareTime) {
        return this.hours <= compareTime.hours && this.minutes <= compareTime.minutes && this.seconds <= compareTime.seconds;
    }

    public boolean smallerOrEquals(float seconds, int minutes, int hours) {
        return this.hours <= hours && this.minutes <= minutes && this.seconds <= seconds;
    }

    public boolean biggerOrEquals(DateTime compareTime) {
        return this.hours <= compareTime.hours && this.minutes <= compareTime.minutes && this.seconds <= compareTime.seconds;
    }

    public boolean biggerOrEquals(float seconds, int minutes, int hours) {
        return this.hours >= hours && this.minutes >= minutes && this.seconds >= seconds;
    }

    private void calcTime() {
        int secondsToMinutes;
        int minutesToHours;

        secondsToMinutes = (int)Math.floor(this.seconds / 60);
        this.seconds %= 60;

        this.minutes += secondsToMinutes;
        minutesToHours = (int)Math.floor((float)this.minutes / 60);
        this.minutes %= 60;

        this.hours += minutesToHours;
    }

    public void reduce(float timeToReduceInSec) {
        this.seconds -= timeToReduceInSec;

        if (smallerOrEquals(0, 0, 0)) {
            this.seconds = 0;
            return;
        }

        if (this.seconds < 0) {
            this.minutes--;
            this.seconds += 60;
        }
        if (this.minutes < 0) {
            this.hours--;
            this.minutes += 60;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DateTime compareTime)) {
            return false;
        }

        return this.seconds == compareTime.seconds && this.minutes == compareTime.minutes && this.hours == compareTime.hours;
    }

    public boolean equals(int seconds, int minutes, int hours) {
        return this.seconds == seconds && this.minutes == minutes && this.hours == hours;
    }

    @Override
    public String toString() {
        String secondsString;
        String minutesString;

        if (seconds >= 10) {
            secondsString = ":" + (int) seconds;
        } else {
            secondsString = ":0" + (int) seconds;
        }

        if (minutes >= 10) {
            minutesString = ":" + minutes;
        } else {
            minutesString = ":0" + minutes;
        }

        return hours + minutesString + secondsString;
    }
}
