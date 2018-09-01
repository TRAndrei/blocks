package com.rtb.blocks.api.row;

import java.util.function.*;

public interface IRowBlock<Sim> extends IBaseRowBlock<IRowBlock<Sim>, Sim> {
    void accept(ObjDoubleConsumer<Sim> consumer);

    IRowBlock<Sim> map(DoubleUnaryOperator mapper);

    <R> R collect(Supplier<R> supplier, ObjectDoubleFunction<R, Sim> accumulator);

    interface ObjectDoubleFunction<State, Sim> {
        State apply(State state, double value, Sim simulation);
    }
}
