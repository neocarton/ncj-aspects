package org.ncj.aspects.lock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {

    String DEFAULT_LOCK_PROVIDER_NAME = "default";

    String value();

    String provider() default DEFAULT_LOCK_PROVIDER_NAME;

    boolean tryLock() default false;

    long timeout() default 0;

    TimeUnit timeoutUnit() default TimeUnit.MILLISECONDS;
}
