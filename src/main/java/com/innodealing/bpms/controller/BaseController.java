package com.innodealing.bpms.controller;

import com.innodealing.bpms.common.model.ReqData;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhou on 2017/1/20.
 */
@Controller
public class BaseController {

    /**
     * 得到PageData
     */
    public ReqData getReqData() {
        ReqData data = new ReqData(this.getRequest());
        return data;
    }

    /**
     * 得到ModelAndView
     */
    public ModelAndView getModelAndView() {
        return new ModelAndView();
    }

    /**
     * 得到request对象
     */
    public HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }
}
