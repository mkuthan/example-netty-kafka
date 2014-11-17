package example;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import kafka.javaapi.producer.Producer;

public class HttpKafkaInitializer extends ChannelInitializer<SocketChannel> {

    private Producer<String, String> kafkaProducer;

    public HttpKafkaInitializer(Producer<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());
        p.addLast(new HttpKafkaServerHandler(kafkaProducer));
    }
}
