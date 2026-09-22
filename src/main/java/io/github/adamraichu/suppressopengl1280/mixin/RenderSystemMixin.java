package io.github.adamraichu.suppressopengl1280.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.device.GpuDevice;
import io.github.adamraichu.suppressopengl1280.GpuBackendRuntime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public abstract class RenderSystemMixin {
  @Inject(method = "initRenderer", at = @At("TAIL"))
  private static void confirmGpuBackend(GpuDevice device, CallbackInfo ci) {
    GpuBackendRuntime.confirmRenderer(device);
  }
}
