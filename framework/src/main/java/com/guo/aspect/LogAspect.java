package com.guo.aspect;

import com.alibaba.fastjson.JSON;
import com.guo.annotation.SystemLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;

/**
 * @Author guo
 * @Date 2023 04 01 17 13
 **/

@Component
@Aspect
@Slf4j
public class LogAspect {

    @Pointcut("@annotation(com.guo.annotation.SystemLog)")
    public void pt(){

    }
    @Around("pt()")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {

        Object ret;
        try {
            handleBefore(joinPoint);
            ret = joinPoint.proceed();
            handleAfter(ret);
        } finally {
            // 结束后换行
            log.info("=======End=======" + System.lineSeparator());
        }

        return ret;
    }

    private void handleAfter(Object ret) {
        // 打印出参
        log.info("Response       : {}",JSON.toJSONString(ret));
    }


    private void handleBefore(ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        // 获取被增强方法上的注解
        SystemLog systemLog = getSystemLog(joinPoint);

        log.info("=======Start=======");
        // 打印请求 URL
        log.info("URL            : {}", request.getRequestURL());
        // 打印描述信息
        log.info("BusinessName   : {}", systemLog.BusinessName());
        // 打印 Http method
        log.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), ((MethodSignature) joinPoint.getSignature()).getName());
        // 打印请求的 IP
        log.info("IP             : {}", request.getRemoteHost());
        // 打印请求入参
        log.info("Request Args   : {}", filterArgs(joinPoint.getArgs()));
    }

    private String filterArgs(Object[] args) {
        if (args == null) {
            return "null";
        }
        Object[] filteredArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) args[i];
                filteredArgs[i] = "MultipartFile: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)";
            } else {
                filteredArgs[i] = args[i];
            }
        }
        return JSON.toJSONString(filteredArgs);
    }






//    private void handleBefore(ProceedingJoinPoint joinPoint) {
//        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = requestAttributes.getRequest();
//
//        //获取被增强方法上的注解
//        SystemLog systemLog = getSystemLog(joinPoint);
//
//        log.info("=======Start=======");
//        // 打印请求 URL
//        log.info("URL            : {}",request.getRequestURL());
//        // 打印描述信息
//        log.info("BusinessName   : {}",systemLog.BusinessName());
//        // 打印 Http method
//        log.info("HTTP Method    : {}",request.getMethod());
//        // 打印调用 controller 的全路径以及执行方法
//        log.info("Class Method   : {}.{}",joinPoint.getSignature().getDeclaringTypeName(),((MethodSignature) joinPoint.getSignature()).getName());
//        // 打印请求的 IP
//        log.info("IP             : {}",request.getRemoteHost());
//        // 打印请求入参
//        log.info("Request Args   : {}", JSON.toJSONString(joinPoint.getArgs()));
//    }

    private SystemLog getSystemLog(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        SystemLog systemLog = methodSignature.getMethod().getAnnotation(SystemLog.class);
        return systemLog;
    }

}
