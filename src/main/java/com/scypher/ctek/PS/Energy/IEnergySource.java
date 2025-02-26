package com.scypher.ctek.PS.Energy;

public interface IEnergySource {
    public EnergyNetwork getEnergyNetwork();
    public void setEnergyNetwork(EnergyNetwork network);
    public int getComponentID();
}
