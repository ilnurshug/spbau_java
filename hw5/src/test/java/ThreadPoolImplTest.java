import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ThreadPoolImplTest {

    @Test(expected = LightExecutionException.class)
    public void shutdownTest() throws LightExecutionException, InterruptedException {
        ThreadPool threadPool = new ThreadPoolImpl(1);
        LightFuture<Integer> future = null;
        for (int i = 0; i < 100; i++) {
            future = threadPool.submit(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException ignored) {
                }
                return 0;
            });
        }
        threadPool.shutdown();
        future.get();
    }

    @Test
    public void isReadyTest() throws InterruptedException {
        ThreadPool threadPool = new ThreadPoolImpl(1);
        LightFuture<Integer> future = threadPool.submit(() -> 0);
        TimeUnit.MILLISECONDS.sleep(10);
        assertTrue(future.isReady());
        threadPool.shutdown();
    }

    @Test
    public void getTest() throws LightExecutionException, InterruptedException {
        ThreadPool threadPool = new ThreadPoolImpl(1);
        LightFuture<Integer> future = threadPool.submit(() -> 10);
        assertEquals(10, (int) future.get());
        threadPool.shutdown();
    }

    @Test
    public void thenApplyTest() throws LightExecutionException, InterruptedException {
        ThreadPool threadPool = new ThreadPoolImpl(1);
        LightFuture<Integer> future = threadPool.submit(() -> 10);
        LightFuture<Integer> thenApply = future.thenApply(i -> i * i);
        assertEquals(100, (int) thenApply.get());

        future = threadPool.submit(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException ignored) {
            }
            return 10;
        });
        thenApply = future.thenApply(i -> i * i);
        assertEquals(100, (int) thenApply.get());
        threadPool.shutdown();
    }

    @Test
    public void countThreadsTest() throws LightExecutionException, InterruptedException {
        ThreadPool threadPool = new ThreadPoolImpl(10);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
        LightFuture<Integer> submit = null;
        for (int i = 0; i < 10; i++) {
            submit = threadPool.submit(() -> {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException ignored) {
                }
                return 10;
            });
        }
        assertEquals(10, (int) submit.get());
        threadPool.shutdown();
    }

}