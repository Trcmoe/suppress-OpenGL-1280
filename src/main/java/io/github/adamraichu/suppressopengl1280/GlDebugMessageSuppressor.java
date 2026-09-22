package io.github.adamraichu.suppressopengl1280;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntPredicate;

/**
 * Tracks OpenGL API error messages that have already been allowed through once using a bounded
 * FIFO cache. An evicted signature can be allowed through again.
 *
 * <p>This class deliberately uses OpenGL's numeric constants so it can be tested without
 * loading Minecraft or LWJGL.</p>
 */
public final class GlDebugMessageSuppressor {
  public static final int GL_DEBUG_SOURCE_API = 0x8246;
  public static final int GL_DEBUG_TYPE_ERROR = 0x824C;
  private static final int DEFAULT_CAPACITY = 256;

  private final int capacity;
  private final Map<MessageSignature, Boolean> seenMessages = new LinkedHashMap<>();

  public GlDebugMessageSuppressor() {
    this(DEFAULT_CAPACITY);
  }

  GlDebugMessageSuppressor(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("capacity must be positive");
    }
    this.capacity = capacity;
  }

  /**
   * Returns whether this callback should be cancelled. The first enabled matching signature is
   * always allowed through; only identical later callbacks are suppressed.
   */
  public synchronized boolean shouldSuppress(int source, int type, int id, int severity, String message,
      IntPredicate isSuppressionEnabled) {
    if (source != GL_DEBUG_SOURCE_API || type != GL_DEBUG_TYPE_ERROR
        || !isSuppressionEnabled.test(id)) {
      return false;
    }

    MessageSignature signature = new MessageSignature(source, type, id, severity, message);
    if (seenMessages.containsKey(signature)) {
      return true;
    }

    if (seenMessages.size() == capacity) {
      Iterator<MessageSignature> iterator = seenMessages.keySet().iterator();
      iterator.next();
      iterator.remove();
    }
    seenMessages.put(signature, Boolean.TRUE);
    return false;
  }

  private record MessageSignature(int source, int type, int id, int severity, String message) {
  }
}
