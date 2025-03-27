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
    public static long getTime()
    {
        return _time;
    }
    static long _time = 0;
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
        if(id >= _components.length || id < 0)
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
        _time = data.get("time").getAsLong();
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
        data.addProperty("time", _time);
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
            EnergyNetwork.createConnector(iec, world, pos);
        if(component instanceof IOnPlaced ion)
            ion.OnPlaced(world, pos);
        return component.ComponentID;
    }
    public static void DestroyComponent(int cID)
    {
        PSComponent component = _components[cID];
        _components[cID] = null;
        if(component instanceof IEnergyConnector iec)
            EnergyNetwork.destroyConnector(iec);
        CTek.LOGGER.info("PSC {} destroyed", cID);
    }
    public static void OnTick(MinecraftServer server)
    {
        _time++;
        EnergyNetwork.tick();
        _ScheduledTickOffset = ++_ScheduledTickOffset % _ScheduledTicks.length;
        for (int i = 0; i < _ScheduledTickCount[_ScheduledTickOffset]; i++) {
            if(_components[_ScheduledTicks[_ScheduledTickOffset][i]] instanceof IScheduler is)
                is.OnScheduledTick();
        }
        _ScheduledTickCount[_ScheduledTickOffset] = 0;
    }
    static int[][] _ScheduledTicks = new int[100][];
    static int[] _ScheduledTickCount = new int[100];
    static int _ScheduledTickOffset = 0;
    public static void Schedule(int delay, int cID)
    {
        if(!(getComponent(cID) instanceof IScheduler))
            throw new IllegalArgumentException("Component " + cID + " is not an instance of IScheduler");
        if(delay >= _ScheduledTicks.length)
            throw new IllegalArgumentException("Delay of " + delay + " ticks exceeds the max delay of " + (_ScheduledTicks.length-1) + " ticks");
        int targetOffset = _ScheduledTickOffset + delay;
        targetOffset %= _ScheduledTicks.length;
        if(_ScheduledTicks[targetOffset].length < _ScheduledTickCount[targetOffset])
        {
            int[] nst = new int[_ScheduledTicks[targetOffset].length + ArrayIncrementStep];
            System.arraycopy(_ScheduledTicks[targetOffset], 0, nst, 0, _ScheduledTicks[targetOffset].length);
            _ScheduledTicks[targetOffset] = nst;
        }
        _ScheduledTicks[targetOffset][_ScheduledTickCount[targetOffset]] = cID;
        _ScheduledTickCount[targetOffset]++;
    }
    public PSManager(){}
}