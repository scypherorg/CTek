package com.scypher.ctek.blocks;

import com.scypher.ctek.PS.PSC_CreativeEnergySource;
import com.scypher.ctek.PS.base.PSComponent;
import com.scypher.ctek.blocks.base.PSCBlock;

public class PSCB_CreativeEnergySource extends PSCBlock {

    public PSCB_CreativeEnergySource(Settings settings) {
        super(settings);
    }
    @Override
    public PSComponent createPSC()
    {
        return new PSC_CreativeEnergySource();
    }
}
