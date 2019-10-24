package org.ncj.aspects.lock;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.ncj.aspects.lock.errors.LockException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public final class LockAspect {

    @Around("execution(@org.ncj.aspects.lock.Lock * * (..)) && @annotation(annotation)")
    public Object wrap(ProceedingJoinPoint point, org.ncj.aspects.lock.Lock annotation) throws Throwable {
        // Get lock parameters
        LockParams lockParams = new LockParams(annotation);
        // Lock
        Lock lock = getLock(lockParams);
        boolean locked = lock(lock, lockParams);
        if (!locked) {
            throw new LockException("Failed to lock '" + lockParams.value() + "'");
        }
        try {
            Object result = point.proceed();
            return result;
        } finally {
            // Unlock
            lock.unlock();
        }
    }

    private Lock getLock(LockParams lockParams) {
        String providerName = lockParams.provider();
        String lockName = lockParams.value();
        LockProvider provider = getLockProvider(providerName);
        Lock lock = provider.getLock(lockName);
        if (lock == null) {
            throw new NoSuchElementException("Cannot get lock from provider with name '" + providerName + "'");
        }
        return lock;
    }

    private LockProvider getLockProvider(String providerName) {
        LockProvider provider = LockProviderManager.getInstance().getLockProvider(providerName);
        if (provider == null) {
            throw new NoSuchElementException("Cannot get lock-provider with name '" + providerName + "'");
        }
        return provider;
    }

    private boolean lock(Lock lock, LockParams lockParams) throws Throwable {
        if (!lockParams.tryLock()) {
            lock.lock();
            return true;
        }
        long timeout = lockParams.timeout();
        if (timeout <= 0) {
            return lock.tryLock();
        } else {
            TimeUnit timeoutUnit = lockParams.timeoutUnit();
            return lock.tryLock(timeout, timeoutUnit);
        }
    }
}
