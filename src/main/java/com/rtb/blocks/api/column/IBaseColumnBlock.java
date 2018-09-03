package com.rtb.blocks.api.column;

import com.rtb.blocks.api.row.IHorizontallyComposable;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface IBaseColumnBlock<Row, RowBlock, VisitableRowBlock, Sim, Block extends IBaseColumnBlock<Row, RowBlock,
        VisitableRowBlock, Sim, Block>>
        extends IHorizontallyComposable<Block>, IVerticallyComposable<Block> {

    Stream<Sim> getSimulationIds();

    int getSimulationCount();

    Stream<Row> getRows();

    int getRowCount();

    Block filterBySimulation(Predicate<Sim> simulationIdPredicate);

    Block filterByRow(Predicate<Row> rowPredicate);

    Block getDenseBlock();

    RowBlock getRowBlock(Row row);

    VisitableRowBlock getVisitableRowBlock(Row row);
}
