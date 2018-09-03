package com.rtb.blocks.api.column;

import com.google.common.collect.ImmutableList;
import com.rtb.blocks.api.column.visitor.IColumnValueVisitor.IColumnMajorVisitor;
import com.rtb.blocks.api.column.visitor.IColumnValueVisitor.IRowMajorVisitor;
import com.rtb.blocks.api.row.IRowBlock;
import com.rtb.blocks.api.row.IRowValueBlock;
import com.rtb.blocks.api.row.visitor.IVisitableValueRow;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.column.EmptyColumnValueBlock.EMPTY_COLUMN;

public class ColumnValueBlock<Row, Value, Sim> implements IColumnValueBlock<Row, Value, Sim> {
    private final Map<Row, IRowValueBlock<Value>> rowsMap;
    private final List<Sim> simulations;

    public ColumnValueBlock(Map<Row, IRowValueBlock<Value>> rowsMap, List<Sim> simulations) {
        this.rowsMap = rowsMap;
        this.simulations = simulations;
    }

    @Override
    public void accept(IColumnMajorVisitor<Row, Value, Sim> visitor) {
        List<Row> rows = rowsMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
        List<IVisitableValueRow<Value, Sim>> visitableRows =
                rowsMap.entrySet().stream().map(e -> getVisitableRowBlock(e.getKey())).collect(Collectors.toList());

        for (int simIdx = 0; simIdx < simulations.size(); simIdx++) {
            Sim simulation = simulations.get(simIdx);

            visitor.onSimulationStart(simulation);

            for (int rowIdx = 0; rowIdx < rows.size(); rowIdx++) {
                Row row = rows.get(rowIdx);
                IVisitableValueRow<Value, Sim> visitableRow = visitableRows.get(rowIdx);

                visitableRow.tryConsume((v, s) -> visitor.visit(v, row, s));
            }

            visitor.onSimulationEnd(simulation);
        }
    }

    @Override
    public void accept(IRowMajorVisitor<Row, Value, Sim> visitor) {
        for (Map.Entry<Row, IRowValueBlock<Value>> entry : rowsMap.entrySet()) {
            Row row = entry.getKey();
            IRowValueBlock<Value> rowValueBlock = entry.getValue();
            IVisitableValueRow<Value, Sim> visitableValueRow = rowValueBlock.getVisitableRow(simulations);

            visitor.onRowStart(row);
            visitableValueRow.consumeRemaining((v, s) -> visitor.visit(v, row, s));
            visitor.onRowEnd(row);
        }
    }

    @Override
    public <R> IColumnValueBlock<R, Value, Sim> convertRows(Predicate<Row> rowFilter, Function<Row, R> mapper) {
        Map<R, IRowValueBlock<Value>> newRowsMap =
                rowsMap.entrySet().stream().filter(e -> rowFilter.test(e.getKey())).
                        collect(Collectors.toMap(e -> mapper.apply(e.getKey()), Map.Entry::getValue, (e1, e2) -> e1,
                                () -> new LinkedHashMap<>(rowsMap.size())));

        return newRowsMap.isEmpty() ? EMPTY_COLUMN : new ColumnValueBlock<>(newRowsMap, simulations);
    }

    @Override
    public <State, V> IColumnValueBlock<Row, V, Sim> convertValues(Predicate<Row> rowFilter,
                                                                   Function<Row, State> rowStateBuilder,
                                                                   IRowValueConvertor<State, Row, Value, V> convertor) {

        Map<Row, IRowValueBlock<V>> newRowsMap = new LinkedHashMap<>(rowsMap.size());

        for (Map.Entry<Row, IRowValueBlock<Value>> entry : rowsMap.entrySet()) {
            Row row = entry.getKey();

            if (rowFilter.test(row)) {
                IRowValueBlock<Value> rowValueBlock = entry.getValue();
                State rowState = rowStateBuilder.apply(row);
                IRowValueBlock<V> newRowValueBlock = rowValueBlock.
                        convertValues(v -> convertor.convert(rowState, row, v));

                newRowsMap.put(row, newRowValueBlock);
            }
        }

        return newRowsMap.isEmpty() ? EMPTY_COLUMN : new ColumnValueBlock<>(newRowsMap, simulations);
    }

    @Override
    public <State> IColumnBlock<Row, Sim> toColumnBlock(Predicate<Row> rowFilter, Function<Row, State> rowStateBuilder,
                                                        IRowConvertor<State, Row, Value> mapper) {
        Map<Row, IRowBlock> newRowsMap = new LinkedHashMap<>(rowsMap.size());

        for (Map.Entry<Row, IRowValueBlock<Value>> entry : rowsMap.entrySet()) {
            Row row = entry.getKey();

            if (rowFilter.test(row)) {
                IRowValueBlock<Value> rowValueBlock = entry.getValue();
                State rowState = rowStateBuilder.apply(row);
                IRowBlock newRowValueBlock = rowValueBlock.toRowBlock(v -> mapper.convert(rowState, row, v));

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
        List<Sim> newSimulations = simulations.stream().filter(simulationIdPredicate).collect(Collectors.toList());

        return newSimulations.isEmpty() ? EMPTY_COLUMN : new ColumnValueBlock<>(rowsMap, simulations);
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> filterByRow(Predicate<Row> rowPredicate) {
        Map<Row, IRowValueBlock<Value>> newRowsMap =
                rowsMap.entrySet().stream().filter(e -> rowPredicate.test(e.getKey())).collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return newRowsMap.isEmpty() ? EMPTY_COLUMN : new ColumnValueBlock<>(newRowsMap, simulations);
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> getDenseBlock() {
        return this;
    }

    @Override
    public IRowValueBlock<Value> getRowBlock(Row row) {
        return rowsMap.get(row);
    }

    @Override
    public IVisitableValueRow<Value, Sim> getVisitableRowBlock(Row row) {
        return rowsMap.get(row).getVisitableRow(simulations);
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeVertically(List<IColumnValueBlock<Row, Value, Sim>> other) {
        int rowSize = getRowCount() + other.stream().mapToInt(IColumnValueBlock::getRowCount).sum();
        Map<Row, IRowValueBlock<Value>> newRowMap = new LinkedHashMap<>(rowSize);

        newRowMap.putAll(rowsMap);
        for (int idx = 0; idx < other.size(); idx++) {
            IColumnValueBlock<Row, Value, Sim> otherBlock = other.get(idx);

            otherBlock.getRows().forEachOrdered(r -> newRowMap.put(r, otherBlock.getRowBlock(r)));
        }

        return new ColumnValueBlock<>(newRowMap, simulations);
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeVertically(IColumnValueBlock<Row, Value, Sim> other) {
        int rowSize = getRowCount() + other.getRowCount();
        Map<Row, IRowValueBlock<Value>> newRowMap = new LinkedHashMap<>(rowSize);

        newRowMap.putAll(rowsMap);
        other.getRows().forEachOrdered(r -> newRowMap.put(r, other.getRowBlock(r)));

        return new ColumnValueBlock<>(newRowMap, simulations);
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeHorizontally(List<IColumnValueBlock<Row, Value, Sim>> other) {
        List<IColumnValueBlock<Row, Value, Sim>> newBlocks =
                Stream.concat(Stream.of(this), other.stream()).filter(b -> EMPTY_COLUMN != b).
                collect(Collectors.toList());

        return new CombinedColumnValueBlock<>(newBlocks);
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeHorizontally(IColumnValueBlock<Row, Value, Sim> other) {
        if (EMPTY_COLUMN == other) {
            return this;
        }

        return new CombinedColumnValueBlock<>(ImmutableList.of(this, other));
    }
}
