package com.velocityscoreboardapi.internal;

import com.velocitypowered.api.proxy.Player;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.jetbrains.annotations.NotNull;

public class ChannelInjection extends ChannelDuplexHandler {

    @NotNull
    private final Player player;

    public ChannelInjection(@NotNull Player player) {
        this.player = player;
    }

    @Override
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
        super.write(context, packet, channelPromise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
}
