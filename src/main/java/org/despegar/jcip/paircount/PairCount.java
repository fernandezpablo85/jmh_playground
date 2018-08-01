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


    /*
     * Unsafe. Reorder of operations may result in invariant violation.
     * e.g: numbers = [1, 2] and sum = 3
     */
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


    /*
     * Safe but slow. Synchronize guarantees operation order and invariant enforcement, but only one thread at a time.
     */
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

    /*
     * Safe and fast, but non sequential. Invariants hold since Holder is immutable, but it may happen that we
     * get the same sum() twice due to a check-then-act race condition.
     */
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

    /*
     * Safe, pretty fast and sequential. Holder guarantees invariants and AtomicReference CAS guarantees monotonicity.
     */
    @State(Scope.Benchmark)
    public static class SafeSeqCounter implements Counter
    {
        AtomicReference<Holder> holder = new AtomicReference<>(new Holder(new long[]{0, 0}));

        private Holder nextHolder(Holder h)
        {
            long n = h.getNumbers()[0];
            long next = n + 1;
            long[] newNumbers = new long[]{next, next};
            return new Holder(newNumbers);
        }

        @Override
        public long add()
        {
            Holder current = holder.get();
            Holder newHolder = this.nextHolder(current);

            boolean success = this.holder.compareAndSet(current, newHolder);
            while(!success) {
                current = holder.get();
                newHolder = this.nextHolder(current);
                success = this.holder.compareAndSet(current, newHolder);
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
