package com.rtb.blocks.api.column;

import com.google.common.collect.Maps;
import com.rtb.blocks.api.column.visitor.IColumnValueVisitor;
import com.rtb.blocks.api.row.IRowBlock;
import com.rtb.blocks.api.row.IRowValueBlock;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.column.EmptyColumnValueBlock.EMPTY_COLUMN;

public class ColumnValueBlock<Row, Value, Sim> implements IColumnValueBlock<Row, Value, Sim> {
    private final Map<Row, IRowValueBlock<Value, Sim>> rowsMap;
    private final List<Sim> simulations;

    public ColumnValueBlock(Map<Row, IRowValueBlock<Value, Sim>> rowsMap, List<Sim> simulations) {
        this.rowsMap = rowsMap;
        this.simulations = simulations;
    }

    @Override
    public void accept(IColumnValueVisitor<Row, Value, Sim> visitor) {
        for (Map.Entry<Row, IRowValueBlock<Value, Sim>> entry : rowsMap.entrySet()) {
            Row row = entry.getKey();
            IRowValueBlock<Value, Sim> rowValueBlock = entry.getValue();

            rowValueBlock.accept((v, s) -> visitor.visit(v, row, s));
        }
    }

    @Override
    public void accept(IColumnValueVisitor.IRowMajorVisitor<Row, Value, Sim> visitor) {
        for (Map.Entry<Row, IRowValueBlock<Value, Sim>> entry : rowsMap.entrySet()) {
            Row row = entry.getKey();
            IRowValueBlock<Value, Sim> rowValueBlock = entry.getValue();

            visitor.onRowStart(row);
            rowValueBlock.accept((v, s) -> visitor.visit(v, row, s));
            visitor.onRowEnd(row);
        }
    }

    @Override
    public <R> IColumnValueBlock<R, Value, Sim> convertRows(Function<Row, R> mapper) {
        Map<R, IRowValueBlock<Value, Sim>> newRowsMap =
                rowsMap.entrySet().stream().collect(Collectors.toMap(e -> mapper.apply(e.getKey()),
                        Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return new ColumnValueBlock<>(newRowsMap, simulations);
    }

    @Override
    public <State, V> IColumnValueBlock<Row, V, Sim> convertValues(Predicate<Row> rowFilter,
                      Function<Row, State> rowStateBuilder, IRowValueConvertor<State, Row, Value, V> convertor) {
        Map<Row, IRowValueBlock<V, Sim>> newRowsMap = Maps.newLinkedHashMap();

        for(Map.Entry<Row, IRowValueBlock<Value, Sim>> entry : rowsMap.entrySet()) {
            Row row = entry.getKey();

            if (rowFilter.test(row)) {
                IRowValueBlock<Value, Sim> rowValueBlock = entry.getValue();
                State rowState = rowStateBuilder.apply(row);
                IRowValueBlock<V, Sim> newRowValueBlock = rowValueBlock.
                        convertValues(v -> convertor.convert(rowState, row, v));

                newRowsMap.put(row, newRowValueBlock);
            }
        }

        return newRowsMap.isEmpty() ? EMPTY_COLUMN : new ColumnValueBlock<>(newRowsMap, simulations);
    }

    @Override
    public <State> IColumnBlock<Row, Sim> toColumnBlock(Predicate<Row> rowFilter, Function<Row, State> rowStateBuilder,
                                          IRowConvertor<State, Row, Value> mapper) {
        Map<Row, IRowBlock<Sim>> newRowsMap = Maps.newLinkedHashMap();

        for(Map.Entry<Row, IRowValueBlock<Value, Sim>> entry : rowsMap.entrySet()) {
            Row row = entry.getKey();

            if (rowFilter.test(row)) {
                IRowValueBlock<Value, Sim> rowValueBlock = entry.getValue();
                State rowState = rowStateBuilder.apply(row);
                IRowBlock<Sim> newRowValueBlock = rowValueBlock.toRowBlock(v -> mapper.convert(rowState, row, v));

                newRowsMap.put(row, newRowValueBlock);
            }
        }

        return newRowsMap.isEmpty() ? EmptyColumnBlock.EMPTY_COLUMN : new ColumnBlock<>(newRowsMap, simulations);
    }

    @Override
    public Stream<Sim> getSimulationIds() {
        return simulations.stream();
    }

    @Override
    public int getSimulationCount() {
        return simulations.size();
    }

    @Override
    public Stream<Row> getRows() {
        return rowsMap.keySet().stream();
    }

    @Override
    public int getRowCount() {
        return rowsMap.size();
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> filterBySimulation(Predicate<Sim> simulationIdPredicate) {
        Map<Row, IRowValueBlock<Value, Sim>> newRowsMap =
                rowsMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().filterBySimulation(simulationIdPredicate), (e1, e2) -> e1, LinkedHashMap::new));

        return new ColumnValueBlock<>(newRowsMap, simulations);
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> filterByRow(Predicate<Row> rowPredicate) {
        Map<Row, IRowValueBlock<Value, Sim>> newRowsMap =
                rowsMap.entrySet().stream().filter(e -> rowPredicate.test(e.getKey())).collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return newRowsMap.isEmpty() ? EMPTY_COLUMN : new ColumnValueBlock<>(newRowsMap, simulations);
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> getDenseBlock() {
        return this;
    }

    @Override
    public IRowValueBlock<Value, Sim> getRowBlock(Row row) {
        return rowsMap.get(row);
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeVerically(List<IColumnValueBlock<Row, Value, Sim>> other) {
        return null;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeVertically(IColumnValueBlock<Row, Value, Sim> other) {
        return null;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeHorizontally(List<IColumnValueBlock<Row, Value, Sim>> other) {
        return null;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeHorizontally(IColumnValueBlock<Row, Value, Sim> other) {
        return null;
    }
}