package org.scypher.ctek.blocks;

import net.minecraft.block.AbstractBlock;
import org.scypher.ctek.PS.PSC_Oven;
import org.scypher.ctek.PS.base.PSComponent;
import org.scypher.ctek.blocks.base.PSCDirectionalBlock;

public class PSCB_Oven extends PSCDirectionalBlock {
    public PSCB_Oven(AbstractBlock.Settings settings) {
        super(settings);
    }
    @Override
    public PSComponent createPSC()
    {
        return new PSC_Oven();
    }
}
