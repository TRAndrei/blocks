package com.rtb.blocks.api.row;

import com.rtb.blocks.api.row.visitor.IVisitableRow;

import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

public interface IRowBlock<Sim> extends IBaseRowBlock<IRowBlock<Sim>, Sim> {
    IVisitableRow<Sim> asVisitable();

    void accept(ObjDoubleConsumer<Sim> consumer);

    IRowBlock<Sim> map(DoubleUnaryOperator mapper);

    <R> R collect(Supplier<R> supplier, ObjectDoubleFunction<R, Sim> accumulator);

    interface ObjectDoubleFunction<State, Sim> {
        State apply(State state, double value, Sim simulation);
    }
}
