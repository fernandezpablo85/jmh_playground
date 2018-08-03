package org.despegar.jcip;

import org.openjdk.jmh.annotations.*;

import java.text.DateFormat;
import java.text.Format;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class Optimizations
{
  @State(Scope.Benchmark)
  public static class DoubleHolder
  {
    double val = 10;
  }

  @Benchmark
  public void doNothing()
  {
    // delivers.
  }

  @Benchmark
  public void logImpl(DoubleHolder dh)
  {
    Math.log(dh.val);
  }

  @Benchmark
  public double logImplTwo()
  {
    return Math.log(10);
  }

  @Benchmark
  public double logImplThree(DoubleHolder dh)
  {
    return Math.log(dh.val);
  }

}
