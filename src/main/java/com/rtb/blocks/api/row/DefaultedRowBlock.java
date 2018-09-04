package com.rtb.blocks.api.row;

import com.google.common.collect.ImmutableList;
import com.rtb.blocks.api.row.visitor.IVisitableRow;

import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.EmptyRowBlock.EMPTY_ROW;

public class DefaultedRowBlock implements IRowBlock {
    private final double defaultValue;
    private final double value;
    private final int valueIdx;
    private final int valueCount;

    public DefaultedRowBlock(double defaultValue, double value, int valueIdx, int valueCount) {
        this.defaultValue = defaultValue;
        this.value = value;
        this.valueIdx = valueIdx;
        this.valueCount = valueCount;
    }

    @Override
    public int getSize() {
        return valueCount;
    }

    @Override
    public boolean isDelegate() {
        return true;
    }

    @Override
    public IRowBlock map(DoubleUnaryOperator mapper) {
        return new DefaultedRowBlock(mapper.applyAsDouble(defaultValue), mapper.applyAsDouble(value), valueIdx,
                valueCount);
    }

    @Override
    public <Sim> IVisitableRow<Sim> getVisitableRow(List<Sim> simulations) {
        return new VisitableRow<>(simulations);
    }

    @Override
    public IRowBlock composeHorizontally(List<IRowBlock> other) {
        List<IRowBlock> newBlocks = Stream.concat(Stream.of(this), other.stream()).filter(b -> EMPTY_ROW != b).
                collect(Collectors.toList());

        return new CombinedRowBlock(newBlocks);
    }

    @Override
    public IRowBlock composeHorizontally(IRowBlock other) {
        if (EMPTY_ROW == other) {
            return this;
        }

        return new CombinedRowBlock(ImmutableList.of(this, other));
    }

    private class VisitableRow<Sim> implements IVisitableRow<Sim> {
        private final List<Sim> simulations;
        private int idx = 0;

        private VisitableRow(List<Sim> simulations) {
            this.simulations = simulations;
        }

        @Override
        public boolean tryConsume(ObjDoubleConsumer<Sim> consumer) {
            if (idx < simulations.size()) {
                consumer.accept(simulations.get(idx), idx == valueIdx ? value : defaultValue);
                idx++;
                return true;
            }

            return false;
        }

        @Override
        public void consumeRemaining(ObjDoubleConsumer<Sim> consumer) {
            for (; idx < simulations.size(); idx++) {
                consumer.accept(simulations.get(idx), idx == valueIdx ? value : defaultValue);
            }
        }

        @Override
        public boolean hasValueForSimulation(int simulationIndex) {
            return valueIdx == simulationIndex;
        }
    }
}
