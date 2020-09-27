package com.aiden.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: aiden
 * Date: 2020-09-27
 * Time: 22:39
 */
public class RpcProxyHandler extends ChannelInboundHandlerAdapter {
  private Object response;

  public Object getResponse() {
    return response;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    response = msg;
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    System.out.println("client exception is generate");
  }
}
