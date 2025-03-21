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
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if(world.isClient)
            return;
        UpdateIEConnections(world, pos, state);
    }
    @Override
    public void onNeighborPSCBUpdate(World world, BlockPos pos, BlockState state) {
        UpdateIEConnections(world, pos, state);
    }

    void UpdateIEConnections(World world, BlockPos pos, BlockState state)
    {
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
