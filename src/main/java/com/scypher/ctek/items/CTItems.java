package com.scypher.ctek.items;

import com.scypher.ctek.CTek;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CTItems {
    public static final RegistryKey<ItemGroup> ITEMGROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), new Identifier(CTek.MOD_ID, "items"));
    public static final ItemGroup ITEMGROUP = InitItemGroup();
    //Items
    public static final Item DEBUG_TOOL = Register(new CTI_DebugTool(new FabricItemSettings()), "debug_tool");

    public static Item Register(Item item, String id)
    {
        Item registeredItem = Registry.register(Registries.ITEM, new Identifier(CTek.MOD_ID, id), item);
        ItemGroupEvents.modifyEntriesEvent(ITEMGROUP_KEY).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(registeredItem);
        });
        return registeredItem;
    }
    public static ItemGroup InitItemGroup(){
        ItemGroup ig = FabricItemGroup.builder()
                .icon(() -> new ItemStack(DEBUG_TOOL))
                .displayName(Text.translatable("itemgroup.ctek.items"))
                .build();
        Registry.register(Registries.ITEM_GROUP, ITEMGROUP_KEY, ig);
        return ig;
    }
    public static void Initialize()
    {
    }
}
