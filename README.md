# Suppress OpenGL Errors

> This mod does not fix OpenGL errors. It keeps repeated driver messages from spamming the log while preserving the first copy for diagnosis.

The mod watches OpenGL messages whose source is `API` and whose type is `ERROR`. For each enabled error ID, the first unique message is logged normally; only later messages with the same source, type, ID, severity, and text are suppressed. Different diagnostics that happen to share an ID remain visible.

The following IDs are enabled by default and can be changed through the Mod Menu configuration screen:

- `2` (vendor-specific; historically reported by Blur)
- `1280` (`GL_INVALID_ENUM`)
- `1281` (`GL_INVALID_VALUE`)
- `1282` (`GL_INVALID_OPERATION`)
- `1286` (`GL_INVALID_FRAMEBUFFER_OPERATION`, which can occur in shader/framebuffer paths)

`GL_OUT_OF_MEMORY` (`1285`) is intentionally never suppressed.

## Minecraft 26.3 rendering backends

Version 1.4.0 targets Fabric on Minecraft 26.3 and Java 25. It operates on Minecraft's RenderPearl OpenGL debug callback and is compatible with the OpenGL paths used by Sodium and Iris.

After Minecraft creates its graphics device, the mod reads the selected backend from RenderPearl. OpenGL keeps the suppression feature and configuration active. Vulkan, or any future non-OpenGL backend, disables all suppression logic and skips configuration initialization. A single informational log entry records that decision.

Fabric resolves mod metadata and mixins before Minecraft selects a graphics backend, so a mod cannot remove its own JAR from Fabric Loader at that point. “Disabled” therefore means that no OpenGL messages are read or filtered and no Cloth Config data is initialized; the mod may still appear in the loaded-mod list.

The mod is also naturally inactive when Sodium creates a no-error OpenGL context, because that environment does not produce this OpenGL debug stream. Sodium's no-error context is hardware- and driver-dependent, so it does not make this mod redundant for every Sodium/Iris installation.

See the [project wiki](https://github.com/AdamRaichu/suppress-OpenGL-1280/wiki) for more background on the original error.
