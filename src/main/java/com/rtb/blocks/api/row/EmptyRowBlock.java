package com.rtb.blocks.api.row;

import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmptyRowBlock<Sim> implements IRowBlock<Sim> {
    private static final long serialVersionUID = -491165442124015525L;
    public static final IRowBlock EMPTY_ROW = new EmptyRowBlock<>();

    private EmptyRowBlock() {

    }

    @Override
    public void accept(ObjDoubleConsumer<Sim> consumer) {

    }

    @Override
    public IRowBlock<Sim> map(DoubleUnaryOperator mapper) {
        return EMPTY_ROW;
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjectDoubleFunction<R, Sim> accumulator) {
        return supplier.get();
    }

    @Override
    public Stream<Sim> getSimulationIds() {
        return Stream.empty();
    }

    @Override
    public int getSimulationCount() {
        return 0;
    }

    @Override
    public IRowBlock<Sim> getDenseBlock() {
        return this;
    }

    @Override
    public IRowBlock<Sim> filterBySimulation(Predicate<Sim> simulationIdPredicate) {
        return this;
    }

    @Override
    public IRowBlock<Sim> composeHorizontally(List<IRowBlock<Sim>> blocks) {
        List<IRowBlock<Sim>> newBlocks = blocks.stream().filter(b -> EMPTY_ROW != b).collect(Collectors.toList());

        return newBlocks.isEmpty() ? EMPTY_ROW : new CombinedRowBlock<>(newBlocks);
    }

    @Override
    public IRowBlock<Sim> composeHorizontally(IRowBlock<Sim> other) {
        return other;
    }
}