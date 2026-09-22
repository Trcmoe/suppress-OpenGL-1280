package io.github.adamraichu.suppressopengl1280.mixin;

import io.github.adamraichu.suppressopengl1280.GlDebugMessageSuppressor;
import io.github.adamraichu.suppressopengl1280.config.ConfigOptions;
import me.shedaniel.autoconfig.AutoConfig;

import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.renderpearl.backend.opengl.GlDebug;

@Mixin(GlDebug.class)
public abstract class GlDebugMixin {
  private static final GlDebugMessageSuppressor SUPPRESSOR = new GlDebugMessageSuppressor();

  @Inject(at = @At(value = "HEAD"), method = "printDebugLog", cancellable = true)
  private static void suppressMessage(int source, int type, int id, int severity, int messageLength, long message,
      long l,
      CallbackInfo ci) {
    ConfigOptions config = AutoConfig.getConfigHolder(ConfigOptions.class).getConfig();

    String messageText = MemoryUtil.memUTF8(message, messageLength);
    if (SUPPRESSOR.shouldSuppress(source, type, id, severity, messageText,
        errorId -> isSuppressionEnabled(config, errorId))) {
      ci.cancel();
    }
  }

  private static boolean isSuppressionEnabled(ConfigOptions config, int errorId) {
    return switch (errorId) {
      case 1280 -> config.suppress1280;
      case 1281 -> config.suppress1281;
      case 1282 -> config.suppress1282;
      case 2 -> config.suppress2;
      default -> false;
    };
  }
}
