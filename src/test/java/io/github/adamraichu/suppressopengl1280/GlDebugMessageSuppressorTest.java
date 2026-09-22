package io.github.adamraichu.suppressopengl1280;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;

class GlDebugMessageSuppressorTest {
  private static final int API = GlDebugMessageSuppressor.GL_DEBUG_SOURCE_API;
  private static final int ERROR = GlDebugMessageSuppressor.GL_DEBUG_TYPE_ERROR;

  @Test
  void allowsFirstMessageAndSuppressesAnIdenticalRepeat() {
    GlDebugMessageSuppressor suppressor = new GlDebugMessageSuppressor();

    assertFalse(suppressor.shouldSuppress(API, ERROR, 1280, 1, "invalid enum", Set.of(1280)::contains));
    assertTrue(suppressor.shouldSuppress(API, ERROR, 1280, 1, "invalid enum", Set.of(1280)::contains));
  }

  @Test
  void allowsDifferentMessagesWithTheSameId() {
    GlDebugMessageSuppressor suppressor = new GlDebugMessageSuppressor();

    assertFalse(suppressor.shouldSuppress(API, ERROR, 2, 1, "first diagnostic", Set.of(2)::contains));
    assertFalse(suppressor.shouldSuppress(API, ERROR, 2, 1, "second diagnostic", Set.of(2)::contains));
  }

  @Test
  void allowsNonApiAndNonErrorMessages() {
    GlDebugMessageSuppressor suppressor = new GlDebugMessageSuppressor();

    assertFalse(suppressor.shouldSuppress(0, ERROR, 2, 1, "message", Set.of(2)::contains));
    assertFalse(suppressor.shouldSuppress(API, 0, 2, 1, "message", Set.of(2)::contains));
  }

  @Test
  void allowsDisabledTargets() {
    GlDebugMessageSuppressor suppressor = new GlDebugMessageSuppressor();

    assertFalse(suppressor.shouldSuppress(API, ERROR, 1280, 1, "message", id -> false));
  }

  @Test
  void suppressesRepeatedInvalidFramebufferOperation() {
    GlDebugMessageSuppressor suppressor = new GlDebugMessageSuppressor();

    assertFalse(suppressor.shouldSuppress(API, ERROR, 1286, 1, "framebuffer incomplete", Set.of(1286)::contains));
    assertTrue(suppressor.shouldSuppress(API, ERROR, 1286, 1, "framebuffer incomplete", Set.of(1286)::contains));
  }
}
