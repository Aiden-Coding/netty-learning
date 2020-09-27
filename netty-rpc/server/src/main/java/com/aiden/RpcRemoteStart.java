package com.aiden;

import com.aiden.registry.RpcProvider;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: aiden
 * Date: 2020-09-27
 * Time: 22:46
 */
public class RpcRemoteStart {
  public static void main(String[] args) {
    new RpcProvider(8080).start();
  }
}
