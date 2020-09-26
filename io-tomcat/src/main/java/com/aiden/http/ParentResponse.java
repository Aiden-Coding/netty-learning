package com.aiden.http;

import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: aiden
 * Date: 2020-09-26
 * Time: 13:36
 */
public class ParentResponse {
  private OutputStream out;
  public ParentResponse (OutputStream out) {
    this.out = out;
  }

  public void write(String s) throws Exception{
    //输出也要遵循HTTP
    //状态码为200
    StringBuilder sb = new StringBuilder();
    sb.append("HTTP/1.1 200 OK \n")
      .append("Content-Type: text/html;\n")
      .append("\r\n")
      .append(s);
    out.write(sb.toString().getBytes());
  }
}
