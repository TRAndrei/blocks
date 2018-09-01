package com.rtb.blocks.api.column;

import com.google.common.collect.Maps;
import com.rtb.blocks.api.column.visitor.IColumnVisitor;
import com.rtb.blocks.api.row.IRowBlock;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.column.EmptyColumnBlock.EMPTY_COLUMN;

public class ColumnBlock<Row, Sim> implements IColumnBlock<Row, Sim> {
    private final Map<Row, IRowBlock<Sim>> rowsMap;
    private final List<Sim> simulations;

    public ColumnBlock(Map<Row, IRowBlock<Sim>> rowsMap, List<Sim> simulations) {
        this.rowsMap = rowsMap;
        this.simulations = simulations;
    }

    @Override
    public void accept(IColumnVisitor<Row, Sim> visitor) {
        for (Map.Entry<Row, IRowBlock<Sim>> entry : rowsMap.entrySet()) {
            Row row = entry.getKey();
            IRowBlock<Sim> rowBlock = entry.getValue();

            rowBlock.accept((s, v) -> visitor.visit(v, row, s));
        }
    }

    @Override
    public void accept(IColumnVisitor.IRowMajorVisitor<Row, Sim> visitor) {
        for (Map.Entry<Row, IRowBlock<Sim>> entry : rowsMap.entrySet()) {
            Row row = entry.getKey();
            IRowBlock<Sim> rowBlock = entry.getValue();

            visitor.onRowStart(row);
            rowBlock.accept((s, v) -> visitor.visit(v, row, s));
            visitor.onRowEnd(row);
        }
    }

    @Override
    public <R> IColumnBlock<R, Sim> convertRows(Function<Row, R> mapper) {
        Map<R, IRowBlock<Sim>> newRowsMap =
                rowsMap.entrySet().stream().collect(Collectors.toMap(e -> mapper.apply(e.getKey()),
                        Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return new ColumnBlock<>(newRowsMap, simulations);
    }

    @Override
    public <State> IColumnBlock<Row, Sim> convertValues(Predicate<Row> rowFilter, Function<Row, State> rowStateBuilder, DoubleMapper<State, Row> mapper) {
        Map<Row, IRowBlock<Sim>> newRowsMap = Maps.newLinkedHashMap();

        for(Map.Entry<Row, IRowBlock<Sim>> entry : rowsMap.entrySet()) {
            Row row = entry.getKey();

            if (rowFilter.test(row)) {
                IRowBlock<Sim> rowBlock = entry.getValue();
                State rowState = rowStateBuilder.apply(row);
                IRowBlock<Sim> newRowBlock = rowBlock.map(v -> mapper.map(rowState, row, v));

                newRowsMap.put(row, newRowBlock);
            }
        }

        return newRowsMap.isEmpty() ? EMPTY_COLUMN : new ColumnBlock<>(newRowsMap, simulations);
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
    public IColumnBlock<Row, Sim> filterBySimulation(Predicate<Sim> simulationIdPredicate) {
        Map<Row, IRowBlock<Sim>> newRowsMap =
                rowsMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().filterBySimulation(simulationIdPredicate), (e1, e2) -> e1, LinkedHashMap::new));

        return new ColumnBlock<>(newRowsMap, simulations);
    }

    @Override
    public IColumnBlock<Row, Sim> filterByRow(Predicate<Row> rowPredicate) {
        Map<Row, IRowBlock<Sim>> newRowsMap =
                rowsMap.entrySet().stream().filter(e -> rowPredicate.test(e.getKey())).collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return newRowsMap.isEmpty() ? EMPTY_COLUMN : new ColumnBlock<>(newRowsMap, simulations);
    }

    @Override
    public IColumnBlock<Row, Sim> getDenseBlock() {
        return this;
    }

    @Override
    public IRowBlock getRowBlock(Row row) {
        return rowsMap.get(row);
    }

    @Override
    public IColumnBlock<Row, Sim> composeVerically(List<IColumnBlock<Row, Sim>> iColumnBlocks) {
        return null;
    }

    @Override
    public IColumnBlock<Row, Sim> composeVertically(IColumnBlock<Row, Sim> other) {
        return null;
    }

    @Override
    public IColumnBlock<Row, Sim> composeHorizontally(List<IColumnBlock<Row, Sim>> iColumnBlocks) {
        return null;
    }

    @Override
    public IColumnBlock<Row, Sim> composeHorizontally(IColumnBlock<Row, Sim> other) {
        return null;
    }
}
