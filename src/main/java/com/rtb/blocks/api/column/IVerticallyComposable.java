package com.rtb.blocks.api.column;

import java.util.List;

public interface IVerticallyComposable<Block extends IVerticallyComposable<Block>> {
    Block composeVerically(List<Block> blocks);

    Block composeVertically(Block other);
}
