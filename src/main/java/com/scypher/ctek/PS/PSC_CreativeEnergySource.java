package com.scypher.ctek.PS;

import com.scypher.ctek.PS.Energy.EnergyNetwork;
import com.scypher.ctek.PS.Energy.IEnergyConnector;
import com.scypher.ctek.PS.Energy.IEnergySource;
import com.scypher.ctek.PS.base.IOnPlaced;
import com.scypher.ctek.PS.base.PSComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PSC_CreativeEnergySource extends PSComponent implements IEnergyConnector, IEnergySource, IOnPlaced {
    EnergyNetwork _energyNetwork;
    @Override
    public EnergyNetwork getEnergyNetwork() {
        return _energyNetwork;
    }
    @Override
    public PSComponent CreateInstance() {
        return new PSC_CreativeEnergySource();
    }
    @Override
    public void setEnergyNetwork(EnergyNetwork network) {
        _energyNetwork = network;
    }

    @Override
    public void OnPlaced(World world, BlockPos pos) {
        _energyNetwork.setProduction(this, 69);
    }
}