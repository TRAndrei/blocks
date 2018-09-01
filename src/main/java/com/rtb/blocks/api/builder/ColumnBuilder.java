package com.rtb.blocks.api.builder;

import com.rtb.blocks.api.column.IColumnBlock;

import java.util.List;
import java.util.function.DoubleFunction;

public class ColumnBuilder<Row, Sim> implements IBlockBuilders.IColumnBuilder<Row, Sim> {
    private final List<Sim> simulations;
    private final List<Row> rows;
    private final DoubleFunction<Row> defaultRowValue;

    public ColumnBuilder(List<Sim> simulations, List<Row> rows, DoubleFunction<Row> defaultRowValue) {
        this.simulations = simulations;
        this.rows = rows;
        this.defaultRowValue = defaultRowValue;
    }

    @Override
    public IBlockBuilders.IRowBuilder<Sim> getRowBuilder(Row row) {
        return null;
    }

    @Override
    public IColumnBlock<Row, Sim> build() {
        return null;
    }
}
