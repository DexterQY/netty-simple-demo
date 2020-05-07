package per.qy.netty.reverse.proxy.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.ssl.SslHandler;
import per.qy.netty.reverse.proxy.util.SslUtil;

import java.net.URL;

public class ProxyClient {

    public static void proxy(EventLoopGroup workGroup, String proxyPath, Object msg,
                             Channel responseChannel) throws Exception {
        URL url = new URL(proxyPath);
        final int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        if ("https".equals(url.getProtocol())) {
                            System.out.println("添加ssl处理");
                            pipeline.addLast(new SslHandler(SslUtil.getClientSslEngine(url.getHost(), port)));
                        }
                        pipeline.addLast(new HttpClientCodec());
//                        pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
                        pipeline.addLast(new ProxyClientHandler(responseChannel));
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect(url.getHost(), port);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("目标服务连接成功");
                    if ("https".equals(url.getProtocol()) && msg instanceof HttpRequest) {
                        HttpRequest httpRequest = (HttpRequest) msg;
                        httpRequest.headers().set("Host", url.getHost() + ":" + port);
                        httpRequest.headers().set("Referer", proxyPath);
                        System.out.println("uri=" + httpRequest.uri());
                        channelFuture.channel().writeAndFlush(httpRequest);
                    } else {
                        channelFuture.channel().writeAndFlush(msg);
                    }
                } else {
                    System.out.println("目标服务连接失败");
                    channelFuture.channel().close();
                    responseChannel.close();
                }
            }
        });
    }
}
