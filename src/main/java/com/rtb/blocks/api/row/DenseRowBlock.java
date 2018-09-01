package com.rtb.blocks.api.row;

import com.google.common.collect.ImmutableList;
import com.rtb.blocks.api.row.visitor.IVisitableRow;

import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.EmptyRowBlock.EMPTY_ROW;

public class DenseRowBlock<Sim> implements IRowBlock<Sim> {
    private static final long serialVersionUID = -5779075886311091969L;
    private final double[] values;
    private final List<Sim> simulations;

    public DenseRowBlock(double[] values, List<Sim> simulations) {
        this.values = values;
        this.simulations = simulations;
    }

    @Override
    public IVisitableRow<Sim> asVisitable() {
        return new Visitable();
    }

    @Override
    public void accept(ObjDoubleConsumer<Sim> consumer) {
        for (int idx = 0; idx < values.length; idx++) {
            consumer.accept(simulations.get(idx), values[idx]);
        }
    }

    @Override
    public IRowBlock<Sim> map(DoubleUnaryOperator mapper) {
        double[] newValues = new double[values.length];
        for (int idx = 0; idx < values.length; idx++) {
            newValues[idx] = mapper.applyAsDouble(values[idx]);
        }

        return new DenseRowBlock<>(newValues, simulations);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjectDoubleFunction<R, Sim> accumulator) {
        R state = supplier.get();

        for (int idx = 0; idx < values.length; idx++) {
            state = accumulator.apply(state, values[idx], simulations.get(idx));
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

        if (newSimulations.size() == simulations.size()) {
            return this;
        } else if (newSimulations.isEmpty()) {
            return null;
        }

        double[] newValues = new double[newSimulations.size()];
        int newIdx = 0;
        Sim currentSim = newSimulations.get(newIdx);

        for (int idx = 0; idx < simulations.size(); idx++) {
            if (currentSim == simulations.get(idx)) {
                newValues[newIdx++] = values[idx];
                currentSim = newSimulations.get(newIdx);
            }
        }

        return new DenseRowBlock<>(newValues, newSimulations);
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
            if (current < values.length) {
                consumer.accept(simulations.get(current), values[current]);
                current++;
                return true;
            } else {
                return false;
            }
        }
    }
}
