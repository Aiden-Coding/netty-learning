package com.aiden;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: aiden
 * Date: 2020-10-07
 * Time: 19:45
 */
public class Client {
  private static final String SERVER_HOST = "01tech.online";

  public static void main(String[] args) {
    new Client().start(Server.BEGIN_PORT, Server.N_PORT);
  }

  private void start(int beginPort, int nPort) {
    System.out.println("客户端启动。。。");
    EventLoopGroup event = new NioEventLoopGroup();
    final Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(event)
      .channel(NioSocketChannel.class)
      .option(ChannelOption.SO_REUSEADDR, true)
      .handler(new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {

        }
      });
    int index = 0;
    int port;
    while (!Thread.interrupted()) {
      port = beginPort + index;
      try {
        ChannelFuture channelFuture = bootstrap.connect(SERVER_HOST, port);
        channelFuture.addListener(new ChannelFutureListener() {
          @Override
          public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
              System.out.println("连接失败，程序关闭");
              System.exit(0);
            }
          }
        });
        channelFuture.get();
      } catch (Exception e) {

      }
      if (port == nPort) {
        index = 0;
      } else {
        index++;
      }
    }
  }
}
