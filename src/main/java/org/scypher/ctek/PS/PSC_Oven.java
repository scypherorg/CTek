package org.scypher.ctek.PS;

import org.scypher.ctek.PS.Energy.EnergyNetwork;
import org.scypher.ctek.PS.Energy.IEnergyConnector;
import org.scypher.ctek.PS.Energy.IEnergyDrain;
import org.scypher.ctek.PS.base.PSComponent;

public class PSC_Oven extends PSComponent implements IEnergyConnector, IEnergyDrain {
    EnergyNetwork _energyNetwork;
    @Override
    public EnergyNetwork getEnergyNetwork() {
        return _energyNetwork;
    }
    @Override
    public PSComponent CreateInstance() {
        return new PSC_Oven();
    }
    @Override
    public void setEnergyNetwork(EnergyNetwork network) {
        _energyNetwork = network;
    }
    @Override
    public void onEnergyNetworkPowerStatusChanged(boolean hasPower) {

    }
}
