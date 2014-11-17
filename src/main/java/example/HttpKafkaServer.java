package example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import java.util.Properties;

public class HttpKafkaServer {

    static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        Properties kafkaProperties = new Properties();
        kafkaProperties.load(HttpKafkaServer.class.getResourceAsStream("/kafka.properties"));
        ProducerConfig kafkaConfig = new ProducerConfig(kafkaProperties);
        Producer<String, String> kafkaProducer = new Producer<>(kafkaConfig);

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpKafkaInitializer(kafkaProducer));
            Channel ch = b.bind(PORT).sync().channel();
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
