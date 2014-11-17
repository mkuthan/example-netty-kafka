package example;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpKafkaServerHandler extends SimpleChannelInboundHandler<Object> {

    private Producer<String, String> kafkaProducer;

    public HttpKafkaServerHandler(Producer<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;

            String uri = httpRequest.getUri();

            KeyedMessage<String, String> message = new KeyedMessage<>("example", uri);
            kafkaProducer.send(message);

            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
