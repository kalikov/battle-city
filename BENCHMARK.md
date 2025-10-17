REMEMBER: The numbers below are just data.

Method                 Throughput (ops/ms)   GC alloc rate (norm, B/op)   GC alloc rate (MB/sec)   GC time (ms)
clear:                                 233                        0.216                    0.045              -
draw (image):                           18                        2.587                    0.045              -
draw (blending):                        16                      482.856                    7.557              1
drawRect (src-over):                     5                     1456.335                    7.731             22
drawRect (src):                       1227                        0.038                    0.045              -
fillRect (src-over):                     6                     4872.149                   31.258             23
fillRect (src):                        218                        0.224                    0.045              -
drawLine (src-over):                    11                     1999.114                   21.963             67
drawLine (src):                       2224                        0.021                    0.045              -
fillText:                              556                        0.085                    0.045              2
fillText (blending):                    81                    15020.878                 1164.835            990

NOTE: Drawing blended images can be optimised by caching result in a texture. This gives results comparable
to the results of drawing a texture (3.673 B/op). Texture drawing throughput heavily depends on the texture
size (good, limited from above). Text rendering with blending **must** be optimised via texture cache due to
big memory footprint. All the primitives (except for the text) are slower and use significantly more memory
than regular texture drawing when using the "src-over" Porter-Duff rule; and significantly faster with a little
memory footprint when using the "src" Porter-Duff rule.

Scene (draw)           Throughput (ops/ms)   GC alloc rate (norm, B/op)   GC alloc rate (MB/sec)   GC time (ms)
MainMenuScene                           70                       41.363                    2.776              2
StageScene                               8                     7146.756                   57.489             88


Benchmark                                                               Mode  Cnt      Score     Error   Units
DefaultEventManagerBenchmark.benchmarkFireEvent                        thrpt    5  19123.447 ± 366.649  ops/ms
DefaultEventManagerBenchmark.benchmarkFireEvent:gc.alloc.rate          thrpt    5      0.001 ±   0.001  MB/sec
DefaultEventManagerBenchmark.benchmarkFireEvent:gc.alloc.rate.norm     thrpt    5     ≈ 10⁻⁴              B/op
DefaultEventManagerBenchmark.benchmarkFireEvent:gc.count               thrpt    5        ≈ 0            counts
DefaultEventManagerBenchmark.benchmarkSubscription                     thrpt    5   5023.579 ± 370.133  ops/ms
DefaultEventManagerBenchmark.benchmarkSubscription:gc.alloc.rate       thrpt    5      0.001 ±   0.001  MB/sec
DefaultEventManagerBenchmark.benchmarkSubscription:gc.alloc.rate.norm  thrpt    5     ≈ 10⁻⁴              B/op
DefaultEventManagerBenchmark.benchmarkSubscription:gc.count            thrpt    5        ≈ 0            counts