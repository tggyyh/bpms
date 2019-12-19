package com.innodealing.bpms.appconfig.history.commap;

import com.innodealing.bpms.model.CustomMatter;
import com.innodealing.bpms.service.CustomMatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CustomMatterMap {
    @Autowired
    private CustomMatterService customMatterService;

    public static Map map = new HashMap();
    private static CustomMatterMap customMatterMap;

    @PostConstruct
    private void init(){
        customMatterMap = this;
        customMatterMap.customMatterService = this.customMatterService;
    }

    public static void refreshMap(){
        List<CustomMatter> customMatterList = customMatterMap.customMatterService.findAllCustomMatter();
        map = customMatterList.stream().collect(Collectors.toMap(CustomMatter::getId, CustomMatter::getName));
    }
}
