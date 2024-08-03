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
* Velocity 3.3.0 (latest build)
* Supports Minecraft 1.7.2 &ndash; 1.21

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

