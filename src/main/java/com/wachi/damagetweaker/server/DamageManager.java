package com.wachi.damagetweaker.server;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import com.wachi.damagetweaker.Config;
import com.wachi.damagetweaker.DamageTweakerMod;
import com.wachi.damagetweaker.config.DamageConfig;
import com.wachi.damagetweaker.config.DmgP;
import com.wachi.damagetweaker.config.PlayerActionResult;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = DamageTweakerMod.MODID)
public class DamageManager {

    // x entity cooldown to x damage
    public static Map<Entity, Map<DamageSource, Integer>> cooldown_map = new HashMap<>();

    // damage quantity that x entity has taken in this tick
    public static Map<Entity, Map<DamageSource, Integer>> dmg_q = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void updateDmg(ServerTickEvent.Post event) {
        updateCooldownMap();
        addDmgListToCooldownMap();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityHurt(LivingDamageEvent event) {
        Entity ent = event.getEntity();
        DamageSource source = event.getSource();

        if (ent.level().isClientSide())
            return;

        boolean vanillaCooldown = ent.level().getServer().getGameRules()
                .getBoolean(DTGamerules.VANILLA_DAMAGE_INVULNERABILITY);

        if(Config.multiply)
            if ((Config.apply_to_indirect || !source.isIndirect()) && source.getEntity() instanceof ServerPlayer pl) {
                float charge = pl.getAttackStrengthScale(1f);

                if (charge < Config.charge_minimum / 100f) {
                    event.setAmount(event.getAmount() * (float) Config.multiply_value);
                }
                else
                    if(Config.when_not_charged == PlayerActionResult.MULTIPLIED_FULL)
                        event.setAmount(event.getAmount() * (float) Config.multiply_value);
            }

        if (!vanillaCooldown) {
            ent.invulnerableTime = 0;
            addDamageToServer(ent, source);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void cancelIfCooldown(LivingAttackEvent event) {
        Entity ent = event.getEntity();
        DamageSource dSource = event.getSource();
        if (ent.level().isClientSide())
            return;

        boolean flag1 = hasEntityCooldown(ent, dSource);
        boolean flag2 = hasEntityReachedMaxDmgInTickGEN(ent, dSource);
        boolean flag3 = dSource.getEntity() != null && hasEntityReachedMaxDmgInTickSPEC(ent, dSource);
        boolean flag4 = !(dSource.getEntity() instanceof ServerPlayer pl) || attackBarAllowsThis(pl);

        if (flag1 || flag2 || flag3 || !flag4)
            event.setCanceled(true);
    }

    private static void updateCooldownMap() {
        Map<Entity, Map<DamageSource, Integer>> new_cooldown_map = new HashMap<>(cooldown_map);

        for (Entity i_ent : cooldown_map.keySet()) {
            Map<DamageSource, Integer> i_map = new HashMap<>(cooldown_map.get(i_ent));

            for (DamageSource dSource : i_map.keySet()) {
                int i = i_map.get(dSource);
                i++;
                if (i >= DamageConfig.getValue(dSource, DmgP.COOLDOWN))
                    new_cooldown_map.get(i_ent).remove(dSource);
                else
                    new_cooldown_map.get(i_ent).put(dSource, i);
            }
            if (new_cooldown_map.get(i_ent).isEmpty())
                new_cooldown_map.remove(i_ent);
        }

        cooldown_map = new_cooldown_map;
    }

    private static void addDmgListToCooldownMap() {
        for (Entity i_ent : dmg_q.keySet()) {
            Map<DamageSource, Integer> i_map = new HashMap<>();
            if (cooldown_map.containsKey(i_ent))
                i_map = cooldown_map.get(i_ent);

            for (DamageSource dSource : dmg_q.get(i_ent).keySet()) {
                if (DamageConfig.getValue(dSource, DmgP.COOLDOWN) > 0)
                    i_map.putIfAbsent(dSource, -DamageConfig.getValue(dSource, DmgP.PRE_COOLDOWN));
            }

            cooldown_map.put(i_ent, i_map);
        }

        dmg_q.clear();
    }

    private static boolean attackBarAllowsThis(ServerPlayer pl){
        return Config.when_not_charged != PlayerActionResult.CANCEL_ATTACK
                || pl.getAttackStrengthScale(1f) >= Config.charge_minimum / 100f;
    }

    private static boolean hasEntityCooldown(Entity ent, DamageSource dSource) {
        if(cooldown_map.containsKey(ent))
            for(DamageSource i_src : cooldown_map.get(ent).keySet())
                if(i_src.typeHolder().equals(dSource.typeHolder()))
                    return cooldown_map.get(ent).get(i_src) >= 0;
        return false;
    }

    private static boolean hasEntityReachedMaxDmgInTickGEN(Entity ent, DamageSource dSource) {
        int v = DamageConfig.getValue(dSource, DmgP.MAX_IN_TICK_GEN);
        return v != -1 && getEntDmgByTypeThisTick(ent, dSource, true) >= v;
    }

    private static boolean hasEntityReachedMaxDmgInTickSPEC(Entity ent, DamageSource dSource) {
        int v = DamageConfig.getValue(dSource, DmgP.MAX_IN_TICK_SPEC);
        return v != -1 && getEntDmgByTypeThisTick(ent, dSource, false) >= v;
    }

    private static void addDamageToServer(Entity victim, DamageSource dSource) {
        addDamageToDmgQuantity(victim, dSource);
    }

    private static int getEntDmgByTypeThisTick(Entity victim, DamageSource dSource, boolean ignore_src_ent) {
        int i = 0;
        if(dmg_q.containsKey(victim))
            for(DamageSource i_src : dmg_q.get(victim).keySet())
                if(areSameSource(i_src, dSource, ignore_src_ent))
                    i += dmg_q.get(victim).get(i_src);

        return i;
    }

    private static void addDamageToDmgQuantity(Entity victim, DamageSource dSource) {
        boolean flag = false;
        Map<DamageSource, Integer> map = new HashMap<>();
        if(dmg_q.containsKey(victim)) {
            map = new HashMap<>(dmg_q.get(victim));
            for (DamageSource i_src : dmg_q.get(victim).keySet())
                if (areSameSource(i_src, dSource, true)) {
                    flag = true;
                    map.put(i_src, dmg_q.get(victim).get(i_src) + 1);
                    break;
                }
        }

        if(!flag)
            map.put(dSource, 1);

        dmg_q.put(victim, map);
    }

    private static boolean areSameSource(DamageSource src, DamageSource src2, boolean ignore_src_ent){
        return (src.type().equals(src2.type())
                && (ignore_src_ent || (uuidOrNull(src.getEntity()).equals(uuidOrNull(src2.getEntity())))));
    }

    private static String uuidOrNull(Entity ent){
        return (ent != null) ? ent.getUUID().toString() : "null";
    }

}
