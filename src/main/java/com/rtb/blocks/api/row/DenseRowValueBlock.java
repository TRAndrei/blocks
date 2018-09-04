package com.rtb.blocks.api.row;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.rtb.blocks.api.row.visitor.IVisitableValueRow;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.EmptyRowValueBlock.EMPTY_ROW;

public class DenseRowValueBlock<Value> implements IRowValueBlock<Value> {
    private static final long serialVersionUID = 6421929554021826403L;
    private final List<Value> values;

    public DenseRowValueBlock(List<Value> values) {
        this.values = values;
    }

    @Override
    public int getSize() {
        return values.size();
    }

    @Override
    public boolean isDelegate() {
        return false;
    }

    @Override
    public <V> IRowValueBlock<V> convertValues(Function<Value, V> mapper) {
        return new DenseRowValueBlock<>(values.stream().map(mapper).collect(Collectors.toList()));
    }

    @Override
    public IRowBlock toRowBlock(ToDoubleFunction<Value> mapper) {
        return new DenseRowBlock(values.stream().mapToDouble(mapper).toArray());
    }

    @Override
    public <Sim> IVisitableValueRow<Value, Sim> getVisitableRow(List<Sim> simulations) {
        Preconditions.checkArgument(values.size() == simulations.size());
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
                consumer.accept(values.get(idx), simulations.get(idx++));
                return true;
            }
            return false;
        }

        @Override
        public void consumeRemaining(BiConsumer<Value, Sim> consumer) {
            for (; idx < simulations.size(); idx++) {
                consumer.accept(values.get(idx), simulations.get(idx));
            }
        }

        @Override
        public boolean hasValueForSimulation(int simulationIndex) {
            return true;
        }
    }
}
