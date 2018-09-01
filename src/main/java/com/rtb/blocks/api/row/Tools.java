package com.rtb.blocks.api.row;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Tools {
    private Tools() {
        //
    }

    public static <T> Stream<T> concat(List<Stream<T>> streams) {
        List<Spliterator<T>> spliterators = streams.stream().map(BaseStream::spliterator).collect(Collectors.toList());

        return StreamSupport.stream(new CombinedSpliterator<>(spliterators), false);
    }

    private static final class CombinedSpliterator<T> implements Spliterator<T> {
        private final List<Spliterator<T>> delegates;
        private int current = 0;

        private CombinedSpliterator(List<Spliterator<T>> delegates) {
            this.delegates = delegates;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            int idx = current;
            if (idx < delegates.size()) {
                do {
                    if (delegates.get(idx).tryAdvance(action)) {
                        current = idx;
                        return true;
                    }
                } while (++idx < delegates.size());
                current = delegates.size();
            }
            return false;
        }

        @Override
        public Spliterator<T> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            long size = 0;
            for (int idx = current; idx < delegates.size(); idx++) {
                size += delegates.get(idx).estimateSize();
                if (size < 0) {
                    return Long.MAX_VALUE;
                }
            }
            return size;
        }

        @Override
        public int characteristics() {
            return 0;
        }
    }
}
