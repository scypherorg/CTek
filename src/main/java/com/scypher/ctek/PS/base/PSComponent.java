package com.scypher.ctek.PS.base;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;

public class PSComponent {
    //Static
    static HashMap<String, PSComponent> _componentClasses = new HashMap<String, PSComponent>();
    public static void RegisterClass(String type, PSComponent component)
    {
        _componentClasses.put(type, component);
    }
    public static PSComponent CreateFromData(JsonObject data)
    {
        PSComponent component = _componentClasses.get(data.get("type").getAsString()).CreateInstance();
        component.LoadData(data);
        return component;
    }
    //Instance
    public int ComponentID = -1;
    public int getComponentID() {
        return ComponentID;
    }
    public PSComponent() {}
    public PSComponent CreateInstance()
    {
        throw new NotImplementedException("Implement CreateInstance on PSC-Child!");
    }
    public void LoadData(JsonObject data)
    {
        ComponentID = data.get("ID").getAsInt();
    }
    public JsonObject SaveData()
    {
        JsonObject data = new JsonObject();
        data.addProperty("ID", ComponentID);
        data.addProperty("type", this.getClass().getSimpleName());
        return data;
    }
}