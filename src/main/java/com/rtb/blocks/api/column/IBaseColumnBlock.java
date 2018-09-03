package com.rtb.blocks.api.column;

import com.rtb.blocks.api.row.IHorizontallyComposable;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface IBaseColumnBlock<Row, VisitableRowBlock, Sim, Block extends IBaseColumnBlock<Row, VisitableRowBlock, Sim, Block>>
        extends IHorizontallyComposable<Block>, IVerticallyComposable<Block> {

    Stream<Sim> getSimulationIds();

    int getSimulationCount();

    Stream<Row> getRows();

    int getRowCount();

    Block filterBySimulation(Predicate<Sim> simulationIdPredicate);

    Block filterByRow(Predicate<Row> rowPredicate);

    Block getDenseBlock();

    VisitableRowBlock getRowBlock(Row row);
}
