package com.example.prayertimes;

public class DayParts {
    private int fajr;
    private int sunrise;
    private int zohar;
    private int asar;
    private int magrib;
    private int isha;

    public DayParts(int fajr, int sunrise, int zohar, int asar, int magrib, int isha) {
        this.fajr = fajr;
        this.sunrise = sunrise;
        this.zohar = zohar;
        this.asar = asar;
        this.magrib = magrib;
        this.isha = isha;
    }

    public int getFajr() {
        return fajr;
    }

    public int getSunrise() {
        return sunrise;
    }

    public int getZohar() {
        return zohar;
    }

    public int getAsar() {
        return asar;
    }

    public int getMagrib() {
        return magrib;
    }

    public int getIsha() {
        return isha;
    }

    public String toString() {
        return  fajr + ", " + sunrise + ", " + zohar + ", " + asar + ", " + magrib + ", " + isha;
    }
}
