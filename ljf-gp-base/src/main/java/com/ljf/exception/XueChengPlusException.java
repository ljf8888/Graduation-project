package com.ljf.exception;

/*
 * @Classname XueChengPlusException
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 15:42
 * @Created by 李炯飞
 **/
public class XueChengPlusException extends RuntimeException {

    private String errMessage;

    public XueChengPlusException() {
        super();
    }

    public XueChengPlusException(String message) {
        super(message);
        this.errMessage = message;
    }

    public String getErrMessage(){
        return errMessage;
    }

    public static void cast(String errMessage){
        throw new XueChengPlusException(errMessage);
    }
    public static void cast(CommonError commonError){
        throw new XueChengPlusException(commonError.getErrMessage());
    }
}
