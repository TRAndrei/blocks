package com.rtb.blocks.api.column;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.rtb.blocks.api.column.visitor.IColumnVisitor.IColumnMajorVisitor;
import com.rtb.blocks.api.column.visitor.IColumnVisitor.IRowMajorVisitor;
import com.rtb.blocks.api.row.IBaseRowBlock;
import com.rtb.blocks.api.row.IRowBlock;
import com.rtb.blocks.api.row.visitor.IVisitableRow;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.column.EmptyColumnBlock.EMPTY_COLUMN;

public class ColumnBlock<Row, Sim> implements IColumnBlock<Row, Sim> {
    private final Map<Row, IRowBlock> rowsMap;
    private final List<Sim> simulations;

    public ColumnBlock(Map<Row, IRowBlock> rowsMap, List<Sim> simulations) {
        this.rowsMap = rowsMap;
        this.simulations = simulations;
    }

    @Override
    public void accept(IColumnMajorVisitor<Row, Sim> visitor) {
        List<Row> rows = rowsMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
        List<IVisitableRow<Sim>> visitableRows =
                rowsMap.entrySet().stream().map(e -> getVisitableRowBlock(e.getKey())).collect(Collectors.toList());

        for (int simIdx = 0; simIdx < simulations.size(); simIdx++) {
            Sim simulation = simulations.get(simIdx);

            visitor.onSimulationStart(simulation);

            for (int rowIdx = 0; rowIdx < rows.size(); rowIdx++) {
                Row row = rows.get(rowIdx);
                IVisitableRow<Sim> visitableRow = visitableRows.get(rowIdx);

                visitableRow.tryConsume((s, v) -> visitor.visit(v, row, s));
            }

            visitor.onSimulationEnd(simulation);
        }
    }

    @Override
    public void accept(IRowMajorVisitor<Row, Sim> visitor) {
        for (Map.Entry<Row, IRowBlock> entry : rowsMap.entrySet()) {
            Row row = entry.getKey();
            IRowBlock rowBlock = entry.getValue();
            IVisitableRow<Sim> visitableRow = rowBlock.getVisitableRow(simulations);

            visitor.onRowStart(row);
            visitableRow.consumeRemaining((s, v) -> visitor.visit(v, row, s));
            visitor.onRowEnd(row);
        }
    }

    @Override
    public <R> IColumnBlock<R, Sim> convertRows(Predicate<Row> rowFilter, Function<Row, R> mapper) {
        Map<R, IRowBlock> newRowsMap =
                rowsMap.entrySet().stream().filter(e -> rowFilter.test(e.getKey())).
                        collect(Collectors.toMap(e -> mapper.apply(e.getKey()), Map.Entry::getValue, (e1, e2) -> e1,
                                () -> new LinkedHashMap<>(rowsMap.size())));

        return new ColumnBlock<>(newRowsMap, simulations);
    }

    @Override
    public <State> IColumnBlock<Row, Sim> convertValues(Predicate<Row> rowFilter, Function<Row, State> rowStateBuilder,
                                                        DoubleMapper<State, Row> mapper) {
        Map<Row, IRowBlock> newRowsMap = new LinkedHashMap<>(rowsMap.size());

        for (Map.Entry<Row, IRowBlock> entry : rowsMap.entrySet()) {
            Row row = entry.getKey();

            if (rowFilter.test(row)) {
                IRowBlock rowBlock = entry.getValue();
                State rowState = rowStateBuilder.apply(row);
                IRowBlock newRowBlock = rowBlock.map(v -> mapper.map(rowState, row, v));

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
        List<Sim> newSimulations = simulations.stream().filter(simulationIdPredicate).collect(Collectors.toList());
        return newSimulations.isEmpty() ? EMPTY_COLUMN : new ColumnBlock<>(rowsMap, newSimulations);
    }

    @Override
    public IColumnBlock<Row, Sim> filterByRow(Predicate<Row> rowPredicate) {
        Map<Row, IRowBlock> newRowsMap =
                rowsMap.entrySet().stream().filter(e -> rowPredicate.test(e.getKey())).collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                                () -> new LinkedHashMap<>(rowsMap.size())));

        return newRowsMap.isEmpty() ? EMPTY_COLUMN : new ColumnBlock<>(newRowsMap, simulations);
    }

    @Override
    public IColumnBlock<Row, Sim> getDenseBlock() {
        List<IVisitableRow<Sim>> delegateBlocks =
                rowsMap.entrySet().stream().map(Map.Entry::getValue).filter(IBaseRowBlock::isDelegate).
                        map(e -> e.getVisitableRow(simulations)).collect(Collectors.toList());

        if (delegateBlocks.isEmpty()) {
            return this;
        }

        List<Sim> newSimulations = Lists.newArrayList();

        for (int simIdx = 0; simIdx < simulations.size(); simIdx++) {
            for (int rowIdx = 0; rowIdx < delegateBlocks.size(); rowIdx++) {
                if (delegateBlocks.get(rowIdx).hasValueForSimulation(simIdx)) {
                    newSimulations.add(simulations.get(simIdx));
                    break;
                }
            }
        }

        return new ColumnBlock<>(rowsMap, newSimulations);
    }

    @Override
    public IRowBlock getRowBlock(Row row) {
        return rowsMap.get(row);
    }

    @Override
    public IVisitableRow<Sim> getVisitableRowBlock(Row row) {
        return rowsMap.get(row).getVisitableRow(simulations);
    }

    @Override
    public IColumnBlock<Row, Sim> composeVertically(List<IColumnBlock<Row, Sim>> other) {
        int rowSize = getRowCount() + other.stream().mapToInt(IColumnBlock::getRowCount).sum();
        Map<Row, IRowBlock> newRowMap = new LinkedHashMap<>(rowSize);

        newRowMap.putAll(rowsMap);
        for (int idx = 0; idx < other.size(); idx++) {
            IColumnBlock<Row, Sim> otherBlock = other.get(idx);

            otherBlock.getRows().forEachOrdered(r -> newRowMap.put(r, otherBlock.getRowBlock(r)));
        }

        return new ColumnBlock<>(newRowMap, simulations);
    }

    @Override
    public IColumnBlock<Row, Sim> composeVertically(IColumnBlock<Row, Sim> other) {
        int rowSize = getRowCount() + other.getRowCount();
        Map<Row, IRowBlock> newRowMap = new LinkedHashMap<>(rowSize);

        newRowMap.putAll(rowsMap);
        other.getRows().forEachOrdered(r -> newRowMap.put(r, other.getRowBlock(r)));

        return new ColumnBlock<>(newRowMap, simulations);
    }

    @Override
    public IColumnBlock<Row, Sim> composeHorizontally(List<IColumnBlock<Row, Sim>> other) {
        List<IColumnBlock<Row, Sim>> newBlocks = Stream.concat(Stream.of(this), other.stream()).filter(b -> EMPTY_COLUMN != b).
                collect(Collectors.toList());

        return new CombinedColumnBlock<>(newBlocks);
    }

    @Override
    public IColumnBlock<Row, Sim> composeHorizontally(IColumnBlock<Row, Sim> other) {
        if (EMPTY_COLUMN == other) {
            return this;
        }

        return new CombinedColumnBlock<>(ImmutableList.of(this, other));
    }
}
