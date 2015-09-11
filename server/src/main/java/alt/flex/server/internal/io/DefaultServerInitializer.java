package alt.flex.server.internal.io;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;
import alt.flex.protocol.FlexProtocol.Request;
import alt.flex.server.internal.ServerInstance;

/**
 * 
 * @author Albert Shift
 *
 */

public class DefaultServerInitializer extends ChannelInitializer<SocketChannel> {

	private final ServerInstance serverInstance;
	private final SslContext sslCtx;

	public DefaultServerInitializer(ServerInstance serverInstance, SslContext sslCtx) {
		this.serverInstance = serverInstance;
		this.sslCtx = sslCtx;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		if (sslCtx != null) {
			p.addLast(sslCtx.newHandler(ch.alloc()));
		}

		p.addLast(new ProtobufVarint32FrameDecoder());
		p.addLast(new ProtobufDecoder(Request.getDefaultInstance()));

		p.addLast(new ProtobufVarint32LengthFieldPrepender());
		p.addLast(new ProtobufEncoder());

		p.addLast(new DefaultServerHandler(serverInstance));
	}

}
