package aiden.enums;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: aiden
 * Date: 2020-09-26
 * Time: 13:29
 */
public enum RequestMethodEnum {

  GET("GET"),
  POST("POST");

  public String code;
  RequestMethodEnum(String code) {
    this.code=code;
  }
}
