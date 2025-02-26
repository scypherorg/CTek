package com.scypher.ctek.PS.Energy;

public interface IEnergyConnector {
    public EnergyNetwork getEnergyNetwork();
    public void setEnergyNetwork(EnergyNetwork network);
    public int getComponentID();
}
