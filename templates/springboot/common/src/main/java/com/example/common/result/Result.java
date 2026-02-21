package com.example.common.result;

import lombok.Data;

/**
 * 统一返回结果类
 */
@Data
public class Result<T> {

    private Integer code; // 编码：1成功，0和其它数字为失败
    private String msg; // success/错误信息
    private T data; // 数据

    public static Result<Void> success() {
        Result<Void> result = new Result<>();
        result.code = 1;
        result.msg = "success";
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 1;
        result.msg = "success";
        result.data = data;
        return result;
    }

    public static Result<Void> error(String msg) {
        Result<Void> result = new Result<>();
        result.code = 0;
        result.msg = msg;
        return result;
    }

}
