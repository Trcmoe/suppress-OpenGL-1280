package io.github.adamraichu.suppressopengl1280;

/**
 * Thread-safe classification of the renderer selected by Minecraft.
 *
 * <p>The undetermined state intentionally permits OpenGL debug callbacks: those callbacks can be
 * installed while the OpenGL device is being built, before {@code RenderSystem.tryGetDevice()}
 * can report the device. Once Minecraft confirms another backend, all mod runtime behavior is
 * disabled for the rest of that renderer lifetime.</p>
 */
public final class GpuBackendState {
  public enum Status {
    UNDETERMINED,
    OPENGL_ACTIVE,
    DISABLED
  }

  private Status status = Status.UNDETERMINED;

  public synchronized Status status() {
    return status;
  }

  /** Returns whether code that runs on OpenGL debug callbacks may proceed. */
  public synchronized boolean permitsRuntimeLogic() {
    return status != Status.DISABLED;
  }

  /**
   * Records Minecraft's selected backend. The first confirmation is final; repeated calls are
   * idempotent. Only the exact public backend name "OpenGL" is active.
   */
  public synchronized Status confirmBackend(String backendName) {
    if (status != Status.UNDETERMINED) {
      return status;
    }
    status = "OpenGL".equals(backendName) ? Status.OPENGL_ACTIVE : Status.DISABLED;
    return status;
  }
}
