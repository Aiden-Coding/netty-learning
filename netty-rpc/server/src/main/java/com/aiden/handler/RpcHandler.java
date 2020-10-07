package com.aiden.handler;

import com.aiden.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: aiden
 * Date: 2020-09-27
 * Time: 13:53
 */
public class RpcHandler extends ChannelInboundHandlerAdapter {
  //保存服务实现的类实例
  public static ConcurrentHashMap<String, Object> serviceImplMap = new ConcurrentHashMap<>();
  //保存服务实现的类名
  public List<String> classNames = new ArrayList<>();

  public RpcHandler() {
    //递归扫描
    scannerClass("com.aiden.serviceimpl");
    doRegister();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    Object result = new Object();
    RpcProtocol request = (RpcProtocol) msg;
    if (serviceImplMap.containsKey(request.getClassName())) {
      Object clazz = serviceImplMap.get(request.getClassName());
      Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParames());
      result = method.invoke(clazz, request.getValues());
    }
    ctx.write(result);
    ctx.flush();
    ctx.close();
  }

  //扫描实现的class
  private void scannerClass(String packageName) {
    URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
    File dir = new File(url.getFile());
    for (File file : dir.listFiles()) {
      if (file.isDirectory()) {
        scannerChildFile(packageName + "." + file.getName(), classNames, file);
      } else {
        classNames.add(packageName + "." + file.getName().replace(".class", "").trim());
      }
    }
  }

  private void scannerChildFile(String packageName, List<String> classNames, File child) {
    for (File file : child.listFiles()) {
      if (file.isDirectory()) {
        scannerChildFile(packageName + "." + file.getName(), classNames, file);
      } else {
        classNames.add(packageName + "." + file.getName().replace(".class", "").trim());
      }
    }
  }

  private void doRegister() {
    if (classNames.size() == 0) {
      return;
    }
    for (String className : classNames) {
      try {
        Class<?> clazz = Class.forName(className);
        Class<?> serviceInterface = clazz.getInterfaces()[0];
        serviceImplMap.put(serviceInterface.getName(), clazz.newInstance());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
