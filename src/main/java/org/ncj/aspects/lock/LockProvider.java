package org.ncj.aspects.lock;

import java.util.concurrent.locks.Lock;

public interface LockProvider {

    Lock getLock(String name);
}
