package com.rtb.blocks.api.row;

import com.google.common.base.Preconditions;
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

public class CombinedRowValueBlock<Value> implements IRowValueBlock<Value> {
    private static final long serialVersionUID = -992236671173392831L;
    private final List<IRowValueBlock<Value>> blocks;

    public CombinedRowValueBlock(List<IRowValueBlock<Value>> blocks) {
        this.blocks = blocks;
    }

    @Override
    public int getSize() {
        return blocks.stream().mapToInt(IRowValueBlock::getSize).sum();
    }

    @Override
    public void accept(Consumer<Value> consumer) {
        for (int idx = 0; idx < blocks.size(); idx++) {
            blocks.get(idx).accept(consumer);
        }
    }

    @Override
    public <V> IRowValueBlock<V> convertValues(Function<Value, V> mapper) {
        return new CombinedRowValueBlock<>(blocks.stream().map(block -> block.convertValues(mapper)).collect(Collectors.toList()));
    }

    @Override
    public IRowBlock toRowBlock(ToDoubleFunction<Value> mapper) {
        return new CombinedRowBlock(blocks.stream().map(block -> block.toRowBlock(mapper)).collect(Collectors.toList()));
    }

    @Override
    public <Sim> IVisitableValueRow<Value, Sim> getVisitableRow(List<Sim> simulations) {
        Preconditions.checkArgument(getSize() == simulations.size());
        return new VisitableRow<>(simulations);
    }

    @Override
    public IRowValueBlock<Value> composeHorizontally(List<IRowValueBlock<Value>> other) {
        List<IRowValueBlock<Value>> newBlocks = Stream.concat(blocks.stream(), other.stream()).
                filter(b -> EMPTY_ROW != b).collect(Collectors.toList());

        return new CombinedRowValueBlock<>(newBlocks);
    }

    @Override
    public IRowValueBlock<Value> composeHorizontally(IRowValueBlock<Value> other) {
        if (EMPTY_ROW == other) {
            return this;
        }

        return new CombinedRowValueBlock<>(ImmutableList.<IRowValueBlock<Value>>builder().
                addAll(blocks).add(other).build());
    }

    private class VisitableRow<Sim> implements IVisitableValueRow<Value, Sim> {
        private final List<Sim> simulations;
        private int simIdx = 0;
        private int blockIdx = 0;
        private int startSimIdx = 0;
        private IVisitableValueRow<Value, Sim> currentVisitableBlock = null;

        private VisitableRow(List<Sim> simulations) {
            this.simulations = simulations;
        }

        @Override
        public boolean tryConsume(BiConsumer<Value, Sim> consumer) {
            if (blockIdx < blocks.size() && simIdx < simulations.size()) {
                do {
                    if (currentVisitableBlock == null) {
                        IRowValueBlock<Value> rowBlock = blocks.get(blockIdx);
                        startSimIdx = simIdx;
                        currentVisitableBlock = rowBlock.getVisitableRow(simulations.subList(simIdx,
                                simIdx + rowBlock.getSize()));
                    }

                    if (currentVisitableBlock.tryConsume(consumer)) {
                        simIdx++;
                        return true;
                    } else {
                        blockIdx++;
                        currentVisitableBlock = null;
                    }
                } while (blockIdx < blocks.size());
            }

            return false;
        }

        @Override
        public void consumeRemaining(BiConsumer<Value, Sim> consumer) {
            if (currentVisitableBlock != null) {
                currentVisitableBlock.consumeRemaining(consumer);
                blockIdx++;
            }

            for (; blockIdx < blocks.size(); blockIdx++) {
                IRowValueBlock<Value> rowBlock = blocks.get(blockIdx);
                int blockSize = rowBlock.getSize();
                IVisitableValueRow<Value, Sim> visitableRow = rowBlock.getVisitableRow(
                        simulations.subList(startSimIdx, startSimIdx + blockSize));
                visitableRow.consumeRemaining(consumer);
                startSimIdx += blockSize;
            }
        }
    }
}
