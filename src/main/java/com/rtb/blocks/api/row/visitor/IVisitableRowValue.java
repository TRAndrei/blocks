package com.rtb.blocks.api.row.visitor;

import java.util.function.BiConsumer;

public interface IVisitableRowValue<Value, Sim> {
    boolean tryConsume(BiConsumer<Value, Sim> consumer);
}
