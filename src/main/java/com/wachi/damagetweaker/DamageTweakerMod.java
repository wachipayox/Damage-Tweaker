package com.wachi.damagetweaker;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(DamageTweakerMod.MODID)
public class DamageTweakerMod
{
    public static final String MODID = "damage_tweaker";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DamageTweakerMod(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);

        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

}
