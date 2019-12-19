package com.innodealing.bpms.appconfig.history.commap;

import com.innodealing.bpms.model.Area;
import com.innodealing.bpms.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AreaMap {
    @Autowired
    private AreaService areaService;

    public static Map map = new HashMap();
    private static AreaMap areaMap;

    @PostConstruct
    private void init(){
        areaMap = this;
        areaMap.areaService = this.areaService;
    }

    public static void refreshMap(){
        List<Area> areaList = areaMap.areaService.findAll();
        map = areaList.stream().collect(Collectors.toMap(Area::getCode, Area::getName));
    }
}
