package com.ricardo.reggie.config;

import com.ricardo.reggie.common.CustomException;
import com.ricardo.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalException {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String[] split1 = split[2].split("'");
            String msg = split1[1]+"用户名已被注册";
            return R.error(msg);
        }
       return R.error("未知错误");
    }

    @ExceptionHandler({CustomException.class})
    public R<String> exceptionHandler (CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }
}
