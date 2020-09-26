package aiden;

import aiden.http.ParentRequest;
import aiden.http.ParentResponse;
import aiden.http.ParentServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: aiden
 * Date: 2020-09-26
 * Time: 14:01
 */
public class TomcatStart {
  private int port = 8080;
  private Map<String, ParentServlet> servletMapping = new HashMap<String, ParentServlet>();
  private Properties webProperties = new Properties();

  private void init() {
    try {
      String WEB_INF = this.getClass().getResource("/").getPath();
      FileInputStream fis = new FileInputStream(WEB_INF + "web.properties");
      webProperties.load(fis);
      for (Object k : webProperties.keySet()) {
        String key = k.toString();
        if (key.endsWith(".url")) {
          String servletName = key.replaceAll("\\.url$", "");
          String url = webProperties.getProperty(key);
          String className = webProperties.getProperty(servletName + ".className");
          //单实例  多线程
          ParentServlet obj = (ParentServlet) Class.forName(className).newInstance();
          servletMapping.put(url, obj);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void start() {
    //1.加载配置类，初始化servletMapping
    init();
    // Netty  NIO Reactor模型 Boss Worker
    //Boss 线程
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    //Work线程
    EventLoopGroup workGroup = new NioEventLoopGroup();
    ServerBootstrap server = null;

    try {
      //创建对象
      server = new ServerBootstrap();
      //配置参数
      //链式编程
      server.group(bossGroup, workGroup)
        //主线程处理类，
        .channel(NioServerSocketChannel.class)
        //子线程处理类
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel client) throws Exception {
            //无锁化串行编程
            //netty对http的封装 对顺序有要求
            //httpResponseEncoder 编码器
            client.pipeline().addLast(new HttpResponseEncoder());
            //httprequestDecoder 解码器
            client.pipeline().addLast(new HttpRequestDecoder());
            //业务处理器
            client.pipeline().addLast(new TomcatHandler());
          }
        })
        //主线程 线程最大数量128
        .option(ChannelOption.SO_BACKLOG, 128)
        //子线程配置 保存长连接
        .childOption(ChannelOption.SO_KEEPALIVE, true);
      ChannelFuture f = server.bind(port).sync();
      System.out.println("Tomcat 已启动，监听端口是：" + this.port);
      f.channel().closeFuture().sync();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      bossGroup.shutdownGracefully();
      workGroup.shutdownGracefully();
    }
  }

  public class TomcatHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if (msg instanceof HttpRequest) {
        System.out.println("hello request");
        HttpRequest req = (HttpRequest) msg;
        ParentRequest request = new ParentRequest(ctx, req);
        ParentResponse response = new ParentResponse(ctx, req);
        String url = request.getUrl();
        if (servletMapping.containsKey(url)) {
          //7.调用实例化对象的service方法
          servletMapping.get(url).service(request, response);
        } else {
          response.write("404 - Not Found");
        }
      }
    }
  }

  public static void main(String[] args) {
    //启动
    new TomcatStart().start();
  }
}
