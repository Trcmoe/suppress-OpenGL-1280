# Changelog

## 1.3.0 - 2026-09-23

- Port to Minecraft 26.3, Fabric Loader 0.19.5, Java 25, and the new RenderPearl OpenGL backend.
- Suppress only exact repeats of OpenGL API error messages, preserving distinct diagnostics that share an ID.
- Add configurable suppression for error 1286 (`GL_INVALID_FRAMEBUFFER_OPERATION`) for shader/framebuffer log spam.
- Never suppress non-error or non-API messages, including performance messages that reuse a configured ID.
- Add unit tests for the suppression policy.
- Remove the unused aggregate Fabric API dependency; Cloth Config still brings only the Fabric API modules it needs.
- Update local and CI toolchains for Java 25 and current Gradle Actions.

## 1.2.1

Fix the error in the language files which caused error #2 to show as error #1282 in the config files.

## 1.2.0

Suppress OpenGL error #2.

Thank you [@nijahplays](https://github.com/AdamRaichu/suppress-OpenGL-1280/issues/3) for requesting suppression for 2.

Change mod name to `Suppress OpenGL Errors`.

## 1.1.1

Change mod name to `Suppress OpenGL Error 128(0-2)`.

## 1.1.0

Now suppresses 1281 and 1282 as well, but error suppression can be configured for each error using cloth config.

(Mod Menu integration is supported.)

Thank you [@shad0wolf0](https://github.com/AdamRaichu/suppress-OpenGL-1280/issues/1) for requesting suppression for 1281 and 1282.

## 1.0.0

First working version.
The first time the error is detected, the mod's warning message is logged, then the original error is logged.
All subsequent OpenGL errors with the id `1280` are cancelled before logging.
