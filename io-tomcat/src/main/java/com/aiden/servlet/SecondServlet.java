package com.aiden.servlet;

import com.aiden.http.ParentRequest;
import com.aiden.http.ParentResponse;
import com.aiden.http.ParentServlet;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: aiden
 * Date: 2020-09-26
 * Time: 13:42
 */
public class SecondServlet extends ParentServlet {
  @Override
  protected void doPost(ParentRequest request, ParentResponse response) throws Exception {
    response.write("this is the second");
  }

  @Override
  protected void doGet(ParentRequest request, ParentResponse response) throws Exception {
    this.doPost(request,response);
  }
}
