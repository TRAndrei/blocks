package com.rtb.blocks.api.row;

import com.rtb.blocks.api.row.visitor.IVisitableValueRow;

import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public interface IRowValueBlock<Value> extends IBaseRowBlock<IRowValueBlock<Value>> {
    <V> IRowValueBlock<V> convertValues(Function<Value, V> mapper);

    IRowBlock toRowBlock(ToDoubleFunction<Value> mapper);

    <Sim> IVisitableValueRow<Value, Sim> getVisitableRow(List<Sim> simulations);
}
