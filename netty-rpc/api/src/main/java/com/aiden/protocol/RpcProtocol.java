package com.aiden.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: aiden
 * Date: 2020-09-27
 * Time: 13:41
 */
@Data
public class RpcProtocol implements Serializable {

  /**类名**/
  private String className;
  /**方法名**/
  private String methodName;
  /**参数类型**/
  private Class<?>[] parames;
  /**参数值**/
  private Object[] values;
}
