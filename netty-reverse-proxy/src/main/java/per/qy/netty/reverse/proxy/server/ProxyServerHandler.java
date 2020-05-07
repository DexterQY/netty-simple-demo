package per.qy.netty.reverse.proxy.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import per.qy.netty.reverse.proxy.client.ProxyClient;

public class ProxyServerHandler extends ChannelInboundHandlerAdapter {

    private String proxyPath;
    private EventLoopGroup workGroup;

    public ProxyServerHandler(String proxyPath, EventLoopGroup workGroup) {
        this.proxyPath = proxyPath;
        this.workGroup = workGroup;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("使用代理请求目标服务 " + msg.getClass());
        ProxyClient.proxy(workGroup, proxyPath, msg, ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("server exception: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
