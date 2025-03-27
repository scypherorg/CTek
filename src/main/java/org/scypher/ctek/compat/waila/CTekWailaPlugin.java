package org.scypher.ctek.compat.waila;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.scypher.ctek.CTek;
import org.scypher.ctek.blocks.base.PSCBEntity;
import org.scypher.ctek.blocks.base.PSCBlock;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class CTekWailaPlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(CTekWailaProvider.INSTANCE, PSCBEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CTekWailaProvider.INSTANCE, PSCBlock.class);
    }
}

