package com.rtb.blocks.api.row;

import com.rtb.blocks.api.row.visitor.IVisitableRow;

import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;

import static com.rtb.blocks.api.row.visitor.EmptyVisitableRow.EMPTY;

public class EmptyRowBlock implements IRowBlock {
    public static final IRowBlock EMPTY_ROW = new EmptyRowBlock();
    private static final long serialVersionUID = -491165442124015525L;

    private EmptyRowBlock() {

    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void accept(DoubleConsumer consumer) {

    }

    @Override
    public IRowBlock map(DoubleUnaryOperator mapper) {
        return EMPTY_ROW;
    }

    @Override
    public <Sim> IVisitableRow<Sim> getVisitableRow(List<Sim> simulations) {
        return EMPTY;
    }

    @Override
    public IRowBlock composeHorizontally(List<IRowBlock> blocks) {
        List<IRowBlock> newBlocks = blocks.stream().filter(b -> EMPTY_ROW != b).collect(Collectors.toList());

        return newBlocks.isEmpty() ? EMPTY_ROW : new CombinedRowBlock(newBlocks);
    }

    @Override
    public IRowBlock composeHorizontally(IRowBlock other) {
        return other;
    }
}
