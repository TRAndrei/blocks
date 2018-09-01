package com.rtb.blocks.api.builder;

import com.rtb.blocks.api.row.IRowValueBlock;

public class RowValueBuilder<Value, Sim> implements IBlockBuilders.IRowValueBuilder<Value, Sim> {

    @Override
    public IBlockBuilders.IRowValueBuilder<Value, Sim> setValue(Value value, Sim sim) {
        return null;
    }

    @Override
    public IRowValueBlock<Value, Sim> build() {
        return null;
    }
}
