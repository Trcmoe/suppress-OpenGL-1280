package io.github.adamraichu.suppressopengl1280;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GpuBackendStateTest {
  @Test
  void undeterminedStatePermitsEarlyOpenGlCallbacks() {
    GpuBackendState state = new GpuBackendState();

    assertEquals(GpuBackendState.Status.UNDETERMINED, state.status());
    assertTrue(state.permitsRuntimeLogic());
  }

  @Test
  void exactOpenGlBackendEnablesRuntimeLogic() {
    GpuBackendState state = new GpuBackendState();

    assertEquals(GpuBackendState.Status.OPENGL_ACTIVE, state.confirmBackend("OpenGL"));
    assertTrue(state.permitsRuntimeLogic());
  }

  @Test
  void vulkanBackendDisablesRuntimeLogic() {
    GpuBackendState state = new GpuBackendState();

    assertEquals(GpuBackendState.Status.DISABLED, state.confirmBackend("Vulkan"));
    assertFalse(state.permitsRuntimeLogic());
  }

  @Test
  void unknownFutureBackendIsDisabled() {
    GpuBackendState state = new GpuBackendState();

    assertEquals(GpuBackendState.Status.DISABLED, state.confirmBackend("WebGPU"));
    assertFalse(state.permitsRuntimeLogic());
  }

  @Test
  void firstBackendConfirmationCannotBeReversed() {
    GpuBackendState state = new GpuBackendState();

    state.confirmBackend("Vulkan");

    assertEquals(GpuBackendState.Status.DISABLED, state.confirmBackend("OpenGL"));
    assertFalse(state.permitsRuntimeLogic());
  }
}
