package com.rtb.blocks.api.row;

import java.io.Serializable;

public interface IBaseRowBlock<Block extends IBaseRowBlock<Block>> extends IHorizontallyComposable<Block>,
        Serializable {

    int getSize();

    boolean isDelegate();
}
