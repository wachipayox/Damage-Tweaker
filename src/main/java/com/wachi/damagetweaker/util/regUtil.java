package com.wachi.damagetweaker.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class regUtil {
    public static RegistryAccess gRegistryAccess() {
        try {
            return servergRegistryAccess();
        } catch (Exception e) {
            return clientgRegistryAccess();
        }
    }

    public static ResourceLocation getEntityRegistryName(Entity entity) {
        EntityType<?> entityType = entity.getType();
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
    }

    private static RegistryAccess servergRegistryAccess() {
        return ServerLifecycleHooks.getCurrentServer().registryAccess();
    }

    private static RegistryAccess clientgRegistryAccess() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level.registryAccess();
    }
}
