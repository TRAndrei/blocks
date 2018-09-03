package com.rtb.blocks.api.row;

import com.rtb.blocks.api.row.visitor.IVisitableRow;

import java.io.Serializable;
import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleUnaryOperator;

public interface IRowBlock extends IHorizontallyComposable<IRowBlock>, Serializable {
    int getSize();

    void accept(DoubleConsumer consumer);

    IRowBlock map(DoubleUnaryOperator mapper);

    <Sim> IVisitableRow<Sim> getVisitableRow(List<Sim> simulations);
}
