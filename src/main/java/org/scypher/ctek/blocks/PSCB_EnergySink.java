package org.scypher.ctek.blocks;

import org.scypher.ctek.PS.PSC_Cable;
import org.scypher.ctek.PS.PSC_EnergySink;
import org.scypher.ctek.PS.base.PSComponent;
import org.scypher.ctek.blocks.base.PSCBlock;

public class PSCB_EnergySink extends PSCBlock {

    public PSCB_EnergySink(Settings settings) {
        super(settings);
    }
    @Override
    public PSComponent createPSC()
    {
        return new PSC_EnergySink();
    }
}
