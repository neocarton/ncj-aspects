package org.ncj.aspects.lock;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ncj.aspects.lock.reentrant.ReentrantLockProvider;

class LockProviderManager {

    private static final LockProviderManager INSTANCE = new LockProviderManager();

    private final Map<String, LockProvider> lockProviders = new HashMap<>();

    public static LockProviderManager getInstance() {
        return INSTANCE;
    }

    private LockProviderManager() {
        // Do nothing
    }

    public void register(String name, LockProvider provider) {
        // TODO validate
        lockProviders.put(name, provider);
    }

    @SuppressWarnings("unchecked")
    public <T extends LockProvider> T getLockProvider(String name) {
        if (StringUtils.isBlank(name) || Lock.DEFAULT_LOCK_PROVIDER_NAME.equals(name)) {
            return getDefaultLockProvider();
        }
        LockProvider provider = lockProviders.get(name);
        return (T) provider;
    }

    @SuppressWarnings("unchecked")
    public <T extends LockProvider> T getDefaultLockProvider() {
        return (T) ReentrantLockProvider.getInstance();
    }
}
