package io.github.timemachinelab.common;

public enum ResultCode {
    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "配置不存在"),
    CONFLICT(409, "配置已存在"),
    SERVER_ERROR(500, "服务器内部错误");
    
    private final int code;
    private final String message;
    
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() { return code; }
    public String getMessage() { return message; }
}