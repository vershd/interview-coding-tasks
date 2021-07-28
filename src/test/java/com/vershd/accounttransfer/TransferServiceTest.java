package com.vershd.accounttransfer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransferServiceTest {

    private TransferService transferService = new TransferService();
    private final Random random = new Random();
    private final int THREAD_NUM = Runtime.getRuntime().availableProcessors();

    private volatile Account[] accounts;

    @BeforeEach
    public void setUp() {
        accounts = new Account[5];
        for (int i = 0; i < 5; i++) {
            accounts[i] = new Account();
            accounts[i].setAmount(BigInteger.valueOf(0));
            accounts[i].setId(i);
        }
    }

    @Test
    public void testViaCoundDownLatch() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        Runnable runnable = () -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int from = random.nextInt(5);
            int to = random.nextInt(5);
            System.out.println("From " + from + " to " + to);
            transferService.transferManualSync(accounts[random.nextInt(2)], accounts[random.nextInt(2)], BigInteger.valueOf(100));
        };

        List<Thread> threads = Stream.generate(() -> new Thread(runnable))
                .limit(THREAD_NUM)
                .peek(Thread::start)
                .collect(Collectors.toList());

        latch.countDown();

        for (Thread thread : threads) {
            thread.join();
        }

        Assertions.assertEquals(0, Arrays.stream(accounts).map(it -> it.getAmount().intValue()).reduce(0, Integer::sum));
    }

    @Test
    public void testViaPhaser() {

    }

    @Test
    public void testViaExecutors() throws ExecutionException, InterruptedException {

        Runnable runnable = () -> {
            int from = random.nextInt(5);
            int to = random.nextInt(5);
            System.out.println("From " + from + " to " + to);
            transferService.transferManualSync(accounts[random.nextInt(2)], accounts[random.nextInt(2)], BigInteger.valueOf(100));
        };
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);

        for (int i = 0; i < 100; i++) {
            List<Future> futures = Stream.generate(() -> executor.submit(runnable)).limit(THREAD_NUM).collect(Collectors.toList());
            for (Future future : futures) {
                future.get();
            }
            Assertions.assertEquals(0, Arrays.stream(accounts).map(it -> it.getAmount().intValue()).reduce(0, Integer::sum));
        }
    }

    @Test
    public void testViaExecutorsAndCompletableFuture() {

        Runnable runnable = () -> {
            int from = random.nextInt(5);
            int to = random.nextInt(5);
            System.out.println("From " + from + " to " + to);
            transferService.transferManualSync(accounts[random.nextInt(2)], accounts[random.nextInt(2)], BigInteger.valueOf(100));
        };
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);

        for (int i = 0; i < 100; i++) {
            CompletableFuture<?> future = CompletableFuture.allOf(
                    Stream.generate(() -> CompletableFuture.runAsync(runnable))
                            .limit(THREAD_NUM).toArray(CompletableFuture[]::new));
            future.join();

            Assertions.assertEquals(0, Arrays.stream(accounts).map(it -> it.getAmount().intValueExact()).reduce(0, Integer::sum));
        }
    }

    @Test
    public void testViaCyclicBarrier() throws InterruptedException {

        CyclicBarrier barrier = new CyclicBarrier(THREAD_NUM, () -> {
            System.out.println("Done");
            Assertions.assertEquals(0, Arrays.stream(accounts).map(it -> it.getAmount().intValue()).reduce(0, Integer::sum));
        });

        Runnable r = () -> {
            for (int i = 0; i < 10; i++) {
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }

                int from = random.nextInt(5);
                int to = random.nextInt(5);
                System.out.println("From " + from + " to " + to);
                transferService.transferManualSync(accounts[random.nextInt(2)], accounts[random.nextInt(2)], BigInteger.valueOf(100));

            }
        };

        List<Thread> threads = Stream.generate(() -> new Thread(r))
                .limit(Runtime.getRuntime().availableProcessors())
                .peek(Thread::start).collect(Collectors.toList());

        for (Thread thread : threads) {
            thread.join();
        }

        /*Random random = new Random();
        IntStream.range(0, 100)
                .parallel()
                .forEach(i -> {
                    int from = random.nextInt(5);
                    int to = random.nextInt(5);
                    System.out.println("From " + from + " to " + to);
                    transferService.transferManualSync(random.nextInt(2), random.nextInt(2), 100);
                });*/

    }
}
