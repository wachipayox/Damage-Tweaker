package com.wachi.damagetweaker.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import com.wachi.damagetweaker.DamageTweakerMod;
import com.wachi.damagetweaker.util.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = DamageTweakerMod.MODID)
public class DamageConfig {

    // Map overlaying default values for specific damages and for specific entities
    public static Map<String, Map<String, Map<DmgP, JsonElement>>> def_map = new HashMap<>() {
        {
            put("any mob", new HashMap<>(){{
                put("example damage", new HashMap<>() {
                    {
                        put(DmgP.COOLDOWN, jsonUtil.oTj(0));
                        put(DmgP.MAX_IN_TICK_GEN, jsonUtil.oTj(-1));
                        put(DmgP.MAX_IN_TICK_SPEC, jsonUtil.oTj(-1));
                    }
                });

                put("other example damage", new HashMap<>() {
                    {
                        put(DmgP.COOLDOWN, jsonUtil.oTj(100));
                        put(DmgP.PRE_COOLDOWN, jsonUtil.oTj("default"));
                        put(DmgP.MAX_IN_TICK_GEN, jsonUtil.oTj(-1));
                        put(DmgP.MAX_IN_TICK_SPEC, jsonUtil.oTj(-1));
                    }
                });
            }});

            put("a mod or minecraft entity id (minecraft:slime)", new HashMap<>(){{
                put("another example damage", new HashMap<>() {
                    {
                        put(DmgP.COOLDOWN, jsonUtil.oTj(10));
                        put(DmgP.MAX_IN_TICK_SPEC, jsonUtil.oTj(1));
                        put(DmgP.MAX_IN_TICK_GEN, jsonUtil.oTj(1));
                    }
                });
            }});
        }
    };

    // Map loaded to the server to get values faster
    private static final Map<String, Map<String, Map<DmgP, Integer>>> config_map = new HashMap<>();

    @SubscribeEvent
    public static void onServerLoad(ServerStartingEvent event) {
        System.out.println("damage_tweaker: Loading damage config...");
        try {
            initFile();
            loadFileOnMap();
        } catch (Exception e) {
            System.out.println("damage_tweaker: Failed load of damage config:");
            System.out.println(e.getMessage());
        }
    }

    public static int getValue(DamageSource dSource, DmgP dcp) {
        Holder<DamageType> dType = dSource.typeHolder();
        String d_n = dType.getRegisteredName();

        Entity ent = dSource.getEntity();
        String e_n = (ent != null) ? "" + regUtil.getEntityRegistryName(ent) : "any mob";

        if(config_map.containsKey(e_n) && config_map.get(e_n).containsKey(d_n) && config_map.get(e_n).get(d_n).containsKey(dcp))
            return config_map.get(e_n).get(d_n).get(dcp);

        if(def_map.containsKey(e_n) && def_map.get(e_n).containsKey(d_n) && def_map.get(e_n).get(d_n).containsKey(dcp)
                && !def_map.get(e_n).get(d_n).get(dcp).getAsString().equals("default"))
            return def_map.get(e_n).get(d_n).get(dcp).getAsInt();

        if(config_map.containsKey(e_n) && config_map.get(e_n).containsKey("any damage") && config_map.get(e_n).get("any damage").containsKey(dcp))
            return config_map.get(e_n).get("any damage").get(dcp);

        if(def_map.containsKey(e_n) && def_map.get(e_n).containsKey("any damage") && def_map.get(e_n).get("any damage").containsKey(dcp)
                && !def_map.get(e_n).get("any damage").get(dcp).getAsString().equals("default"))
            return def_map.get(e_n).get("any damage").get(dcp).getAsInt();

        if(config_map.containsKey("any mob") && config_map.get("any mob").containsKey(d_n) && config_map.get("any mob").get(d_n).containsKey(dcp))
            return config_map.get("any mob").get(d_n).get(dcp);

        if(def_map.containsKey("any mob") && def_map.get("any mob").containsKey(d_n) && def_map.get("any mob").get(d_n).containsKey(dcp)
                && !def_map.get("any mob").get(d_n).get(dcp).getAsString().equals("default"))
            return def_map.get("any mob").get(d_n).get(dcp).getAsInt();


        return dcp.getDefaultValue(dSource.typeHolder());
    }

    private static void loadFileOnMap() throws Exception {
        JsonObject main = FileManager.getJOFromFile(getConfigFile());

        //for each entity type
        for (String name : main.asMap().keySet()) {
            JsonObject submain = main.asMap().get(name).getAsJsonObject();

            //for each damage type
            for (String subname : submain.asMap().keySet()) {
                JsonObject subsubmain = submain.asMap().get(subname).getAsJsonObject();

                //for each property
                for (DmgP dcp : DmgP.values()) {
                    if (subsubmain.has(dcp.name) && !subsubmain.get(dcp.name).getAsString().equals("default")) {

                        Map<String, Map<DmgP, Integer>> map = new HashMap<>();
                        Map<DmgP, Integer> submap = new HashMap<>();

                        if (config_map.containsKey(name)) {
                            map = new HashMap<>(config_map.get(name));
                            if(config_map.get(name).containsKey(subname))
                                submap = new HashMap<>(config_map.get(name).get(subname));
                        }

                        submap.put(dcp, subsubmain.get(dcp.name).getAsInt());
                        map.put(subname, submap);
                        config_map.put(name, map);
                    }
                }
            }
        }
    }

    private static void initFile() throws Exception {
        File file = getConfigFile();
        if (file.exists())
            checkFile();
        else {
            System.out.println("damage_tweaker: Damage Config file was not found. Generating a new one..");
            FileManager.createFile(file);
            FileManager.writeJOinFile(file, getDefaultConfigFile());
        }

    }

    private static void checkFile() throws Exception {
        JsonObject main = FileManager.getJOFromFile(getConfigFile());
        JsonObject submain;
        boolean changed = false;

        if(main.has("any mob")) {
            submain = main.get("any mob").getAsJsonObject();

            for (Holder<DamageType> h : dmgRegistry().listElements().toList()) {
                String name = h.getRegisteredName();
                JsonObject jType = getDefaultConfig();
                if (submain.has(name)) {
                    JsonObject subsubmain = submain.getAsJsonObject(name);
                    for (DmgP dcp : DmgP.values())
                        if (!subsubmain.has(dcp.name)) {
                            subsubmain.add(dcp.name, jType.get(dcp.name));
                            System.out.println("damage_tweaker: Founded an incomplete DamageType config. Adding default...");
                            changed = true;
                        }

                } else {
                    System.out.println("damage_tweaker: Founded DamageType without config. Setting default...");
                    submain.add(name, jType);
                    changed = true;
                }
            }
        }
        else {
            main.add("any mob", getAnyMobJO());
            System.out.println("damage_tweaker: Damage Config file doesn't have values for `any mob`," +
                    " which is necessary. Regenerating..");
            changed = true;
        }

        if (changed)
            FileManager.writeJOinFile(getConfigFile(), main);
    }

    private static JsonObject getAnyMobJO(){
        JsonObject submain = new JsonObject();
        dmgRegistry().listElements().forEach((h) -> submain.add(h.getRegisteredName(), getDefaultConfig()));
        return submain;
    }

    private static JsonObject getExampleMobJO(){
        JsonObject submain = new JsonObject();
        submain.add("any damage", getDefaultConfig());
        submain.add("example_damage", getDefaultConfig());
        return submain;
    }

    private static JsonObject getDefaultConfigFile() {
        JsonObject main = new JsonObject();
        main.add("any mob", getAnyMobJO());
        main.add("minecraft:example_mob1", getExampleMobJO());
        main.add("minecraft:example_mob2", getExampleMobJO());
        return main;
    }

    private static JsonObject getDefaultConfig() {
        JsonObject jType = new JsonObject();
        for (DmgP dcp : DmgP.values()) {
            jType.add(dcp.name, jsonUtil.oTj("default"));
        }
        return jType;
    }

    private static RegistryLookup<DamageType> dmgRegistry() {
        return regUtil.gRegistryAccess().lookupOrThrow(Registries.DAMAGE_TYPE);
    }

    private static File getConfigFile() {
        return new File(
                (FMLPaths.CONFIGDIR.get().toString()),
                File.separator + "damage_tweaker.json");
    }

}
