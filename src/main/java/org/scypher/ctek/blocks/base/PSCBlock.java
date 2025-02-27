package org.scypher.ctek.blocks.base;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;
import org.scypher.ctek.CTek;
import org.scypher.ctek.PS.base.PSComponent;
import org.scypher.ctek.PS.base.PSManager;


public class PSCBlock extends BlockWithEntity {
    public BlockEntityType<PSCBEntity> PSCBE_ref;
    public PSCBlock(Settings settings) {
        super(settings);
    }
    public PSComponent createPSC()
    {
        throw new NotImplementedException("Override createPSC in your Child!");
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(world.isClient)
            return;
        PSCBEntity entity = (PSCBEntity) world.getBlockEntity(pos);
        if(entity == null)
            CTek.LOGGER.error("onPlaced: PSCBEntity is null!");
        else
        {
            entity.ComponentID = PSManager.RegisterComponent(createPSC(), world, pos);
            entity.markDirty();
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }
    @Override
    public void onBreak(World world,
                        BlockPos pos,
                        BlockState state,
                        PlayerEntity player)
    {
        super.onBreak(world, pos, state, player);
        if(world.isClient())
            return;
        PSCBEntity entity = (PSCBEntity) world.getBlockEntity(pos);
        if(entity == null)
            CTek.LOGGER.error("onBreak: PSCBEntity is null!");
        else {
            try {
                PSManager.DestroyComponent(entity.ComponentID);
            }catch(Exception e)
            {CTek.LOGGER.error("onBreak: {} - {}", e.getStackTrace()[0], e.getMessage());}
        }
    }
    @Override
    public @Nullable net.minecraft.block.entity.BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PSCBEntity(pos, state);
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}