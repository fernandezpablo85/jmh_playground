## 10 threads

```
Benchmark                              Mode  Cnt    Score    Error  Units
PairCount.safeButNonSequentialCounter  avgt   10  139.195 ±  8.395  ns/op
PairCount.safeSeqCounter               avgt   10  832.904 ± 39.106  ns/op
PairCount.safeSlowCounter              avgt   10  520.929 ± 40.503  ns/op
PairCount.unsafeCounter                avgt   10  148.105 ±  1.432  ns/op
```


## 50 threads

```
Benchmark                  Mode  Cnt     Score     Error  Units
PairCount.safeSeqCounter   avgt   10  3910.529 ± 585.328  ns/op
PairCount.safeSlowCounter  avgt   10  2758.401 ± 210.236  ns/op
```