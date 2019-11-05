package com.glebworx.pomodoro.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {

    private static final AtomicInteger counter = new AtomicInteger();

    public static int next() {
        return counter.getAndIncrement();
    }

}
