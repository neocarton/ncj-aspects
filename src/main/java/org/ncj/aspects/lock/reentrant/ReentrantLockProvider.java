package org.ncj.aspects.lock.reentrant;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.ncj.aspects.lock.AbstractLocalLockProvider;
import org.ncj.aspects.lock.LockProvider;

public class ReentrantLockProvider extends AbstractLocalLockProvider  implements LockProvider {

    private static final ReentrantLockProvider INSTANCE = new ReentrantLockProvider();

    public static ReentrantLockProvider getInstance() {
        return INSTANCE;
    }

    private ReentrantLockProvider() {
        // Do nothing
    }

    @Override
    public Lock createLock(String name) {
        return new ReentrantLock();
    }
}
