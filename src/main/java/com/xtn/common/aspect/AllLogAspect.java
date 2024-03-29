package com.xtn.common.aspect;

import com.xtn.common.config.FrontApplicationInterceptor;
import com.xtn.common.utils.HttpUtil;
import com.xtn.common.utils.JsonUtil;
import com.xtn.common.utils.SpringContextUtil;
import com.xtn.common.utils.TLocalHelper;
import com.xtn.modules.system.service.SysLogService;
import com.xtn.modules.system.service.SysUserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author luoyang
 */
@Order(1)
@Slf4j
@Component
@Aspect
public class AllLogAspect {

    @Around(value = "execution(* com.xtn..controller..*.*(..))")
    public Object goodsLog(ProceedingJoinPoint proceeding) throws Throwable {
        long startTime = System.currentTimeMillis();
        // 创建流水号
        TLocalHelper.createSeq();
        Thread.currentThread().setName(TLocalHelper.getSeq());
        HttpServletRequest request = HttpUtil.getRequest();
        Signature signature = proceeding.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        //请求参数
        String requestParam = JsonUtil.toJsonString(getParameter(method, proceeding.getArgs()));
        if (request != null)
            log.info("接口请求执行开始，路径：{}，请求参数：{}，流水号：{}", request.getRequestURI(), requestParam, TLocalHelper.getSeq());
        Object result = proceeding.proceed();
        if (request != null)
            log.info("接口请求执行结束，耗时：{}ms，返回数据：{}，流水号：{}", System.currentTimeMillis() - startTime, Objects.nonNull(result) ? result.toString() : "null", TLocalHelper.getSeq());
        return result;
    }

    /**
     * 根据方法和传入的参数获取请求参数
     */
    public static Object getParameter(Method method, Object[] args) {
        List<Object> argList = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            //将RequestBody注解修饰的参数作为请求参数
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null) {
                argList.add(args[i]);
            }
            //将RequestParam注解修饰的参数作为请求参数
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if (requestParam != null) {
                Map<String, Object> map = new HashMap<>();
                String key = parameters[i].getName();
                if (!StringUtils.isEmpty(requestParam.value())) {
                    key = requestParam.value();
                }
                map.put(key, args[i]);
                argList.add(map);
            }
        }
        if (argList.size() == 0) {
            return null;
        } else if (argList.size() == 1) {
            return argList.get(0);
        } else {
            return argList;
        }
    }

}
