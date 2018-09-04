package com.rtb.blocks.api.row;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.rtb.blocks.api.row.visitor.IVisitableRow;

import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.EmptyRowBlock.EMPTY_ROW;

public class CombinedRowBlock implements IRowBlock {
    private static final long serialVersionUID = -6563412556291079221L;
    private final List<IRowBlock> blocks;
    private final int[] startIndices;
    private boolean isDelegate;

    public CombinedRowBlock(List<IRowBlock> blocks) {
        this.blocks = blocks;
        this.startIndices = new int[blocks.size()];

        int currentIdx = 0;
        for (int idx = 0; idx < blocks.size(); idx++) {
            startIndices[idx] = currentIdx;
            IRowBlock block = blocks.get(idx);
            currentIdx += block.getSize();
            isDelegate |= block.isDelegate();
        }
    }

    @Override
    public int getSize() {
        return blocks.stream().mapToInt(IRowBlock::getSize).sum();
    }

    @Override
    public boolean isDelegate() {
        return isDelegate;
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

        @Override
        public boolean hasValueForSimulation(int simulationIndex) {
            if (!isDelegate) {
                return true;
            }

            int blockIndex = Arrays.binarySearch(startIndices, simulationIndex);
            blockIndex = blockIndex >= 0 ? blockIndex : -(blockIndex + 1);
            int endSimulationIdx = blockIndex < blocks.size() - 1 ? startIndices[blockIndex + 1] : simulations.size();
            return blocks.get(blockIndex).getVisitableRow(simulations.subList(startIndices[blockIndex],
                    endSimulationIdx)).hasValueForSimulation(simulationIndex);
        }
    }
}
