package com.scypher.ctek.blocks;

import com.scypher.ctek.CTek;
import com.scypher.ctek.PS.PSC_Cable;
import com.scypher.ctek.PS.base.PSComponent;
import com.scypher.ctek.blocks.base.PSCBEntity;
import com.scypher.ctek.blocks.base.PSCBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.world.World;

public class PSCB_DebugCable extends PSCBlock {
    public static final IntProperty NETVIS = IntProperty.of("netvis", 0, 4);
    public PSCB_DebugCable(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(NETVIS, 4));
    }
    @Override
    public PSComponent createPSC()
    {
        return new PSC_Cable();
    }
    //Coloring
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NETVIS);
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        // Make sure to check world.isClient if you only want to tick only on serverside.
        CTek.LOGGER.info("--- REGISTERING TICKER ---");
        CTek.LOGGER.info("validated: {}", checkType(type, PSCBE_ref, PSCBEntity::tick)!=null);
        CTek.LOGGER.info("validated against ASSIGNER: {}", checkType(type, CTBlocks.PSCBE_ASSIGNER, PSCBEntity::tick)!=null);
        return checkType(type, PSCBE_ref, PSCBEntity::tick);
    }
}
