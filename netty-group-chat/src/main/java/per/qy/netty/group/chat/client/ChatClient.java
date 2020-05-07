package per.qy.netty.group.chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.time.LocalDateTime;
import java.util.Scanner;

public class ChatClient {

    private String serverIp;
    private int serverPort;
    private Channel channel;

    public ChatClient(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public void run() throws InterruptedException {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    // 头两个字节定义长度，解决粘包拆包
                                    .addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2))
                                    .addLast(new LengthFieldPrepender(2))
                                    .addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(new ChatClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(serverIp, serverPort).sync();
            System.out.println("chat client running...");

            channel = channelFuture.channel();
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                if ("exit".equals(msg)) {
                    System.out.println("退出群聊");
                    channel.close();
                    break;
                }
                sendMsg(msg);
            }
            channel.closeFuture().sync();
        } finally {
            workGroup.shutdownGracefully();
        }
    }

    private void sendMsg(String msg) {
        System.out.println("我[" + LocalDateTime.now() + "]: " + msg);
        channel.writeAndFlush(msg);
    }

    public static void main(String[] args) throws InterruptedException {
        new ChatClient("127.0.0.1", 6666).run();
    }
}
