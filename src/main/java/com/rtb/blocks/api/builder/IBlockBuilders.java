package com.rtb.blocks.api.builder;

import com.rtb.blocks.api.column.IColumnBlock;
import com.rtb.blocks.api.column.IColumnValueBlock;
import com.rtb.blocks.api.row.IRowBlock;
import com.rtb.blocks.api.row.IRowValueBlock;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.ToDoubleBiFunction;

public interface IBlockBuilders {
    <Row, Value, Sim> IColumnValueBlock<Row, Value, Sim> getColumnValueBlock(List<Sim> simulations, List<Row> rows,
                                                                             BiFunction<Row, Sim, Value> valueProvider);

    <Row, Value, Sim> IColumnValueBlock<Row, Value, Sim> getColumnValueBlock(List<Sim> simulations, List<Row> rows,
                                                                             Function<Row, List<Value>> valuesProvider);

    <Row, Value, Sim> IColumnValueBuilder<Row, Value, Sim> getValueBlockBuilder(List<Sim> simulations, List<Row> rows,
                                                                                Function<Row, Value> defaultRowValue);

    <Row, Sim> IColumnBlock<Row, Sim> getColumnBlock(List<Sim> simulations, List<Row> rows,
                                                     ToDoubleBiFunction<Row, Sim> valueProvider);

    <Row, Sim> IColumnBlock<Row, Sim> getColumnBlock(List<Sim> simulations, List<Row> rows,
                                                                             Function<Row, double[]> valuesProvider);

    <Row, Sim> IColumnBuilder<Row, Sim> getColumnBlockBuilder(List<Sim> simulations, List<Row> rows,
                                                                                DoubleFunction<Row> defaultRowValue);

    interface IColumnValueBuilder<Row, Value, Sim> {
        IRowValueBuilder<Value, Sim> getRowBuilder(Row row);

        IColumnValueBlock<Row, Value, Sim> build();
    }

    interface IColumnBuilder<Row, Sim> {
        IRowBuilder<Sim> getRowBuilder(Row row);

        IColumnBlock<Row, Sim> build();
    }

    interface IRowValueBuilder<Value, Sim> {
        IRowValueBuilder<Value, Sim> setValue(Value value, Sim sim);

        IRowValueBlock<Value> build();
    }

    interface IRowBuilder<Sim> {
        IRowBuilder<Sim> setValue(double value, Sim sim);

        IRowBlock build();
    }
}
