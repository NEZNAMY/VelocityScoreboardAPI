<!--suppress ALL -->
<p align="center">
    <img src="images/banner.png" alt="Velocity Scoreboard API" />
    <a href="https://github.com/NEZNAMY/VelocityScoreboardAPI/actions/workflows/ci.yml">
        <img src="https://img.shields.io/github/actions/workflow/status/NEZNAMY/VelocityScoreboardAPI/ci.yml?branch=master&logo=github"/>
    </a> 
    <a href="https://repo.william278.net/#/releases/net/william278/velocityscoreboardapi/">
        <img src="https://repo.william278.net/api/badge/latest/releases/net/william278/velocityscoreboardapi?color=00fb9a&name=Maven&prefix=v"/>
    </a>
</p>
<br/>

**VelocityScoreboardAPI** is an API plugin by NEZNAMY ([TAB](https://github.com/NEZNAMY/TAB)), William278 ([Velocitab](https://github.com/WiIIiam278/Velocitab)) and AlexDev_  ([Velocitab](https://github.com/WiIIiam278/Velocitab)), providing Velocity proxy servers with an API for interfacing with Scoreboard. This allows developers a standard way of performing scoreboard/team operations in lieu of an API provided by Velocity. This plugin does not provide any features on its own.

## Server owners
Please add the plugin to your Velocity server's plugins folder. Make sure you are running the latest version of Velocity (check the requirements below).

### Requirements
* Java 17+
* Velocity 3.4.0 build 539+

## Developers
VelocityScoreboardAPI is available [on Maven](https://repo.william278.net/#/releases/net/william278/velocityscoreboardapi/). You can browse the Javadocs [here](https://repo.william278.net/javadoc/releases/net/william278/velocityscoreboardapi/latest).

First, add the Maven repository to your `build.gradle` file:
```groovy
repositories {
    maven { url "https://repo.william278.net/releases" }
    // maven { url "https://repo.william278.net/snapshots" } // For snapshot builds
}
```

Then, add the dependency itself. Replace `VERSION` with the latest version. (e.g., `1.0`)

```groovy
dependencies {
    implementation "net.william278:velocityscoreboardapi:VERSION"
}
```

Using Maven/something else? There's instructions on how to include VelocityScoreboardAPI on [the repo browser](https://repo.william278.net/#/releases/net/william278/velocityscoreboardapi).

## Config
A configuration file is provided for the plugin. The configuration file is located at `plugins/velocity-scoreboard-api/config.yml`. The configuration file is automatically generated when the plugin is loaded for the first time. The configuration file contains the following options:

```yaml
# Whether scoreboard API events should be fired
call_scoreboard_events: true
# Whether to log invalid packets received from downstream servers
print_invalid_downstream_packet_warnings: true
```

## Detailed overview of the plugin
* **Proxy vs backend scoreboard** - Every player has 2 scoreboards.
  * **Backend scoreboard** - A read-only tracker of objectives and teams coming from backend server (also used internally for proper compatibility).
  * **Proxy scoreboard** - Scoreboard fully editable using the API. All teams (and their entries) and objectives will take priority over backend scoreboard. Every player has their own proxy scoreboard instance.
* **Event system** - Events called every time an objective or team is registered, unregistered or player is added/removed from a team. Can be disabled in config.
* **Consumers for updating properties** - In order to allow performing multiple changes at once with a single packet instead of sending update packet for each individual change, all updates support methods using Consumer<?>. Unlike with an all-arg method, this offers great compatibility in case something is added in future MC versions without having to explicitly support plugins using old method.
* **High performance** - Tested on a huge server with 1500+ players using spark, the plugin has reached perfect performance. Some code is slightly complex because of this, however, simplifying code at the cost of worse performance can be done any time if decided without having to worry about performance.
* **Invalid packet cancellation** - Invalid backend packets are cancelled or fixed to prevent warnings/errors/disconnects in the client when an invalid backend packet is received.
* **Custom TextHolder class** - For allowing to configure both legacy texts and modern components, custom class `TextHolder` is used as value, allowing users to explicitly define per-version values without having to check for client version (if one of the two values is not specified, it will be calculated)