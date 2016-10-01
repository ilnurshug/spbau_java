
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {
    private final Thread[] threads;
    private final Queue<LightFutureImpl> taskQueue = new LinkedList<>();

    public ThreadPoolImpl(int n) {
        threads = new Thread[n];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new PoolThread(taskQueue);
            threads[i].start();
        }
    }

    @Override
    public <R> LightFuture<R> submit(Supplier<R> supplier) {
        LightFutureImpl<R> f = new LightFutureImpl<>(this, supplier);
        addFuture(f);
        return f;
    }

    private <R> void addFuture(LightFutureImpl<R> f) {
        synchronized (taskQueue) {
            taskQueue.add(f);
            taskQueue.notify();
        }
    }

    @Override
    public void shutdown() {
        for (Thread t : threads) {
            t.interrupt();
        }

        synchronized (taskQueue) {
            taskQueue.stream().forEach(t -> t.setException(new InterruptedException()));
        }
    }

    private static final class PoolThread extends Thread {
        private final Queue<LightFutureImpl> taskQueue;

        private PoolThread(Queue<LightFutureImpl> queue) {
            taskQueue = queue;
        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                synchronized (taskQueue) {

                    while (taskQueue.isEmpty()) {
                        try {
                            taskQueue.wait();
                        }
                        catch (InterruptedException ignored) {}

                        if (isInterrupted()) {
                            return;
                        }
                    }
                }

                if (taskQueue.isEmpty()) return;

                LightFutureImpl<?> f = taskQueue.poll();

                try {
                    f.runnable.run();
                }
                catch (Throwable e) {
                    f.cause = e;
                }

                f.updateDependent();
                f.finished = true;

                synchronized (f.lock) {
                    f.lock.notifyAll();
                }
            }
        }
    }

    private static final class LightFutureImpl<R> implements LightFuture<R> {
        private volatile Throwable cause;
        private volatile R result;

        private final ThreadPoolImpl pool;
        private final List<LightFutureImpl> dependencies = new LinkedList<>();

        private final Object lock = new Object();
        private final Runnable runnable;

        private volatile boolean finished = false;

        private LightFutureImpl(ThreadPoolImpl pool, Supplier<R> supplier) {
            this.runnable = () -> result = supplier.get();
            this.pool = pool;
        }

        private LightFutureImpl(Throwable e) {
            pool = null;
            runnable = null;

            cause = e;
            finished = true;
        }

        @Override
        public R get() throws LightExecutionException, InterruptedException {
            if (!isReady()) {
                synchronized (lock) {
                    while (!isReady()) {
                        lock.wait();
                    }
                }
            }

            if (cause == null) {
                return result;
            }

            throw new LightExecutionException(cause);
        }

        @Override
        public boolean isReady() {
            return finished;
        }

        @Override
        public <U> LightFuture<U> thenApply(Function<? super R, ? extends U> f) {
            if (isReady()) {
                return thenApplyReady(f);
            }

            LightFutureImpl<U> future;

            synchronized (dependencies) {
                synchronized (lock) {
                    if (isReady()) {
                        return thenApplyReady(f);
                    }
                    future = new LightFutureImpl<>(pool, () -> f.apply(LightFutureImpl.this.result));
                    dependencies.add(future);
                }
            }

            return future;
        }

        private <U> LightFuture<U> thenApplyReady(Function<? super R, ? extends U> f) {
            if (cause != null) {
                return new LightFutureImpl<>(cause);
            }
            else {
                return pool.submit(() -> f.apply(result));
            }
        }

        private void addTaskToQueue() {
            synchronized (dependencies) {
                dependencies.parallelStream().forEach(pool::addFuture);

                dependencies.clear();
            }
        }

        private void updateDependent() {
            if (cause != null) {
                broadcastException();
            }
            else {
                addTaskToQueue();
            }
        }

        private void setException(Exception e) {
            synchronized (lock) {
                cause = e;
                finished = true;

                lock.notifyAll();
            }

            broadcastException();
        }

        private void broadcastException() {
            synchronized (dependencies) {
                dependencies.parallelStream().forEach(
                        lightFuture -> {
                            lightFuture.cause = cause;
                            lightFuture.finished = finished;
                            synchronized (lightFuture.lock) {
                                lightFuture.lock.notifyAll();
                            }
                            lightFuture.broadcastException();
                        });

                dependencies.clear();
            }
        }
    }
}