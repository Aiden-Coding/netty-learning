package com.aiden.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: aiden
 * Date: 2020-09-26
 * Time: 13:35
 */
public class ParentRequest {
  private String method;
  private String url;

  public ParentRequest(InputStream in) {
    try {
      String content = "";
      byte[] buff = new byte[1024];
      int len = 0;
      if ((len = in.read(buff)) > 0) {
        content = new String(buff,0,len);
      }
      String line = content.split("\\n")[0];
      String [] arr = line.split("\\s");
      this.method = arr[0];
      System.out.println(method);
      this.url = arr[1].split("\\?")[0];
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getUrl() {
    return url;
  }

  public String getMethod() {
    return method;
  }

}
