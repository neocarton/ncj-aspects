package org.ncj.aspects.lock.test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ncj.aspects.lock.errors.LockException;

import static org.assertj.core.api.Assertions.assertThat;

public class LockTest {

    private static final int NUMBER_THREAD = 3;

    @Before
    public void initTest() {
        TestOnMethod.RUN_ORDER.clear();
    }

    @After
    public void tearDown() {
        TestOnMethod.RUN_ORDER.clear();
    }

    @Test
    public void lock() throws Exception {
        // Run tests
        List<AsyncTestRunner> runners = new ArrayList<>();
        int start = 1;
        int end = NUMBER_THREAD;
        for (int i = start; i <= end; i++) {
            int number = i;
            AsyncTestRunner runner = AsyncTestRunner.start(() -> new TestOnMethod().execWithLock(number));
            runners.add(runner);
        }
        // Wait tests
        for (AsyncTestRunner runner : runners) {
            runner.join();
        }
        // Assert results
        List<Class<? extends Throwable>> errorTypes = runners.stream()
            .map(runner -> (runner.error == null) ? null : runner.error.getClass())
            .collect(Collectors.toList());
        assertThat(errorTypes).isNotEmpty().doesNotContain(LockException.class);
        List<Integer> expRunOrder = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
        assertThat(TestOnMethod.RUN_ORDER).containsExactlyInAnyOrderElementsOf(expRunOrder);
    }

    @Test
    public void tryLock() throws Exception {
        // Run tests
        List<AsyncTestRunner> runners = new ArrayList<>();
        int start = 1;
        int end = NUMBER_THREAD;
        for (int i = start; i <= end; i++) {
            int number = i;
            AsyncTestRunner runner = AsyncTestRunner.start(() -> new TestOnMethod().execWithTryLock(number));
            runners.add(runner);
        }
        // Wait tests
        for (AsyncTestRunner runner : runners) {
            runner.join();
        }
        // Assert results
        List<Class<? extends Throwable>> errorTypes = runners.stream()
            .map(runner -> (runner.error == null) ? null : runner.error.getClass())
            .collect(Collectors.toList());
        assertThat(errorTypes).isNotEmpty().contains(LockException.class);
        assertThat(TestOnMethod.RUN_ORDER).hasSize(1);
    }

    public static class AsyncTestRunner {

        private Supplier<Object> function;
        private Object result;
        private Throwable error;
        private Thread thread;

        public static AsyncTestRunner start(Supplier<Object> function) {
            AsyncTestRunner test = new AsyncTestRunner(function);
            test.start();
            return test;
        }

        public AsyncTestRunner(Supplier<Object> function) {
            this.function = function;
        }

        public void start() {
            thread = new Thread(() -> {
                try {
                    result = function.get();
                } catch (Throwable exc) {
                    this.error = exc;
                }
            });
            thread.start();
        }

        public void join() throws InterruptedException {
            thread.join();
        }
    }
}
