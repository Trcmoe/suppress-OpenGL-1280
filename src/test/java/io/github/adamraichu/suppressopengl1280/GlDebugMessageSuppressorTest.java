package io.github.adamraichu.suppressopengl1280;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

  @Test
  void evictsTheOldestSignatureWhenCapacityIsReached() {
    GlDebugMessageSuppressor suppressor = new GlDebugMessageSuppressor(2);

    assertFalse(suppressor.shouldSuppress(API, ERROR, 2, 1, "first", id -> true));
    assertFalse(suppressor.shouldSuppress(API, ERROR, 2, 1, "second", id -> true));
    assertTrue(suppressor.shouldSuppress(API, ERROR, 2, 1, "first", id -> true));
    assertFalse(suppressor.shouldSuppress(API, ERROR, 2, 1, "third", id -> true));
    assertFalse(suppressor.shouldSuppress(API, ERROR, 2, 1, "first", id -> true));
  }

  @Test
  void rejectsNonPositiveCacheCapacity() {
    assertThrows(IllegalArgumentException.class, () -> new GlDebugMessageSuppressor(0));
  }

  @Test
  void allowsOnlyOneConcurrentFirstOccurrence() throws Exception {
    GlDebugMessageSuppressor suppressor = new GlDebugMessageSuppressor();
    ExecutorService executor = Executors.newFixedThreadPool(8);
    CountDownLatch start = new CountDownLatch(1);
    try {
      var results = java.util.stream.IntStream.range(0, 32)
          .mapToObj(ignored -> executor.submit(() -> {
            start.await();
            return suppressor.shouldSuppress(API, ERROR, 1280, 1, "same message", id -> true);
          }))
          .toList();
      start.countDown();

      long allowed = 0;
      for (var result : results) {
        if (!result.get()) {
          allowed++;
        }
      }
      assertEquals(1, allowed);
    } finally {
      executor.shutdown();
      assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }
  }
}
