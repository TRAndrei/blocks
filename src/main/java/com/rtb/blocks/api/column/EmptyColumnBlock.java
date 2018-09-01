package com.rtb.blocks.api.column;

import com.rtb.blocks.api.column.visitor.IColumnVisitor;
import com.rtb.blocks.api.row.IRowBlock;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.EmptyRowBlock.EMPTY_ROW;

public class EmptyColumnBlock<Row, Sim> implements IColumnBlock<Row, Sim> {
    public static final IColumnBlock EMPTY_COLUMN = new EmptyColumnBlock();

    private EmptyColumnBlock() {
        //
    }

    @Override
    public void accept(IColumnVisitor<Row, Sim> visitor) {

    }

    @Override
    public void accept(IColumnVisitor.IRowMajorVisitor<Row, Sim> visitor) {

    }

    @Override
    public <R> IColumnBlock<R, Sim> convertRows(Function<Row, R> mapper) {
        return EMPTY_COLUMN;
    }

    @Override
    public <State> IColumnBlock<Row, Sim> convertValues(Predicate<Row> rowFilter, Function<Row, State> rowStateBuilder, DoubleMapper<State, Row> mapper) {
        return EMPTY_COLUMN;
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
    public IColumnBlock<Row, Sim> filterBySimulation(Predicate<Sim> simulationIdPredicate) {
        return EMPTY_COLUMN;
    }

    @Override
    public IColumnBlock<Row, Sim> filterByRow(Predicate<Row> rowPredicate) {
        return EMPTY_COLUMN;
    }

    @Override
    public IColumnBlock<Row, Sim> getDenseBlock() {
        return EMPTY_COLUMN;
    }

    @Override
    public IRowBlock getRowBlock(Row row) {
        return EMPTY_ROW;
    }

    @Override
    public IColumnBlock<Row, Sim> composeVerically(List<IColumnBlock<Row, Sim>> other) {
        return null;
    }

    @Override
    public IColumnBlock<Row, Sim> composeVertically(IColumnBlock<Row, Sim> other) {
        return null;
    }

    @Override
    public IColumnBlock<Row, Sim> composeHorizontally(List<IColumnBlock<Row, Sim>> other) {
        return null;
    }

    @Override
    public IColumnBlock<Row, Sim> composeHorizontally(IColumnBlock<Row, Sim> other) {
        return null;
    }
}
