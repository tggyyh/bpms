package com.innodealing.bpms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.mapper.CompanyMapper;
import com.innodealing.bpms.mapper.CompanyMatterMapper;
import com.innodealing.bpms.mapper.CustomMatterMapper;
import com.innodealing.bpms.model.Company;
import com.innodealing.bpms.model.CompanyMatter;
import com.innodealing.bpms.model.CustomMatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.stream.Collectors;

@Service("companyService")
public class CompanyService {
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private CompanyMatterMapper companyMatterMapper;
    @Autowired
    private CustomMatterMapper customMatterMapper;

    private final static Logger logger = LoggerFactory.getLogger(CompanyService.class);

    //事务
    @Autowired
    DataSourceTransactionManager transactionManager;

    public int isExist(Company company){
        return companyMapper.isExist(company);
    }

    public PageInfo<Company> findAll(ReqData reqData){

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
            List<Company> companyList = companyMapper.findAll(reqData);

            //组织关联事项
            String[] companyNames = new String[companyList.size()];
            int index = 0;
            for (Company company: companyList) {
                companyNames[index] = company.getName();
                index++;
            }
            if(null!=companyNames && companyNames.length>0){
                ReqData compData = new ReqData();
                compData.put("matterType", "0");
                compData.put("companyNames", companyNames);
                List<CompanyMatter> companyMatterList = companyMatterMapper.findByCompanyNames(compData);
                companyList.stream().forEach(company -> {
                    company.setCompanyMatterList(companyMatterList.stream().filter(companyMatter -> companyMatter.getCompanyName().equals(company.getName())).collect(Collectors.toList()));
                });

                //组织自定义关联事项
                ReqData customData = new ReqData();
                customData.put("customType", "0");
                customData.put("relation", "1");
                customData.put("customKeys", companyNames);
                List<CustomMatter> customMatterList = customMatterMapper.findByKeys(customData);
                companyList.stream().forEach(company -> {
                    company.setCustomMatterList(customMatterList.stream().filter(customMatter -> customMatter.getKey().equals(company.getName())).collect(Collectors.toList()));
                });
            }



            page = new PageInfo(companyList);

        } finally {
            PageHelper.clearPage();
        }

        return page;
    }

    public Company findById(int id){
        Company company = companyMapper.findById(id);
        return company;
    }

    public Company findByName(String name){
        Company company = companyMapper.findByName(name);
        return company;
    }

    public int insertCompany(Company company){
        return companyMapper.insertCompany(company);
    }

    public int updateStatusByIds(int[] ids){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            //先删除发行人模板关联表
            count = companyMatterMapper.deleteByCompanyIds(ids);
            //再更新发行人表
            count = companyMapper.updateStatusByIds(ids);

            transactionManager.commit(status);
            logger.info("删除成功");
        }catch(Exception ex){
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("删除失败:" + ex.getMessage());
        }
        return count;
    }

    public int updateStatus(ReqData reqData){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            int id = reqData.getInteger("id").intValue();
            String companyName = reqData.getString("companyName");
            //更新发行人关联模板
            count = companyMatterMapper.updateStatusByCompanyName(companyName);
            //再更新发行人表
            count = companyMapper.updateStatusById(id);

            transactionManager.commit(status);
            logger.info("删除成功");
        }catch(Exception ex){
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("删除失败:" + ex.getMessage());
        }
        return count;
    }

    public int updateCompany(Company company){
        return companyMapper.updateCompany(company);
    }

    public List<Company> findLinkMatterCount(int templateId){
        return companyMapper.findLinkMatterCount(templateId);
    }

    public PageInfo<Company> findLinkMatterAll(ReqData reqData){

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
            List<Company> companyList = companyMapper.findLinkMatterAll(reqData);
            page = new PageInfo(companyList);

        } finally {
            PageHelper.clearPage();
        }

        return page;
    }

    public List<Company> findCompLinkAll(ReqData reqData){
        return companyMapper.findCompLinkAll(reqData);
    }
}
