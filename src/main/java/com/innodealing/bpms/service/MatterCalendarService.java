package com.innodealing.bpms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.mapper.MatterCalendarMapper;
import com.innodealing.bpms.model.MatterCalendar;
import com.innodealing.bpms.model.MatterCalendarDate;
import com.innodealing.bpms.model.SurviveProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service("matterCalendarService")
public class MatterCalendarService {

    @Autowired
    MatterCalendarMapper matterCalendarMapper;

    public PageInfo<MatterCalendar> findCalendarData(ReqData reqData){

        Integer pageNum = 0;
        Integer pageSize = 30;
        PageInfo page;
        if (reqData.getInteger("offset") != null) {
            pageNum = reqData.getInteger("offset");
        }
        if (reqData.getInteger("pageSize") != null) {
            pageSize = reqData.getInteger("pageSize");
        }
        try {
            PageHelper.offsetPage(pageNum, pageSize);
            List<MatterCalendar> matterCalendarList = matterCalendarMapper.findCalendarData(reqData);
            page = new PageInfo(matterCalendarList);

        } finally {
            PageHelper.clearPage();
        }

        return page;
    }

    public List<MatterCalendarDate> findMonthProcessDate(ReqData reqData){
        return matterCalendarMapper.findMonthProcessDate(reqData);
    }

    public List<MatterCalendar> findMonthCompleteMatter(ReqData reqData){
        return matterCalendarMapper.findMonthCompleteMatter(reqData);
    }

    public List<MatterCalendar> findProcessDay(ReqData reqData){
        return matterCalendarMapper.findProcessDay(reqData);
    }

    public List<MatterCalendar> findMonthRemindMatter(ReqData reqData){
        return matterCalendarMapper.findMonthRemindMatter(reqData);
    }

    public List<MatterCalendar> findMonthRemindProcess(ReqData reqData){
        return matterCalendarMapper.findMonthRemindProcess(reqData);
    }

    public List<MatterCalendar> findProcessCompleteData(ReqData reqData){
        return matterCalendarMapper.findProcessCompleteData(reqData);
    }
}
