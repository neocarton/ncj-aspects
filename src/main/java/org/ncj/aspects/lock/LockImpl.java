package org.ncj.aspects.lock;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import lombok.extern.slf4j.Slf4j;
import org.ncj.aspects.lock.errors.LockException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@Slf4j
public final class LockImpl {

    @Around("execution(@org.ncj.aspects.lock.Lock * * (..)) && @annotation(annotation)")
    public Object around(ProceedingJoinPoint point, org.ncj.aspects.lock.Lock annotation) throws Throwable {
        // Get lock parameters
        String lockKey = annotation.value();
        LockParams lockParams = new LockParams(annotation);
        log.trace("Locking '{}' with parameters: {}", lockKey, lockParams);
        // Lock
        Lock lock = getLock(lockParams);
        boolean locked = lock(lock, lockParams);
        if (!locked) {
            throw new LockException("Failed to lock '" + lockKey + "' with parameters: " + lockParams);
        }
        log.debug("Locked '{}' with parameters: {}", lockKey, lockParams);
        try {
            Object result = point.proceed();
            return result;
        } finally {
            // Unlock
            log.trace("Unlocking '{}' with parameters: {}", lockKey, lockParams);
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
