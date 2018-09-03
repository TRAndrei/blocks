package com.rtb.blocks.api.column;

import com.rtb.blocks.api.column.visitor.IColumnValueVisitor.IColumnMajorVisitor;
import com.rtb.blocks.api.column.visitor.IColumnValueVisitor.IRowMajorVisitor;
import com.rtb.blocks.api.row.EmptyRowValueBlock;
import com.rtb.blocks.api.row.IRowValueBlock;
import com.rtb.blocks.api.row.visitor.IVisitableValueRow;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.visitor.EmptyVisitableValueRow.EMPTY;

public class EmptyColumnValueBlock<Row, Value, Sim> implements IColumnValueBlock<Row, Value, Sim> {
    public static final IColumnValueBlock EMPTY_COLUMN = new EmptyColumnValueBlock();

    private EmptyColumnValueBlock() {
        //
    }

    @Override
    public void accept(IColumnMajorVisitor<Row, Value, Sim> visitor) {

    }

    @Override
    public void accept(IRowMajorVisitor<Row, Value, Sim> visitor) {

    }

    @Override
    public <R> IColumnValueBlock<R, Value, Sim> convertRows(Predicate<Row> rowFilter, Function<Row, R> mapper) {
        return EMPTY_COLUMN;
    }

    @Override
    public <State, V> IColumnValueBlock<Row, V, Sim> convertValues(Predicate<Row> rowFilter,
                                                                   Function<Row, State> rowStateBuilder,
                                                                   IRowValueConvertor<State, Row, Value, V> convertor) {
        return EMPTY_COLUMN;
    }

    @Override
    public <State> IColumnBlock<Row, Sim> toColumnBlock(Predicate<Row> rowFilter, Function<Row, State> rowStateBuilder,
                                                        IRowConvertor<State, Row, Value> mapper) {
        return EmptyColumnBlock.EMPTY_COLUMN;
    }

    @Override
    public Stream<Sim> getSimulationIds() {
        return Stream.empty();
    }

    @Override
    public int getSimulationCount() {
        return 0;
    }

    @Override
    public Stream<Row> getRows() {
        return Stream.empty();
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> filterBySimulation(Predicate<Sim> simulationIdPredicate) {
        return EMPTY_COLUMN;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> filterByRow(Predicate<Row> rowPredicate) {
        return EMPTY_COLUMN;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> getDenseBlock() {
        return EMPTY_COLUMN;
    }

    @Override
    public IRowValueBlock<Value> getRowBlock(Row row) {
        return EmptyRowValueBlock.EMPTY_ROW;
    }

    @Override
    public IVisitableValueRow<Value, Sim> getVisitableRowBlock(Row row) {
        return EMPTY;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeVertically(List<IColumnValueBlock<Row, Value, Sim>> other) {
        int rowSize = other.stream().mapToInt(IColumnValueBlock::getRowCount).sum();
        Map<Row, IRowValueBlock<Value>> newRowMap = new LinkedHashMap<>(rowSize);

        for (int idx = 0; idx < other.size(); idx++) {
            IColumnValueBlock<Row, Value, Sim> otherBlock = other.get(idx);

            otherBlock.getRows().forEachOrdered(r -> newRowMap.put(r, otherBlock.getRowBlock(r)));
        }

        return newRowMap.isEmpty() ? EMPTY_COLUMN : new ColumnValueBlock<>(newRowMap, other.get(0).getSimulationIds().collect(Collectors.toList()));
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeVertically(IColumnValueBlock<Row, Value, Sim> other) {
        return other;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeHorizontally(List<IColumnValueBlock<Row, Value, Sim>> other) {
        List<IColumnValueBlock<Row, Value, Sim>> newBlocks =
                other.stream().filter(b -> EMPTY_COLUMN != b).collect(Collectors.toList());

        return newBlocks.isEmpty() ? EMPTY_COLUMN : new CombinedColumnValueBlock<>(newBlocks);
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeHorizontally(IColumnValueBlock<Row, Value, Sim> other) {
        return other;
    }
}
