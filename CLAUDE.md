# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Syn is a Minecraft Bukkit/Spigot/Paper/Folia plugin that manages player access via a verification code allowlist system. It includes ban management, guest mode, event logging, and optional DiscordSRV integration.

## Build & Test Commands

```bash
# Build the plugin JAR
./gradlew build

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.rock_mc.syn.CmdExecutorTest"

# Run a single test method
./gradlew test --tests "com.rock_mc.syn.CmdExecutorTest.testMethodName"

# Copy built JAR to local test server
./gradlew testOnServer
```

Java 17 is required. Tests use JUnit 5 with MockBukkit.

## Architecture

**Entry point:** `Syn.java` — extends `JavaPlugin`. Initializes `ConfigManager`, `DbManager`, `CmdManager`, `LogManager`, registers event listeners, and detects Folia at runtime.

**Command flow:** All commands go through `/syn`. `CmdExecutor` parses the subcommand and delegates to the corresponding API class in `api/` (e.g., `Verify`, `Ban`, `GenCode`). Each API class has a static `exec()` method that takes the plugin instance, a logger, the player, and args. All API calls are synchronized on `Syn.apiLock`.

**Database layer:** `DbManager` wraps a `Database` interface (currently only `SQLite` implementation). It adds thread-safe caching (`synchronized(dbLock)`) over all DB operations. The DB stores verification codes, allowlist, banlist, failed attempts, player info, and event logs.

**Event handling:** `EventListener` handles Bukkit events (login, join, quit, movement, damage, chat). Unverified players are frozen in place via `freezePlayerMap` and given a time window (`WaitVerify`) to enter their code. `DiscordListener` handles DiscordSRV integration (soft dependency).

**Custom plugin events:** `JoinEvent` and `KickEvent` in `event/pluginevent/` are fired via `PluginEventSender` to decouple verification logic from player kick/join actions.

**Logging:** `LogManager` holds static logger instances (`LoggerPlugin` for in-game, `LoggerDiscord` for Discord). Both implement the `Logger` interface.

**Config:** `ConfigManager` loads `config.yml`. Config key constants are in `Config.java`.

## Key Conventions

- The plugin's user-facing messages are in Traditional Chinese (繁體中文).
- Package is `com.rock_mc.syn`. The `utlis` package name (typo of "utils") is intentional — do not rename.
- Test classes extend `PluginTest` which sets up MockBukkit server and loads the plugin.
- The `config.yml` is processed through Gradle's `processResources` to inject the version into `plugin.yml`.
