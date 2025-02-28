package org.scypher.ctek.PS.Energy;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.scypher.ctek.CTek;
import org.scypher.ctek.PS.base.PSManager;
import org.scypher.ctek.blocks.base.PSCBEntity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class EnergyNetwork {
    //Static Stuff
    static int getNextNetworkID()
    {
        int id = 0;
        while (_networks.containsKey(id))
            id++;
        return id;
    }
    static HashMap<Integer, EnergyNetwork> _networks = new HashMap<>();
    public static EnergyNetwork CreateNetwork() {
        EnergyNetwork net = new EnergyNetwork(getNextNetworkID());
        _networks.put(net.getID(), net);
        return net;
    }
    public static void Tick()
    {
        for (EnergyNetwork net : _networks.values())
            net.Update();
    }
    public static void DestroyConnector(IEnergyConnector connector) {
        EnergyNetwork net = connector.getEnergyNetwork();
        if(net._connectorsCount == 1 && net._connectorComponents[0].getComponentID() == connector.getComponentID())
        {
            DestroyNetwork(net);
            return;
        }
        int cIndex = net.getConnectorIndex(connector.getComponentID());
        if(cIndex == -1)
            return;
        if(net._connectorConnections[cIndex].length == 1)
        {
            net.RemoveConnectorConnection(net._connectorConnections[cIndex][0], connector.getComponentID());
            net.RemoveConnector(cIndex);
            return;
        }
        //Handle Network Splitting
        int[] probePoints = net._connectorConnections[cIndex];
        for (int probePoint : probePoints)
            net.RemoveConnectorConnection(probePoint, connector.getComponentID());
        net.RemoveConnector(cIndex);
        SplitNetwork(net, probePoints[0]);
    }
    public static void SplitNetwork(EnergyNetwork net, int originConnectorID)
    {
        boolean[] connected = new boolean[net._connectorsCount];
        Queue<Integer> availabeConnectors = new LinkedList<>();
        availabeConnectors.offer(net.getConnectorIndex(originConnectorID));
        while (!availabeConnectors.isEmpty()) {
            int cIndex = availabeConnectors.poll();
            connected[cIndex] = true;
            int ccIndex;
            for (int i = 0; i < net._connectorConnections[cIndex].length; i++)
            {
                ccIndex = net.getConnectorIndex(net._connectorConnections[cIndex][i]);
                if(!connected[ccIndex])
                    availabeConnectors.offer(ccIndex);
            }
        }
        boolean allConnected = true;
        int connectedCount = 0;
        for (boolean b : connected)
        {
            allConnected &= b;
            if(b)
                connectedCount++;
        }
        if(allConnected)
            return;
        EnergyNetwork nn = CreateNetwork();
        IEnergyConnector[] cccomp = new IEnergyConnector[connectedCount];
        IEnergyConnector[] nccomp = new IEnergyConnector[net._connectorsCount-connectedCount];
        int[][] cccon = new int[connectedCount][];
        int[][] nccon = new int[net._connectorsCount-connectedCount][];
        int cOffset = 0;
        int nOffset = 0;
        for (int i = 0; i < net._connectorsCount; i++)
        {
            if(connected[i])
            {
                cccomp[cOffset] = net._connectorComponents[i];
                cccon[cOffset++] = net._connectorConnections[i];
                continue;
            }
            nccomp[nOffset] = net._connectorComponents[i];
            nccon[nOffset++] = net._connectorConnections[i];
            net._connectorComponents[i].setEnergyNetwork(nn);
            if(net._connectorComponents[i] instanceof IEnergySource source)
            {
                nn.AddSource(source);
                nn.setProduction(source, net.getProduction(source));
                net.RemoveSource(source);
            }
            if(net._connectorComponents[i] instanceof IEnergyDrain drain)
            {
                nn.AddDrain(drain);
                nn.setConsumption(drain, net.getConsumption(drain));
                net.RemoveDrain(drain);
            }
        }
        net._connectorComponents = cccomp;
        net._connectorConnections = cccon;
        net._connectorsCount = connectedCount;
        net._updateTotalProduction = true;
        net._updateTotalConsumption = true;
        nn._connectorComponents = nccomp;
        nn._connectorConnections = nccon;
        nn._connectorsCount = nccomp.length;
        SplitNetwork(nn, nn._connectorComponents[0].getComponentID());
    }
    static void DestroyNetwork(EnergyNetwork network) {
        _networks.remove(network.getID());
    }
    public static void CreateConnector(IEnergyConnector connector, World world, BlockPos pos)
    {
        IEnergyConnector neighbor = GetDifferentNeighborConnector(-1, world, pos);
        if(neighbor == null)
        {
            CreateNetwork().AddConnector(connector, world, pos);
            return;
        }
        EnergyNetwork net = neighbor.getEnergyNetwork();
        neighbor = GetDifferentNeighborConnector(net.getID(), world, pos);
        while (neighbor != null)
        {
            net = MergeNetworks(net, neighbor.getEnergyNetwork());
            neighbor = GetDifferentNeighborConnector(net.getID(), world, pos);
        }
        net.AddConnector(connector, world, pos);
    }
    static EnergyNetwork MergeNetworks(EnergyNetwork net1, EnergyNetwork net2)
    {
        if(net1.getID() == net2.getID())
            return net1;
        if(net1.getID()>net2.getID())
        {
            EnergyNetwork tmp = net1;
            net1 = net2;
            net2 = tmp;
        }
        net1._updateTotalProduction = true;
        net1._updateTotalConsumption = true;
        //Merge Connectors
        IEnergyConnector[] _connectorComponents = new IEnergyConnector[net1._connectorsCount + net2._connectorsCount];
        int[][] _connections = new int[_connectorComponents.length][];
        System.arraycopy(net1._connectorComponents, 0, _connectorComponents, 0, net1._connectorsCount);
        System.arraycopy(net2._connectorComponents, 0, _connectorComponents, net1._connectorsCount, net2._connectorsCount);
        System.arraycopy(net1._connectorConnections, 0, _connections, 0, net1._connectorsCount);
        System.arraycopy(net2._connectorConnections, 0, _connections, net1._connectorsCount, net2._connectorsCount);
        for(int i = 0; i < net2._connectorsCount; i++)
            net2._connectorComponents[i].setEnergyNetwork(net1);
        net1._connectorComponents = _connectorComponents;
        net1._connectorConnections = _connections;
        net1._connectorsCount = _connectorComponents.length;
        //Merge Sources
        IEnergySource[] _sources = new IEnergySource[net1._sourcesCount + net2._sourcesCount];
        int[] _sourcesProduction = new int[net1._sourcesCount + net2._sourcesCount];
        System.arraycopy(net1._energySourcesComponent, 0, _sources, 0, net1._sourcesCount);
        System.arraycopy(net2._energySourcesComponent, 0, _sources, net1._sourcesCount, net2._sourcesCount);
        System.arraycopy(net1._energySourcesProduction, 0, _sourcesProduction, 0, net1._sourcesCount);
        System.arraycopy(net2._energySourcesProduction, 0, _sourcesProduction, net1._sourcesCount, net2._sourcesCount);
        net1._energySourcesComponent = _sources;
        net1._energySourcesProduction = _sourcesProduction;
        net1._sourcesCount = _sourcesProduction.length;
        //Merge Drains
        IEnergyDrain[] _drains = new IEnergyDrain[net1._drainCount + net2._drainCount];
        int[] _drainConsumption = new int[net1._drainCount + net2._drainCount];
        System.arraycopy(net1._drainComponents, 0, _drains, 0, net1._drainCount);
        System.arraycopy(net2._drainComponents, 0, _drains, net1._drainCount, net2._drainCount);
        System.arraycopy(net1._drainConsumptions, 0, _drainConsumption, 0, net1._drainCount);
        System.arraycopy(net2._drainConsumptions, 0, _drainConsumption, net1._drainCount, net2._drainCount);
        net1._drainComponents = _drains;
        net1._drainConsumptions = _drainConsumption;
        net1._drainCount = _drains.length;
        //Cleanup
        DestroyNetwork(net2);
        CTek.LOGGER.info("Merged Energy-Networks " + net1.getID() + " & " + net2.getID());
        return net1;
    }
    static IEnergyConnector GetDifferentNeighborConnector(int netID, World world, BlockPos pos)
    {
        if(world.getBlockEntity(pos.north()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector iec && iec.getEnergyNetwork().getID() != netID)
            return iec;
        if(world.getBlockEntity(pos.south()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector iec && iec.getEnergyNetwork().getID() != netID)
            return iec;
        if(world.getBlockEntity(pos.west()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector iec && iec.getEnergyNetwork().getID() != netID)
            return iec;
        if(world.getBlockEntity(pos.east()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector iec && iec.getEnergyNetwork().getID() != netID)
            return iec;
        if(world.getBlockEntity(pos.up()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector iec && iec.getEnergyNetwork().getID() != netID)
            return iec;
        if(world.getBlockEntity(pos.down()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector iec && iec.getEnergyNetwork().getID() != netID)
            return iec;
        return null;
    }
    static int[] ComputeNeighborConnectors(World world, BlockPos pos)
    {
        int[] neighbors = new int[6];
        int count = 0;
        if(world.getBlockEntity(pos.north()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector iec)
            neighbors[count++] = iec.getComponentID();
        if(world.getBlockEntity(pos.south()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector iec)
            neighbors[count++] = iec.getComponentID();
        if(world.getBlockEntity(pos.west()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector iec)
            neighbors[count++] = iec.getComponentID();
        if(world.getBlockEntity(pos.east()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector iec)
            neighbors[count++] = iec.getComponentID();
        if(world.getBlockEntity(pos.up()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector iec)
            neighbors[count++] = iec.getComponentID();
        if(world.getBlockEntity(pos.down()) instanceof PSCBEntity pscbe && PSManager.getComponent(pscbe.ComponentID) instanceof IEnergyConnector iec)
            neighbors[count++] = iec.getComponentID();
        int[] res = new int[count];
        System.arraycopy(neighbors, 0, res, 0, count);
        return res;
    }
    public static EnergyNetwork getNetwork(int ID)
    { return _networks.get(ID); }
    public static void LoadData(JsonObject data)
    {
        JsonArray networks = data.get("_networks").getAsJsonArray();
        _networks.clear();
        for (int i = 0; i < networks.size(); i++)
            LoadNetworkFromData(networks.get(i).getAsJsonObject());
        CTek.LOGGER.info("Loaded " + _networks.size() + " Energy-Networks");
    }
    static void LoadNetworkFromData(JsonObject data)
    {
        //Load Connectors
        JsonArray jconnectors = data.get("connectors").getAsJsonArray();
        IEnergyConnector[] connectorComponents = new IEnergyConnector[jconnectors.size()];
        int[][] connectorConnections = new int[jconnectors.size()][];
        for (int i = 0; i < jconnectors.size(); i++)
        {
            JsonObject item = jconnectors.get(i).getAsJsonObject();
            connectorComponents[i] = (IEnergyConnector) PSManager.getComponent(item.get("CID").getAsInt());
            JsonArray connections = item.get("connections").getAsJsonArray();
            connectorConnections[i] = new int[connections.size()];
            for (int j = 0; j < connections.size(); j++)
                connectorConnections[i][j] = connections.get(j).getAsInt();
        }
        //Load Sources
        JsonArray jsources = data.get("sources").getAsJsonArray();
        IEnergySource[] sourceComponents = new IEnergySource[jsources.size()];
        int[] sourcesProduction = new int[jsources.size()];
        for (int i = 0; i < jsources.size(); i++)
        {
            JsonObject item = jsources.get(i).getAsJsonObject();
            sourceComponents[i] = (IEnergySource) PSManager.getComponent(item.get("CID").getAsInt());
            sourcesProduction[i] = item.get("p").getAsInt();
        }
        //Load Drains
        JsonArray jdrains = data.get("drains").getAsJsonArray();
        IEnergyDrain[] drainComponents = new IEnergyDrain[jdrains.size()];
        int[] drainConsumption = new int[jdrains.size()];
        for (int i = 0; i < jdrains.size(); i++)
        {
            JsonObject item = jdrains.get(i).getAsJsonObject();
            drainComponents[i] = (IEnergyDrain) PSManager.getComponent(item.get("CID").getAsInt());
            drainConsumption[i] = item.get("c").getAsInt();
        }
        _networks.put(data.get("ID").getAsInt(), new EnergyNetwork(data.get("ID").getAsInt(), connectorComponents, connectorConnections, sourceComponents, sourcesProduction, drainComponents, drainConsumption));
    }
    public static JsonObject SaveData()
    {
        JsonObject data = new JsonObject();
        JsonArray networks = new JsonArray();
        for (HashMap.Entry<Integer, EnergyNetwork> entry : _networks.entrySet())
            networks.add(SaveNetworkData(entry.getValue()));
        data.add("_networks", networks);
        CTek.LOGGER.info("Saved " + networks.size() + " Energy-Networks");
        return data;
    }
    static JsonObject SaveNetworkData(EnergyNetwork network)
    {
        JsonObject data = new JsonObject();
        data.addProperty("ID", network.getID());
        //Save Connectors
        JsonArray jconnectors = new JsonArray();
        for (int i = 0; i < network._connectorsCount; i++) {
            JsonObject item = new JsonObject();
            item.addProperty("CID", network._connectorComponents[i].getComponentID());
            JsonArray connections = new JsonArray();
            for (int j = 0; j < network._connectorConnections[i].length; j++)
                connections.add(network._connectorConnections[i][j]);
            item.add("connections", connections);
            jconnectors.add(item);
        }
        data.add("connectors", jconnectors);
        //Save Sources
        JsonArray jsources = new JsonArray();
        for (int i = 0; i < network._sourcesCount; i++) {
            JsonObject item = new JsonObject();
            item.addProperty("CID", network._energySourcesComponent[i].getComponentID());
            item.addProperty("p", network._energySourcesProduction[i]);
            jsources.add(item);
        }
        data.add("sources", jsources);
        //Save Drains
        JsonArray jdrains = new JsonArray();
        for (int i = 0; i < network._drainCount; i++)
        {
            JsonObject item = new JsonObject();
            item.addProperty("CID", network._drainComponents[i].getComponentID());
            item.addProperty("c", network._drainComponents[i].getComponentID());
            jdrains.add(item);
        }
        data.add("drains", jdrains);
        return data;
    }
    //private Variables
    int _id;
    boolean _hasPower;
    //Sources
    int _totalProduction = 0;
    boolean _updateTotalProduction = false;
    IEnergySource[] _energySourcesComponent;
    int[] _energySourcesProduction;
    int _sourcesCount;
    //Drains
    int _totalConsumption = 0;
    boolean _updateTotalConsumption = false;
    IEnergyDrain[] _drainComponents;
    int[] _drainConsumptions;
    int _drainCount;
    //Connectors
    IEnergyConnector[] _connectorComponents;
    int[][] _connectorConnections;
    int _connectorsCount;
    int _maxRate = 69;
    //Public getters
    public boolean hasPower()
    {
        return _hasPower;
    }
    public int getID()
        {return _id;}
    public int getTotalProduction()
        {return _totalProduction;}
    public int getTotalConsumption()
        {return _totalConsumption;}
    public int getMaxTransferRate()
        {return _maxRate;}
    //Initializers
    public EnergyNetwork(int id, IEnergyConnector[] connectors, int[][] connectorConnections, IEnergySource[] sources, int[] sourcesProduction, IEnergyDrain[] drains, int[] drainConsumptions)
    {
        _id = id;
        _updateTotalProduction = true;
        _updateTotalConsumption = true;
        _connectorComponents = connectors;
        _connectorConnections = connectorConnections;
        _connectorsCount = connectors.length;
        for (int i = 0; i < _connectorsCount; i++)
            _connectorComponents[i].setEnergyNetwork(this);
        _energySourcesComponent = sources;
        _energySourcesProduction = sourcesProduction;
        _sourcesCount = sources.length;
        _drainComponents = drains;
        _drainConsumptions = drainConsumptions;
        _drainCount = drains.length;
        _hasPower = true;
    }
    public EnergyNetwork(int id)
    {
        _id = id;
        _connectorComponents = new IEnergyConnector[PSManager.ArrayIncrementStep];
        _connectorConnections = new int[PSManager.ArrayIncrementStep][];
        _connectorsCount = 0;
        _energySourcesComponent = new IEnergySource[PSManager.ArrayIncrementStep];
        _energySourcesProduction = new int[PSManager.ArrayIncrementStep];
        _sourcesCount = 0;
        _drainComponents = new IEnergyDrain[PSManager.ArrayIncrementStep];
        _drainConsumptions = new int[PSManager.ArrayIncrementStep];
        _drainCount = 0;
        _hasPower = true;
    }
    //Logic
    void setPower(boolean hasPower)
    {
        if(_hasPower == hasPower)
            return;
        _hasPower = hasPower;
        for (int i = 0; i < _drainCount; i++)
            _drainComponents[i].onEnergyNetworkPowerStatusChanged(hasPower);
    }
    void AddConnector(IEnergyConnector connector, World world, BlockPos pos)
    {
        for (int i = 0; i < _connectorsCount; i++)
            if(_connectorComponents[i].getComponentID() == connector.getComponentID())
                return;
        if (_connectorsCount == _connectorComponents.length)
        {
            IEnergyConnector[] newConnectors = new IEnergyConnector[_connectorsCount + PSManager.ArrayIncrementStep];
            System.arraycopy(_connectorComponents, 0, newConnectors, 0, _connectorsCount);
            _connectorComponents = newConnectors;
            int[][] newConnections = new int[_connectorsCount + PSManager.ArrayIncrementStep][];
            System.arraycopy(_connectorConnections, 0, newConnections, 0, _connectorsCount);
            _connectorConnections = newConnections;
        }
        _connectorComponents[_connectorsCount] = connector;
        _connectorConnections[_connectorsCount] = ComputeNeighborConnectors(world, pos);
        for (int i = 0; i < _connectorsCount; i++)
            if(isIntInArray(_connectorComponents[i].getComponentID(), _connectorConnections[_connectorsCount]))
            {
                int[] nnc = new int[_connectorConnections[i].length+1];
                System.arraycopy(_connectorConnections[i], 0, nnc, 0, _connectorConnections[i].length);
                nnc[_connectorConnections[i].length] = connector.getComponentID();
                _connectorConnections[i] = nnc;
            }
        _connectorsCount++;
        connector.setEnergyNetwork(this);
        if(connector instanceof IEnergySource source)
            AddSource(source);
        if(connector instanceof IEnergyDrain drain)
            AddDrain(drain);
    }
    void AddDrain(IEnergyDrain drain)
    {
        if(_drainCount == _drainComponents.length)
        {
            IEnergyDrain[] newDrains = new IEnergyDrain[_drainCount+PSManager.ArrayIncrementStep];
            System.arraycopy(_drainComponents, 0, newDrains, 0, _drainCount);
            _drainComponents = newDrains;
            int[] newConsumptions = new int[_drainCount+PSManager.ArrayIncrementStep];
            System.arraycopy(_drainConsumptions, 0, newConsumptions, 0, _drainCount);
            _drainConsumptions = newConsumptions;
        }
        _drainComponents[_drainCount] = drain;
        _drainConsumptions[_drainCount] = 0;
        _drainCount++;
    }
    void RemoveDrain(IEnergyDrain drain)
    {
        int drainIndex = getDrainIndex(drain.getComponentID());
        if(_drainConsumptions[drainIndex] != 0)
            _updateTotalConsumption = true;
        for (int i = drainIndex; i < _drainCount-1; i++) {
            _drainComponents[i] = _drainComponents[i+1];
            _drainConsumptions[i] = _drainConsumptions[i+1];
        }
        _drainCount--;
        _drainComponents[_drainCount] = null;
        _drainConsumptions[_drainCount] = 0;
    }
    int getDrainIndex(int drainID)
    {
        for (int i = 0; i < _drainCount; i++)
            if(_drainComponents[i].getComponentID() == drainID)
                return i;
        return -1;
    }
    void AddSource(IEnergySource source)
    {
        if(_sourcesCount == _energySourcesComponent.length)
        {
            IEnergySource[] newSourcesComponent = new IEnergySource[_sourcesCount + PSManager.ArrayIncrementStep];
            System.arraycopy(_energySourcesComponent, 0, newSourcesComponent, 0, _sourcesCount);
            _energySourcesComponent = newSourcesComponent;
            int[] newSourcesProduction = new int[_sourcesCount + PSManager.ArrayIncrementStep];
            System.arraycopy(_energySourcesProduction, 0, newSourcesProduction, 0, _sourcesCount);
            _energySourcesProduction = newSourcesProduction;
        }
        _energySourcesComponent[_sourcesCount] = source;
        _energySourcesProduction[_sourcesCount] = 0;
        _sourcesCount++;
    }
    void RemoveSource(IEnergySource source)
    {
        int sourceIndex = getSourceIndex(source.getComponentID());
        if(_energySourcesProduction[sourceIndex] != 0)
            _updateTotalProduction = true;
        for (int i = sourceIndex; i < _sourcesCount-1; i++)
        {
            _energySourcesComponent[i] = _energySourcesComponent[i+1];
            _energySourcesProduction[i] = _energySourcesProduction[i+1];
        }
        _sourcesCount--;
        _energySourcesComponent[_sourcesCount] = null;
        _energySourcesProduction[_sourcesCount] = 0;
    }
    public void setProduction(IEnergySource source, int production)
    {
        int sI = getSourceIndex(source.getComponentID());
        if(_energySourcesProduction[sI] == production)
            return;
        _energySourcesProduction[sI] = production;
        _updateTotalProduction = true;
    }
    public int getProduction(IEnergySource source)
    {
        return _energySourcesProduction[getSourceIndex(source.getComponentID())];
    }
    public void setConsumption(IEnergyDrain drain, int consumption)
    {
        int dI = getDrainIndex(drain.getComponentID());
        if(_drainConsumptions[dI] == consumption)
            return;
        _drainConsumptions[dI] = consumption;
        _updateTotalConsumption = true;
    }
    public int getConsumption(IEnergyDrain drain)
    {
        return _drainConsumptions[getDrainIndex(drain.getComponentID())];
    }
    int getSourceIndex(int sourceID)
    {
        for (int i = 0; i < _sourcesCount; i++)
            if(_energySourcesComponent[i].getComponentID() == sourceID)
                return i;
        return -1;
    }
    int getConnectorIndex(int connectorID)
    {
        for (int i = 0; i < _connectorsCount; i++)
            if(_connectorComponents[i].getComponentID() == connectorID)
                return i;
        return -1;
    }
    void RemoveConnectorConnection(int connectorID, int connectedConnectorID)
    {
        int cI = getConnectorIndex(connectorID);
        boolean found = false;
        for (int i = 0; i < _connectorConnections[cI].length; i++)
            found |= _connectorConnections[cI][i] == connectedConnectorID;
        if(!found)
            return;
        int[] ncc = new int[_connectorConnections[cI].length-1];
        int offset = 0;
        for(int i = 0; i < _connectorConnections[cI].length; i++)
            if(_connectorConnections[cI][i] != connectedConnectorID)
                ncc[offset++] = _connectorConnections[cI][i];
        _connectorConnections[cI] = ncc;
    }
    void RemoveConnector(int connectorIndex)
    {
        if(_connectorComponents[connectorIndex] instanceof IEnergySource source)
            RemoveSource(source);
        if(_connectorComponents[connectorIndex] instanceof IEnergyDrain drain)
            RemoveDrain(drain);
        for (int i = connectorIndex; i < _connectorsCount-1; i++) {
            _connectorConnections[i] = _connectorConnections[i+1];
            _connectorComponents[i] = _connectorComponents[i+1];
        }
        _connectorsCount--;
        _connectorConnections[_connectorsCount] = null;
        _connectorComponents[_connectorsCount] = null;
    }
    void Update()
    {
        boolean hasChanges = false;
        if(_updateTotalConsumption)
            hasChanges |= updateConsumption();
        if(_updateTotalProduction)
            hasChanges |= updateProduction();
        if (!hasChanges)
            return;
        if(_totalConsumption > _totalProduction && _hasPower)
            setPower(false);
        if(_totalConsumption <= _totalProduction && !_hasPower)
            setPower(true);
    }
    boolean updateConsumption()
    {
        CTek.LOGGER.info("Updating Energy Drain Consumption for Network {}", _id);
        _updateTotalConsumption = false;
        int totalConsumption = 0;
        for (int i = 0; i < _drainCount; i++)
            totalConsumption += _drainConsumptions[i];
        if(totalConsumption == _totalConsumption)
            return false;
        _totalConsumption = totalConsumption;
        return true;
    }
    boolean updateProduction()
    {
        CTek.LOGGER.info("Updating Energy Source Production for Network {}", _id);
        _updateTotalProduction = false;
        int totalProduction = 0;
        for (int i = 0; i < _sourcesCount; i++)
            totalProduction += _energySourcesProduction[i];
        if(_totalProduction == totalProduction)
            return false;
        _totalProduction = totalProduction;
        return true;
    }
    boolean isIntInArray(int i, int[] array)
    {
        for (int k : array) {
            if (k == i)
                return true;
        }
        return false;
    }
}