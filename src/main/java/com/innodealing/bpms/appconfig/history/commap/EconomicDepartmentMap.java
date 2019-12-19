package com.innodealing.bpms.appconfig.history.commap;

import com.innodealing.bpms.model.EconomicDepartment;
import com.innodealing.bpms.service.EconomicDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EconomicDepartmentMap {
    @Autowired
    private EconomicDepartmentService economicDepartmentService;

    public static Map map = new HashMap();
    private static EconomicDepartmentMap economicDepartmentMap;

    @PostConstruct
    private void init(){
        economicDepartmentMap = this;
        economicDepartmentMap.economicDepartmentService = this.economicDepartmentService;
    }

    public static void refreshMap(){
        List<EconomicDepartment> economicDepartmentList = economicDepartmentMap.economicDepartmentService.findAll();
        map = economicDepartmentList.stream().collect(Collectors.toMap(EconomicDepartment::getCode, EconomicDepartment::getMark));
    }
}
