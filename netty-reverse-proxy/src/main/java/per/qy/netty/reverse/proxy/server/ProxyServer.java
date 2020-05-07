package per.qy.netty.reverse.proxy.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Arrays;

public class ProxyServer {

    private ProxyConfig proxyConfig;

    public ProxyServer(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

    private void run() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1000)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new HttpServerCodec())
//                                .addLast(new HttpObjectAggregator(Integer.MAX_VALUE))
                                .addLast(new ProxyServerHandler(proxyConfig.getProxyPath(), workerGroup));
                    }
                });
        bootstrap.bind(proxyConfig.getPort()).sync();
        System.out.println(proxyConfig.getPort() + " proxy server running...");
    }

    public static void main(String[] args) throws Exception {
        Yaml yaml = new Yaml();
        InputStream inputStream = ProxyServer.class.getClassLoader().getResourceAsStream("config.yml");
        ProxyConfigList configList = yaml.loadAs(inputStream, ProxyConfigList.class);
        System.out.println(Arrays.toString(configList.getProxy().toArray()));
        for (ProxyConfig proxyConfig : configList.getProxy()) {
            new ProxyServer(proxyConfig).run();
        }
    }
}
