package com.rtb.blocks.api.row;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.rtb.blocks.api.row.visitor.IVisitableRowValue;

import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.EmptyRowValueBlock.EMPTY_ROW;

public class DenseRowValueBlock<Value, Sim> implements IRowValueBlock<Value, Sim> {
    private static final long serialVersionUID = 6421929554021826403L;
    private final List<Value> values;
    private final List<Sim> simulations;

    public DenseRowValueBlock(List<Value> values, List<Sim> simulations) {
        this.values = values;
        this.simulations = simulations;
    }

    @Override
    public IVisitableRowValue<Value, Sim> asVisitable() {
        return new Visitable();
    }

    @Override
    public void accept(BiConsumer<Value, Sim> consumer) {
        for (int idx = 0; idx < values.size(); idx++) {
            consumer.accept(values.get(idx), simulations.get(idx));
        }
    }

    @Override
    public <V> IRowValueBlock<V, Sim> convertValues(Function<Value, V> mapper) {
        return new DenseRowValueBlock<>(values.stream().map(mapper).collect(Collectors.toList()), simulations);
    }

    @Override
    public IRowBlock<Sim> toRowBlock(ToDoubleFunction<Value> mapper) {
        return new DenseRowBlock<>(values.stream().mapToDouble(mapper).toArray(), simulations);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, IRowConsumer<R, Value, Sim> accumulator) {
        R state = supplier.get();

        for (int idx = 0; idx < values.size(); idx++) {
            state = accumulator.consume(state, values.get(idx), simulations.get(idx));
        }

        return state;
    }

    @Override
    public Stream<Sim> getSimulationIds() {
        return simulations.stream();
    }

    @Override
    public int getSimulationCount() {
        return simulations.size();
    }

    @Override
    public IRowValueBlock<Value, Sim> getDenseBlock() {
        return this;
    }

    @Override
    public IRowValueBlock<Value, Sim> filterBySimulation(Predicate<Sim> simulationIdPredicate) {
        int size = values.size();
        List<Value> newValues = Lists.newArrayListWithCapacity(size);
        List<Sim> newSimulations = Lists.newArrayListWithCapacity(size);

        for (int idx = 0; idx < size; idx++) {
            Sim sim = simulations.get(idx);
            if (simulationIdPredicate.test(sim)) {
                newValues.add(values.get(idx));
                newSimulations.add(sim);
            }
        }

        return newSimulations.isEmpty() ? null : new DenseRowValueBlock<>(newValues, newSimulations);
    }

    @Override
    public IRowValueBlock<Value, Sim> composeHorizontally(List<IRowValueBlock<Value, Sim>> other) {
        List<IRowValueBlock<Value, Sim>> newBlocks = Stream.concat(Stream.of(this), other.stream()).
                filter(b -> EMPTY_ROW != b).collect(Collectors.toList());

        return new CombinedRowValueBlock<>(newBlocks);
    }

    @Override
    public IRowValueBlock<Value, Sim> composeHorizontally(IRowValueBlock<Value, Sim> other) {
        if (EMPTY_ROW == other) {
            return this;
        }

        return new CombinedRowValueBlock<>(ImmutableList.of(this, other));
    }

    private class Visitable implements IVisitableRowValue<Value, Sim> {
        int current = 0;

        @Override
        public boolean tryConsume(BiConsumer<Value, Sim> consumer) {
            if (current< values.size()) {
                consumer.accept(values.get(current), simulations.get(current));
                current++;
                return true;
            } else {
                return false;
            }
        }
    }
}
