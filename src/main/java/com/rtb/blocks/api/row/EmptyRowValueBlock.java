package com.rtb.blocks.api.row;

import com.rtb.blocks.api.row.visitor.IVisitableRowValue;

import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rtb.blocks.api.row.EmptyRowValueBlock.Visitable.EMPTY_VISITABLE;

public class EmptyRowValueBlock<Value, Sim> implements IRowValueBlock<Value, Sim> {
    private static final long serialVersionUID = 823524966029353222L;
    public static final IRowValueBlock EMPTY_ROW = new EmptyRowValueBlock();

    private EmptyRowValueBlock() {
        //
    }

    @Override
    public IVisitableRowValue<Value, Sim> asVisitable() {
        return EMPTY_VISITABLE;
    }

    @Override
    public void accept(BiConsumer<Value, Sim> consumer) {

    }

    @Override
    public <V> IRowValueBlock<V, Sim> convertValues(Function<Value, V> mapper) {
        return EMPTY_ROW;
    }

    @Override
    public IRowBlock<Sim> toRowBlock(ToDoubleFunction<Value> mapper) {
        return EmptyRowBlock.EMPTY_ROW;
    }

    @Override
    public <R> R collect(Supplier<R> supplier, IRowConsumer<R, Value, Sim> accumulator) {
        return supplier.get();
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
    public IRowValueBlock<Value, Sim> getDenseBlock() {
        return this;
    }

    @Override
    public IRowValueBlock<Value, Sim> filterBySimulation(Predicate<Sim> simulationIdPredicate) {
        return this;
    }

    @Override
    public IRowValueBlock<Value, Sim> composeHorizontally(List<IRowValueBlock<Value, Sim>> blocks) {
        List<IRowValueBlock<Value, Sim>> newBlocks = blocks.stream().filter(b -> EMPTY_ROW != b).
                collect(Collectors.toList());

        return newBlocks.isEmpty() ? EMPTY_ROW : new CombinedRowValueBlock<>(newBlocks);
    }

    @Override
    public IRowValueBlock<Value, Sim> composeHorizontally(IRowValueBlock<Value, Sim> other) {
        return other;
    }

    static final class Visitable<Value, Sim> implements IVisitableRowValue<Value, Sim> {
        static final IVisitableRowValue EMPTY_VISITABLE = new Visitable();

        private Visitable() {
            //
        }

        @Override
        public boolean tryConsume(BiConsumer<Value, Sim> consumer) {
            return false;
        }
    }
}
