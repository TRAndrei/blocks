package com.rtb.blocks.api.row;

import com.google.common.collect.ImmutableList;
import com.rtb.blocks.api.row.visitor.IVisitableValueRow;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.EmptyRowValueBlock.EMPTY_ROW;

public class SingleRowValueBlock<Value, Sim> implements IRowValueBlock<Value> {
    private static final long serialVersionUID = 3138934904095516007L;
    private final Value value;
    private final List<Sim> simulations;

    public SingleRowValueBlock(Value value, List<Sim> simulations) {
        this.value = value;
        this.simulations = simulations;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public void accept(Consumer<Value> consumer) {
        for (int idx = 0; idx < simulations.size(); idx++) {
            consumer.accept(value);
        }
    }

    @Override
    public <V> IRowValueBlock<V> convertValues(Function<Value, V> mapper) {
        return new SingleRowValueBlock<>(mapper.apply(value), simulations);
    }

    @Override
    public IRowBlock toRowBlock(ToDoubleFunction<Value> mapper) {
        return new SingleRowBlock(mapper.applyAsDouble(value));
    }

    @Override
    public <Sim> IVisitableValueRow<Value, Sim> getVisitableRow(List<Sim> simulations) {
        return new VisitableRow<>(simulations);
    }

    @Override
    public IRowValueBlock<Value> composeHorizontally(List<IRowValueBlock<Value>> other) {
        List<IRowValueBlock<Value>> newBlocks = Stream.concat(Stream.of(this), other.stream()).
                filter(b -> EMPTY_ROW != b).collect(Collectors.toList());

        return new CombinedRowValueBlock<>(newBlocks);
    }

    @Override
    public IRowValueBlock<Value> composeHorizontally(IRowValueBlock<Value> other) {
        if (EMPTY_ROW == other) {
            return this;
        }

        return new CombinedRowValueBlock<>(ImmutableList.of(this, other));
    }

    private class VisitableRow<Sim> implements IVisitableValueRow<Value, Sim> {
        private final List<Sim> simulations;
        private int idx = 0;

        private VisitableRow(List<Sim> simulations) {
            this.simulations = simulations;
        }

        @Override
        public boolean tryConsume(BiConsumer<Value, Sim> consumer) {
            if (idx < simulations.size()) {
                consumer.accept(value, simulations.get(idx++));
                return true;
            }
            return false;
        }

        @Override
        public void consumeRemaining(BiConsumer<Value, Sim> consumer) {
            for (; idx < simulations.size(); idx++) {
                consumer.accept(value, simulations.get(idx));
            }
        }
    }
}
