package com.kit.maximus.freshskinweb.exception;

import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalHandleException{
     @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseAPI> handlingMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
         String enumKey = ex.getFieldError().getDefaultMessage();
         ErrorCode errorCode = ErrorCode.KEY_INVALID;
         ResponseAPI responseAPI = new ResponseAPI();
         try {
             errorCode = ErrorCode.valueOf(enumKey);
         } catch (IllegalArgumentException e) {
             System.out.println(e.getMessage());
         }
         responseAPI.setCode(errorCode.getCode());
         responseAPI.setMessage(errorCode.getMessage());
         return ResponseEntity.badRequest().body(responseAPI);
     }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ResponseAPI> handlingAppException(AppException ex) {
         ResponseAPI responseAPI = new ResponseAPI();
         ErrorCode errorCode = ex.getErrorCode();

         responseAPI.setCode(errorCode.getCode());
         responseAPI.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(responseAPI);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseAPI> handlingRuntimeException(RuntimeException ex) {
        ResponseAPI responseAPI = new ResponseAPI();
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;

        responseAPI.setCode(errorCode.getCode());
        responseAPI.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(responseAPI);
    }

}
