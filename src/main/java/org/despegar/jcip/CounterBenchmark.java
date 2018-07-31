package org.despegar.jcip;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class CounterBenchmark
{
    @State(Scope.Benchmark)
    public static class LongValue
    {
        long val = 0;
    }

    @State(Scope.Benchmark)
    public static class AtomicLongValue
    {
        AtomicLong val = new AtomicLong(0);
    }

    @State(Scope.Benchmark)
    public static class SyncAdder
    {
        public synchronized long add(long current)
        {
            return current + 1;
        }
    }

//    @Benchmark
    public long increaseLong(LongValue holder)
    {
        return holder.val + 1;
    }

//    @Benchmark
    public long increaseAtomic(AtomicLongValue holder)
    {
        return holder.val.incrementAndGet();
    }

//    @Benchmark
    public long increaseSync(LongValue holder, SyncAdder adder)
    {
        return adder.add(holder.val);
    }

//    @Benchmark @Threads(2)
    public long increaseLong_t2(LongValue holder)
    {
        return holder.val + 1;
    }

    @Benchmark @Threads(5)
    public long increaseAtomic_t2(AtomicLongValue holder)
    {
        return holder.val.incrementAndGet();
    }

    @Benchmark @Threads(5)
    public long increaseSync_t2(LongValue holder, SyncAdder adder)
    {
        return adder.add(holder.val);
    }
}
