REMEMBER: The numbers below are just data.

Method                 Throughput (ops/ms)   GC alloc rate (norm, B/op)   GC alloc rate (MB/sec)   GC time (ms)
clear:                                 233                        0.216                    0.045              -
draw (image):                           18                        2.587                    0.045              -
draw (blending):                        15                        7.095                  474.991              1
drawRect:                                5                     1456.335                    7.731             22
fillRect:                                6                     4872.149                   31.258             23
drawLine:                               11                     1999.114                   21.963             67
fillText:                              556                        0.085                    0.045              2
fillText (blending):                    81                    15020.878                 1164.835            990

NOTE: Drawing blended images can be optimised by caching result in a texture. This gives results comparable
to the results of drawing a texture. Texture drawing heavily throughput depends on the texture size
(good, limited from above). Text rendering with blending **must** be optimised via texture cache. All the
primitives (except for the text) are significantly slower that texture drawing.

Scene (draw)           Throughput (ops/ms)   GC alloc rate (norm, B/op)   GC alloc rate (MB/sec)   GC time (ms)
MainMenuScene                           28                       43.931                    1.197              -