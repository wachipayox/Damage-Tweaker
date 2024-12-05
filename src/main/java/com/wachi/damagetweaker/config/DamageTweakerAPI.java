package com.wachi.damagetweaker.config;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

public class DamageTweakerAPI {

    public static void addToConfig(String src_entity, String src_damage, DmgP p, JsonElement e){
        addToConfig(src_entity, src_damage, new HashMap<>(){{put(p, e);}});
    }

    //Merges the config into the def_map changing just the desired values
    public static void addToConfig(String src_entity, String src_damage, Map<DmgP, JsonElement> adding){
        Map<String, Map<String, Map<DmgP, JsonElement>>> def_map = DamageConfig.def_map;

        if (def_map.containsKey(src_entity))
            if (def_map.get(src_entity).containsKey(src_damage))
                for(DmgP p : adding.keySet())
                    def_map.get(src_entity).get(src_damage).put(p, adding.get(p));
            else
                def_map.get(src_entity).put(src_damage, adding);
        else
            def_map.put(src_entity, new HashMap<>(){{put(src_damage, adding);}});
    }
}
