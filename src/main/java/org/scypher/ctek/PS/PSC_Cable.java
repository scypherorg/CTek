package org.scypher.ctek.PS;

import org.scypher.ctek.PS.Energy.EnergyNetwork;
import org.scypher.ctek.PS.Energy.IEnergyConnector;
import org.scypher.ctek.PS.base.PSComponent;

public class PSC_Cable extends PSComponent implements IEnergyConnector {
    EnergyNetwork _energyNetwork;
    @Override
    public EnergyNetwork getEnergyNetwork() {
        return _energyNetwork;
    }
    @Override
    public PSComponent CreateInstance() {
        return new PSC_Cable();
    }
    @Override
    public void setEnergyNetwork(EnergyNetwork network) {
        _energyNetwork = network;
    }
}
