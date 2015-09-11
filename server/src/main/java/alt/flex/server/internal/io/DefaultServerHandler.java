package alt.flex.server.internal.io;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.UUID;

import alt.flex.protocol.FlexProtocol.InitParams;
import alt.flex.protocol.FlexProtocol.Request;
import alt.flex.protocol.FlexProtocol.Response;
import alt.flex.protocol.FlexProtocol.ResponseType;
import alt.flex.protocol.FlexProtocol.ServerParams;
import alt.flex.server.internal.ClientManager;
import alt.flex.server.internal.ServerConstants;
import alt.flex.server.internal.ServerInstance;
import alt.flex.support.util.UuidConverter;

/**
 * 
 * @author Albert Shift
 *
 */

public class DefaultServerHandler extends SimpleChannelInboundHandler<Request> {

	private final ServerInstance serverInstance;
	private final ClientManager clientManager;
	
  protected DefaultServerHandler(ServerInstance serverInstance) {
  	this.serverInstance = serverInstance;
  	this.clientManager = serverInstance.getClientManager();
  }
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
		System.out.println("add new client " + ctx.channel().toString());
		
		super.channelActive(ctx);
		
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		clientManager.unregisterClient(ctx.channel());
		
		System.out.println("disconnect the client " + ctx.channel().toString());
		
		super.channelInactive(ctx);
	}

	

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {

		switch(request.getRequestType()) {
		
		case INIT:
			processInit(ctx, request);
			break;
			
		case OPERATION:
			serverInstance.submitOperation(ctx, request);
			break;
			
		case CANCEL_OPERATION:
			serverInstance.cancelOperation(ctx, request);
			break;
		
		default:
			unknownRequest(ctx, request);
			break;
			
		}


	}

	private void processInit(ChannelHandlerContext ctx, Request request) {
		
		if (!request.hasInit()) {
			
			System.err.println("Init params not found in " + ctx.channel() + ", request=" + request);
			ctx.close();
			return;
		}
		
		InitParams init = request.getInit();
		
		if (init.getVersion() != ServerConstants.SERVER_VERISON) {
			
			System.err.println("Wrong client version " + init.getVersion() + " in " + ctx.channel() + ", request=" + request);
			ctx.close();
			return;
			
		}

		clientManager.registerNewClient(ctx.channel(), init);
		
		UUID clientId = UuidConverter.toJavaUuid(init.getClientId());
		
		System.out.println("client INIT " + clientId);

		ServerParams serverParams = ServerParams.newBuilder()
				.setHeartBreathIntervalMls(serverInstance.getSettings().getHeartBreathIntervalMls())
				.build();
		
		Response.Builder res = Response.newBuilder();
		
		res.setTimeoutSeq(request.getTimeoutSeq());
		res.setRequestNum(request.getRequestNum());
		res.setResponseType(ResponseType.INITED);
		res.setServerParams(serverParams);
		
		if (init.getGetStores()) {
			serverInstance.getStoreNameManager().addStorePayloads(res);
		}
		
		Channel channel = ctx.channel();
		if (channel.isActive()) {
			ctx.writeAndFlush(res.build());
		}
	}

	private void unknownRequest(ChannelHandlerContext ctx, Request request) {
		
		System.err.println("unknown request " + request);
		
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	
}
