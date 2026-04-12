package com.student.growth.common;

import lombok.Data;
/**
 * 统一 API 响应结果封装类
 * 作用：规范后端返回给前端的 JSON 数据格式，做到所有接口响应格式划一
 * @param <T> 泛型，代表具体返回的数据类型
 */
@Data
public class Result<T> {

    // 业务状态码 (约定：200代表成功，500代表后端异常，其他代表各种业务错误)
    private Integer code;

    // 提示信息 (主要用于前端弹窗提示，如"登录成功"、"用户不存在"等)
    private String message;

    // 实际的业务数据 (泛型 T，具体类型由调用者决定)
    private T data;

    /**
     * 成功响应（无数据版）
     * 场景：例如执行删除、更新等操作，只需要告诉前端成功即可，无需返回具体数据
     * @return Result
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        return result;
    }

    /**
     * 成功响应（带数据版）
     * 场景：例如查询学生详情，需要把查询到的 Student 对象装在 data 里返回给前端
     * @param data 要返回给前端的核心数据
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（自定义提示语 + 带数据版）
     * 场景：例如生成成绩单成功，需要返回成绩单数据，并且提示"成绩单生成完毕"
     * @param message 自定义成功提示
     * @param data    要返回的数据
     * @return Result
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败响应（默认状态码版）
     * 场景：普通的业务报错，如"密码错误"，直接抛出 500 错误码
     * @param message 错误提示信息
     * @return Result
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败响应（自定义状态码 + 提示语版）
     * 场景：细粒度的错误控制，例如权限不足时返回 403，未登录返回 401
     * @param code    自定义的错误状态码
     * @param message 错误提示信息
     * @return Result
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
