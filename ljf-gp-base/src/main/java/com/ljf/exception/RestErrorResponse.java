package com.ljf.exception;

import java.io.Serializable;

/*
 * @Classname RestErrorResponse
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 15:42
 * @Created by 李炯飞
 **/
public class RestErrorResponse implements Serializable {

    private String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
