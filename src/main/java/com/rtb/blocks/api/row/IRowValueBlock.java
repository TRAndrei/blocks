package com.rtb.blocks.api.row;

import java.util.function.*;

public interface IRowValueBlock<Value, Sim> extends IBaseRowBlock<IRowValueBlock<Value, Sim>, Sim> {

    void accept(BiConsumer<Value, Sim> consumer);

    <V> IRowValueBlock<V, Sim> convertValues(Function<Value, V> mapper);

    IRowBlock<Sim> toRowBlock(ToDoubleFunction<Value> mapper);

    <R> R collect(Supplier<R> supplier, IRowConsumer<R, Value, Sim> accumulator);

    interface IRowConsumer<State, Value, Sim> {
        State consume(State state, Value value, Sim simulation);
    }
}
