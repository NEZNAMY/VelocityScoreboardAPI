import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.scoreboard.DisplaySlot;
import com.velocitypowered.proxy.protocol.*;
import com.velocitypowered.proxy.protocol.packet.scoreboard.*;
import com.velocitypowered.proxy.scoreboard.VelocityScoreboard;
import com.velocitypowered.scoreboardapi.PacketRegistry;
import io.netty.buffer.Unpooled;
import java.util.Arrays;

/** Standalone integration probe against the actual distributed Velocity and plugin jars. */
public class ProtocolSmoke {
    public static void main(String[] args) throws Exception {
        PacketRegistry.registerPackets(VelocityScoreboard.MAXIMUM_SUPPORTED_VERSION);
        var field = StateRegistry.class.getDeclaredField("clientbound");
        field.setAccessible(true);
        var registry = field.get(StateRegistry.PLAY);
        var lookup = registry.getClass().getDeclaredMethod("getProtocolRegistry", ProtocolVersion.class);
        lookup.setAccessible(true);
        int checks = 0;
        for (var version : new ProtocolVersion[]{ProtocolVersion.MINECRAFT_26_1, ProtocolVersion.MINECRAFT_26_2, ProtocolVersion.MINECRAFT_26_3}) {
            var protocol = (StateRegistry.PacketRegistry.ProtocolRegistry) lookup.invoke(registry, version);
            MinecraftPacket[] packets = {
                new DisplayObjectivePacket(DisplaySlot.SIDEBAR, "smoke"),
                new ObjectivePacket(ObjectivePacket.ObjectiveAction.UNREGISTER, "smoke", null, null, null),
                new ScoreSetPacket("player", "smoke", 42, null, null),
                new ScoreResetPacket("player", "smoke"),
                TeamPacket.unregister("smoke"),
                TeamPacket.addOrRemovePlayer("smoke", "player", true),
                TeamPacket.addOrRemovePlayer("smoke", "player", false)
            };
            // IDs from Mojang's 26.3 data generator reports/packets.json.
            int[] ids = version == ProtocolVersion.MINECRAFT_26_3
                    ? new int[]{0x64, 0x6C, 0x70, 0x50, 0x6F, 0x6F, 0x6F}
                    : new int[]{0x62, 0x6A, 0x6E, 0x4F, 0x6D, 0x6D, 0x6D};
            for (int i = 0; i < packets.length; i++) {
                var packet = packets[i];
                if (!protocol.containsPacket(packet)) throw new AssertionError(version + " unregistered " + packet.getClass());
                if (protocol.getPacketId(packet) != ids[i]) throw new AssertionError("Unexpected packet ID");
                var decoded = protocol.createPacket(ids[i]);
                if (decoded == null || decoded.getClass() != packet.getClass()) throw new AssertionError("Wrong packet factory");
                var first = Unpooled.buffer();
                var second = Unpooled.buffer();
                try {
                    packet.encode(first, ProtocolUtils.Direction.CLIENTBOUND, version);
                    byte[] expected = new byte[first.readableBytes()];
                    first.getBytes(first.readerIndex(), expected);
                    decoded.decode(first, ProtocolUtils.Direction.CLIENTBOUND, version);
                    if (first.isReadable()) throw new AssertionError("Unread payload");
                    decoded.encode(second, ProtocolUtils.Direction.CLIENTBOUND, version);
                    byte[] actual = new byte[second.readableBytes()];
                    second.readBytes(actual);
                    if (!Arrays.equals(expected, actual)) throw new AssertionError("Round-trip mismatch");
                } finally { first.release(); second.release(); }
                checks++;
            }
            System.out.println("PASS " + version + ": registration, IDs, factories, codec round trips");
        }
        System.out.println("PASS " + checks + " packet/version cases (not an in-game client test)");
    }
}
