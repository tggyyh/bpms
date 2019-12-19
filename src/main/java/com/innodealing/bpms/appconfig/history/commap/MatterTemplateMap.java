package com.innodealing.bpms.appconfig.history.commap;

import com.innodealing.bpms.model.MatterTemplate;
import com.innodealing.bpms.service.MatterTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MatterTemplateMap {
    @Autowired
    private MatterTemplateService matterTemplateService;

    public static Map map = new HashMap();
    private static MatterTemplateMap matterTemplateMap;

    @PostConstruct
    private void init(){
        matterTemplateMap = this;
        matterTemplateMap.matterTemplateService = this.matterTemplateService;
    }

    public static void refreshMap(){
        List<MatterTemplate> matterTemplateList = matterTemplateMap.matterTemplateService.findMatterAll();
        map = matterTemplateList.stream().collect(Collectors.toMap(MatterTemplate::getId, MatterTemplate::getName));
    }
}
