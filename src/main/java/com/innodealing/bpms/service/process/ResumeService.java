package com.innodealing.bpms.service.process;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResumeService implements JavaDelegate {
    private static final Logger logger = LoggerFactory.getLogger(ResumeService.class);

    @Override
    public void execute(DelegateExecution execution) {
        String applicantName = (String) execution.getVariable("applicantName");
        logger.info(applicantName+"ddddddddddddddddddddddddd");
    }

}