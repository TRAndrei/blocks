package com.rtb.blocks.api.row;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.EmptyRowValueBlock.EMPTY_ROW;

public class SingleRowValueBlock<Value, Sim> implements IRowValueBlock<Value, Sim> {
    private static final long serialVersionUID = 3138934904095516007L;
    private final Value value;
    private final List<Sim> simulations;

    public SingleRowValueBlock(Value value, List<Sim> simulations) {
        this.value = value;
        this.simulations = simulations;
    }

    @Override
    public void accept(BiConsumer<Value, Sim> consumer) {
        for (int idx = 0; idx < simulations.size(); idx++) {
            consumer.accept(value, simulations.get(idx));
        }
    }

    @Override
    public <V> IRowValueBlock<V, Sim> convertValues(Function<Value, V> mapper) {
        return new SingleRowValueBlock<>(mapper.apply(value), simulations);
    }

    @Override
    public IRowBlock<Sim> toRowBlock(ToDoubleFunction<Value> mapper) {
        return new SingleRowBlock<>(mapper.applyAsDouble(value), simulations);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, IRowConsumer<R, Value, Sim> accumulator) {
        R state = supplier.get();

        for (int idx = 0; idx < simulations.size(); idx++) {
            state = accumulator.consume(state, value, simulations.get(idx));
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
        List<Sim> newSimulations = simulations.stream().filter(simulationIdPredicate).collect(Collectors.toList());

        return newSimulations.isEmpty() ? EMPTY_ROW : new SingleRowValueBlock<>(value, newSimulations);
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
}
