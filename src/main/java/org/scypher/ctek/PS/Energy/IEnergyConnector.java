package org.scypher.ctek.PS.Energy;

public interface IEnergyConnector {
    EnergyNetwork getEnergyNetwork();

    void setEnergyNetwork(EnergyNetwork network);

    int getComponentID();
}
