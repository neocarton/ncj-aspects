package org.ncj.aspects.lock.test;

import java.util.ArrayList;
import java.util.List;

import org.ncj.aspects.lock.Lock;
import org.apache.commons.lang3.RandomUtils;

class TestOnMethod {

    public static final List<Integer> RUN_ORDER = new ArrayList<>();

    @Lock(value = "test-lock-on-method")
    public int execWithLock(int number) {
        return doFunc(number);
    }

    @Lock(value = "test-try-lock-on-method", tryLock = true)
    public int execWithTryLock(int number) {
        return doFunc(number);
    }

    private int doFunc(int number) {
        try {
            RUN_ORDER.add(number);
            Thread.sleep(RandomUtils.nextInt(1000, 2000));
            return number;
        } catch (Exception exc) {
            throw new RuntimeException("Failed to execute with i = " + number, exc);
        }
    }
}
