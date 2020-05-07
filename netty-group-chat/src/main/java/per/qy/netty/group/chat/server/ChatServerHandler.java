package per.qy.netty.group.chat.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    private static final Map<String, ChannelHandlerContext> CONTEXT_MAP = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String msg = ctx.channel().remoteAddress().toString() + "[" + LocalDateTime.now() + "]" + " 加入群聊";
        System.out.println(msg);
        CONTEXT_MAP.put(ctx.channel().id().asLongText(), ctx);
        for (ChannelHandlerContext otherCtx : CONTEXT_MAP.values()) {
            otherCtx.channel().writeAndFlush(msg);
        }
        System.out.println("当前群聊人数=" + CONTEXT_MAP.size());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String msg = ctx.channel().remoteAddress().toString() + "[" + LocalDateTime.now() + "]" + " 退出群聊";
        System.out.println(msg);
        CONTEXT_MAP.remove(ctx.channel().id().asLongText());
        for (ChannelHandlerContext otherCtx : CONTEXT_MAP.values()) {
            otherCtx.channel().writeAndFlush(msg);
        }
        ctx.close();
        System.out.println("当前群聊人数=" + CONTEXT_MAP.size());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String id = ctx.channel().id().asLongText();
        for (ChannelHandlerContext otherCtx : CONTEXT_MAP.values()) {
            if (!id.equals(otherCtx.channel().id().asLongText())) {
                otherCtx.channel().writeAndFlush(otherCtx.channel().remoteAddress() + "[" + LocalDateTime.now() +
                        "]: " + msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("socketChannel exception " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
