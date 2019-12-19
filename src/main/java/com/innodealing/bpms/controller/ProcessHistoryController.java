package com.innodealing.bpms.controller;

import com.google.gson.Gson;
import com.innodealing.bpms.appconfig.history.CompareObject;
import com.innodealing.bpms.model.ProcessHistory;
import com.innodealing.bpms.model.ProcessInfo;
import com.innodealing.bpms.service.ProcessHistoryService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/process-history")
public class ProcessHistoryController {
    @Autowired
    private ProcessHistoryService processHistoryService;
    private static final Logger log = LoggerFactory.getLogger(ProcessHistoryController.class);
    @RequestMapping(value="/{pId}", method = RequestMethod.GET)
    public String viewTask(@PathVariable String pId, Model model) throws Exception{
        List<ProcessHistory> phs =  processHistoryService.findByProcessId(pId);
        List<ProcessHistory> newPhs = new ArrayList();
        Gson gson = new Gson();
        if(!CollectionUtils.isEmpty(phs)) {
            ProcessHistory  comparePh = null;
            for (ProcessHistory ph : phs) {
                int taskType = ph.getTaskType();
                int type = ph.getType();
                if (0 == taskType) {
                    comparePh = ph;
                    continue;
                } else if (1 == taskType) {
                    if (3 != type && null != comparePh) {
                        ProcessInfo pi1 = gson.fromJson(ph.getProcessInfo(), ProcessInfo.class);
                        ProcessInfo pi2 = gson.fromJson(comparePh.getProcessInfo(), ProcessInfo.class);
                        List<String> list = CompareObject.compare(pi2,pi1);
                        ph.setChangeList(list);
                        newPhs.add(ph);
                    }else{
                        newPhs.add(ph);
                    }
                } else {
                    ProcessInfo pi = gson.fromJson(ph.getProcessInfo(), ProcessInfo.class);
                    ph.setPi(pi);
                    newPhs.add(ph);
                }
            }
        }
        Collections.reverse(newPhs);
        model.addAttribute("phs", newPhs);
        return "app/bpms/process-history/view";
    }

}
