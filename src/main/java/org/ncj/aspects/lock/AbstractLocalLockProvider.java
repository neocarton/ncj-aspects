package org.ncj.aspects.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public abstract class AbstractLocalLockProvider implements LockProvider {

    private final Map<String, Lock> locks = new HashMap<>();

    public abstract Lock createLock(String name);

    @Override
    public Lock getLock(String name) {
        synchronized (locks) {
            Lock lock = locks.computeIfAbsent(name, key -> createLock(name));
            return lock;
        }
    }
}
