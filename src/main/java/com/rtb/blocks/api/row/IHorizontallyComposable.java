package com.rtb.blocks.api.row;

import java.util.List;

public interface IHorizontallyComposable<Block extends IHorizontallyComposable<Block>> {
    Block composeHorizontally(List<Block> blocks);

    Block composeHorizontally(Block other);
}
