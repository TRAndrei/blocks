package com.rtb.blocks.api.column;

import com.rtb.blocks.api.column.visitor.IColumnValueVisitor;
import com.rtb.blocks.api.row.visitor.IVisitableValueRow;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.visitor.EmptyVisitableValueRow.EMPTY;

public class EmptyColumnValueBlock<Row, Value, Sim> implements IColumnValueBlock<Row, Value, Sim> {
    public static final IColumnValueBlock EMPTY_COLUMN = new EmptyColumnValueBlock();

    private EmptyColumnValueBlock() {
        //
    }

    @Override
    public void accept(IColumnValueVisitor.IColumnMajorVisitor<Row, Value, Sim> visitor) {

    }

    @Override
    public void accept(IColumnValueVisitor.IRowMajorVisitor<Row, Value, Sim> visitor) {

    }

    @Override
    public <R> IColumnValueBlock<R, Value, Sim> convertRows(Predicate<Row> rowFilter, Function<Row, R> mapper) {
        return EMPTY_COLUMN;
    }

    @Override
    public <State, V> IColumnValueBlock<Row, V, Sim> convertValues(Function<Row, State> rowStateBuilder,
                                                                   IRowValueConvertor<State, Row, Value, V> convertor) {
        return EMPTY_COLUMN;
    }

    @Override
    public <State> IColumnBlock<Row, Sim> toColumnBlock(Function<Row, State> rowStateBuilder,
                                                        Function<Row, IRowConvertor<State, Row, Value>> mapper) {
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
    public IVisitableValueRow<Value, Sim> getRowBlock(Row row) {
        return EMPTY;
    }

    @Override
    public IColumnValueBlock<Row, Value, Sim> composeVertically(List<IColumnValueBlock<Row, Value, Sim>> other) {
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
