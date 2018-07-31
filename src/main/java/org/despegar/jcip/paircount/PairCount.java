package org.despegar.jcip.paircount;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class PairCount
{
    private interface Counter
    {
        long add();
    }

    @State(Scope.Benchmark)
    public static class UnsafeCounter implements Counter
    {
        long[] numbers = new long[]{0, 0};
        long sum = 0;

        @Override
        public long add()
        {
            long n = this.numbers[0];
            long next = n + 1;
            this.numbers[0] = next;
            this.numbers[1] = next;
            this.sum = this.numbers[0] + this.numbers[1];
            return this.sum;
        }
    }


    @State(Scope.Benchmark)
    public static class SafeButSlowCounter implements Counter
    {
        long[] numbers = new long[]{0, 0};
        long sum = 0;

        @Override
        public synchronized long add()
        {
            long n = this.numbers[0];
            long next = n + 1;
            this.numbers[0] = next;
            this.numbers[1] = next;
            this.sum = this.numbers[0] + this.numbers[1];
            return this.sum;
        }
    }

    private static class Holder
    {
        private final long[] numbers;

        private Holder(long[] numbers)
        {
            this.numbers = numbers;
        }

        long getSum()
        {
            return this.numbers[0] + this.numbers[1];
        }

        long[] getNumbers()
        {
            return new long[]{this.numbers[0], this.numbers[0]};
        }

        public boolean equals(Object other)
        {
            if (other == null)
                return false;
            if (! (other instanceof Holder))
                return false;
            else {
                Holder otherHolder = (Holder) other;
                return otherHolder.numbers.equals(this.numbers);
            }
        }

        public int hashCode()
        {
            return this.numbers.hashCode();
        }

    }

    @State(Scope.Benchmark)
    public static class SafeNonSeqCounter implements Counter
    {
        Holder holder = new Holder(new long[]{0, 0});

        @Override
        public long add()
        {
            long n = holder.getNumbers()[0];
            long next = n + 1;
            long[] newNumbers = new long[]{next, next};
            Holder newHolder = new Holder(newNumbers);
            this.holder = newHolder;
            return newHolder.getSum();
        }
    }

    @State(Scope.Benchmark)
    public static class SafeSeqCounter implements Counter
    {
        AtomicReference<Holder> holder = new AtomicReference(new Holder(new long[]{0, 0}));

        @Override
        public long add()
        {
            Holder currentHolder = holder.get();
            long n = currentHolder.getNumbers()[0];
            long next = n + 1;
            long[] newNumbers = new long[]{next, next};
            Holder newHolder = new Holder(newNumbers);

            boolean success = this.holder.compareAndSet(currentHolder, newHolder);
            while(!success) {
                success = this.holder.compareAndSet(currentHolder, newHolder);
            }
            return newHolder.getSum();
        }
    }

    @Benchmark @Threads(4)
    public long unsafeCounter(UnsafeCounter counter)
    {
        return counter.add();
    }

    @Benchmark @Threads(4)
    public long safeSlowCounter(SafeButSlowCounter counter)
    {
        return counter.add();
    }

    @Benchmark @Threads(4)
    public long safeButNonSequentialCounter(SafeNonSeqCounter counter)
    {
        return counter.add();
    }

    @Benchmark @Threads(4)
    public long safeSeqCounter(SafeSeqCounter counter)
    {
        return counter.add();
    }

}
