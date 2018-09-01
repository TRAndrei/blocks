package com.rtb.blocks.api.column;

import com.rtb.blocks.api.column.visitor.IColumnVisitor.IColumnMajorVisitor;
import com.rtb.blocks.api.column.visitor.IColumnVisitor.IRowMajorVisitor;
import com.rtb.blocks.api.row.IRowBlock;

import java.util.function.Function;
import java.util.function.Predicate;

public interface IColumnBlock<Row, Sim> extends IBaseColumnBlock<Row, IRowBlock, Sim, IColumnBlock<Row, Sim>> {
    void accept(IColumnMajorVisitor<Row, Sim> visitor);

    void accept(IRowMajorVisitor<Row, Sim> visitor);

    <R> IColumnBlock<R, Sim> convertRows(Function<Row, R> mapper);

    <State> IColumnBlock<Row, Sim> convertValues(Predicate<Row> rowFilter,
                                                 Function<Row, State> rowStateBuilder, DoubleMapper<State, Row> mapper);

    @FunctionalInterface
    interface DoubleMapper<State, Row> {
        double map(State state, Row row, double value);
    }
}
