package org.ncj.aspects.lock;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import lombok.ToString;

public final class LockParams implements Lock {

    private final Lock lock;

    public LockParams(Lock lock) {
        if (lock == null) {
            throw new NullPointerException();
        }
        this.lock = lock;
    }

    @Override
    public String value() {
        return lock.value();
    }

    @Override
    public String provider() {
        return lock.provider();
    }

    @Override
    public boolean tryLock() {
        return lock.tryLock();
    }

    @Override
    public long timeout() {
        return lock.timeout();
    }

    @Override
    public TimeUnit timeoutUnit() {
        return lock.timeoutUnit();
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return lock.annotationType();
    }
}
