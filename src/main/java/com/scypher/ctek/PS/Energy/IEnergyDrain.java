package com.scypher.ctek.PS.Energy;

public interface IEnergyDrain {
    public int getComponentID();
    public EnergyNetwork getEnergyNetwork();
    public void setEnergyNetwork(EnergyNetwork network);
    public void setPowerStatus(boolean hasPower);
}
