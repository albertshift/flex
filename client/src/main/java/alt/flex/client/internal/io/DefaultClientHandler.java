package alt.flex.client.internal.io;

import alt.flex.client.internal.ClientInstance;
import alt.flex.client.internal.SynchronizationManager;
import alt.flex.protocol.FlexProtocol.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 
 * @author Albert Shift
 *
 */

public class DefaultClientHandler extends SimpleChannelInboundHandler<Response> {

	private final ClientInstance clientInstance;
	private final SynchronizationManager synchronizationManager;
	//private final BlockingQueue<Response> answer = new LinkedBlockingQueue<Response>();

	private volatile Channel channel = null;

	protected DefaultClientHandler(ClientInstance clientInstance) {
		super(false);
		this.clientInstance = clientInstance;
		this.synchronizationManager = clientInstance.getSynchronizationManager();
	}

	public Channel getChannel() {
		return channel;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		
		//System.out.println("Connection was closed " + ctx.channel());
		
		clientInstance.onClosedConnection();
	}
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) {
		channel = ctx.channel();
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
		
		switch(response.getResponseType()) {
		
		case HEART_BREATH:
			clientInstance.processHeartBreath();
			break;
			
		case INITED:
		case OPERATION_RESULT:			
			synchronizationManager.fireResult(response);
			break;
		
		default:
			processUnknownReponse(response);
			break;
			
		}
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
		clientInstance.onClosedConnection();
	}
	
	private void processUnknownReponse(Response response) {
		System.err.println("unknown response=" + response);
	}

}
