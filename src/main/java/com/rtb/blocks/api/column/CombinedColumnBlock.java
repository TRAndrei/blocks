package com.rtb.blocks.api.column;

import com.rtb.blocks.api.column.visitor.IColumnVisitor.IColumnMajorVisitor;
import com.rtb.blocks.api.column.visitor.IColumnVisitor.IRowMajorVisitor;
import com.rtb.blocks.api.row.IRowBlock;
import com.rtb.blocks.api.row.visitor.IVisitableRow;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CombinedColumnBlock<Row, Sim> implements IColumnBlock<Row, Sim> {
    private final List<IColumnBlock<Row, Sim>> blocks;

    public CombinedColumnBlock(List<IColumnBlock<Row, Sim>> blocks) {
        this.blocks = blocks;
    }

    @Override
    public void accept(IColumnMajorVisitor<Row, Sim> visitor) {

    }

    @Override
    public void accept(IRowMajorVisitor<Row, Sim> visitor) {

    }

    @Override
    public <R> IColumnBlock<R, Sim> convertRows(Predicate<Row> rowFilter, Function<Row, R> mapper) {
        return null;
    }

    @Override
    public <State> IColumnBlock<Row, Sim> convertValues(Predicate<Row> rowFilter, Function<Row, State> rowStateBuilder, DoubleMapper<State, Row> mapper) {
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
    public IColumnBlock<Row, Sim> filterBySimulation(Predicate<Sim> simulationIdPredicate) {
        return null;
    }

    @Override
    public IColumnBlock<Row, Sim> filterByRow(Predicate<Row> rowPredicate) {
        return null;
    }

    @Override
    public IColumnBlock<Row, Sim> getDenseBlock() {
        return null;
    }

    @Override
    public IRowBlock getRowBlock(Row row) {
        return null;
    }

    @Override
    public IVisitableRow<Sim> getVisitableRowBlock(Row row) {
        return null;
    }

    @Override
    public IColumnBlock<Row, Sim> composeVertically(List<IColumnBlock<Row, Sim>> iColumnBlocks) {
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
