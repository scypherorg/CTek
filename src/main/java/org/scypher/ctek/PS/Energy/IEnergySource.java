package org.scypher.ctek.PS.Energy;

public interface IEnergySource {
    EnergyNetwork getEnergyNetwork();

    void setEnergyNetwork(EnergyNetwork network);

    int getComponentID();
}
