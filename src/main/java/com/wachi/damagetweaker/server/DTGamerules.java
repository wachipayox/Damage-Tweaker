
package com.wachi.damagetweaker.server;

import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class DTGamerules {
	public static GameRules.Key<GameRules.BooleanValue> VANILLA_DAMAGE_INVULNERABILITY;

	@SubscribeEvent
	public static void registerGameRules(FMLCommonSetupEvent event) {
		VANILLA_DAMAGE_INVULNERABILITY = GameRules.register("vanillaDamageInvulnerability", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
	}
}

