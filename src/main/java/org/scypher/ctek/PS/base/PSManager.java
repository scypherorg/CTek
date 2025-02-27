package org.scypher.ctek.PS.base;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.scypher.ctek.CTek;
import org.scypher.ctek.PS.Energy.EnergyNetwork;
import org.scypher.ctek.PS.Energy.IEnergyConnector;

public class PSManager {
    //Variables
    public static int ArrayIncrementStep = 8;
    static PSComponent[] _components = new PSComponent[ArrayIncrementStep];
    //Private
    static void ExtendComponentsArray()
    {
        PSComponent[] nc = new PSComponent[_components.length + ArrayIncrementStep];
        System.arraycopy(_components, 0, nc, 0, _components.length);
        _components = nc;
    }
    //Public
    public static PSComponent getComponent(int id)
    {
        if(id >= _components.length)
            return null;
        return _components[id];
    }
    public static int getFreeComponentID()
    {
        for (int i = 0; i < _components.length; i++) {
            if(_components[i] == null)
                return i;
        }
        ExtendComponentsArray();
        return getFreeComponentID();
    }
    public static void LoadData(JsonObject data)
    {
        JsonArray comps = data.getAsJsonArray("_components");
        _components = new PSComponent[comps.size()];
        for (int i = 0; i < comps.size(); i++) {
            if(comps.get(i).getAsJsonObject().get("type") != null)
                _components[i] = PSComponent.CreateFromData(comps.get(i).getAsJsonObject());
        }
        CTek.LOGGER.info("PSM loaded {} components", _components.length);
    }
    public static JsonObject SaveData()
    {
        JsonObject data = new JsonObject();
        JsonArray comps = new JsonArray();
        for (PSComponent component : _components)
            if (component == null)
                comps.add(new JsonObject());
            else
                comps.add(component.SaveData());
        data.add("_components", comps);
        return data;
    }
    public static int RegisterComponent(PSComponent component, World world, BlockPos pos)
    {
        if(component.ComponentID == -1)
            component.ComponentID = getFreeComponentID();
        _components[component.ComponentID] = component;
        if(component instanceof IEnergyConnector iec)
            EnergyNetwork.CreateConnector(iec, world, pos);
        if(component instanceof IOnPlaced ion)
            ion.OnPlaced(world, pos);
        CTek.SaveData();
        return component.ComponentID;
    }
    public static void DestroyComponent(int cID)
    {
        PSComponent component = _components[cID];
        _components[cID] = null;
        if(component instanceof IEnergyConnector iec)
            EnergyNetwork.DestroyConnector(iec);
        CTek.SaveData();
        CTek.LOGGER.info("PSC {} destroyed", cID);
    }
    public static void OnTick(MinecraftServer server)
    {
        EnergyNetwork.Tick();
    }
    public PSManager(){}
}