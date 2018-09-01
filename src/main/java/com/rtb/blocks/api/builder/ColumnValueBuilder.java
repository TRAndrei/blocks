package com.rtb.blocks.api.builder;

import com.rtb.blocks.api.column.IColumnValueBlock;

import java.util.List;
import java.util.function.Function;

public class ColumnValueBuilder<Row, Value, Sim> implements IBlockBuilders.IColumnValueBuilder<Row, Value, Sim> {
    private final List<Sim> simulations;
    private final List<Row> rows;
    private final Function<Row, Value> defaultRowValue;

    public ColumnValueBuilder(List<Sim> simulations, List<Row> rows, Function<Row, Value> defaultRowValue) {
        this.simulations = simulations;
        this.rows = rows;
        this.defaultRowValue = defaultRowValue;
    }

    @Override
    public IBlockBuilders.IRowValueBuilder<Value, Sim> getRowBuilder(Row row) {
        return null;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> build() {
        return null;
    }
}
