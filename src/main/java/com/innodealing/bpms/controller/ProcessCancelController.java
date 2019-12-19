package com.innodealing.bpms.controller;

import com.innodealing.bpms.service.ConfirmMatterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/process-cancel")
public class ProcessCancelController {
    private static final Logger logger = LoggerFactory.getLogger(ProcessCancelController.class);
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(Model model){
        return "app/bpms/process-cancel/index";
    }
}
