package com.github.zhongl.ipage;

import com.github.zhongl.util.Benchmarks;
import com.github.zhongl.util.FileTestContext;
import com.github.zhongl.util.Md5;
import com.google.common.util.concurrent.FutureCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.*;

/** @author <a href="mailto:zhong.lunfu@gmail.com">zhongl<a> */
public class IPageBenchmark extends FileTestContext {

    private IPage<Integer, byte[]> iPage;
    private ExecutorService service;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        int threads = Runtime.getRuntime().availableProcessors();
        service = new ThreadPoolExecutor(
                threads,
                threads,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Test
    public void addThenRemove() throws Exception {
        dir = testDir("addThenRemove");

        initIPage(100000, 5000L, 10000);

        final int times = 1000000;
        final CountDownLatch aLatch = new CountDownLatch(times);

        Benchmarks.benchmark("add", new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < times; i++) {
                    final int num = i;
                    service.submit(new Runnable() {
                        @Override
                        public void run() {
                            iPage.add(num, new byte[1024], new FutureCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    aLatch.countDown();
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                        }
                    });
                }

                try {
                    aLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, times);

        final CountDownLatch rLatch = new CountDownLatch(times);
        Benchmarks.benchmark("remove", new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < times; i++) {
                    final int num = i;
                    service.submit(new Runnable() {
                        @Override
                        public void run() {
                            iPage.remove(num, new FutureCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    rLatch.countDown();
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                        }
                    });
                }

                try {
                    rLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, times);


    }

    private void initIPage(final int ephemeronThroughout, final long flushMillis, final int flushCount) throws Exception {
        iPage = new IPage<Integer, byte[]>(dir, new BytesCodec(), ephemeronThroughout, flushMillis, flushCount) {
            @Override
            protected Key transform(Integer key) {
                return new Key(Md5.md5(key.toString().getBytes()));
            }
        };
    }

    @Test
    public void get() throws Exception {
        dir = testDir("get");

        initIPage(100000, 5000L, 10000);

        final int times = 100000;
        final CountDownLatch aLatch = new CountDownLatch(times);

        for (int i = 0; i < times; i++) {
            final int num = i;
            service.submit(new Runnable() {
                @Override
                public void run() {
                    iPage.add(num, new byte[1024], new FutureCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            aLatch.countDown();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            });
        }

        try {
            aLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final Random random = new Random();

        final CountDownLatch gLatch = new CountDownLatch(times * 10);
        Benchmarks.benchmark("get", new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < times * 10; i++) {
                    service.submit(new Runnable() {
                        @Override
                        public void run() {
                            iPage.get(random.nextInt(times));
                            gLatch.countDown();
                        }
                    });
                }

                try {
                    gLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();  // TODO right
                }
            }
        }, times * 10);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        iPage.stop();
        service.shutdownNow();
        super.tearDown();
    }
}
