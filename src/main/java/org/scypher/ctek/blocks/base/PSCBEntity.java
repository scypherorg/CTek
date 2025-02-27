package org.scypher.ctek.blocks.base;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.scypher.ctek.CTek;
import org.scypher.ctek.PS.Energy.IEnergyConnector;
import org.scypher.ctek.PS.base.PSManager;
import org.scypher.ctek.blocks.CTBlocks;
import org.scypher.ctek.blocks.PSCB_DebugCable;

public class PSCBEntity extends BlockEntity{
    public int ComponentID;
    public PSCBEntity(BlockPos pos, BlockState state) {
        super(CTBlocks.PSCBE_ASSIGNER, pos, state);
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("CID", ComponentID);
        super.writeNbt(nbt);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        ComponentID = nbt.getInt("CID");
        super.readNbt(nbt);
    }
    public static void tick(World world, BlockPos pos, BlockState state, PSCBEntity pscbe) {
        if (world.isClient)
            return;
        IEnergyConnector con = (IEnergyConnector) PSManager.getComponent(pscbe.ComponentID);
        if(con == null)
        {
            CTek.LOGGER.error("Unable to find EnergyConnector for {}", pscbe.ComponentID);
            return;
        }
        if(con.getEnergyNetwork() == null)
        {
            CTek.LOGGER.error("Unable to find EnergyNetwork for {}", pscbe.ComponentID);
            return;
        }
        //    if(state.get(PSCB_DebugCable.NETVIS) != con.getEnergyNetwork().getID())
        world.setBlockState(pos, world.getBlockState(pos).with(PSCB_DebugCable.NETVIS, Math.min(con.getEnergyNetwork().getID(),4)), PSCBlock.NOTIFY_ALL);
    }
}
