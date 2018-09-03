package com.rtb.blocks.api.row.visitor;

import java.util.function.BiConsumer;

public interface IVisitableValueRow<Value, Sim> {
    boolean tryConsume(BiConsumer<Value, Sim> consumer);

    void consumeRemaining(BiConsumer<Value, Sim> consumer);
}
