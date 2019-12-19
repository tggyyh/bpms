package com.innodealing.bpms.service;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.mapper.CustomAttachmentMapper;
import com.innodealing.bpms.mapper.CustomMatterMapper;
import com.innodealing.bpms.mapper.CustomRuleMapper;
import com.innodealing.bpms.mapper.CustomSubMatterMapper;
import com.innodealing.bpms.model.CustomAttachment;
import com.innodealing.bpms.model.CustomMatter;
import com.innodealing.bpms.model.CustomRule;
import com.innodealing.bpms.model.CustomSubMatter;
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
import java.util.stream.Collectors;

@Transactional
@Service("customMatterService")
public class CustomMatterService {

    private static final String UPLOAD_URL = "bpms/fujian/";

    @Autowired
    CustomMatterMapper customMatterMapper;
    @Autowired
    CustomRuleMapper customRuleMapper;
    @Autowired
    CustomAttachmentMapper customAttachmentMapper;
    @Autowired
    CustomSubMatterMapper customSubMatterMapper;

    //事务
    @Autowired
    DataSourceTransactionManager transactionManager;

    private final static Logger logger = LoggerFactory.getLogger(CustomMatterService.class);


    public List<CustomMatter> findAllCustomMatter(){
        return customMatterMapper.findAllCustomMatter();
    }

    public List<CustomMatter> findCustomMatterByKey(CustomMatter customMatter){
        List<CustomMatter> customMatterList = customMatterMapper.findCustomMatterByKey(customMatter);

        return customMatterList;
    }

    public List<CustomMatter> findByKey(CustomMatter customMatter){
        List<CustomMatter> customMatterList = customMatterMapper.findByKey(customMatter);

        return customMatterList;
    }

    public List<CustomMatter> findByKeys(ReqData reqData){
        List<CustomMatter> customMatterList = customMatterMapper.findByKeys(reqData);

        return customMatterList;
    }

    public CustomMatter findById(int id){
        CustomMatter customMatter = customMatterMapper.findById(id);
        return customMatter;
    }

    public int insertCustomMatter(CustomMatter customMatter){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            count = customMatterMapper.insertCustomMatter(customMatter);
            for(CustomRule customRule : customMatter.getCustomRuleList()){
                customRule.setCustomId(customMatter.getId());
            }

            count = customRuleMapper.insertCustomRuleList(customMatter.getCustomRuleList());

            if(null != customMatter.getCustomAttachmentList() && customMatter.getCustomAttachmentList().size() > 0){
                for(CustomAttachment customAttachment : customMatter.getCustomAttachmentList()){
                    customAttachment.setCustomId(customMatter.getId());
                }
                count = customAttachmentMapper.insertCustomAttachmentList(customMatter.getCustomAttachmentList());
            }

            if(customMatter.getType()==1 && customMatter.getRightLine()==1){
                for(CustomSubMatter customSubMatter : customMatter.getCustomSubMatterList()){
                    customSubMatter.setCustomId(customMatter.getId());
                }
                count = customSubMatterMapper.insertCustomSubMatterList(customMatter.getCustomSubMatterList());
            }

            transactionManager.commit(status);
            logger.info("添加成功:" + customMatter.getName());
        }catch(Exception ex){
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("添加失败:" + ex.getMessage());
        }
        return count;
    }

    public int updateCustomMatter(CustomMatter customMatter){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            if(customMatter.getRightLine()==0) {
                //规则
                List<CustomRule> updateRuleList = customMatter.getCustomRuleList().stream().filter(m -> m.getId() > 0).collect(Collectors.toList());
                int[] ruleIds = new int[updateRuleList.size()];
                for (int i = 0; i < updateRuleList.size(); i++) {
                    ruleIds[i] = updateRuleList.get(i).getId();
                }
                Map<String, Object> map = new HashMap<>();
                map.put("customId", customMatter.getId());
                map.put("ids", ruleIds);
                //删除
                customRuleMapper.deleteByIds(map);
                //更新
                if (null != updateRuleList && updateRuleList.size() > 0) {
                    customRuleMapper.updateCustomRuleList(updateRuleList);
                }
                //新增
                List<CustomRule> insertRuleList = customMatter.getCustomRuleList().stream().filter(m -> m.getId() == 0).collect(Collectors.toList());
                if (null != insertRuleList && insertRuleList.size() > 0) {
                    customRuleMapper.insertCustomRuleList(insertRuleList);
                }
            }
            //先删除附件
            customAttachmentMapper.deleteByCustomId(customMatter.getId());
            customSubMatterMapper.deleteByCustomId(customMatter.getId());

            //更新模板
            count = customMatterMapper.updateCustomMatter(customMatter);

            //新增规则，附件
            if(null != customMatter.getCustomAttachmentList() && customMatter.getCustomAttachmentList().size() > 0){
                count = customAttachmentMapper.insertCustomAttachmentList(customMatter.getCustomAttachmentList());
            }

            if(customMatter.getType()==1 && customMatter.getRightLine()==1){
                count = customSubMatterMapper.insertCustomSubMatterList(customMatter.getCustomSubMatterList());
            }

            transactionManager.commit(status);
            logger.info("添加成功:" + customMatter.getName());
        }catch(Exception ex){
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("添加失败:" + ex.getMessage());
        }
        return count;
    }

    public int deleteCustomMatter(int id){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            //删除规则
            count = customRuleMapper.deleteByCustomId(id);
            //删除附件
            count = customAttachmentMapper.deleteByCustomId(id);
            //删除行权付息
            count = customSubMatterMapper.deleteByCustomId(id);
            //删除模板
            //count = customMatterMapper.deleteById(id);
            CustomMatter customMatter = new CustomMatter();
            customMatter.setId(id);
            customMatter.setStatus(1);
            count = customMatterMapper.updateStatus(customMatter);

            transactionManager.commit(status);
            logger.info("删除成功:" + id);
        }catch(Exception ex){
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("删除失败:" + ex.getMessage());
        }
        return count;
    }
}
