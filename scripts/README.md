# Java 25 / Velocity 4.2 verification

Build and check source license headers (JDK 25):

```sh
bash ./gradlew clean build --refresh-dependencies
```

`build` runs Spotless license checks. `licenseFormat` and `updateLicenses` are
compatibility aliases for `spotlessApply`; checks do not silently rewrite source files.

Run the standalone integration probe against the **built plugin JAR** and the
**distributed Velocity 4.2.0 build 30 JAR** (not its Maven compile-only artifact):

```sh
java -cp '/path/to/VelocityScoreboardAPI.jar:/path/to/velocity-4.2.0-30.jar' scripts/ProtocolSmoke.java
```

The probe checks packet registration, IDs, factories and encode/decode round trips
for display objectives, objective removal, score set/reset and team removal/member
updates on 26.1, 26.2 and 26.3. It fails on the previous 26.2 support ceiling.
The 26.3 IDs come from the official server's generated packet report:

```sh
java -DbundlerMainClass=net.minecraft.data.Main -jar server-26.3.jar --reports --output generated
```

`generated/reports/packets.json` gives display objective `0x64`, objective `0x6C`,
score `0x70`, reset score `0x50`, and team `0x6F`. Server download SHA-1:
`33680f5f2ac32864d6d7cf5e56a705fdb3e05f4c`.

`VsaMojangCodecSmoke.java` additionally decodes these sample payloads with Mojang's
actual 26.3 `STREAM_CODEC` implementations and re-encodes them byte-for-byte.
Run with the extracted `versions/26.3/server-26.3.jar`, the plugin JAR, the Velocity
JAR and **all** extracted `libraries/**/*.jar` on the classpath, in that order:

```sh
java -Xmx512m -cp "$CLASSPATH" scripts/VsaMojangCodecSmoke.java
```

These probes cover removal/member-update packets and scores without optional
components/number formats, not every objective/team creation payload.
These tests exercise the actual jars but are **not** a client rendering or backend
switch E2E test. There is currently no Gradle JUnit suite.

For a runtime smoke, start Velocity with the built plugin in an isolated directory,
bind it to loopback, confirm `Successfully injected Scoreboard API.`, run `vsa`,
and stop with `shutdown`. No unsupported-version or packet-registration errors
should appear. Do not use the publishing workflow as a feature-branch build check:
it publishes to Modrinth and remains restricted to master/manual dispatch.
