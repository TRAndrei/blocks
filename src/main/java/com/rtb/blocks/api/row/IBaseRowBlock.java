package com.rtb.blocks.api.row;

import java.io.Serializable;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface IBaseRowBlock<Block extends IBaseRowBlock<Block, Sim>, Sim> extends IHorizontallyComposable<Block>,
        Serializable {
    Stream<Sim> getSimulationIds();

    int getSimulationCount();

    Block getDenseBlock();

    Block filterBySimulation(Predicate<Sim> simulationIdPredicate);
}
