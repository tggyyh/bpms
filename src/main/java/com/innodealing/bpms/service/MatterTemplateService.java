package com.innodealing.bpms.service;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.mapper.*;
import com.innodealing.bpms.model.*;
import com.innodealing.bpms.unit.DateFormat;
import com.innodealing.bpms.unit.Generate;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service("matterTemplateService")
public class MatterTemplateService {

    private static final String UPLOAD_URL = "bpms/fujian/";

    @Autowired
    MatterTemplateMapper matterTemplateMapper;
    @Autowired
    TemplateAttachmentMapper templateAttachmentMapper;
    @Autowired
    TemplateRuleMapper templateRuleMapper;
    @Autowired
    BondMatterMapper bondMatterMapper;
    @Autowired
    CompanyMatterMapper companyMatterMapper;
    @Autowired
    HolidayMapper holidayMapper;
    @Autowired
    SurviveProcessMapper surviveProcessMapper;


    //事务
    @Autowired
    DataSourceTransactionManager transactionManager;

    private final static Logger logger = LoggerFactory.getLogger(MatterTemplateService.class);

    public List<MatterTemplate> findMatterAll(){
        return matterTemplateMapper.findMatterAll();
    }

    public int isExist(MatterTemplate matterTemplate){
        return matterTemplateMapper.isExist(matterTemplate);
    }

    public List<MatterTemplate> findAll(){
        List<MatterTemplate> matterTemplateList = matterTemplateMapper.findAll();
        List<TemplateRule> templateRuleList = templateRuleMapper.findAll();
        List<TemplateAttachment> templateAttachmentList = templateAttachmentMapper.findAll();

        for (MatterTemplate matterTemplate : matterTemplateList) {
            List<TemplateRule> objTemplateRule = new ArrayList<TemplateRule>();
            List<TemplateAttachment> objTemplateAttachment = new ArrayList<TemplateAttachment>();

            for(TemplateRule templateRule : templateRuleList){
                if(matterTemplate.getId()==templateRule.getTemplateId()){
                    objTemplateRule.add(templateRule);
                }
            }
            if(objTemplateRule.size()>0){
                matterTemplate.setTemplateRuleList(objTemplateRule);
            }

            for(TemplateAttachment templateAttachment : templateAttachmentList){
                if(matterTemplate.getId()==templateAttachment.getTemplateId()){
                    objTemplateAttachment.add(templateAttachment);
                }
            }
            if(objTemplateAttachment.size()>0){
                matterTemplate.setTemplateAttachmentList(objTemplateAttachment);
            }

        }

        return matterTemplateList;
    }

    public List<MatterTemplate> findByType(int type){
        List<MatterTemplate> matterTemplateList = matterTemplateMapper.findByType(type);
        List<TemplateRule> templateRuleList = templateRuleMapper.findAll();
        List<TemplateAttachment> templateAttachmentList = templateAttachmentMapper.findAll();

        for (MatterTemplate matterTemplate : matterTemplateList) {
            List<TemplateRule> objTemplateRule = new ArrayList<TemplateRule>();
            List<TemplateAttachment> objTemplateAttachment = new ArrayList<TemplateAttachment>();

            for(TemplateRule templateRule : templateRuleList){
                if(matterTemplate.getId()==templateRule.getTemplateId()){
                    objTemplateRule.add(templateRule);
                }
            }
            if(objTemplateRule.size()>0){
                matterTemplate.setTemplateRuleList(objTemplateRule);
            }

            for(TemplateAttachment templateAttachment : templateAttachmentList){
                if(matterTemplate.getId()==templateAttachment.getTemplateId()){
                    objTemplateAttachment.add(templateAttachment);
                }
            }
            if(objTemplateAttachment.size()>0){
                matterTemplate.setTemplateAttachmentList(objTemplateAttachment);
            }

        }

        return matterTemplateList;
    }

    public MatterTemplate findById(Integer id){
        MatterTemplate matterTemplate = matterTemplateMapper.findById(id);
        List<TemplateRule> templateRuleList = templateRuleMapper.findByTemplateId(id);
        List<TemplateAttachment> templateAttachmentList = templateAttachmentMapper.findByTemplateId(id);
        matterTemplate.setTemplateRuleList(templateRuleList);
        matterTemplate.setTemplateAttachmentList(templateAttachmentList);
        return matterTemplate;
    }

    public int insertMatterTemplate(MatterTemplate matterTemplate){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            //先存入Rule
            count = matterTemplateMapper.insertMatterTemplate(matterTemplate);
            for(TemplateRule templateRule : matterTemplate.getTemplateRuleList()){
                templateRule.setTemplateId(matterTemplate.getId());
            }
            for(TemplateAttachment templateAttachment : matterTemplate.getTemplateAttachmentList()){
                templateAttachment.setTemplateId(matterTemplate.getId());
            }
            count = templateRuleMapper.insertTemplateRuleList(matterTemplate.getTemplateRuleList());
            if(null != matterTemplate.getTemplateAttachmentList() && matterTemplate.getTemplateAttachmentList().size() > 0){
                count = templateAttachmentMapper.insertTemplateAttachmentList(matterTemplate.getTemplateAttachmentList());
            }

            //自动关联
            if(matterTemplate.getAutoRelate()==1){
                if(matterTemplate.getType()==0){
                    count = companyMatterMapper.insertByTemplateId(matterTemplate.getId());
                }else{
                    count = bondMatterMapper.insertByTemplateId(matterTemplate.getId());
                }
            }
            transactionManager.commit(status);
            logger.info("添加成功:" + matterTemplate.getName());
        }catch(Exception ex){
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("添加失败:" + ex.getMessage());
        }
        return count;
    }

    public int updateMatterTemplate(MatterTemplate matterTemplate){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            //规则
            List<TemplateRule> updateRuleList = matterTemplate.getTemplateRuleList().stream().filter(m -> m.getId()>0).collect(Collectors.toList());
            int[] ruleIds = new int[updateRuleList.size()];
            for(int i=0;i<updateRuleList.size();i++){
                ruleIds[i] = updateRuleList.get(i).getId();
            }
            Map<String, Object> map = new HashMap<>();
            map.put("templateId", matterTemplate.getId());
            map.put("ids", ruleIds);
            //删除
            templateRuleMapper.deleteByIds(map);
            //更新
            if(null!=updateRuleList && updateRuleList.size()>0){
                templateRuleMapper.updateTemplateRuleList(updateRuleList);
            }
            //新增
            List<TemplateRule> insertRuleList = matterTemplate.getTemplateRuleList().stream().filter(m -> m.getId()==0).collect(Collectors.toList());
            if(null!=insertRuleList && insertRuleList.size()>0){
                templateRuleMapper.insertTemplateRuleList(insertRuleList);
            }
            //附件
            templateAttachmentMapper.deleteByTemplateId(matterTemplate.getId());

            //更新模板
            count = matterTemplateMapper.updateMatterTemplate(matterTemplate);

            //新附件
            if(null != matterTemplate.getTemplateAttachmentList() && matterTemplate.getTemplateAttachmentList().size() > 0){
                count = templateAttachmentMapper.insertTemplateAttachmentList(matterTemplate.getTemplateAttachmentList());
            }

            transactionManager.commit(status);
            logger.info("添加成功:" + matterTemplate.getName());
        }catch(Exception ex){
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("添加失败:" + ex.getMessage());
        }
        return count;
    }

    public int deleteMatterTemplate(Integer id){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            //删除规则
            //count = templateRuleMapper.deleteByTemplateId(id);
            //删除附件
            //count = templateAttachmentMapper.deleteByTemplateId(id);
            //删除模板
            //count = matterTemplateMapper.deleteById(id);
            count = bondMatterMapper.updateStatusByTemplateId(id);
            count = companyMatterMapper.updateStatusByTemplateId(id);
            count = matterTemplateMapper.updateStatusById(id);

            transactionManager.commit(status);
            logger.info("删除成功:" + id);
        }catch(Exception ex){
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("删除失败:" + ex.getMessage());
        }
        return count;
    }

    public MatterTemplate findTemplateByProcessId(String pId){
        return matterTemplateMapper.findTemplateByProcessId(pId);
    }

    public List<MatterTemplate> findByCompanyId(int companyId){
        return matterTemplateMapper.findByCompanyId(companyId);
    }

    public MatterTemplate findByTemplateId(int id){
        return matterTemplateMapper.findByTemplateId(id);
    }

}
