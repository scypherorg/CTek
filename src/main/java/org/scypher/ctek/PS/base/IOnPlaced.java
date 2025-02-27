package org.scypher.ctek.PS.base;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IOnPlaced {
    void OnPlaced(World world, BlockPos pos);
}
