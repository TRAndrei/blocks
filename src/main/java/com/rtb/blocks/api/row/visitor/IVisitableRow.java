package com.rtb.blocks.api.row.visitor;

import java.util.function.ObjDoubleConsumer;

public interface IVisitableRow<Sim> {
    boolean tryConsume(ObjDoubleConsumer<Sim> consumer);
}
