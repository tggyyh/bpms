package com.innodealing.bpms.appconfig.history;

import com.innodealing.bpms.appconfig.history.commap.CustomMatterMap;
import com.innodealing.bpms.model.*;
import com.innodealing.bpms.service.AreaService;
import com.innodealing.bpms.service.BondService;
import com.innodealing.bpms.service.EconomicDepartmentService;
import com.innodealing.bpms.service.IndustryService;
import org.activiti.engine.impl.transformer.BigDecimalToString;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CompareObject {

    public static List<String> compare(Object ob1, Object ob2) throws Exception {
        List<String> result = new ArrayList<>();
        if (ob1 == null || ob2 == null) {
            throw new Exception("比较对象不能为空");
        }
        if (!ob1.getClass().equals(ob2.getClass())) {
            throw new Exception("比较对象不能为不同类型");
        }

        String subName = "";
        if(ob1 instanceof CustomSubMatter){
            CustomSubMatter customSubMatter = (CustomSubMatter) ob1;
            subName = "子事项" + customSubMatter.getOrderIndex() + "—";
        }

        Field[] fields1 = ob1.getClass().getDeclaredFields();
        Field[] fields2 = ob2.getClass().getDeclaredFields();
        for (Field f1 : fields1) {
            CompareField cf = f1.getAnnotation(CompareField.class);
            if (null != cf) {
                PropertyDescriptor pd1 = new PropertyDescriptor(f1.getName(), ob1.getClass());
                Method getMethod1 = pd1.getReadMethod();
                Object o1 = getMethod1.invoke(ob1);
                Object o2 = getMethod1.invoke(ob2);
                if(!Objects.equals(o1,o2)){
                    int type = cf.type();
                    String s1 = "";
                    String s2 = "";
                    switch (type) {
                        case 0:
                            s1 = o1 == null ? "" : o1.toString();
                            s2 = o2 == null ? "" : o2.toString();
                            break;
                        case 1:
                            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
                            s1 = o1 == null ? "" : sdf.format(o1);
                            s2 = o2 == null ? "" : sdf.format(o2);
                            break;
                        case 2:
                            Map map = CompareObjectMap.getMapByKind(cf.kind());
                            s1 = o1 == null ? "" : o1.toString();
                            s2 = o2 == null ? "" : o2.toString();
                            s1 = (String)map.get(s1);
                            s2 = (String)map.get(s2);
                            break;
                        case 3:
                            BigDecimal bd1 = null==o1 ? new BigDecimal(0) : new BigDecimal(o1.toString());
                            BigDecimal bd2 = null==o1 ? new BigDecimal(0) : new BigDecimal(o2.toString());
                            s1 = bd1.subtract(bd2).compareTo(new BigDecimal(0))==0 ? "" : o1.toString();
                            s2 = bd1.subtract(bd2).compareTo(new BigDecimal(0))==0 ? "" : o2.toString();
                            break;
                        case 4:
                            SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd");
                            s1 = o1 == null ? "" : sdf4.format(o1);
                            s2 = o2 == null ? "" : sdf4.format(o2);
                            break;
                        case 5:
                            List<String> strList5 = compare(o1, o2);
                            result.addAll(strList5);
                            break;
                        case 6:
                            List<String> strList6 = compareSubCustomMatter(o1, o2);
                            result.addAll(strList6);
                            break;

                    }
                    if (!s1.equals(s2)) {
                        if(cf.kind()==10){
                            Map mapUser = CompareObjectMap.getMapByKind(cf.kind());
                            List<BondManager> bondManagerList1 = (List<BondManager>)o1;
                            List<BondManager> bondManagerList2 = (List<BondManager>)o2;
                            String[] r1 = new String[bondManagerList1.size()];
                            String[] r2 = new String[bondManagerList2.size()];
                            int index = 0;
                            for(BondManager bondManager : bondManagerList1){
                                r1[index] = mapUser.get(bondManager.getUserId()).toString();
                                index += 1;
                            }
                            index = 0;
                            for(BondManager bondManager : bondManagerList2){
                                r2[index] = mapUser.get(bondManager.getUserId()).toString();
                                index += 1;
                            }
                            s1 = Arrays.toString(r1);
                            s2 = Arrays.toString(r2);
                        }
                        result.add("<span style='font-weight: bold;'>" + subName + cf.mark() + "</span>:由 “" + s1 + "” <span style='font-weight: bold;'>修改为</span> “" + s2 + "”");
                    }
                }
            }
        }
        return result;
    }

    //项目模板关联
    public static List<String> compareBondList(List<Object> ob1List, List<Object> ob2List) throws Exception {
        List<String> result = new ArrayList<>();
        if (ob1List == null || ob2List == null) {
            throw new Exception("比较对象不能为空");
        }
        if(ob1List.size()<=0 && ob2List.size()<=0){
            throw new Exception("比较对象不能为空");
        }
        if (ob1List.size()>0 && ob2List.size()>0){
            if(!ob1List.get(0).getClass().equals(ob2List.get(0).getClass())) {
                throw new Exception("比较对象不能为不同类型");
            }
        }

        Map mapBond = CompareObjectMap.getMapByKind(9);
        if (!ob1List.equals(ob2List)) {
            for(Object obj1 : ob1List){
                BondMatter bondMatter1 = (BondMatter)obj1;
                boolean isFlag = false;
                for(Object obj2 : ob2List){
                    BondMatter bondMatter2 = (BondMatter)obj2;
                    if(bondMatter1.equals(bondMatter2)){
                        isFlag = true;
                        break;
                    }
                }
                if(!isFlag){
                    if(null!=mapBond && mapBond.size()>0){
                        result.add("<span style='font-weight: bold;'>" + mapBond.get(bondMatter1.getBondCode()) + "</span>：由 “勾选” <span style='font-weight: bold;'>修改为</span> “未勾选”");
                    }
                }
            }

            for(Object obj2 : ob2List){
                BondMatter bondMatter2 = (BondMatter)obj2;
                boolean isFlag = false;
                for(Object obj1 : ob1List){
                    BondMatter bondMatter1 = (BondMatter)obj1;
                    if(bondMatter1.equals(bondMatter2)){
                        isFlag = true;
                        break;
                    }
                }
                if(!isFlag){
                    if(null!=mapBond && mapBond.size()>0){
                        result.add("<span style='font-weight: bold;'>" + mapBond.get(bondMatter2.getBondCode()) + "</span>：由 “未勾选” <span style='font-weight: bold;'>修改为</span> “勾选”");
                    }
                }
            }
        }
        return result;
    }
    //发行人模板关联
    public static List<String> compareCompanyList(List<Object> ob1List, List<Object> ob2List) throws Exception {
        List<String> result = new ArrayList<>();
        if (ob1List == null || ob2List == null) {
            throw new Exception("比较对象不能为空");
        }
        if(ob1List.size()<=0 && ob2List.size()<=0){
            throw new Exception("比较对象不能为空");
        }
        if (ob1List.size()>0 && ob2List.size()>0){
            if(!ob1List.get(0).getClass().equals(ob2List.get(0).getClass())) {
                throw new Exception("比较对象不能为不同类型");
            }
        }

        if (!ob1List.equals(ob2List)) {
            for(Object obj1 : ob1List){
                CompanyMatter companyMatter1 = (CompanyMatter)obj1;
                boolean isFlag = false;
                for(Object obj2 : ob2List){
                    CompanyMatter companyMatter2 = (CompanyMatter)obj2;
                    if(companyMatter1.equals(companyMatter2)){
                        isFlag = true;
                        break;
                    }
                }
                if(!isFlag){
                    result.add("<span style='font-weight: bold;'>" + companyMatter1.getCompanyName() + "</span>: 由  “勾选” <span style='font-weight: bold;'>修改为</span> “未勾选”");
                }
            }

            for(Object obj2 : ob2List){
                CompanyMatter companyMatter2 = (CompanyMatter)obj2;
                boolean isFlag = false;
                for(Object obj1 : ob1List){
                    CompanyMatter companyMatter1 = (CompanyMatter)obj1;
                    if(companyMatter1.equals(companyMatter2)){
                        isFlag = true;
                        break;
                    }
                }
                if(!isFlag){
                    result.add("<span style='font-weight: bold;'>" + companyMatter2.getCompanyName() + "</span>: 由  “未勾选” <span style='font-weight: bold;'>修改为</span> “勾选”");
                }
            }
        }

        return result;
    }
    //项目关联
    public static List<String> compareBondMatterList(List<Object> ob1List, List<Object> ob2List) throws Exception {
        List<String> result = new ArrayList<>();
        if (ob1List == null || ob2List == null) {
            throw new Exception("比较对象不能为空");
        }
        if(ob1List.size()<=0 && ob2List.size()<=0){
            throw new Exception("比较对象不能为空");
        }
        if (ob1List.size()>0 && ob2List.size()>0){
            if(!ob1List.get(0).getClass().equals(ob2List.get(0).getClass())) {
                throw new Exception("比较对象不能为不同类型");
            }
        }

        Map mapTemplate = CompareObjectMap.getMapByKind(11);
        if (!ob1List.equals(ob2List)) {
            for(Object obj1 : ob1List){
                BondMatter bondMatter1 = (BondMatter)obj1;
                boolean isFlag = false;
                for(Object obj2 : ob2List){
                    BondMatter bondMatter2 = (BondMatter)obj2;
                    if(bondMatter1.equals(bondMatter2)){
                        isFlag = true;
                        break;
                    }
                }
                if(!isFlag){
                    if(null!=mapTemplate && mapTemplate.size()>0){
                        result.add("<span style='font-weight: bold;'>" + mapTemplate.get(bondMatter1.getTemplateId()) + "</span>：由 “勾选” <span style='font-weight: bold;'>修改为</span> “未勾选”");
                    }
                }
            }

            for(Object obj2 : ob2List){
                BondMatter bondMatter2 = (BondMatter)obj2;
                boolean isFlag = false;
                for(Object obj1 : ob1List){
                    BondMatter bondMatter1 = (BondMatter)obj1;
                    if(bondMatter1.equals(bondMatter2)){
                        isFlag = true;
                        break;
                    }
                }
                if(!isFlag){
                    if(null!=mapTemplate && mapTemplate.size()>0){
                        result.add("<span style='font-weight: bold;'>" + mapTemplate.get(bondMatter2.getTemplateId()) + "</span>：由 “未勾选” <span style='font-weight: bold;'>修改为</span> “勾选”");
                    }
                }
            }
        }

        return result;
    }
    //发行人关联
    public static List<String> compareCompanyMatterList(List<Object> ob1List, List<Object> ob2List) throws Exception {
        List<String> result = new ArrayList<>();
        if (ob1List == null || ob2List == null) {
            throw new Exception("比较对象不能为空");
        }
        if(ob1List.size()<=0 && ob2List.size()<=0){
            throw new Exception("比较对象不能为空");
        }
        if (ob1List.size()>0 && ob2List.size()>0){
            if(!ob1List.get(0).getClass().equals(ob2List.get(0).getClass())) {
                throw new Exception("比较对象不能为不同类型");
            }
        }
        if(ob1List.get(0) instanceof CompanyMatter){
            Map mapTemplate = CompareObjectMap.getMapByKind(11);
            if (!ob1List.equals(ob2List)) {
                for(Object obj1 : ob1List){
                    CompanyMatter companyMatter1 = (CompanyMatter)obj1;
                    boolean isFlag = false;
                    for(Object obj2 : ob2List){
                        CompanyMatter companyMatter2 = (CompanyMatter)obj2;
                        if(companyMatter1.equals(companyMatter2)){
                            isFlag = true;
                            break;
                        }
                    }
                    if(!isFlag){
                        if(null!=mapTemplate && mapTemplate.size()>0){
                            result.add("<span style='font-weight: bold;'>" + mapTemplate.get(companyMatter1.getTemplateId()) + "</span>：由 “勾选” <span style='font-weight: bold;'>修改为</span> “未勾选”");
                        }
                    }
                }

                for(Object obj2 : ob2List){
                    CompanyMatter companyMatter2 = (CompanyMatter)obj2;
                    boolean isFlag = false;
                    for(Object obj1 : ob1List){
                        CompanyMatter companyMatter1 = (CompanyMatter)obj1;
                        if(companyMatter1.equals(companyMatter2)){
                            isFlag = true;
                            break;
                        }
                    }
                    if(!isFlag){
                        if(null!=mapTemplate && mapTemplate.size()>0){
                            result.add("<span style='font-weight: bold;'>" + mapTemplate.get(companyMatter2.getTemplateId()) + "</span>：由  “未勾选” <span style='font-weight: bold;'>修改为</span> “勾选”");
                        }
                    }
                }
            }
        }

        return result;
    }
    //自定义模板关联
    public static List<String> compareCustomMatter(List<Integer> obj1, List<Integer> obj2) throws Exception {
        List<String> result = new ArrayList<>();
        if (obj1 == null || obj2 == null) {
            throw new Exception("比较对象不能为空");
        }
        if(obj1.size()<=0 && obj2.size()<=0){
            throw new Exception("比较对象不能为空");
        }

        if (!obj1.equals(obj2)) {
            Map mapCustomTemplate = CompareObjectMap.getMapByKind(12);

            for(int i=0;i<obj1.size();i++){
                int o1 = obj1.get(i).intValue();
                boolean isFlag = false;
                for(int j=0;j<obj2.size();j++){
                    int o2 = obj2.get(j).intValue();
                    if(o1==o2){
                        isFlag = true;
                        break;
                    }
                }
                if(!isFlag){
                    if(null!=mapCustomTemplate && mapCustomTemplate.size()>0){
                        result.add("<span style='font-weight: bold;'>" + mapCustomTemplate.get(o1) + "</span>：由 “勾选” <span style='font-weight: bold;'>修改为</span> “未勾选”");
                    }
                }
            }

            for(int i=0;i<obj2.size();i++){
                int o2 = obj2.get(i).intValue();
                boolean isFlag = false;
                for(int j=0;j<obj1.size();j++){
                    int o1 = obj1.get(j).intValue();
                    if(o1==o2){
                        isFlag = true;
                        break;
                    }
                }
                if(!isFlag){
                    if(null!=mapCustomTemplate && mapCustomTemplate.size()>0){
                        result.add("<span style='font-weight: bold;'>" + mapCustomTemplate.get(o2) + "</span>：由 “未勾选” <span style='font-weight: bold;'>修改为</span> “勾选”");
                    }
                }
            }
        }
        return result;
    }
    //行权付息
    public static List<String> compareSubCustomMatter(Object ob1List, Object ob2List) throws Exception {
        List<String> result = new ArrayList<>();
        List<CustomSubMatter> customSubMatterList1 = (List<CustomSubMatter>)ob1List;
        List<CustomSubMatter> customSubMatterList2 = (List<CustomSubMatter>)ob2List;
        if (customSubMatterList1 == null || customSubMatterList2 == null) {
            throw new Exception("比较对象不能为空");
        }
        if(customSubMatterList1.size()<=0 && customSubMatterList2.size()<=0){
            throw new Exception("比较对象不能为空");
        }

        for(CustomSubMatter customSubMatter1 : customSubMatterList1){
            boolean isFlag = false;
            for(CustomSubMatter customSubMatter2 : customSubMatterList2){
                if(customSubMatter1.getOrderIndex()==customSubMatter2.getOrderIndex()){
                    result.addAll(compare(customSubMatter1, customSubMatter2));
                    isFlag = true;
                    break;
                }
            }
            if(!isFlag){
                result.add("<span style='font-weight: bold;'>子事项-" + customSubMatter1.getOrderIndex() + "</span>：被删除");
            }
        }
        for(CustomSubMatter customSubMatter2 : customSubMatterList2){
            boolean isFlag = false;
            for(CustomSubMatter customSubMatter1 : customSubMatterList1){
                if(customSubMatter1.getOrderIndex()==customSubMatter2.getOrderIndex()){
                    //contentList.addAll(compare(customSubMatter1, customSubMatter2));
                    isFlag = true;
                    break;
                }
            }
            if(!isFlag){
                result.add("<span style='font-weight: bold;'>子事项-" + customSubMatter2.getOrderIndex() + "</spn>：新增");
            }
        }
        return result;
    }

}
