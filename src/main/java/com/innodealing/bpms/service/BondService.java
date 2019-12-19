package com.innodealing.bpms.service;

import com.ctc.wstx.util.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.mapper.*;
import com.innodealing.bpms.model.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("bondService")
public class BondService {
    @Autowired
    BondMapper bondMapper;
    @Autowired
    BondMatterMapper bondMatterMapper;
    @Autowired
    BondManagerMapper bondManagerMapper;
    @Autowired
    BankManagerMapper bankManagerMapper;
    @Autowired
    CompanyMapper companyMapper;
    @Autowired
    CompanyLinkmanMapper companyLinkmanMapper;
    @Autowired
    CustomMatterMapper customMatterMapper;
    @Autowired
    CompanyMatterMapper companyMatterMapper;
    @Autowired
    ProcessInfoService processInfoService;
    @Autowired
    ProcessService processService;
    //事务
    @Autowired
    DataSourceTransactionManager transactionManager;

    private final static Logger logger = LoggerFactory.getLogger(BondService.class);

    public Bond findById(int id){
        return bondMapper.findById(id);
    }

    public int isExist(Bond bond){
        return bondMapper.isExist(bond);
    }

    public List<Bond> findAllBond(){
        return bondMapper.findAllBond();
    }

    public PageInfo<Bond> findAll(ReqData reqData){

        Integer pageNum = 0;
        Integer pageSize = 30;
        PageInfo page;
        if (reqData.getInteger("offset") != null) {
            pageNum = reqData.getInteger("offset");
        }
        if (reqData.getInteger("pageSize") != null) {
            pageSize = reqData.getInteger("pageSize");
        }
        try {
            PageHelper.offsetPage(pageNum, pageSize);
            List<Bond> bondList = bondMapper.findAll(reqData);

            String[] bondCodes = new String[bondList.size()];
            int index = 0;
            for (Bond bond : bondList) {
                bondCodes[index] = bond.getCode();
                index++;
            }

            if(null!=bondCodes && bondCodes.length>0){

                //组织关联事项
                ReqData bondData = new ReqData();
                bondData.put("matterType", "1");
                bondData.put("bondCodes", bondCodes);
                List<BondMatter> bondMatterList = bondMatterMapper.findByBondCodes(bondData);
                bondList.stream().forEach(bond -> {
                    bond.setBondMatterList(bondMatterList.stream().filter(bondMatter -> bondMatter.getBondCode().equals(bond.getCode())).collect(Collectors.toList()));
                });

                //组织自定义关联事项
                ReqData customData = new ReqData();
                customData.put("customType", "1");
                customData.put("relation", "1");
                customData.put("customKeys", bondCodes);
                List<CustomMatter> customMatterList = customMatterMapper.findByKeys(customData);
                bondList.stream().forEach(bond -> {
                    bond.setCustomMatterList(customMatterList.stream().filter(customMatter -> customMatter.getKey().equals(bond.getCode())).collect(Collectors.toList()));
                });

                //组织项目负责人
                List<BondManager> bondManagerList = bondManagerMapper.findByBondCodes(bondData);
                bondList.stream().forEach(bond -> {
                    bond.setBondManagerList(bondManagerList.stream().filter(bondManager -> bondManager.getBondCode().equals(bond.getCode())).collect(Collectors.toList()));
                });
            }

            page = new PageInfo(bondList);

        } finally {
            PageHelper.clearPage();
        }

        return page;
    }

    public Bond findByBondId(int bondId){
        Bond bond = bondMapper.findByBondId(bondId);
        return bond;
    }

    public List<Bond> findByCompanyId(int companyId){
        List<Bond> bondList = bondMapper.findByCompanyId(companyId);
        return bondList;
    }

    public Bond findByBondCode(String bondCode){
        Bond bond = bondMapper.findByBondCode(bondCode);
        return bond;
    }

    public List<Bond> findOtherBond(ReqData reqData){
        List<Bond> bondList = bondMapper.findOtherBond(reqData);
        return bondList;
    }

    public int deleteByIds(int[] ids){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            //先删除项目模板关联表
            count = bondMatterMapper.deleteByBondIds(ids);
            //再更新项目表
            count = bondMapper.deleteByIds(ids);

            transactionManager.commit(status);
            logger.info("删除成功");
        }catch(Exception ex){
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("删除失败:" + ex.getMessage());
        }
        return count;
    }

    public int addSave(Bond bond){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            //发行人信息
            Company company = companyMapper.findByName(bond.getCompanyName());
            if(null!=company && company.getId()>0){
                Company baseCompany = bond.getCompany();
                company.setStockCode(baseCompany.getStockCode());
                company.setIndustryScale(baseCompany.getIndustryScale());
                company.setIndustryType(baseCompany.getIndustryType());
                company.setIndustryBigType(baseCompany.getIndustryBigType());
                company.setEconomicDepartment(baseCompany.getEconomicDepartment());
                company.setEconomicDepartmentDetail(baseCompany.getEconomicDepartmentDetail());;
                company.setEconomicSector(baseCompany.getEconomicSector());
                company.setProvinceCode(baseCompany.getProvinceCode());
                company.setCityCode(baseCompany.getCityCode());
                company.setRatingCompany1(baseCompany.getRatingCompany1());
                company.setCorporateRating1(baseCompany.getCorporateRating1());
                company.setRatingCompany2(baseCompany.getRatingCompany2());
                company.setCorporateRating2(baseCompany.getCorporateRating2());
                company.setCorporateOrgCode2(baseCompany.getCorporateOrgCode2());
                companyMapper.updateCompany(company);
            }else{
                company = bond.getCompany();
                company.setStatus(0);
                companyMapper.insertCompany(company);

                //模板自动关联
                companyMatterMapper.insertByCompanyName(company.getName());
            }
            if(null!=company && company.getId()>0){
                //清除现在的发行人对接人
                companyLinkmanMapper.deleteAllByCompanyName(company.getName());
                //再新增
                List<CompanyLinkman> companyLinkmanList = bond.getCompany().getCompanyLinkmanList();
                if(null!=companyLinkmanList && companyLinkmanList.size()>0){
                    companyLinkmanMapper.insertCompanyLinkmans(companyLinkmanList);
                }
            }


            //债券信息
            Bond baseBond = bondMapper.findByBondCode(bond.getCode());
            if(null!=baseBond && baseBond.getId()>0){
                bond.setId(baseBond.getId());
                bond.setCreateTime(baseBond.getCreateTime());
                bondMapper.updateBond(bond);
            }else{
                bondMapper.insertBond(bond);
                //模板自动关联
                bondMatterMapper.insertByBondCode(bond.getCode());
            }
            if(null!=bond && bond.getId()>0){
                //清除项目负责人
                bondManagerMapper.deleteByBondCode(bond.getCode());
                //添加项目负责人
                bondManagerMapper.insertBondManager(bond.getBondManagerList());

                //清除监管银行负责人
                bankManagerMapper.deleteByBondCode(bond.getCode());
                if(null!=bond.getBankManagerList() && bond.getBankManagerList().size()>0){
                    bankManagerMapper.insertBankManagers(bond.getBankManagerList());
                }
            }

            count = 1;
            transactionManager.commit(status);
            logger.info("保存成功");
        }catch(Exception ex){
            count = -1;
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("保存失败:" + ex.getMessage());
        }
        return count;
    }

    public int editSave(Bond bond){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            //获取现发行人信息
            Company company = companyMapper.findByName(bond.getCompanyName());
            if(null!=company && company.getId()>0){
                    Company baseCompany = bond.getCompany();
                    company.setStockCode(baseCompany.getStockCode());
                    company.setIndustryScale(baseCompany.getIndustryScale());
                    company.setIndustryType(baseCompany.getIndustryType());
                    company.setIndustryBigType(baseCompany.getIndustryBigType());
                    company.setEconomicDepartment(baseCompany.getEconomicDepartment());
                    company.setEconomicDepartmentDetail(baseCompany.getEconomicDepartmentDetail());;
                    company.setEconomicSector(baseCompany.getEconomicSector());
                    company.setProvinceCode(baseCompany.getProvinceCode());
                    company.setCityCode(baseCompany.getCityCode());
                    company.setRatingCompany1(baseCompany.getRatingCompany1());
                    company.setCorporateRating1(baseCompany.getCorporateRating1());
                    company.setRatingCompany2(baseCompany.getRatingCompany2());
                    company.setCorporateRating2(baseCompany.getCorporateRating2());
                    company.setCorporateOrgCode2(baseCompany.getCorporateOrgCode2());
                    companyMapper.updateCompany(company);
                }else {
                    //通过ID查找原发行人
                    Company scompany = companyMapper.findByName(bondMapper.findByBondId(bond.getId()).getCompanyName());
                    //获取原发行人名称
                    String sname = scompany.getName();
                    //获取现发行人
                    company = bond.getCompany();
                    company.setStatus(0);

                    //新增发行人
                    companyMapper.insertCompany(company);
                    //模板自动关联
                    companyMatterMapper.insertByCompanyName(company.getName());
                    //原发行人更改为现发行人
                    companyMatterMapper.updateCompanyName(company.getName(),sname);
                    //更新所有有关债券的发行人名称
                    bondMapper.updateAssociationCompany(company.getName(),sname);
                }

            if(null!=company && company.getId()>0){


                //清除现在的发行人对接人
                companyLinkmanMapper.deleteAllByCompanyName(company.getName());
                //再新增
                List<CompanyLinkman> companyLinkmanList = bond.getCompany().getCompanyLinkmanList();
                if(null!=companyLinkmanList && companyLinkmanList.size()>0){
                    companyLinkmanMapper.insertCompanyLinkmans(companyLinkmanList);
                }
            }

            //债券信息
            bondMapper.updateBond(bond);
            if(null!=bond && bond.getId()>0){
                //清除项目负责人
                bondManagerMapper.deleteByBondCode(bond.getCode());
                //添加项目负责人
                bondManagerMapper.insertBondManager(bond.getBondManagerList());

                //清除监管银行负责人
                bankManagerMapper.deleteByBondCode(bond.getCode());
                if(null!=bond.getBankManagerList() && bond.getBankManagerList().size()>0){
                    bankManagerMapper.insertBankManagers(bond.getBankManagerList());
                    System.out.println("");
                }
            }

            count = 1;
            transactionManager.commit(status);
            logger.info("保存成功");
        }catch(Exception ex){
            count = -1;
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("保存失败:" + ex.getMessage());
        }
        return count;
    }

    //根据发行人名称查找发行人
    public Company SearchCompanyByName(String companyName){
        Company company = companyMapper.SearchCompanyByName(companyName);
        if (company == null){
            return new Company();
        }
        return company;
    }

    public List<Bond> findLinkMatterCount(int templateId){
        return bondMapper.findLinkMatterCount(templateId);
    }

    public PageInfo<Bond> findLinkMatterAll(ReqData reqData){

        Integer pageNum = 0;
        Integer pageSize = 30;
        PageInfo page;
        if (reqData.getInteger("offset") != null) {
            pageNum = reqData.getInteger("offset");
        }
        if (reqData.getInteger("pageSize") != null) {
            pageSize = reqData.getInteger("pageSize");
        }
        try {
            PageHelper.offsetPage(pageNum, pageSize);
            List<Bond> bondPage = bondMapper.findLinkMatterPage(reqData);
            List<Bond> bondList = bondMapper.findLinkMatterAll(reqData);

            for(Bond bond : bondPage){
                for(Bond bondItem : bondList){
                    if(bond.getId()==bondItem.getId()){
                        bond.setCompany(bondItem.getCompany());
                        bond.setBankManagerList(bondItem.getBankManagerList());
                        bond.setBondManagerList(bondItem.getBondManagerList());
                        bond.setBondMatterList(bondItem.getBondMatterList());
                        break;
                    }
                }
            }
            page = new PageInfo(bondPage);

        } finally {
            PageHelper.clearPage();
        }

        return page;
    }

    @Transactional
    public int updateSetBondExpire(Bond bond){
        int count = 0;
        try{
            bondMapper.updateSetBondExpire(bond);
            List<ProcessInfo> list = processInfoService.findByBondCode(bond.getCode());
            if(!CollectionUtils.isEmpty(list)){
                for(ProcessInfo pi:list){
                    processService.cancelPreocess(pi.getProcessId());
                }
            }
            count = 1;
        }catch(Exception ex){
            count = 0;
            ex.printStackTrace();
            logger.info("设置到期失败:" + ex.getMessage());
            logger.error("设置到期失败:",ex);
        }
        return count;
    }

    public int updateResetBondExpire(Bond bond){
        return bondMapper.updateResetBondExpire(bond);
    }

    public int removeBond(Bond bond){
        return bondMapper.removeBond(bond);
    }

    public List<Bond> findBondInfoByCompany(String companyName){
        return bondMapper.findBondInfoByCompany(companyName);
    }

    public Bond findBondByCode(String bondCode){
        return bondMapper.findBondByCode(bondCode);
    }

    public int updateBondExpire(){
        return bondMapper.updateBondExpire();
    }

    public int findIsExistence(String bondCode){
        return bondMapper.findIsExistence(bondCode);
    }

    public Bond findBondManagerByCode(String bondCode){
        return bondMapper.findBondManagerByCode(bondCode);
    }

    public Workbook downExcel(ReqData reqData) throws Exception{
        List<Bond> bondList = bondMapper.findBondExcel(reqData);

        String sheetName ="项目管理";
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
        sheet.setDefaultColumnWidth(16);

        // create style for header cells
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(HSSFColor.BLACK.index);
        style.setFont(font);

        //背景色
        style.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        //边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        //居中
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        //自动换行
        style.setWrapText(true);

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

        Row header = sheet.createRow(0);

        header = sheet.createRow(0);
        header.setHeightInPoints(30);

        header.createCell(0).setCellValue("起息日");
        header.getCell(0).setCellStyle(style);

        header.createCell(1).setCellValue("到期日");
        header.getCell(1).setCellStyle(style);

        header.createCell(2).setCellValue("债券简称");
        header.getCell(2).setCellStyle(style);

        header.createCell(3).setCellValue("发行人注册地");
        header.getCell(3).setCellStyle(style);
        header.createCell(4).setCellValue("债券代码");
        header.getCell(4).setCellStyle(style);
        header.createCell(5).setCellValue("发行人机构名称");
        header.getCell(5).setCellStyle(style);
        header.createCell(6).setCellValue("债券全称");
        header.getCell(6).setCellStyle(style);
        header.createCell(7).setCellValue("上市场所");
        header.getCell(7).setCellStyle(style);
        header.createCell(8).setCellValue("是否跨市");
        header.getCell(8).setCellStyle(style);
        header.createCell(9).setCellValue("批文总额（亿）");
        header.getCell(9).setCellStyle(style);
        header.createCell(10).setCellValue("发行规模");
        header.getCell(10).setCellStyle(style);
        header.createCell(11).setCellValue("债券期限");
        header.getCell(11).setCellStyle(style);
        header.createCell(12).setCellValue("评级（主体/债项）+评级公司名称");
        header.getCell(12).setCellStyle(style);
        header.createCell(13).setCellValue("票面利率");
        header.getCell(13).setCellStyle(style);
        header.createCell(14).setCellValue("主承销商");
        header.getCell(14).setCellStyle(style);
        header.createCell(15).setCellValue("担保机构");
        header.getCell(15).setCellStyle(style);
        header.createCell(16).setCellValue("承销方式");
        header.getCell(16).setCellStyle(style);
        header.createCell(17).setCellValue("受托管理人/债权代理人");
        header.getCell(17).setCellStyle(style);

        header.createCell(18).setCellValue("选择权种类");
        header.getCell(18).setCellStyle(style);
        header.createCell(19).setCellValue("上市日期");
        header.getCell(19).setCellStyle(style);


        header.createCell(20).setCellValue("项目组负责人");
        header.getCell(20).setCellStyle(style);
        header.createCell(21).setCellValue("项目组负责人（电话）");
        header.getCell(21).setCellStyle(style);
        header.createCell(22).setCellValue("项目组负责人（邮箱）");
        header.getCell(22).setCellStyle(style);
        header.createCell(23).setCellValue("发行人负责人（电话）");
        header.getCell(23).setCellStyle(style);
        header.createCell(24).setCellValue("发行人负责人（邮箱）");
        header.getCell(24).setCellStyle(style);
        header.createCell(25).setCellValue("监管银行负责人（电话）");
        header.getCell(25).setCellStyle(style);
        header.createCell(26).setCellValue("监管银行负责人（邮箱）");
        header.getCell(26).setCellStyle(style);



        CellStyle txtStyle = workbook.createCellStyle();

        int rowCount = 1;
        for (Bond bond : bondList) {
            Row visitorRow = sheet.createRow(rowCount++);
            visitorRow.createCell(0).setCellValue(null==bond.getValueDate()?"":sf.format(bond.getValueDate()));
            visitorRow.getCell(0).setCellStyle(txtStyle);
            visitorRow.createCell(1).setCellValue(null==bond.getPayDay()?"":sf.format(bond.getPayDay()));
            visitorRow.getCell(1).setCellStyle(txtStyle);
            visitorRow.createCell(2).setCellValue(null==bond.getShortname()?"":bond.getShortname());
            visitorRow.getCell(2).setCellStyle(txtStyle);
            String city = "";
            if(null!=bond.getCompany()){
                city = null==bond.getCompany().getProvinceName()?"":bond.getCompany().getProvinceName();
                city += null==bond.getCompany().getCityName()?"":bond.getCompany().getCityName();
            }
            visitorRow.createCell(3).setCellValue(city);
            visitorRow.getCell(3).setCellStyle(txtStyle);
            visitorRow.createCell(4).setCellValue(null==bond.getCode()?"":bond.getCode());
            visitorRow.getCell(4).setCellStyle(txtStyle);
            visitorRow.createCell(5).setCellValue(null==bond.getCompanyName()?"":bond.getCompanyName());
            visitorRow.getCell(5).setCellStyle(txtStyle);
            visitorRow.createCell(6).setCellValue(null==bond.getName()?"":bond.getName());
            visitorRow.getCell(6).setCellStyle(txtStyle);

            if(null!=bond.getListedPlace()){
                String[] aryListedPlace = bond.getListedPlace().split(";");
                for(int i=0;i<aryListedPlace.length;i++){
                    String itemListed = aryListedPlace[i];
                    if(aryListedPlace[i].indexOf("-")>0){
                        itemListed = aryListedPlace[i].substring(aryListedPlace[i].indexOf("-")+1);
                    }
                    aryListedPlace[i] = itemListed;
                }
                visitorRow.createCell(7).setCellValue(StringUtils.join(aryListedPlace,","));
            }else{
                visitorRow.createCell(7).setCellValue("");
            }
            visitorRow.getCell(7).setCellStyle(txtStyle);

            if(null!=bond.getListedPlace()){
                String[] aryCrossMarket = bond.getCrossMarket().split(";");
                for(int i=0;i<aryCrossMarket.length;i++){
                    String itemCrossMarket = aryCrossMarket[i];
                    if(aryCrossMarket[i].indexOf("-")>0){
                        itemCrossMarket = aryCrossMarket[i].substring(aryCrossMarket[i].indexOf("-")+1);
                    }
                    aryCrossMarket[i] = itemCrossMarket;
                }
                visitorRow.createCell(8).setCellValue(StringUtils.join(aryCrossMarket,","));
            }else{
                visitorRow.createCell(8).setCellValue("");
            }
            visitorRow.getCell(8).setCellStyle(txtStyle);

            visitorRow.createCell(9).setCellValue(null==bond.getDocumentAmount()?"":bond.getDocumentAmount().toString());
            visitorRow.getCell(9).setCellStyle(txtStyle);
            visitorRow.createCell(10).setCellValue(null==bond.getScale()?"":bond.getScale().toString());
            visitorRow.getCell(10).setCellStyle(txtStyle);
            visitorRow.createCell(11).setCellValue(null==bond.getTimeLimit()?"":bond.getTimeLimit().toString());
            visitorRow.getCell(11).setCellStyle(txtStyle);

            String strRating1 = "", corporateRating1 = "", corporateRating2 = "", ratingCompany1 = "", ratingCompany2 = "";
            if(null!=bond.getCompany()){
                if(null!=bond.getCompany().getCorporateRating1()){
                    String[] aryCorporateRating1 = bond.getCompany().getCorporateRating1().split(";");
                    for(int i=0;i<aryCorporateRating1.length;i++){
                        String itemCorporateRating1 = aryCorporateRating1[i];
                        if(aryCorporateRating1[i].indexOf("-")>0){
                            itemCorporateRating1 = aryCorporateRating1[i].substring(aryCorporateRating1[i].indexOf("-")+1);
                        }
                        aryCorporateRating1[i] = itemCorporateRating1;
                    }
                    corporateRating1 = StringUtils.join(aryCorporateRating1,",");
                }
                //corporateRating1 = null==bond.getCompany().getCorporateRating1()?"":bond.getCompany().getCorporateRating1().split("-")[1];
                ratingCompany1 = null==bond.getCompany().getRatingCompany1()?"":bond.getCompany().getRatingCompany1();

                if(null!=bond.getCompany().getCorporateRating2()){
                    String[] aryCorporateRating2 = bond.getCompany().getCorporateRating2().split(";");
                    for(int i=0;i<aryCorporateRating2.length;i++){
                        String itemCorporateRating2 = aryCorporateRating2[i];
                        if(aryCorporateRating2[i].indexOf("-")>0){
                            itemCorporateRating2 = aryCorporateRating2[i].substring(aryCorporateRating2[i].indexOf("-")+1);
                        }
                        aryCorporateRating2[i] = itemCorporateRating2;
                    }
                    corporateRating2 = StringUtils.join(aryCorporateRating2,",");
                }
                //corporateRating2 = null==bond.getCompany().getCorporateRating2()?"":bond.getCompany().getCorporateRating2().split("-")[1];
                ratingCompany2 = null==bond.getCompany().getRatingCompany2() ? "" : bond.getCompany().getRatingCompany2();
            }
            String rating1 = "";
            if(null!=bond.getRating1()){
                String[] aryRating1 = bond.getRating1().split(";");
                for(int i=0;i<aryRating1.length;i++){
                    String itemRating1 = aryRating1[i];
                    if(aryRating1[i].indexOf("-")>0){
                        itemRating1 = aryRating1[i].substring(aryRating1[i].indexOf("-")+1);
                    }
                    aryRating1[i] = itemRating1;
                }
                rating1 = StringUtils.join(aryRating1,",");
            }
            //String rating1 = null==bond.getRating1()?"":bond.getRating1().split("-")[1];
            if(!"".equals(corporateRating1) || !"".equals(rating1)){
                strRating1 = "(" + corporateRating1 + "/" + rating1 + ")";
            }
            strRating1 += ratingCompany1;

            if(null!=bond.getBondRatingCompany1() && !"".equals(bond.getBondRatingCompany1())){
                strRating1 += "".equals(ratingCompany1)?bond.getBondRatingCompany1():(","+bond.getBondRatingCompany1());
            }

            String rating2 = "";
            if(null!=bond.getRating2()){
                String[] aryRating2 = bond.getRating2().split(";");
                for(int i=0;i<aryRating2.length;i++){
                    String itemRating2 = aryRating2[i];
                    if(aryRating2[i].indexOf("-")>0){
                        itemRating2 = aryRating2[i].substring(aryRating2[i].indexOf("-")+1);
                    }
                    aryRating2[i] = itemRating2;
                }
                rating2 = StringUtils.join(aryRating2,",");
            }
            //String rating2 = null==bond.getRating2() ? "" : bond.getRating2().split("-")[1];
            String bondRatingCompany2 = null==bond.getBondRatingCompany2() ? "" : bond.getBondRatingCompany2();

            String strRating2 = "";
            if(!"".equals(corporateRating2) || !"".equals(rating2)){
                strRating2 = "(" + corporateRating2 + "/" + rating2 + ")";
            }
            strRating2 += ratingCompany2;
            if(!"".equals(bondRatingCompany2)){
                strRating2 += "".equals(ratingCompany2)?bondRatingCompany2:("," + bondRatingCompany2);
            }
            if(!"".equals(strRating1) && !"".equals(strRating2)){
                strRating1 = strRating1 + "\r\n" + strRating2;
            }else{
                strRating1 = strRating1 + strRating2;
            }
            visitorRow.createCell(12).setCellValue(strRating1);
            visitorRow.getCell(12).setCellStyle(txtStyle);

            visitorRow.createCell(13).setCellValue(null==bond.getRate()?"":(bond.getRate()+ "%"));
            visitorRow.getCell(13).setCellStyle(txtStyle);
            visitorRow.createCell(14).setCellValue(null==bond.getUnderwriterName()?"":bond.getUnderwriterName());
            visitorRow.getCell(14).setCellStyle(txtStyle);
            String guaranteeCompany = null==bond.getGuaranteeCompany1() ? "" : bond.getGuaranteeCompany1();
            guaranteeCompany += null==bond.getGuaranteeCompany2() ? "" : bond.getGuaranteeCompany2();
            visitorRow.createCell(15).setCellValue(guaranteeCompany);
            visitorRow.getCell(15).setCellStyle(txtStyle);

            if(null!=bond.getUnderwritingMode()){
                String[] aryUnderwritingMode = bond.getUnderwritingMode().split(";");
                for(int i=0;i<aryUnderwritingMode.length;i++){
                    String itemUnderwritingMode = aryUnderwritingMode[i];
                    if(aryUnderwritingMode[i].indexOf("-")>0){
                        itemUnderwritingMode = aryUnderwritingMode[i].substring(aryUnderwritingMode[i].indexOf("-")+1);
                    }
                    aryUnderwritingMode[i] = itemUnderwritingMode;
                }
                visitorRow.createCell(16).setCellValue(StringUtils.join(aryUnderwritingMode,","));
            }else{
                visitorRow.createCell(16).setCellValue("");
            }
            //visitorRow.createCell(16).setCellValue(null==bond.getUnderwritingMode()?"":bond.getUnderwritingMode().split("-")[1]);
            visitorRow.getCell(16).setCellStyle(txtStyle);

            visitorRow.createCell(17).setCellValue(null==bond.getBondManager()?"":bond.getBondManager());
            visitorRow.getCell(17).setCellStyle(txtStyle);

            if(null!=bond.getOptionType()){
                String[] aryOptionType = bond.getOptionType().split(";");
                for(int i=0;i<aryOptionType.length;i++){
                    String itemOptionType = aryOptionType[i];
                    if(aryOptionType[i].indexOf("-")>0){
                        itemOptionType = aryOptionType[i].substring(aryOptionType[i].indexOf("-")+1);
                    }
                    aryOptionType[i] = itemOptionType;
                }
                visitorRow.createCell(18).setCellValue(StringUtils.join(aryOptionType,","));
            }else{
                visitorRow.createCell(18).setCellValue("");
            }
            visitorRow.getCell(18).setCellStyle(txtStyle);

            visitorRow.createCell(19).setCellValue(null==bond.getListedDate()||"".equals(bond.getListedDate())?"无":sf.format(bond.getListedDate()));
            visitorRow.getCell(19).setCellStyle(txtStyle);


            String mMan = "", mPhone = "", mEmail = "";
            if(null!=bond.getBondManagerList() && bond.getBondManagerList().size()>0){
                for(BondManager bondManager : bond.getBondManagerList()){
                    if(null!=bondManager.getSysUser()){
                        mMan += (null==bondManager.getSysUser().getName()?"":bondManager.getSysUser().getName()) + "\r\n";
                        mPhone += (null==bondManager.getSysUser().getPhone()?"":bondManager.getSysUser().getPhone()) + "\r\n";
                        mEmail += (null==bondManager.getSysUser().getEmail()?"":bondManager.getSysUser().getEmail()) + "\r\n";
                    }
                }
            }

            visitorRow.createCell(20).setCellValue(mMan);
            visitorRow.getCell(20).setCellStyle(txtStyle);
            visitorRow.createCell(21).setCellValue(mPhone);
            visitorRow.getCell(21).setCellStyle(txtStyle);
            visitorRow.createCell(22).setCellValue(mEmail);
            visitorRow.getCell(22).setCellStyle(txtStyle);

            String cPhone = "", cEmail = "";
            if(null!=bond.getCompany() && null!=bond.getCompany().getCompanyLinkmanList() && bond.getCompany().getCompanyLinkmanList().size()>0){
                for(CompanyLinkman companyLinkman : bond.getCompany().getCompanyLinkmanList()){
                    if(null!=companyLinkman){
                        cPhone += (null==companyLinkman.getPhone()?"":companyLinkman.getPhone()) + "\r\n";
                        cEmail += (null==companyLinkman.getMail()?"":companyLinkman.getMail()) + "\r\n";
                    }
                }
            }

            visitorRow.createCell(23).setCellValue(cPhone);
            visitorRow.getCell(23).setCellStyle(txtStyle);
            visitorRow.createCell(24).setCellValue(cEmail);
            visitorRow.getCell(24).setCellStyle(txtStyle);

            String bPhone = "", bEmail = "";
            if(null!=bond.getBankManagerList() && bond.getBankManagerList().size()>0){
                for(BankManager bankManager : bond.getBankManagerList()){
                    if(null!=bankManager){
                        bPhone += (null==bankManager.getPhone()?"":bankManager.getPhone()) + "\r\n";
                        bEmail += (null==bankManager.getEmail()?"":bankManager.getEmail()) + "\r\n";
                    }
                }
            }

            visitorRow.createCell(25).setCellValue(bPhone);
            visitorRow.getCell(25).setCellStyle(txtStyle);
            visitorRow.createCell(26).setCellValue(bEmail);
            visitorRow.getCell(26).setCellStyle(txtStyle);
        }


        return workbook;
    }

    public List<Bond> findBondExpire(){
        return bondMapper.findBondExpire();
    };
    public void updateBondExpireByCode(String code){
        bondMapper.updateBondExpireByCode(code);
    };
}
