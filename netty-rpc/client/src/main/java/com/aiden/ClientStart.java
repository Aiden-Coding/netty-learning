package com.aiden;

import com.aiden.proxy.RpcProxy;
import com.aiden.service.IRpcService;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: aiden
 * Date: 2020-09-27
 * Time: 22:43
 */
public class ClientStart {
  public static void main(String[] args) {
    IRpcService rpcService = RpcProxy.create(IRpcService.class);
    System.out.println(rpcService.test("Aiden"));
  }
}
