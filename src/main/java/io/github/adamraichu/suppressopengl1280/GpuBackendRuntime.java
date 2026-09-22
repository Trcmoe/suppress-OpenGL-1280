package io.github.adamraichu.suppressopengl1280;

import com.mojang.renderpearl.api.device.GpuDevice;
import io.github.adamraichu.suppressopengl1280.config.ConfigOptions;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Coordinates backend gating and the lazily-created OpenGL-only configuration. */
public final class GpuBackendRuntime {
  private static final Logger LOGGER = LoggerFactory.getLogger("suppressopengl1280");
  private static final GpuBackendState BACKEND_STATE = new GpuBackendState();
  private static final Object CONFIGURATION_LOCK = new Object();
  private static boolean configurationRegistered;

  private GpuBackendRuntime() {
  }

  public static boolean permitsRuntimeLogic() {
    return BACKEND_STATE.permitsRuntimeLogic();
  }

  /** Called after RenderSystem has accepted the final GPU device. */
  public static void confirmRenderer(GpuDevice device) {
    String backendName = device.getDeviceInfo().backendName();
    synchronized (CONFIGURATION_LOCK) {
      if (BACKEND_STATE.status() != GpuBackendState.Status.UNDETERMINED) {
        return;
      }
      GpuBackendState.Status status = BACKEND_STATE.confirmBackend(backendName);
      if (status == GpuBackendState.Status.OPENGL_ACTIVE) {
        LOGGER.info("OpenGL renderer confirmed; enabling OpenGL error suppression.");
        ensureConfigurationRegisteredLocked();
      } else {
        LOGGER.info("Renderer backend '{}' confirmed; disabling Suppress OpenGL Errors runtime logic.", backendName);
      }
    }
  }

  /**
   * Registers the configuration at most once. An undetermined state is allowed for the early
   * OpenGL callback path; a confirmed non-OpenGL renderer never reaches AutoConfig.
   */
  public static boolean ensureConfigurationRegistered() {
    synchronized (CONFIGURATION_LOCK) {
      if (!permitsRuntimeLogic()) {
        return false;
      }
      ensureConfigurationRegisteredLocked();
      return true;
    }
  }

  private static void ensureConfigurationRegisteredLocked() {
    if (!configurationRegistered) {
      AutoConfig.register(ConfigOptions.class, GsonConfigSerializer::new);
      configurationRegistered = true;
    }
  }
}
