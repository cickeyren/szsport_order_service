package com.digitalchina.common;

import com.digitalchina.sport.order.api.common.exception.BusinessException;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;

@ControllerAdvice
class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public static final String DEFAULT_ERROR_VIEW = "error";

    //@ExceptionHandler(value = Exception.class)
    //public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
    //    ModelAndView mav = new ModelAndView();
    //    mav.addObject("exception", e);
    //    mav.addObject("url", req.getRequestURL());
    //    mav.setViewName(DEFAULT_ERROR_VIEW);
    //    return mav;
    //}

    @ExceptionHandler(value = Throwable.class)
    @ResponseBody
    public RtnData defaultErrorHandler(Throwable e, HttpServletResponse response) throws Exception {
        //业务类型异常，异常消息会被包装到response的json的message中
        if(e instanceof ServiceRuntimeException){
            return RtnData.fail(null, e.getMessage());
        }

        //如果采用hibernate的validate框架，需要处理validate框架抛出的异常
        if(e instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException validException = (MethodArgumentNotValidException) e;
            BindingResult bindingResult = validException.getBindingResult();
            if(bindingResult == null){
                logger.error("no bindingResult found ", e);
                return RtnData.fail(null, "系统异常");
            }
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            if(allErrors == null || allErrors.size() == 0){
                logger.error("no error message found ", e);
                return RtnData.fail(null, "系统异常");
            }
            ObjectError objectError = allErrors.get(0);
            return RtnData.fail(null, objectError.getDefaultMessage());
        }

        //检查文件上传异常
        if(e instanceof MultipartException){
            if("Current request is not a multipart request".equals(e.getMessage())){
                return RtnData.fail(null, "文件不可以为空");
            }
        }

        //请求方法不支持
        if(e instanceof HttpRequestMethodNotSupportedException){
            response.setStatus(HttpStatus.SC_METHOD_NOT_ALLOWED);
            return RtnData.fail(null, e.getMessage());
        }
        //请求参数不正确
        if(e instanceof MissingServletRequestParameterException){
            response.setStatus(HttpStatus.SC_OK);
            return RtnData.fail(null, e.getMessage());
        }
        //业务异常处理
        if(e instanceof BusinessException){
            response.setStatus(HttpStatus.SC_OK);
            return RtnData.fail(null, e.getMessage());
        }

        if(e instanceof ServletRequestBindingException){
            response.setStatus(HttpStatus.SC_OK);
            return RtnData.fail(null, e.getMessage());
        }

        if(e instanceof UnsupportedEncodingException){
            response.setStatus(HttpStatus.SC_OK);
            return RtnData.fail(null, e.getMessage());
        }




        //如果不是业务类型异常，为未知异常，返回“系统异常”提示信息，日志中打印异常堆栈
        logger.error("system error ", e);
        return RtnData.fail(null, "系统异常");
    }

}
