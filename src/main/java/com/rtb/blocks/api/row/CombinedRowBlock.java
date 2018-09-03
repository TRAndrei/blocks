package com.rtb.blocks.api.row;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.rtb.blocks.api.row.visitor.IVisitableRow;

import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.EmptyRowBlock.EMPTY_ROW;

public class CombinedRowBlock implements IRowBlock {
    private static final long serialVersionUID = -6563412556291079221L;
    private final List<IRowBlock> blocks;

    public CombinedRowBlock(List<IRowBlock> blocks) {
        this.blocks = blocks;
    }

    @Override
    public int getSize() {
        return blocks.stream().mapToInt(IRowBlock::getSize).sum();
    }

    @Override
    public void accept(DoubleConsumer consumer) {
        for (int idx = 0; idx < blocks.size(); idx++) {
            blocks.get(idx).accept(consumer);
        }
    }

    @Override
    public IRowBlock map(DoubleUnaryOperator mapper) {
        return new CombinedRowBlock(blocks.stream().map(block -> block.map(mapper)).collect(Collectors.toList()));
    }

    @Override
    public <Sim> IVisitableRow<Sim> getVisitableRow(List<Sim> simulations) {
        Preconditions.checkArgument(getSize() == simulations.size());
        return new VisitableRow<>(simulations);
    }

    @Override
    public IRowBlock composeHorizontally(List<IRowBlock> other) {
        List<IRowBlock> newBlocks = Stream.concat(blocks.stream(), other.stream()).filter(b -> EMPTY_ROW != b).
                collect(Collectors.toList());

        return new CombinedRowBlock(newBlocks);
    }

    @Override
    public IRowBlock composeHorizontally(IRowBlock other) {
        if (EMPTY_ROW == other) {
            return this;
        }

        return new CombinedRowBlock(ImmutableList.<IRowBlock>builder().addAll(blocks).add(other).build());
    }

    private class VisitableRow<Sim> implements IVisitableRow<Sim> {
        private final List<Sim> simulations;
        private int simIdx = 0;
        private int blockIdx = 0;
        private int startSimIdx = 0;
        private IVisitableRow<Sim> currentVisitableBlock = null;

        private VisitableRow(List<Sim> simulations) {
            this.simulations = simulations;
        }

        @Override
        public boolean tryConsume(ObjDoubleConsumer<Sim> consumer) {
            if (blockIdx < blocks.size() && simIdx < simulations.size()) {
                do {
                    if (currentVisitableBlock == null) {
                        IRowBlock rowBlock = blocks.get(blockIdx);
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
        public void consumeRemaining(ObjDoubleConsumer<Sim> consumer) {
            if (currentVisitableBlock != null) {
                currentVisitableBlock.consumeRemaining(consumer);
                blockIdx++;
            }

            for (; blockIdx < blocks.size(); blockIdx++) {
                IRowBlock rowBlock = blocks.get(blockIdx);
                int blockSize = rowBlock.getSize();
                IVisitableRow<Sim> visitableRow = rowBlock.getVisitableRow(
                        simulations.subList(startSimIdx, startSimIdx + blockSize));
                visitableRow.consumeRemaining(consumer);
                startSimIdx += blockSize;
            }
        }
    }
}
