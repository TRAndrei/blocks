package com.rtb.blocks.api.row;

import com.google.common.collect.ImmutableList;
import com.rtb.blocks.api.row.visitor.IVisitableValueRow;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.EmptyRowValueBlock.EMPTY_ROW;

public class DefaultedRowValueBlock<Value> implements IRowValueBlock<Value> {
    private final Value defaultValue;
    private final Value value;
    private final int valueIdx;
    private final int valueCount;

    public DefaultedRowValueBlock(Value defaultValue, Value value, int valueIdx, int valueCount) {
        this.defaultValue = defaultValue;
        this.value = value;
        this.valueIdx = valueIdx;
        this.valueCount = valueCount;
    }

    @Override
    public int getSize() {
        return valueCount;
    }

    @Override
    public boolean isDelegate() {
        return true;
    }

    @Override
    public <V> IRowValueBlock<V> convertValues(Function<Value, V> mapper) {
        return new DefaultedRowValueBlock<>(mapper.apply(defaultValue), mapper.apply(value), valueIdx, valueCount);
    }

    @Override
    public IRowBlock toRowBlock(ToDoubleFunction<Value> mapper) {
        return new DefaultedRowBlock(mapper.applyAsDouble(defaultValue), mapper.applyAsDouble(value), valueIdx, valueCount);
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
                consumer.accept(idx == valueIdx ? value : defaultValue, simulations.get(idx));
                idx++;
                return true;
            }
            return false;
        }

        @Override
        public void consumeRemaining(BiConsumer<Value, Sim> consumer) {
            for (; idx < simulations.size(); idx++) {
                consumer.accept(idx == valueIdx ? value : defaultValue, simulations.get(idx));
            }
        }

        @Override
        public boolean hasValueForSimulation(int simulationIndex) {
            return valueIdx == simulationIndex;
        }
    }
}
