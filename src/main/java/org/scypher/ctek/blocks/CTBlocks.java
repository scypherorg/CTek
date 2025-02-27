package org.scypher.ctek.blocks;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.scypher.ctek.CTek;
import org.scypher.ctek.PS.base.PSComponent;
import org.scypher.ctek.blocks.base.PSCBEntity;
import org.scypher.ctek.blocks.base.PSCBlock;

public class CTBlocks {
    public static final RegistryKey<ItemGroup> ITEMGROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), new Identifier(CTek.MOD_ID, "blocks"));
    public static final ItemGroup ITEMGROUP = InitItemGroup();
    public static BlockEntityType<PSCBEntity> PSCBE_ASSIGNER;
    //Blocks
    public static final Block INSULATED_COPPER_CABLE = Register(new PSCB_InsulatedCopperCable(AbstractBlock.Settings.create()), "insulated_copper_cable", true);
    public static final Block CREATIVE_ENERGY_SOURCE = Register(new PSCB_CreativeEnergySource(AbstractBlock.Settings.create()), "creative_energy_source", true);

    public static final Block DEBUG_CABLE = Register(new PSCB_DebugCable(AbstractBlock.Settings.create()), "debug_cable", true);

    public static Block Register(Block block, String id, boolean registerItem)
    {
        Identifier identifier = new Identifier(CTek.MOD_ID, id);
        if(registerItem)
        {
            BlockItem item = Registry.register(Registries.ITEM, identifier, new BlockItem(block, new Item.Settings()));
            ItemGroupEvents.modifyEntriesEvent(ITEMGROUP_KEY).register(fabricItemGroupEntries -> {
                fabricItemGroupEntries.add(item);
            });
        }
        Block registeredBlock = Registry.register(Registries.BLOCK, identifier, block);
        if(block instanceof PSCBlock pscBlock)
        {
            CTek.LOGGER.info("Registering PSC Entity " + pscBlock.getClass().getSimpleName());
            PSCBE_ASSIGNER = RegisterBlockEntity(id, BlockEntityType.Builder.create(PSCBEntity::new, registeredBlock).build(null));
            pscBlock.PSCBE_ref = PSCBE_ASSIGNER;
            PSComponent psc = pscBlock.createPSC();
            PSComponent.RegisterClass(psc.getClass().getSimpleName(), psc);
        }
        return registeredBlock;
    }
    public static <T extends BlockEntityType<?>> T RegisterBlockEntity(String path, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(CTek.MOD_ID, path), blockEntityType);
    }

    public static ItemGroup InitItemGroup(){
        ItemGroup ig = FabricItemGroup.builder()
                .icon(() -> new ItemStack(CTBlocks.CREATIVE_ENERGY_SOURCE))
                .displayName(Text.translatable("itemgroup.ctek.blocks"))
                .build();
        Registry.register(Registries.ITEM_GROUP, ITEMGROUP_KEY, ig);
        return ig;
    }
    public static void Initialize()
    {
    }
}
