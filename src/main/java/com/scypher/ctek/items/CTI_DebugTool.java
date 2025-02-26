package com.scypher.ctek.items;

import com.scypher.ctek.PS.Energy.IEnergyConnector;
import com.scypher.ctek.PS.Energy.IEnergySource;
import com.scypher.ctek.PS.base.PSManager;
import com.scypher.ctek.blocks.base.PSCBEntity;
import com.scypher.ctek.blocks.base.PSCBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class CTI_DebugTool extends Item {
    public CTI_DebugTool(Settings settings) {
        super(settings);
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        if(context.getWorld().isClient || context.getPlayer() == null || !context.getPlayer().isPlayer())
            return ActionResult.PASS;
        if(!(context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof PSCBlock))
            return ActionResult.PASS;
        PSCBEntity pscbe = (PSCBEntity)context.getWorld().getBlockEntity(context.getBlockPos());
        context.getPlayer().sendMessage(Text.of(""));
        context.getPlayer().sendMessage(Text.of("   " + context.getWorld().getBlockState(context.getBlockPos()).getBlock().getClass().getSimpleName()+"/"+pscbe.getClass().getSimpleName()+"/"+PSManager.getComponent(pscbe.ComponentID).getClass().getSimpleName()));
        context.getPlayer().sendMessage(Text.of(" -Component-ID: " + pscbe.ComponentID));
        if(PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector iec)
        {
            context.getPlayer().sendMessage(Text.of(" -Network-ID: " + iec.getEnergyNetwork().getID()));
            context.getPlayer().sendMessage(Text.of(" -Load: " + iec.getEnergyNetwork().getTotalConsumption() + "/" + iec.getEnergyNetwork().getTotalProduction()+"EU/t"));
            context.getPlayer().sendMessage(Text.of(" -Max Load: " + iec.getEnergyNetwork().getMaxTransferRate() + "EU/t"));
        }
        if(PSManager.getComponent(pscbe.ComponentID) instanceof IEnergySource ies)
            context.getPlayer().sendMessage(Text.of(" -Production: " + ies.getEnergyNetwork().getProduction(ies) + "EU/t"));
        return ActionResult.PASS;
    }
}
