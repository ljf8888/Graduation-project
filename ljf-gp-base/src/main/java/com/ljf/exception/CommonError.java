
package com.ljf.exception;


/*
 * @Classname CommonError
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 15:42
 * @Created by 李炯飞
 **/
public enum CommonError {

	UNKOWN_ERROR("执行过程异常，请重试。"),
	PARAMS_ERROR("非法参数"),
	OBJECT_NULL("对象为空"),
	QUERY_NULL("查询结果为空"),
	REQUEST_NULL("请求参数为空");

	private String errMessage;

	public String getErrMessage() {
		return errMessage;
	}

	private CommonError( String errMessage) {
		this.errMessage = errMessage;
	}

}
