package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.MatterCalendar;
import com.innodealing.bpms.model.MatterCalendarDate;
import com.innodealing.bpms.model.SurviveProcess;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface MatterCalendarMapper {

    List<MatterCalendar> findCalendarData(ReqData reqData);

    List<MatterCalendarDate> findMonthProcessDate(ReqData reqData);

    List<MatterCalendar> findMonthCompleteMatter(ReqData reqData);

    List<MatterCalendar> findMonthRemindMatter(ReqData reqData);

    List<MatterCalendar> findProcessDay(ReqData reqData);

    List<MatterCalendar> findMonthRemindProcess(ReqData reqData);

    List<MatterCalendar> findProcessCompleteData(ReqData reqData);
}
