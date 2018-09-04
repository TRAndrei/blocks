package com.rtb.blocks.api.row;

import com.rtb.blocks.api.row.visitor.IVisitableRow;

import java.util.List;
import java.util.function.DoubleUnaryOperator;

public interface IRowBlock extends IBaseRowBlock<IRowBlock> {

    IRowBlock map(DoubleUnaryOperator mapper);

    <Sim> IVisitableRow<Sim> getVisitableRow(List<Sim> simulations);
}
