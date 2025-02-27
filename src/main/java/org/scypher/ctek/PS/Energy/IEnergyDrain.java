package org.scypher.ctek.PS.Energy;

public interface IEnergyDrain {
    int getComponentID();

    EnergyNetwork getEnergyNetwork();

    void setEnergyNetwork(EnergyNetwork network);

    void setPowerStatus(boolean hasPower);
}
