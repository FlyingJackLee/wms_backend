package com.lizumin.wms.handler;

import com.lizumin.wms.exception.ControllerExceptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.lizumin.wms.entity.ErrorRes;
import com.lizumin.wms.exception.ControllerException;

@RestControllerAdvice
@Configuration
public class ControllerExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

//    /**
//     * 自定义异常的处理
//     *
//     */
//    @ExceptionHandler({ControllerException.class})
//    public ResponseEntity<ErrorRes> handler(ControllerException e) {
//        return ResponseEntity.status(e.getStatus()).body(new ErrorRes(e));
//    }
//
//    /**
//     * 请求参数不匹配时的处理
//     *
//     */
//    @ExceptionHandler({MissingServletRequestParameterException.class})
//    public ResponseEntity<ErrorRes> handler(MissingServletRequestParameterException e) {
//        log.trace(e.getMessage());
//
//        return ResponseEntity.status(ControllerExceptionType.MISSING_PARAMETER.getStatus())
//                .body(new ErrorRes(ControllerExceptionType.MISSING_PARAMETER));
//    }
//
//    /**
//     * 其他未知异常的处理，预防敏感信息返回给前端
//     *
//     */
//    @ExceptionHandler({Exception.class})
//    public ResponseEntity<ErrorRes> handler(Exception e) {
//        log.debug(e.getMessage());
//
//        return ResponseEntity.status(ControllerExceptionType.UNKNOWN.getStatus()).
//                body(new ErrorRes(ControllerExceptionType.UNKNOWN));
//    }
}
