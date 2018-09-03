package com.rtb.blocks.api.row;

import com.rtb.blocks.api.row.visitor.IVisitableValueRow;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public interface IRowValueBlock<Value> extends IHorizontallyComposable<IRowValueBlock<Value>>, Serializable {
    int getSize();

    void accept(Consumer<Value> consumer);

    <V> IRowValueBlock<V> convertValues(Function<Value, V> mapper);

    IRowBlock toRowBlock(ToDoubleFunction<Value> mapper);

    <Sim> IVisitableValueRow<Value, Sim> getVisitableRow(List<Sim> simulations);
}
