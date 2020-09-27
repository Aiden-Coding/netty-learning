package com.aiden.serviceimpl;

import com.aiden.service.IRpcService;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: aiden
 * Date: 2020-09-26
 * Time: 16:19
 */
public class RpcServiceimpl implements IRpcService {
  @Override
  public String test(String test) {
    return "hello " + test + "!";
  }
}
