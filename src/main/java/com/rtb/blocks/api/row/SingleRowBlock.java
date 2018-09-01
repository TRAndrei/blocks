package com.rtb.blocks.api.row;

import com.google.common.collect.ImmutableList;
import com.rtb.blocks.api.row.visitor.IVisitableRow;

import java.util.Collections;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.EmptyRowBlock.EMPTY_ROW;

public class SingleRowBlock<Sim> implements IRowBlock<Sim> {
    private static final long serialVersionUID = 137164116081742975L;
    private final double value;
    private final List<Sim> simulations;

    public SingleRowBlock(double value, List<Sim> simulations) {
        this.value = value;
        this.simulations = simulations;
    }

    public SingleRowBlock(double value, Sim simulation) {
        this.value = value;
        this.simulations = Collections.singletonList(simulation);
    }

    @Override
    public IVisitableRow<Sim> asVisitable() {
        return new Visitable();
    }

    @Override
    public void accept(ObjDoubleConsumer<Sim> consumer) {
        for (int idx = 0; idx < simulations.size(); idx++) {
            consumer.accept(simulations.get(idx), value);
        }
    }

    @Override
    public IRowBlock<Sim> map(DoubleUnaryOperator mapper) {
        return new SingleRowBlock<>(mapper.applyAsDouble(value), simulations);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjectDoubleFunction<R, Sim> accumulator) {
        R state = supplier.get();

        for (int idx = 0; idx < simulations.size(); idx++) {
            state = accumulator.apply(state, value, simulations.get(idx));
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
    public IRowBlock<Sim> getDenseBlock() {
        return this;
    }

    @Override
    public IRowBlock<Sim> filterBySimulation(Predicate<Sim> simulationIdPredicate) {
        List<Sim> newSimulations = simulations.stream().filter(simulationIdPredicate).collect(Collectors.toList());

        return newSimulations.isEmpty() ? EMPTY_ROW : new SingleRowBlock<>(value, newSimulations);
    }

    @Override
    public IRowBlock<Sim> composeHorizontally(List<IRowBlock<Sim>> other) {
        List<IRowBlock<Sim>> newBlocks = Stream.concat(Stream.of(this), other.stream()).filter(b -> EMPTY_ROW != b).
                collect(Collectors.toList());

        return new CombinedRowBlock<>(newBlocks);
    }

    @Override
    public IRowBlock<Sim> composeHorizontally(IRowBlock<Sim> other) {
        if (EMPTY_ROW == other) {
            return this;
        }

        return new CombinedRowBlock<>(ImmutableList.of(this, other));
    }

    private class Visitable implements IVisitableRow<Sim> {
        int current = 0;

        @Override
        public boolean tryConsume(ObjDoubleConsumer<Sim> consumer) {
            if (current < simulations.size()) {
                consumer.accept(simulations.get(current), value);
                current++;
                return true;
            } else {
                return false;
            }
        }
    }
}
