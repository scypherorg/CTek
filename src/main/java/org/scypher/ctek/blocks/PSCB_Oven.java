package org.scypher.ctek.blocks;

import net.minecraft.block.AbstractBlock;
import org.scypher.ctek.PS.PSC_EnergySink;
import org.scypher.ctek.PS.PSC_Oven;
import org.scypher.ctek.PS.base.PSComponent;
import org.scypher.ctek.blocks.base.PSCBlock;

public class PSCB_Oven extends PSCBlock {
    public PSCB_Oven(AbstractBlock.Settings settings) {
        super(settings);
    }
    @Override
    public PSComponent createPSC()
    {
        return new PSC_Oven();
    }
}
