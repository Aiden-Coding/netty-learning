package com.aiden.registry;

import com.aiden.handler.RpcHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * Created with IntelliJ IDEA.
 * Description: rpc注册
 * User: aiden
 * Date: 2020-09-27
 * Time: 13:44
 */
public class RpcProvider {
  private int port;
  public RpcProvider(int port) {
    this.port = port;
  }
  public void start(){
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(bossGroup,workGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            //协议解码器
            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
            //协议编码器
            pipeline.addLast(new LengthFieldPrepender(4));
            //对象参数类型编码器
            pipeline.addLast(new ObjectEncoder());
            //对象参数类型解码器
            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
            pipeline.addLast(new RpcHandler());
          }
        })
        //主线程 线程最大数量128
        .option(ChannelOption.SO_BACKLOG,128)
        //子线程配置 保存长连接
        .childOption(ChannelOption.SO_KEEPALIVE,true);
      ChannelFuture future = bootstrap.bind(port).sync();
      System.out.println("Rpc start listen at " + port);
      future.channel().closeFuture().sync();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      bossGroup.shutdownGracefully();
      workGroup.shutdownGracefully();
    }
  }

}
