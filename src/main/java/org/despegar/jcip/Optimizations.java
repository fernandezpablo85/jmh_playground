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

  /*
    SPOILERS:

    Benchmark                   Mode  Cnt   Score   Error  Units
    Optimizations.doNothing     avgt   10   0.396 ± 0.036  ns/op
    Optimizations.logImpl       avgt   10   0.373 ± 0.005  ns/op
    Optimizations.logImplThree  avgt   10  30.341 ± 1.375  ns/op
    Optimizations.logImplTwo    avgt   10   3.809 ± 0.425  ns/op
   */

}
