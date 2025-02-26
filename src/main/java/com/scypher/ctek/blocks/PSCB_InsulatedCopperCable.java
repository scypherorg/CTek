package com.scypher.ctek.blocks;

import com.scypher.ctek.PS.PSC_Cable;
import com.scypher.ctek.PS.base.PSComponent;
import com.scypher.ctek.blocks.base.PSCBlock;

public class PSCB_InsulatedCopperCable extends PSCBlock {

    public PSCB_InsulatedCopperCable(Settings settings) {
        super(settings);
    }
    @Override
    public PSComponent createPSC()
    {
        return new PSC_Cable();
    }
}
