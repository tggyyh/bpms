package com.innodealing.bpms.appconfig.history.commap;

import com.innodealing.bpms.model.Industry;
import com.innodealing.bpms.service.IndustryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IndustryMap {
    @Autowired
    private IndustryService industryService;

    public static Map map = new HashMap();
    private static IndustryMap industryMap;

    @PostConstruct
    private void init(){
        industryMap = this;
        industryMap.industryService = this.industryService;
    }

    public static void refreshMap(){
        List<Industry> areaList = industryMap.industryService.findAll();
        map = areaList.stream().collect(Collectors.toMap(Industry::getCode, Industry::getMark));
    }
}
