package com.rtb.blocks.api.row;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.rtb.blocks.api.row.visitor.IVisitableRow;

import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.EmptyRowBlock.EMPTY_ROW;

public class DenseRowBlock implements IRowBlock {
    private static final long serialVersionUID = -5779075886311091969L;
    private final double[] values;

    public DenseRowBlock(double[] values) {
        this.values = values;
    }

    @Override
    public int getSize() {
        return values.length;
    }

    @Override
    public boolean isDelegate() {
        return false;
    }

    @Override
    public IRowBlock map(DoubleUnaryOperator mapper) {
        double[] newValues = new double[values.length];
        for (int idx = 0; idx < values.length; idx++) {
            newValues[idx] = mapper.applyAsDouble(values[idx]);
        }

        return new DenseRowBlock(newValues);
    }

    @Override
    public <Sim> IVisitableRow<Sim> getVisitableRow(List<Sim> simulations) {
        Preconditions.checkArgument(values.length == simulations.size());
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
        private int idx;

        private VisitableRow(List<Sim> simulations) {
            this.simulations = simulations;
        }

        @Override
        public boolean tryConsume(ObjDoubleConsumer<Sim> consumer) {
            if (idx < simulations.size()) {
                consumer.accept(simulations.get(idx), values[idx++]);
                return true;
            }

            return false;
        }

        @Override
        public void consumeRemaining(ObjDoubleConsumer<Sim> consumer) {
            for (; idx < simulations.size(); idx++) {
                consumer.accept(simulations.get(idx), values[idx]);
            }
        }

        @Override
        public boolean hasValueForSimulation(int simulationIndex) {
            return true;
        }
    }
}
