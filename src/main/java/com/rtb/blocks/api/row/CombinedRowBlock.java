package com.rtb.blocks.api.row;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.EmptyRowBlock.EMPTY_ROW;

public class CombinedRowBlock<Sim> implements IRowBlock<Sim> {
    private static final long serialVersionUID = -6563412556291079221L;
    private final List<IRowBlock<Sim>> blocks;

    public CombinedRowBlock(List<IRowBlock<Sim>> blocks) {
        this.blocks = blocks;
    }

    @Override
    public void accept(ObjDoubleConsumer<Sim> consumer) {
        for (int idx = 0; idx < blocks.size(); idx++) {
            blocks.get(idx).accept(consumer);
        }
    }

    @Override
    public IRowBlock<Sim> map(DoubleUnaryOperator mapper) {
        return new CombinedRowBlock<>(blocks.stream().map(block -> block.map(mapper)).collect(Collectors.toList()));
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjectDoubleFunction<R, Sim> accumulator) {
        R state = supplier.get();

        for (int idx = 0; idx < blocks.size(); idx++) {
            R previousState = state;
            state = blocks.get(idx).collect(() -> previousState, accumulator);
        }

        return state;
    }

    @Override
    public Stream<Sim> getSimulationIds() {
        return Tools.concat(blocks.stream().map(IBaseRowBlock::getSimulationIds).collect(Collectors.toList()));
    }

    @Override
    public int getSimulationCount() {
        return blocks.stream().reduce(0, (s, b) -> s += b.getSimulationCount(), (s1, s2) -> s1 + s2);
    }

    @Override
    public IRowBlock<Sim> getDenseBlock() {
        List<IRowBlock<Sim>> newBlocks = blocks.stream().map(IBaseRowBlock::getDenseBlock).filter(b -> EMPTY_ROW != b).
                collect(Collectors.toList());
        return blocks.isEmpty() ? EMPTY_ROW : new CombinedRowBlock<>(newBlocks);
    }

    @Override
    public IRowBlock<Sim> filterBySimulation(Predicate<Sim> simulationIdPredicate) {
        List<IRowBlock<Sim>> newBlocks = blocks.stream().map(block -> block.filterBySimulation(simulationIdPredicate)).
                collect(Collectors.toList());
        return blocks.isEmpty() ? EMPTY_ROW : new CombinedRowBlock<>(newBlocks);
    }

    @Override
    public IRowBlock<Sim> composeHorizontally(List<IRowBlock<Sim>> other) {
        List<IRowBlock<Sim>> newBlocks = Stream.concat(blocks.stream(), other.stream()).filter(b -> EMPTY_ROW != b).
                collect(Collectors.toList());

        return new CombinedRowBlock<>(newBlocks);
    }

    @Override
    public IRowBlock<Sim> composeHorizontally(IRowBlock<Sim> other) {
        if (EMPTY_ROW == other) {
            return this;
        }

        return new CombinedRowBlock<>(ImmutableList.<IRowBlock<Sim>>builder().addAll(blocks).add(other).build());
    }
}
