package com.wachi.damagetweaker.config;

import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import com.wachi.damagetweaker.Config;

public enum DmgP {

    COOLDOWN("cooldown"), // Cooldown in ticks
    PRE_COOLDOWN("pre_cooldown"), // Ticks it takes to start the cooldown
    MAX_IN_TICK_GEN("max_in_tick_general"), // Maximum times damage can be applied in one tick in general (-1 = ∞).
    MAX_IN_TICK_SPEC("max_in_tick_specific") // ^ but the times are only count by a specific UUID
    ;

    public final String name;

    DmgP(String name) {
        this.name = name;
    }

    public int getDefaultValue(Holder<DamageType> h) {
        boolean flag = h.is(DamageTypeTags.BYPASSES_COOLDOWN);

        return switch (this) {
            case COOLDOWN -> flag ? 0 : Config.def_cdw;
            case PRE_COOLDOWN -> Config.def_pre_cdw;
            case MAX_IN_TICK_GEN -> flag ? -1 : Config.def_mit_gen;
            case MAX_IN_TICK_SPEC -> flag ? -1 : Config.def_mit_spec;
        };
    }
}
