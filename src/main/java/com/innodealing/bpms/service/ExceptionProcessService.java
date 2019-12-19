package com.innodealing.bpms.service;

import com.innodealing.bpms.mapper.ExceptionProcessMapper;
import com.innodealing.bpms.model.SurviveProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("exceptionProcessService")
public class ExceptionProcessService {
    @Autowired
    ExceptionProcessMapper exceptionProcessMapper;

    public void insert(SurviveProcess exceptionProcess) {
        exceptionProcessMapper.insert(exceptionProcess);
    }

}
