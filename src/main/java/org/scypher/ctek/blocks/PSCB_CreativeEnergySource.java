package org.scypher.ctek.blocks;

import org.scypher.ctek.PS.PSC_CreativeEnergySource;
import org.scypher.ctek.PS.base.PSComponent;
import org.scypher.ctek.blocks.base.PSCBlock;

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
