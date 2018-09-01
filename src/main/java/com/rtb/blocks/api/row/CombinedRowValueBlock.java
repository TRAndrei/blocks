package com.rtb.blocks.api.row;

import com.google.common.collect.ImmutableList;
import com.rtb.blocks.api.row.visitor.IVisitableRow;
import com.rtb.blocks.api.row.visitor.IVisitableRowValue;

import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.EmptyRowValueBlock.EMPTY_ROW;

public class CombinedRowValueBlock<Value, Sim> implements IRowValueBlock<Value, Sim> {
    private static final long serialVersionUID = -992236671173392831L;
    private final List<IRowValueBlock<Value, Sim>> blocks;

    public CombinedRowValueBlock(List<IRowValueBlock<Value, Sim>> blocks) {
        this.blocks = blocks;
    }

    @Override
    public IVisitableRowValue<Value, Sim> asVisitable() {
        return new Visitable();
    }

    @Override
    public void accept(BiConsumer<Value, Sim> consumer) {
        for (int idx = 0; idx < blocks.size(); idx++) {
            blocks.get(idx).accept(consumer);
        }
    }

    @Override
    public <V> IRowValueBlock<V, Sim> convertValues(Function<Value, V> mapper) {
        return new CombinedRowValueBlock<>(blocks.stream().map(block -> block.convertValues(mapper)).collect(Collectors.toList()));
    }

    @Override
    public IRowBlock<Sim> toRowBlock(ToDoubleFunction<Value> mapper) {
        return new CombinedRowBlock<>(blocks.stream().map(block -> block.toRowBlock(mapper)).collect(Collectors.toList()));
    }

    @Override
    public <R> R collect(Supplier<R> supplier, IRowConsumer<R, Value, Sim> accumulator) {
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
    public IRowValueBlock<Value, Sim> getDenseBlock() {
        List<IRowValueBlock<Value, Sim>> newBLocks = blocks.stream().map(IBaseRowBlock::getDenseBlock).
                filter(b -> EMPTY_ROW != b).collect(Collectors.toList());
        return newBLocks.isEmpty() ? EMPTY_ROW : new CombinedRowValueBlock<>(newBLocks);
    }

    @Override
    public IRowValueBlock<Value, Sim> filterBySimulation(Predicate<Sim> simulationIdPredicate) {
        List<IRowValueBlock<Value, Sim>> newBlocks = blocks.stream().
                map(block -> block.filterBySimulation(simulationIdPredicate)).collect(Collectors.toList());
        return blocks.isEmpty() ? EMPTY_ROW : new CombinedRowValueBlock<>(newBlocks);
    }

    @Override
    public IRowValueBlock<Value, Sim> composeHorizontally(List<IRowValueBlock<Value, Sim>> other) {
        List<IRowValueBlock<Value, Sim>> newBlocks = Stream.concat(blocks.stream(), other.stream()).
                filter(b -> EMPTY_ROW != b).collect(Collectors.toList());

        return new CombinedRowValueBlock<>(newBlocks);
    }

    @Override
    public IRowValueBlock<Value, Sim> composeHorizontally(IRowValueBlock<Value, Sim> other) {
        if (EMPTY_ROW == other) {
            return this;
        }

        return new CombinedRowValueBlock<>(ImmutableList.<IRowValueBlock<Value, Sim>>builder().
                addAll(blocks).add(other).build());
    }

    private class Visitable implements IVisitableRowValue<Value, Sim> {
        int current = 0;
        IVisitableRowValue<Value, Sim> currentVisitable;

        @Override
        public boolean tryConsume(BiConsumer<Value, Sim> consumer) {
            if (current < blocks.size()) {
                do {
                    if (currentVisitable == null) {
                        currentVisitable = blocks.get(current).asVisitable();
                    }

                    if (currentVisitable.tryConsume(consumer)) {
                        return true;
                    } else {
                        current++;
                        currentVisitable = null;
                    }
                } while (current < blocks.size());
            }

            return false;
        }
    }
}
