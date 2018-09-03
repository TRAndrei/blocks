package com.rtb.blocks.api.column;

import com.rtb.blocks.api.column.visitor.IColumnValueVisitor;
import com.rtb.blocks.api.row.IRowValueBlock;
import com.rtb.blocks.api.row.visitor.IVisitableValueRow;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CombinedColumnValueBlock<Row, Value, Sim> implements IColumnValueBlock<Row, Value, Sim> {
    private final List<IColumnValueBlock<Row, Value, Sim>> blocks;

    public CombinedColumnValueBlock(List<IColumnValueBlock<Row, Value, Sim>> blocks) {
        this.blocks = blocks;
    }

    @Override
    public void accept(IColumnValueVisitor.IColumnMajorVisitor<Row, Value, Sim> visitor) {

    }

    @Override
    public void accept(IColumnValueVisitor.IRowMajorVisitor<Row, Value, Sim> visitor) {

    }

    @Override
    public <R> IColumnValueBlock<R, Value, Sim> convertRows(Predicate<Row> rowFilter, Function<Row, R> mapper) {
        return null;
    }

    @Override
    public <State, V> IColumnValueBlock<Row, V, Sim> convertValues(Predicate<Row> rowFilter, Function<Row, State> rowStateBuilder, IRowValueConvertor<State, Row, Value, V> convertor) {
        return null;
    }

    @Override
    public <State> IColumnBlock<Row, Sim> toColumnBlock(Predicate<Row> rowFilter, Function<Row, State> rowStateBuilder, IRowConvertor<State, Row, Value> mapper) {
        return null;
    }

    @Override
    public Stream<Sim> getSimulationIds() {
        return null;
    }

    @Override
    public int getSimulationCount() {
        return 0;
    }

    @Override
    public Stream<Row> getRows() {
        return null;
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> filterBySimulation(Predicate<Sim> simulationIdPredicate) {
        return null;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> filterByRow(Predicate<Row> rowPredicate) {
        return null;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> getDenseBlock() {
        return null;
    }

    @Override
    public IRowValueBlock<Value> getRowBlock(Row row) {
        return null;
    }

    @Override
    public IVisitableValueRow<Value, Sim> getVisitableRowBlock(Row row) {
        return null;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeVertically(List<IColumnValueBlock<Row, Value, Sim>> iColumnValueBlocks) {
        return null;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeVertically(IColumnValueBlock<Row, Value, Sim> other) {
        return null;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeHorizontally(List<IColumnValueBlock<Row, Value, Sim>> iColumnValueBlocks) {
        return null;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeHorizontally(IColumnValueBlock<Row, Value, Sim> other) {
        return null;
    }
}
