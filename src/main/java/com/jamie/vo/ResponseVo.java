package com.jamie.vo;

import com.jamie.enums.ResponseEnum;
import lombok.Data;
import org.springframework.validation.BindingResult;

import java.util.Objects;

/**
 * 响应
 * @param <T>
 */
@Data
public class ResponseVo<T> {

    private Integer code;

    private String message;

    private T data;

    private ResponseVo(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseVo(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseVo(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 返回成功
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> ResponseVo<T> successByMsg(String msg) {
        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(), msg);
    }

    /**
     * 返回成功
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResponseVo<T> success(T data) {
        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 返回成功
     * @param <T>
     * @return
     */
    public static <T> ResponseVo<T> success() {
        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage());
    }

    /**
     * 返回失败
     * @param responseEnum
     * @param <T>
     * @return
     */
    public static <T> ResponseVo<T> error(ResponseEnum responseEnum) {
        return new ResponseVo<>(responseEnum.getCode(), responseEnum.getMessage());
    }

    /**
     * 返回失败
     * @param responseEnum
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> ResponseVo<T> error(ResponseEnum responseEnum, String msg) {
        return new ResponseVo<>(responseEnum.getCode(), msg);
    }

    /**
     * 返回失败
     * @param responseEnum
     * @param bindingResult
     * @param <T>
     * @return
     */
    public static <T> ResponseVo<T> error(ResponseEnum responseEnum, BindingResult bindingResult) {
        return new ResponseVo<>(responseEnum.getCode(),
                Objects.requireNonNull(bindingResult.getFieldError()).getField() + bindingResult.getFieldError().getDefaultMessage());
    }

}
