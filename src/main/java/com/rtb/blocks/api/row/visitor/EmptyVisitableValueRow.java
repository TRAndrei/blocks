package com.rtb.blocks.api.row.visitor;

import com.sun.rowset.internal.Row;

import java.util.function.BiConsumer;

public class EmptyVisitableValueRow<Value, Sim> implements IVisitableValueRow<Value, Row> {
    public static final IVisitableValueRow EMPTY = new EmptyVisitableValueRow();

    private EmptyVisitableValueRow() {
        //
    }

    @Override
    public boolean tryConsume(BiConsumer<Value, Row> consumer) {
        return false;
    }

    @Override
    public void consumeRemaining(BiConsumer<Value, Row> consumer) {

    }

    @Override
    public boolean hasValueForSimulation(int simulationIndex) {
        return false;
    }
}
