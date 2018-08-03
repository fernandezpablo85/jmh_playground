package org.despegar.jcip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleDateFormatTest
{

  public static void main(String[] args) throws Exception
  {
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    final Random rand = new Random();

    final int threads = 250;
    final int roundsPerThread = 200;
    final CountDownLatch latch = new CountDownLatch(threads);
    final AtomicInteger failed = new AtomicInteger(0);

    for(int i = 0; i < threads; i++)
    {
      new Thread( () -> {
        try
        {
          for (int r = 0; r < roundsPerThread; r++)
          {
            format(new Date(rand.nextLong()), sdf);
          }
        }
        catch (Exception e)
        {
          failed.incrementAndGet();
        }
        finally
        {
          latch.countDown();
        }

      }).start();
    }
    latch.await();
    int total = threads * roundsPerThread;
    System.out.printf("%d of %d formats failed (%f) %n", failed.get(), total, ((float) failed.get()) / total);
  }

  static String format(Date date, SimpleDateFormat sdf)
  {
      return sdf.format(date);
  }
}
