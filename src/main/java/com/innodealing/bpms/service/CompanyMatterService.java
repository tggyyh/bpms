package com.innodealing.bpms.service;

import com.innodealing.bpms.common.model.ReqData;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("companyMatterService")
public class CompanyMatterService {

    @Autowired
    private CompanyMatterMapper companyMatterMapper;
    @Autowired
    private CustomMatterMapper customMatterMapper;

    //事务
    @Autowired
    DataSourceTransactionManager transactionManager;

    private final static Logger logger = LoggerFactory.getLogger(CompanyMatterService.class);

    public List<CompanyMatter> findByCompany(ReqData reqData){
        return companyMatterMapper.findByCompany(reqData);
    }

    public List<CompanyMatter> findByCompanyNames(ReqData reqData){
        return companyMatterMapper.findByCompanyNames(reqData);
    }

    public int saveCompanyMatter(String companyName, List<CompanyMatter> companyMatterList, Integer[] customMatterIds){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            //先删除
            count = companyMatterMapper.deleteByCompanyName(companyName);
            //再添加
            if(null!=companyMatterList && companyMatterList.size()>0){
                count = companyMatterMapper.insertCompanyMatterList(companyMatterList);
            }

            //自定义事项
            CustomMatter customMatter = new CustomMatter();
            customMatter.setType(0);
            customMatter.setKey(companyName);
            customMatter.setRelation(0);
            count = customMatterMapper.updateResetRelation(customMatter);
            if(null!=customMatterIds && customMatterIds.length>0){
                count = customMatterMapper.updateRelationByIds(customMatterIds);
            }

            transactionManager.commit(status);
            logger.info("保存成功:" + companyName);
        }catch(Exception ex){
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("保存失败:" + ex.getMessage());
        }
        return count;
    }

    public int insertCompanyMatterList(List<CompanyMatter> companyMatterList){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            String [] companyName = new String [companyMatterList.size()];
            for(int i=0;i<companyMatterList.size();i++){
                companyName[i] = companyMatterList.get(i).getCompanyName();
            }
            Map<String, Object> map = new HashMap<>();
            map.put("templateId", companyMatterList.get(0).getTemplateId());
            map.put("companynames", companyName);

            //先删除
            count = companyMatterMapper.deleteByMatterList(map);
            //再添加
            if(null!=companyMatterList && companyMatterList.size()>0){
                count = companyMatterMapper.insertCompanyMatterList(companyMatterList);
            }

            transactionManager.commit(status);
            logger.info("保存成功:" + companyName);
        }catch(Exception ex){
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("保存失败:" + ex.getMessage());
        }
        return count;
    }

    public int insertCompanyMatter(CompanyMatter companyMatter){
        int count = 0;
        try{
            count = companyMatterMapper.insertCompanyMatter(companyMatter);
            logger.info("保存成功:" + companyMatter.getCompanyName());
        }catch(Exception ex){
            ex.printStackTrace();
            logger.info("保存失败:" + ex.getMessage());
        }
        return count;
    }

    public int deleteLinkMan(CompanyMatter companyMatter){
        int count = 0;
        try{
            count = companyMatterMapper.deleteLinkMan(companyMatter);
            logger.info("保存成功:" + companyMatter.getCompanyName());
        }catch(Exception ex){
            ex.printStackTrace();
            logger.info("保存失败:" + ex.getMessage());
        }
        return count;
    }

    @Transactional
    public int saveLinkCompanyMatterList(List<CompanyMatter> companyMatterList){
        int count = 0;
        try{
            if(null!=companyMatterList && companyMatterList.size()>0){
                companyMatterMapper.deleteByTemplateId(companyMatterList.get(0).getTemplateId());
                count = companyMatterMapper.insertCompanyMatterList(companyMatterList);
                logger.info("保存成功:" + companyMatterList.get(0).getTemplateId());
            }else{
                logger.info("无数据保存:");
            }
        }catch(Exception ex){
            ex.printStackTrace();
            logger.info("保存失败:" + ex.getMessage());
        }
        return count;
    }

    public int deleteByMatterList(Map<String, Object> map){
        return companyMatterMapper.deleteByMatterList(map);
    }

    public int deleteByTemplateId(int templateId){
        int count = 0;
        try{
            companyMatterMapper.deleteByTemplateId(templateId);
            logger.info("保存成功:" + templateId);
        }catch(Exception ex){
            ex.printStackTrace();
            logger.info("保存失败:" + ex.getMessage());
        }
        return count;
    }
}
