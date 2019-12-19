package com.innodealing.bpms.appconfig.history;

import com.innodealing.bpms.appconfig.history.commap.*;

import java.util.HashMap;
import java.util.Map;

public class CompareObjectMap {

    //事项模板
    private static Map<Integer, String> mapMatter;
    //算定义模板
    private static Map<Integer, String> mapCustomerMatter;
    //项目负责人
    private static Map<String, String> mapUser;
    //项目
    private static Map<String, String> mapBond;
    //地域
    private static Map<String, String> mapArea;
    //发行人行业
    private static Map<String, String> mapIndustry;
    //发行人国民经济部门
    private static Map<String, String> mapEconomicDepartment;

    //自动关联
    private static Map<String,String> mapAutoRelate;
    static {
        mapAutoRelate = new HashMap();
        mapAutoRelate.put("0","否");
        mapAutoRelate.put("1","是");
    }
    //督导确认
    private static Map<String,String> mapConfirm;
    static {
        mapConfirm = new HashMap();
        mapConfirm.put("0","不需要");
        mapConfirm.put("1","需要");
    }
    //触发通知
    private static Map<String,String> mapMailUser;
    static {
        mapMailUser = new HashMap();
        mapMailUser.put("0","");
        mapMailUser.put("1","发行人对接人");
        mapMailUser.put("2","项目负责人");
        mapMailUser.put("3","发行人对接人、项目负责人");
        mapMailUser.put("4","督导人员");
        mapMailUser.put("5","发行人对接人、督导人员");
        mapMailUser.put("6","项目负责人、督导人员");
        mapMailUser.put("7","发行人对接人、项目负责人、督导人员");
    }

    //付息频率
    private static Map<String,String> mapPayFrequency;
    static {
        mapPayFrequency = new HashMap();
        mapPayFrequency.put("0","按季支付");
        mapPayFrequency.put("1","按年支付");
    }

    public static Map getMapByKind(int kind) {
        Map map = null;
        switch (kind){
            case 1:
                map = mapAutoRelate;
                break;
            case 2:
                map = mapConfirm;
                break;
            case 3:
                map = mapMailUser;
                break;
            case 4:
                map = mapPayFrequency;
                break;
            case 6:
                map = IndustryMap.map;
                break;
            case 7:
                map = EconomicDepartmentMap.map;
                break;
            case 8:
                map = AreaMap.map;
                break;
            case 9:
                map = BondMap.map;
                break;
            case 10:
                map = UserMap.map;
                break;
            case 11:
                map = MatterTemplateMap.map;
                break;
            case 12:
                map = CustomMatterMap.map;
                break;
        }
        return  map;
    }
}
