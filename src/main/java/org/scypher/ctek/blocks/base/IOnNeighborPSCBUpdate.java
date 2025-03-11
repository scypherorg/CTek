package org.scypher.ctek.blocks.base;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;

public interface IOnNeighborPSCBUpdate {
    public void onNeighborPSCBUpdate(World world, BlockPos pos, BlockState state);
}
