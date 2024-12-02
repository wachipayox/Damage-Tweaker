package com.wachi.damagetweaker;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import com.wachi.damagetweaker.config.PlayerActionResult;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = DamageTweakerMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final String t = "This value will be the option chosen when you type `default`.";

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder()
            .comment("-----------------------")
            .comment("[DEFAULT VALUES FOR THE OTHER CONFIG]")
            .comment("-----------------------")
            .comment("This file section is for the default values, for specific types or entities go to damage_tweaker.json")
            ;

    private static final ModConfigSpec.IntValue DEF_CDW = BUILDER
            .comment("")
            .comment("The cooldown is the time in ticks when the entity can't be hit by a certain damage type")
            .comment(t)
            .defineInRange("default cooldown", 10, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DEF_PRE_CDW = BUILDER
            .comment("")
            .comment("The pre cooldown is the time in ticks that the cooldown waits to starts after the first hit")
            .comment(t)
            .defineInRange("default pre_cooldown", 0, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DEF_MIT_GEN = BUILDER
            .comment("")
            .comment("The 'max in tick general' is the max hits (IN GENERAL) of a certain damage type an entity can receive in the same tick before being immune (-1 = ∞)")
            .comment(t)
            .defineInRange("default max_in_tick_general", 1, -1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue DEF_MIT_SPEC = BUILDER
            .comment("")
            .comment("The 'max in tick specific' is the max hits (BY THE SAME ENTITY) of a certain damage type an entity can receive in the same tick before being immune (TO THE ENTITY)(-1 = ∞)")
            .comment(t)
            .defineInRange("default max_in_tick_specific", 1, -1, Integer.MAX_VALUE);

    private static final ModConfigSpec.EnumValue<PlayerActionResult> NOTCHARGEDRESULT = BUILDER
            .comment("-----------------------")
            .comment("[PLAYER ATTACK SPEED]")
            .comment("-----------------------")
            .comment("VANILLA_ATTACK(default) will make the vanilla damage (the % damage as the % of the attack bar, unless you have mods that modify this))")
            .comment("MULTIPLIED_VANILLA will make like the vanilla-attack but multiplied by [the next config value](see below)")
            .comment("FULL_ATTACK will make the same damage as the full attack bar damage, so is like disabling the attack bar")
            .comment("MULTIPLIED_FULL will make like the full-attack but multiplied by [the next config value](see below)")
            .comment("CANCEL_ATTACK will cancel completely the attack")
            .defineEnum("When player attacks without full charge the attack bar", PlayerActionResult.VANILLA_ATTACK);

    private static final ModConfigSpec.DoubleValue MULTIPLIED_VALUE = BUILDER
            .comment("")
            .comment("If in the previous option you chose MULTIPLIED_VANILLA OR MULTIPLIED_FULL, this is the number by which the vanilla/full attack multiplies")
            .defineInRange("multiplied damage multiplication", 1.0, 0.0, Double.MAX_VALUE);

    private static final ModConfigSpec.IntValue CHARGE_MINIMUM = BUILDER
            .comment("")
            .comment("This number is the minimum charge of the attack bar(percentage) required to act as full charge")
            .comment("P.S.: This not infer in the attack speed of players, its only for modifying results")
            .defineInRange("attack bar full charge minimum", 100, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.BooleanValue APPLY_TO_INDIRECT = BUILDER
            .comment("")
            .comment("This condition represents if the 3 options above affects ranged/indirect attacks")
            .comment("For example, if you type true, the bow charge will behave like the attack bar charge")
            .define("Config affects projectiles/indirect attacks", false);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int def_cdw;
    public static int def_pre_cdw;
    public static int def_mit_gen;
    public static int def_mit_spec;
    public static PlayerActionResult when_not_charged;
    public static double multiply_value;
    public static int charge_minimum;
    public static boolean apply_to_indirect;
    public static boolean multiply;
    public static boolean disable_bar;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        def_cdw = DEF_CDW.get();
        def_pre_cdw = DEF_PRE_CDW.get();
        def_mit_gen = DEF_MIT_GEN.get();
        def_mit_spec = DEF_MIT_SPEC.get();
        when_not_charged = NOTCHARGEDRESULT.get();
        multiply_value = MULTIPLIED_VALUE.get();
        charge_minimum = CHARGE_MINIMUM.get();
        apply_to_indirect = APPLY_TO_INDIRECT.get();

        multiply = when_not_charged == PlayerActionResult.MULTIPLIED_VANILLA
                || when_not_charged ==  PlayerActionResult.MULTIPLIED_FULL;

        disable_bar = when_not_charged == PlayerActionResult.FULL_ATTACK
                || when_not_charged ==  PlayerActionResult.MULTIPLIED_FULL;
    }
}
