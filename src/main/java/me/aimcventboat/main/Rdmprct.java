package me.aimcventboat.main;

import java.util.Random;

public class Rdmprct {

    public static Integer getRandomPrct() {
        Random rand = new Random();
        return rand.nextInt(101);
    }
    public static Integer getRandomPrmt() {
        Random rand = new Random();
        return rand.nextInt(1001);
    }
    public static Integer getRandomplace() {
        Random rand = new Random();
        return rand.nextInt(5) + 1;
    }

}
