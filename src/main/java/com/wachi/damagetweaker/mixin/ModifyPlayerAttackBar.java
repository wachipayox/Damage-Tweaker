package com.wachi.damagetweaker.mixin;

import net.minecraft.world.entity.player.Player;
import com.wachi.damagetweaker.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class ModifyPlayerAttackBar {

    @Inject(method = "getAttackStrengthScale", at = @At("HEAD"), cancellable = true)
    public void removeAttackBar(CallbackInfoReturnable<Float> ci){
        if(Config.disable_bar)
            ci.setReturnValue(1f);
    }

}
