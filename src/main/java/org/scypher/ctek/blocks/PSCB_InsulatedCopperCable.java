package org.scypher.ctek.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.scypher.ctek.CTek;
import org.scypher.ctek.PS.Energy.IEnergyConnector;
import org.scypher.ctek.PS.PSC_Cable;
import org.scypher.ctek.PS.base.PSComponent;
import org.scypher.ctek.PS.base.PSManager;
import org.scypher.ctek.blocks.base.IOnNeighborPSCBUpdate;
import org.scypher.ctek.blocks.base.PSCBEntity;
import org.scypher.ctek.blocks.base.PSCBlock;

public class PSCB_InsulatedCopperCable extends PSCBlock implements IOnNeighborPSCBUpdate {

    public static final BooleanProperty UP = BooleanProperty.of("iecup");
    public static final BooleanProperty DOWN = BooleanProperty.of("iecdown");
    public static final BooleanProperty NORTH = BooleanProperty.of("iecnorth");
    public static final BooleanProperty EAST = BooleanProperty.of("ieceast");
    public static final BooleanProperty SOUTH = BooleanProperty.of("iecsouth");
    public static final BooleanProperty WEST = BooleanProperty.of("iecwest");
    @Override
    public PSComponent createPSC()
    {
        return new PSC_Cable();
    }
    public PSCB_InsulatedCopperCable(Settings settings)
    {
        super(settings);
        setDefaultState(getDefaultState().with(UP, false).with(DOWN, false).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

 /*   @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if(world.isClient)
            return;
        BlockState otherState = world.getBlockState(pos.up());
        if(otherState.contains(DOWN))
            world.setBlockState(pos.up(), otherState.with(DOWN, false));
        otherState = world.getBlockState(pos.down());
        if(otherState.contains(UP))
            world.setBlockState(pos.down(), otherState.with(UP, false));
        otherState = world.getBlockState(pos.north());
        if(otherState.contains(SOUTH))
            world.setBlockState(pos.north(), otherState.with(SOUTH, false));
        otherState = world.getBlockState(pos.south());
        if(otherState.contains(NORTH))
            world.setBlockState(pos.south(), otherState.with(NORTH, false));
        otherState = world.getBlockState(pos.west());
        if(otherState.contains(EAST))
            world.setBlockState(pos.west(), otherState.with(EAST, false));
        otherState = world.getBlockState(pos.east());
        if(otherState.contains(WEST))
            world.setBlockState(pos.east(), otherState.with(WEST, false));
    }*/
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if(world.isClient)
            return;
        UpdateIEConnections(world, pos, state);
    }
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved) {
        super.neighborUpdate(state, world, pos, neighborBlock, neighborPos, moved);
        if(world.isClient)
            return;
        CTek.LOGGER.info("Block update @ {}", pos);
        UpdateIEConnections(world, pos, state);
    }

    @Override
    public void onNeighborPSCBUpdate(World world, BlockPos pos, BlockState state) {
        UpdateIEConnections(world, pos, state);
    }

    void UpdateIEConnections(World world, BlockPos pos, BlockState state)
    {
        if(world.getBlockEntity(pos.up()) instanceof PSCBEntity pscbe)
            CTek.LOGGER.info("ABOVE: {} / CHECKS: {}/{}", world.getBlockState(pos.up()).getBlock().getName(), true, PSManager.getComponent(pscbe.ComponentID));
        else
            CTek.LOGGER.info("ABOVE: {} / CHECKS: {}/?", world.getBlockState(pos.up()).getBlock().getName(), false);
        world.setBlockState(pos, state
                .with(UP, world.getBlockEntity(pos.up()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector)
                .with(DOWN, world.getBlockEntity(pos.down()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector)
                .with(NORTH, world.getBlockEntity(pos.north()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector)
                .with(SOUTH, world.getBlockEntity(pos.south()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector)
                .with(WEST, world.getBlockEntity(pos.west()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector)
                .with(EAST, world.getBlockEntity(pos.east()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector)
        );
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(.25f, .25f, .25,
                .75f, .75f, .75f);
    }
}
