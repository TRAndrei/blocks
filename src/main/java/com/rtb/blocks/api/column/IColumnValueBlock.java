package com.rtb.blocks.api.column;

import com.rtb.blocks.api.column.visitor.IColumnValueVisitor.IColumnMajorVisitor;
import com.rtb.blocks.api.column.visitor.IColumnValueVisitor.IRowMajorVisitor;
import com.rtb.blocks.api.row.IRowValueBlock;

import java.util.function.Function;
import java.util.function.Predicate;

public interface IColumnValueBlock<Row, Value, Sim> extends IBaseColumnBlock<Row, IRowValueBlock<Value, Sim>, Sim,
        IColumnValueBlock<Row, Value, Sim>> {
    void accept(IColumnMajorVisitor<Row, Value, Sim> visitor);

    void accept(IRowMajorVisitor<Row, Value, Sim> visitor);

    <R> IColumnValueBlock<R, Value, Sim> convertRows(Function<Row, R> mapper);

    <State, V> IColumnValueBlock<Row, V, Sim> convertValues(Predicate<Row> rowFilter,
                                                            Function<Row, State> rowStateBuilder, IRowValueConvertor<State, Row, Value, V> convertor);

    <State> IColumnBlock<Row, Sim> toColumnBlock(Predicate<Row> rowFilter, Function<Row, State> rowStateBuilder,
                                                 IRowConvertor<State, Row, Value> mapper);

    interface IRowValueConvertor<S, R, T, U> {
        U convert(S state, R row, T value);
    }

    interface IRowConvertor<S, R, T> {
        double convert(S state, R row, T value);
    }
}
