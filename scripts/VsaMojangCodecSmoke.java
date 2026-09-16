import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.scoreboard.DisplaySlot;
import com.velocitypowered.proxy.protocol.*;
import com.velocitypowered.proxy.protocol.packet.scoreboard.*;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import java.util.Arrays;

public class VsaMojangCodecSmoke {
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void main(String[] args) throws Exception {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        MinecraftPacket[] packets = {
            new DisplayObjectivePacket(DisplaySlot.SIDEBAR, "smoke"),
            new ObjectivePacket(ObjectivePacket.ObjectiveAction.UNREGISTER, "smoke", null, null, null),
            new ScoreSetPacket("player", "smoke", 42, null, null),
            new ScoreResetPacket("player", "smoke"),
            TeamPacket.unregister("smoke"),
            TeamPacket.addOrRemovePlayer("smoke", "player", true),
            TeamPacket.addOrRemovePlayer("smoke", "player", false)
        };
        String[] names = {"SetDisplayObjective", "SetObjective", "SetScore", "ResetScore", "SetPlayerTeam", "SetPlayerTeam", "SetPlayerTeam"};
        for (int i=0;i<packets.length;i++) {
            StreamCodec codec = (StreamCodec) Class.forName("net.minecraft.network.protocol.game.Clientbound"+names[i]+"Packet").getField("STREAM_CODEC").get(null);
            var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
            var output = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
            try {
                packets[i].encode(buffer, ProtocolUtils.Direction.CLIENTBOUND, ProtocolVersion.MINECRAFT_26_3);
                byte[] expected=new byte[buffer.readableBytes()]; buffer.getBytes(buffer.readerIndex(),expected);
                Object decoded=codec.decode(buffer);
                if(buffer.isReadable()) throw new AssertionError("Unread Mojang payload: "+names[i]);
                codec.encode(output,decoded);
                byte[] actual=new byte[output.readableBytes()]; output.readBytes(actual);
                if(!Arrays.equals(expected,actual))throw new AssertionError("Mojang codec mismatch: "+names[i]);
                System.out.println("PASS Mojang26.3 decoder/encoder: "+names[i]);
            } finally {buffer.release();output.release();}
        }
    }
}
