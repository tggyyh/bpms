package com.innodealing.bpms.service;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.mapper.BondMatterMapper;
import com.innodealing.bpms.mapper.CustomMatterMapper;
import com.innodealing.bpms.model.BondMatter;
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

@Service("bondMatterService")
public class BondMatterService {
    @Autowired
    BondMatterMapper bondMatterMapper;
    @Autowired
    CustomMatterMapper customMatterMapper;
    //事务
    @Autowired
    DataSourceTransactionManager transactionManager;

    private final static Logger logger = LoggerFactory.getLogger(BondMatterService.class);

    public List<BondMatter> findByBondCode(String bondCode){
        List<BondMatter> bondMatterList = bondMatterMapper.findByBondCode(bondCode);
        return bondMatterList;
    }

    public List<BondMatter> findBondMatterByBondCode(ReqData reqData){
        List<BondMatter> bondMatterList = bondMatterMapper.findBondMatterByBondCode(reqData);
        return bondMatterList;
    }

    public int saveBondMatter(String bondCode, List<BondMatter> bondMatterList, Integer[] customMatterIds){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            //项目事项
            //先删除
            count = bondMatterMapper.deleteByBondCode(bondCode);
            //再添加
            if(null!=bondMatterList && bondMatterList.size()>0){
                count = bondMatterMapper.insertBondMatterList(bondMatterList);
            }

            //自定义事项
            CustomMatter customMatter = new CustomMatter();
            customMatter.setType(1);
            customMatter.setKey(bondCode);
            customMatter.setRelation(0);
            count = customMatterMapper.updateResetRelation(customMatter);
            if(null!=customMatterIds && customMatterIds.length>0){
                count = customMatterMapper.updateRelationByIds(customMatterIds);
            }


            transactionManager.commit(status);
            logger.info("保存成功:" + bondCode);
        }catch(Exception ex){
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("保存失败:" + ex.getMessage());
        }
        return count;
    }


    public int insertBondMatterList(List<BondMatter> bondMatterList){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            String [] bondCode = new String [bondMatterList.size()];
            for(int i=0;i<bondMatterList.size();i++){
                bondCode[i] = bondMatterList.get(i).getBondCode();
            }
            Map<String, Object> map = new HashMap<>();
            map.put("templateId", bondMatterList.get(0).getTemplateId());
            map.put("bondcodes", bondCode);

            //先删除
            count = bondMatterMapper.deleteByMatterList(map);
            //再添加
            if(null!=bondMatterList && bondMatterList.size()>0){
                count = bondMatterMapper.insertBondMatterList(bondMatterList);
            }

            transactionManager.commit(status);
            logger.info("保存成功:" + bondCode);
        }catch(Exception ex){
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("保存失败:" + ex.getMessage());
        }
        return count;
    }

    public int insertBondMatter(BondMatter bondMatter){
        int count = 0;
        try{
            count = bondMatterMapper.insertBondMatter(bondMatter);
            logger.info("保存成功:" + bondMatter.getBondCode());
        }catch(Exception ex){
            ex.printStackTrace();
            logger.info("保存失败:" + ex.getMessage());
        }
        return count;
    }

    public int deleteLinkMan(BondMatter bondMatter){
        int count = 0;
        try{
            count = bondMatterMapper.deleteLinkMan(bondMatter);
            logger.info("保存成功:" + bondMatter.getBondCode());
        }catch(Exception ex){
            ex.printStackTrace();
            logger.info("保存失败:" + ex.getMessage());
        }
        return count;
    }

    public int deleteByMatterList(Map<String, Object> map){
        return bondMatterMapper.deleteByMatterList(map);
    }

    @Transactional
    public int saveLinkBondMatterList(List<BondMatter> bondMatterList){
        int count = 0;
        try{
            if(null!=bondMatterList && bondMatterList.size()>0){
                bondMatterMapper.deleteByTemplateId(bondMatterList.get(0).getTemplateId());
                count = bondMatterMapper.insertBondMatterList(bondMatterList);
                logger.info("保存成功:" + bondMatterList.get(0).getTemplateId());
            }else{
                logger.info("无数据保存:");
            }
        }catch(Exception ex){
            ex.printStackTrace();
            logger.info("保存失败:" + ex.getMessage());
        }
        return count;
    }

    public int deleteByTemplateId(int templateId){
        int count = 0;
        try{
            bondMatterMapper.deleteByTemplateId(templateId);
            logger.info("保存成功:" + templateId);
        }catch(Exception ex){
            ex.printStackTrace();
            logger.info("保存失败:" + ex.getMessage());
        }
        return count;
    }
}
