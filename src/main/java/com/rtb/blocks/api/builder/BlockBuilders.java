package com.rtb.blocks.api.builder;

import com.rtb.blocks.api.column.ColumnBlock;
import com.rtb.blocks.api.column.ColumnValueBlock;
import com.rtb.blocks.api.column.IColumnBlock;
import com.rtb.blocks.api.column.IColumnValueBlock;
import com.rtb.blocks.api.row.DenseRowBlock;
import com.rtb.blocks.api.row.DenseRowValueBlock;
import com.rtb.blocks.api.row.IRowBlock;
import com.rtb.blocks.api.row.IRowValueBlock;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;

public class BlockBuilders implements IBlockBuilders {
    public static final IBlockBuilders BLOCK_BUILDERS = new BlockBuilders();

    private BlockBuilders() {

    }

    @Override
    public <Row, Value, Sim> IColumnValueBlock<Row, Value, Sim> getColumnValueBlock(List<Sim> simulations, List<Row> rows, BiFunction<Row, Sim, Value> valueProvider) {
        return getColumnValueBlock(simulations, rows,
                r -> simulations.stream().map(s -> valueProvider.apply(r, s)).collect(Collectors.toList()));
    }

    @Override
    public <Row, Value, Sim> IColumnValueBlock<Row, Value, Sim> getColumnValueBlock(List<Sim> simulations,
                                                                                    List<Row> rows, Function<Row, List<Value>> valuesProvider) {
        Map<Row, IRowValueBlock<Value>> rowsMap = rows.stream().collect(Collectors.toMap(Function.identity(),
                r -> new DenseRowValueBlock<>(valuesProvider.apply(r)), (e1, e2) -> e1, LinkedHashMap::new));

        return new ColumnValueBlock<>(rowsMap, simulations);
    }

    @Override
    public <Row, Value, Sim> IColumnValueBuilder<Row, Value, Sim> getValueBlockBuilder(List<Sim> simulations, List<Row> rows, Function<Row, Value> defaultRowValue) {
        return new ColumnValueBuilder<>(simulations, rows, defaultRowValue);
    }

    @Override
    public <Row, Sim> IColumnBlock<Row, Sim> getColumnBlock(List<Sim> simulations, List<Row> rows, ToDoubleBiFunction<Row, Sim> valueProvider) {
        return getColumnBlock(simulations, rows,
                r -> simulations.stream().mapToDouble(s -> valueProvider.applyAsDouble(r, s)).toArray());
    }

    @Override
    public <Row, Sim> IColumnBlock<Row, Sim> getColumnBlock(List<Sim> simulations, List<Row> rows, Function<Row, double[]> valuesProvider) {
        Map<Row, IRowBlock> rowsMap = rows.stream().collect(Collectors.toMap(Function.identity(),
                r -> new DenseRowBlock(valuesProvider.apply(r)), (e1, e2) -> e1, LinkedHashMap::new));

        return new ColumnBlock<>(rowsMap, simulations);
    }

    @Override
    public <Row, Sim> IColumnBuilder<Row, Sim> getColumnBlockBuilder(List<Sim> simulations, List<Row> rows, DoubleFunction<Row> defaultRowValue) {
        return new ColumnBuilder<>(simulations, rows, defaultRowValue);
    }
}
