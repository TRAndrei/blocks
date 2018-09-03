package com.rtb.blocks.api.row;

import com.rtb.blocks.api.row.visitor.IVisitableValueRow;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import static com.rtb.blocks.api.row.visitor.EmptyVisitableValueRow.EMPTY;

public class EmptyRowValueBlock<Value> implements IRowValueBlock<Value> {
    public static final IRowValueBlock EMPTY_ROW = new EmptyRowValueBlock();
    private static final long serialVersionUID = 823524966029353222L;

    private EmptyRowValueBlock() {
        //
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void accept(Consumer<Value> consumer) {

    }

    @Override
    public <V> IRowValueBlock<V> convertValues(Function<Value, V> mapper) {
        return EMPTY_ROW;
    }

    @Override
    public IRowBlock toRowBlock(ToDoubleFunction<Value> mapper) {
        return EmptyRowBlock.EMPTY_ROW;
    }

    @Override
    public <Sim> IVisitableValueRow<Value, Sim> getVisitableRow(List<Sim> simulations) {
        return EMPTY;
    }

    @Override
    public IRowValueBlock<Value> composeHorizontally(List<IRowValueBlock<Value>> blocks) {
        List<IRowValueBlock<Value>> newBlocks = blocks.stream().filter(b -> EMPTY_ROW != b).
                collect(Collectors.toList());

        return newBlocks.isEmpty() ? EMPTY_ROW : new CombinedRowValueBlock<>(newBlocks);
    }

    @Override
    public IRowValueBlock<Value> composeHorizontally(IRowValueBlock<Value> other) {
        return other;
    }
}
