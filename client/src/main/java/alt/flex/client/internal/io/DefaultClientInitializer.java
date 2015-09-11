package alt.flex.client.internal.io;

import alt.flex.client.FlexClientBuilder;
import alt.flex.client.internal.ClientInstance;
import alt.flex.protocol.FlexProtocol.Response;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;

/**
 * 
 * @author Albert Shift
 *
 */

public class DefaultClientInitializer extends ChannelInitializer<SocketChannel> {

	private final ClientInstance clientInstance;
	private final FlexClientBuilder settings;
	private final SslContext sslCtx;

	public DefaultClientInitializer(ClientInstance clientInstance, SslContext sslCtx, FlexClientBuilder settings) {
		this.clientInstance = clientInstance;
		this.settings = settings;
		this.sslCtx = sslCtx;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
		if (sslCtx != null) {
			p.addLast(sslCtx.newHandler(ch.alloc(), settings.getHost(), settings.getPort()));
		}

		p.addLast(new ProtobufVarint32FrameDecoder());
		p.addLast(new ProtobufDecoder(Response.getDefaultInstance()));

		p.addLast(new ProtobufVarint32LengthFieldPrepender());
		p.addLast(new ProtobufEncoder());

		p.addLast(new DefaultClientHandler(clientInstance));
	}

}
