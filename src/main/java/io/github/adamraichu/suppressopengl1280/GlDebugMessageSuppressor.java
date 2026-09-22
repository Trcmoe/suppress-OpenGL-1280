package io.github.adamraichu.suppressopengl1280;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntPredicate;

/**
 * Tracks OpenGL API error messages that have already been allowed through once.
 *
 * <p>This class deliberately uses OpenGL's numeric constants so it can be tested without
 * loading Minecraft or LWJGL.</p>
 */
public final class GlDebugMessageSuppressor {
  public static final int GL_DEBUG_SOURCE_API = 0x8246;
  public static final int GL_DEBUG_TYPE_ERROR = 0x824C;

  private final Set<MessageSignature> seenMessages = ConcurrentHashMap.newKeySet();

  /**
   * Returns whether this callback should be cancelled. The first enabled matching signature is
   * always allowed through; only identical later callbacks are suppressed.
   */
  public boolean shouldSuppress(int source, int type, int id, int severity, String message,
      IntPredicate isSuppressionEnabled) {
    if (source != GL_DEBUG_SOURCE_API || type != GL_DEBUG_TYPE_ERROR
        || !isSuppressionEnabled.test(id)) {
      return false;
    }

    return !seenMessages.add(new MessageSignature(source, type, id, severity, message));
  }

  private record MessageSignature(int source, int type, int id, int severity, String message) {
  }
}
