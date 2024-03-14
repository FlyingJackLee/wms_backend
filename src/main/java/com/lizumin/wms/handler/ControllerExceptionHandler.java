package com.lizumin.wms.handler;

import com.lizumin.wms.entity.ApiRes;
import com.lizumin.wms.tool.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Configuration
public class ControllerExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * 重复插入错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler({DuplicateKeyException.class})
    public ResponseEntity<ApiRes> handler(DuplicateKeyException e) {
        log.trace(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiRes.fail(MessageUtil.getMessageByContext("BPV-016")));
    }

    /**
     * 请求参数不匹配时的处理
     *
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ApiRes> handler(MissingServletRequestParameterException e) {
        log.trace(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiRes.fail(MessageUtil.getMessageByContext("BPV-000")));
    }

    /**
     * 数据完整性错误
     *
     */
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ApiRes> handler(DataIntegrityViolationException e) {
        log.trace(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiRes.fail(MessageUtil.getMessageByContext("BPV-017")));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiRes> handler(AccessDeniedException e) {
        log.trace(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiRes.fail(MessageUtil.getMessageByContext("BPV-018")));
    }

    /**
     * 其他未知异常的处理，预防敏感信息返回给前端
     *
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handler(Exception e) {
        log.debug(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                body(MessageUtil.getMessageByContext("UNKNOWN"));
    }
}
