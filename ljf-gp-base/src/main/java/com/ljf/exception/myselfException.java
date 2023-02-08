package com.ljf.exception;

/*
 * @Classname XueChengPlusException
 * @Description TODO
 * @Version 1.0.0
 * @Date 2023/2/3 15:42
 * @Created by 李炯飞
 **/
public class myselfException extends RuntimeException {

    private String errMessage;

    public myselfException() {
        super();
    }

    public myselfException(String message) {
        super(message);
        this.errMessage = message;
    }

    public String getErrMessage(){
        return errMessage;
    }

    public static void cast(String errMessage){
        throw new myselfException(errMessage);
    }
    public static void cast(CommonError commonError){
        throw new myselfException(commonError.getErrMessage());
    }
}
