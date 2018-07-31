package org.despegar.jcip.atomic;

import java.util.concurrent.CountDownLatch;

public class Main
{
    private static long n = 0;
    private static long twice = 0;

    public static void main(String[] args) throws InterruptedException
    {
        int threads = 200;
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++)
        {
            new Thread( () ->
            {
                for (int j = 0; j < 100; j++)
                {
                    n = n + 1;
                    twice = n + n;

                    if (twice % 2 != 0)
                    {
                        System.out.println("error");
                    }
//                    System.out.println(twice);
                }
                latch.countDown();
            }).start();
        }
        latch.await();
    }
}
