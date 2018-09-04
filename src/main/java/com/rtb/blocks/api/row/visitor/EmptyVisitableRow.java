package com.rtb.blocks.api.row.visitor;

import java.util.function.ObjDoubleConsumer;

public class EmptyVisitableRow<Sim> implements IVisitableRow<Sim> {
    public static final IVisitableRow EMPTY = new EmptyVisitableRow();

    private EmptyVisitableRow() {
        //
    }

    @Override
    public boolean tryConsume(ObjDoubleConsumer<Sim> consumer) {
        return false;
    }

    @Override
    public void consumeRemaining(ObjDoubleConsumer<Sim> consumer) {

    }

    @Override
    public boolean hasValueForSimulation(int simulationIndex) {
        return false;
    }
}
