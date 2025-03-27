package org.scypher.ctek.compat.waila;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import org.scypher.ctek.CTek;
import org.scypher.ctek.PS.Energy.IEnergyConnector;
import org.scypher.ctek.PS.Energy.IEnergyDrain;
import org.scypher.ctek.PS.Energy.IEnergySource;
import org.scypher.ctek.PS.base.PSManager;
import org.scypher.ctek.blocks.base.PSCBEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public enum CTekWailaProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {
        if (accessor.getServerData().contains("nid")) {
            tooltip.add(Text.of("Network ID: " + accessor.getServerData().getInt("nid")));
        }

        if (accessor.getServerData().contains("load")) {
            Text textString = Text.of("Load: " + accessor.getServerData().getString("load") + "EU/t");

            if (accessor.getServerData().contains("networkHasPower") && !accessor.getServerData().getBoolean("networkHasPower")) {
                IElementHelper elements = tooltip.getElementHelper();
                IElement icon = elements.item(new ItemStack(Items.BARRIER), 0.5f).size(new Vec2f(10, 10)).translate(new Vec2f(-1, -1));
                tooltip.add(icon);
                icon.message("Network Overloaded");

                tooltip.append(textString);
            }
            else tooltip.add(textString);
        }

        if (accessor.getServerData().contains("production"))
            tooltip.add(Text.of("Production: " + accessor.getServerData().getInt("production") + "EU/t"));


        if (accessor.getServerData().contains("consumption"))
            tooltip.add(Text.of("Consumption: " + accessor.getServerData().getInt("consumption") + "EU/t"));


    }

    @Override
    public void appendServerData(NbtCompound nbtCompound, BlockAccessor blockAccessor) {
        PSCBEntity pscbe = (PSCBEntity) blockAccessor.getBlockEntity();

        if (pscbe == null)
            return;

        if (PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector iec) {
            nbtCompound.putInt("nid", iec.getEnergyNetwork().getID());

            nbtCompound.putString("load", iec.getEnergyNetwork().getTotalConsumption() + "/" + iec.getEnergyNetwork().getTotalProduction());

            nbtCompound.putBoolean("networkHasPower", iec.getEnergyNetwork().hasPower());
        }

        if (PSManager.getComponent(pscbe.ComponentID) instanceof IEnergySource ies)
            nbtCompound.putInt("production", ies.getEnergyNetwork().getProduction(ies));

        if (PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyDrain ied)
            nbtCompound.putInt("consumption", ied.getEnergyNetwork().getConsumption(ied));

    }

    @Override
    public Identifier getUid() {
        return new Identifier(CTek.MOD_ID, "waila");
    }
}
