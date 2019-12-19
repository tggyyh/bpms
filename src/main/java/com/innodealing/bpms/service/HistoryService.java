package com.innodealing.bpms.service;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.mapper.HistoryMapper;
import com.innodealing.bpms.model.History;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("historyService")
public class HistoryService {
    @Autowired
    private HistoryMapper historyMapper;

    private final static Logger logger = LoggerFactory.getLogger(HistoryService.class);

    public int insertHistory(History history){
        return historyMapper.insertHistory(history);
    }

    public List<History> findAll(ReqData reqData){
        return historyMapper.findAll(reqData);
    }

}
