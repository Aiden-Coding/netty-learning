package com.aiden.proxy;

import com.aiden.handler.RpcProxyHandler;
import com.aiden.protocol.RpcProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: aiden
 * Date: 2020-09-27
 * Time: 09:10
 */
public class RpcProxy {
  public static <T> T create(Class<?> clazz) {
    MethodProxy proxy = new MethodProxy(clazz);
    Class<?> [] interfaces = clazz.isInterface()?new Class[]{clazz}:clazz.getInterfaces();
    T result = (T) Proxy.newProxyInstance(clazz.getClassLoader(),interfaces,proxy);
    return result;
  }
  private static class MethodProxy implements InvocationHandler{
    private Class<?> clazz;

    public MethodProxy(Class<?> clazz) {
      this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (Object.class.equals(method.getDeclaringClass())) {
        return method.invoke(this,args);
      } else {
        return rpcInvoke(proxy,method,args);
      }
    }

    private Object rpcInvoke(Object proxy, Method method, Object[] args) {
      RpcProtocol msg = new RpcProtocol();
      msg.setClassName(this.clazz.getName());
      msg.setMethodName(method.getName());
      msg.setValues(args);
      msg.setParames(method.getParameterTypes());
      final RpcProxyHandler rpcProxyHandler = new RpcProxyHandler();
      EventLoopGroup group = new NioEventLoopGroup();
      try {
        Bootstrap client = new Bootstrap();
        client.group(group)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.TCP_NODELAY,true)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ChannelPipeline pipeline = ch.pipeline();
              //协议解码器
              pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
              //协议编码器
              pipeline.addLast(new LengthFieldPrepender(4));
              //对象参数类型编码器
              pipeline.addLast(new ObjectEncoder());
              //对象参数类型解码器
              pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
              pipeline.addLast(rpcProxyHandler);
            }
          });
        ChannelFuture future = client.connect("localhost",8080).sync();
        future.channel().writeAndFlush(msg).sync();
        future.channel().closeFuture().sync();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        group.shutdownGracefully();
      }
      return rpcProxyHandler.getResponse();
    }
  }
}
