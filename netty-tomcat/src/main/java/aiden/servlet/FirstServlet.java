package aiden.servlet;

import aiden.http.ParentRequest;
import aiden.http.ParentResponse;
import aiden.http.ParentServlet;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: aiden
 * Date: 2020-09-26
 * Time: 13:41
 */
public class FirstServlet extends ParentServlet {
  @Override
  protected void doPost(ParentRequest request, ParentResponse response) throws Exception {
    response.write("This is the first");
  }

  @Override
  protected void doGet(ParentRequest request, ParentResponse response) throws Exception {
    this.doPost(request,response);
  }
}
