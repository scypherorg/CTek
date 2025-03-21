package org.scypher.ctek.mixin;

import net.minecraft.server.world.ServerWorld;
import org.scypher.ctek.CTek;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method = "save", at = @At("TAIL"))
    private void onWorldSave(CallbackInfo ci) {
        if(((ServerWorld)(Object)this).getRegistryKey() == ServerWorld.OVERWORLD)
            CTek.SaveData();
    }
}