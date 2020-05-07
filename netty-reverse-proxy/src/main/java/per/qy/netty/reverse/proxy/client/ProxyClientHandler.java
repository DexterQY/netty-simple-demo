package per.qy.netty.reverse.proxy.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ProxyClientHandler extends ChannelInboundHandlerAdapter {

    private Channel responseChannel;

    public ProxyClientHandler(Channel responseChannel) {
        this.responseChannel = responseChannel;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("目标服务连接断开");
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("将目标服务响应转发给客户端 " + msg.getClass());
        responseChannel.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("client exception: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
