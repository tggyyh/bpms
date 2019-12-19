package com.innodealing.bpms.appconfig.history.commap;

import com.innodealing.bpms.model.Bond;
import com.innodealing.bpms.service.BondService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BondMap {
    @Autowired
    private BondService bondService;

    public static Map map = new HashMap();
    private static BondMap bondMap;

    @PostConstruct
    private void init(){
        bondMap = this;
        bondMap.bondService = this.bondService;
    }

    public static void refreshMap(){
        List<Bond> bondList = bondMap.bondService.findAllBond();
        map = bondList.stream().collect(Collectors.toMap(Bond::getCode, Bond::getShortname));
    }
}
