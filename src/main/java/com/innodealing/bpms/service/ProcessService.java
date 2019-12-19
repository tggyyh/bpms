package com.innodealing.bpms.service;

import com.innodealing.bpms.appconfig.mq.MailProducer;
import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.common.model.Status;
import com.innodealing.bpms.mail.MailService;
import com.innodealing.bpms.mapper.*;
import com.innodealing.bpms.model.*;
import com.innodealing.bpms.task.MailTask;
import com.innodealing.bpms.task.MailThreadPool;
import com.innodealing.bpms.task.rule.RuleService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("processService")
public class ProcessService {
    private static final Logger log = LoggerFactory.getLogger(ProcessService.class);
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    IdentityService identityService;
    @Autowired
    SurviveProcessMapper surviveProcessMapper;
    @Autowired
    SurviveProcessMapper1 surviveProcessMapper1;
    @Autowired
    MatterTemplateService matterTemplateService;
    @Autowired
    ProcessInfoMapper processInfoMapper;
    @Autowired
    ProcessInfoService processInfoService;
    @Autowired
    ProcessTemplateAttachmentMapper processTemplateMapper;
    @Autowired
    ProcessAttachmentMapper processAttachmentMapper;
    @Autowired
    CustomSubMatterMapper customSubMatterMapper;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomMatterService customMatterService;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private ExceptionProcessService exceptionProcessService;
    @Autowired
    private MailProducer mailProducer;
    @Autowired
    private HolidayService holidayService;
    public List<SurviveProcess> findCompanyProcess() {
        return surviveProcessMapper.findCompanyProcess();
    }
    public List<SurviveProcess> findBondProcess() {
        return surviveProcessMapper.findBondProcess();
    }
    public List<SurviveProcess> findCustomBondProcess() {
        return surviveProcessMapper.findCustomBondProcess();
    }
    public List<SurviveProcess> findCustomCompanyProcess() {
        return surviveProcessMapper.findCustomCompanyProcess();
    }

    @Transactional
    public void insertProcessAndSendMail(SurviveProcess sp) {
        surviveProcessMapper.insert(sp);
        MatterTemplate mt = matterTemplateService.findById(sp.getTemplateId());
        ProcessInfo pi = new ProcessInfo();
        pi.setRemindDate(new Date());
        pi.setCompleteDate(sp.getCompleteDate());
        pi.setType(mt.getType());
        pi.setColor(mt.getColor());
        pi.setContent(mt.getContent());
        pi.setCompanyName(sp.getCompanyName());
        pi.setBondCode(sp.getBondCode());
        pi.setBondShortname(sp.getBondShortname());
        pi.setDescription(mt.getDescription());
        pi.setName(mt.getName());
        pi.setProcessId(sp.getProcessId());
        pi.setShortname(mt.getShortname());
        pi.setConfirm(mt.getConfirm());
        pi.setMailUser(mt.getMailUser());
        pi.setMailBeforeDay(mt.getMailBeforeDay());
        pi.setMailFrequency(mt.getMailFrequency());
        pi.setAutoRelate(mt.getAutoRelate());
        List<ProcessTemplateAttachment> processTemplateAttachments = new ArrayList();
        List<TemplateAttachment> attachments = mt.getTemplateAttachmentList();
        if (!CollectionUtils.isEmpty(attachments)) {
            for (TemplateAttachment ta : attachments) {
                ProcessTemplateAttachment pta = new ProcessTemplateAttachment();
                pta.setProcessId(sp.getProcessId());
                pta.setName(ta.getName());
                pta.setDescription(ta.getDescription());
                pta.setType(ta.getType());
                pta.setUrl(ta.getUrl());
                processTemplateAttachments.add(pta);
            }
        }
        pi.setProcessTemplateAttachmentList(processTemplateAttachments);
        processInfoService.insert(pi);
        if(!CollectionUtils.isEmpty(processTemplateAttachments)) {
            processTemplateMapper.insertProcessTemplateAttachment(processTemplateAttachments);
        }
        int confirm = pi.getConfirm();
        if (ConstantUtil.NEED_CONFIRM == confirm) {
            pi.setStatus(Status.CONFIRM.getValue());
        } else {
            pi.setStatus(Status.HANDLE.getValue());
        }
        updateProcessInfoStatus(pi);
        sendMail(pi,false);
    }
    @Transactional
    public void cancelPreocess(String processId) {
        runtimeService.deleteProcessInstance(processId, "发起流程异常取消");
        updateSurviveProcess(ConstantUtil.PROCESS_CANCEL_STATUS, processId);
        ProcessInfo pi = new ProcessInfo();
        pi.setProcessId(processId);
        pi.setStatus(Status.CANCEL.getValue());
        updateProcessInfoStatus(pi);
    }
    @Transactional
    public void updateProcessInfo(ProcessInfo processInfo) {
        processInfoMapper.updateProcessInfo(processInfo);
    }
    @Transactional
    public void updateCheckProcessInfo(ProcessInfo processInfo) {
        processInfoMapper.updateCheckProcessInfo(processInfo);
    }
    @Transactional
    public void updateHandleProcessInfo(ProcessInfo processInfo) {
        processInfoMapper.updateHandleProcessInfo(processInfo);
    }
    @Transactional
    public void insertProcessAttachment(List<ProcessAttachment> processAttachmentList) {
        processAttachmentMapper.insertProcessAttachment(processAttachmentList);
    }

    @Transactional
    public void deleteAndInsertProcessAttachment(String processId, List<ProcessAttachment> attachmentList) {
        processAttachmentMapper.deleteProcessAttachment(processId);
        processAttachmentMapper.insertProcessAttachment(attachmentList);
    }
    @Transactional
    public void updateProcessInfoStatus(ProcessInfo processInfo) {
        processInfoMapper.updateProcessInfoStatus(processInfo);
    }
    @Transactional
    public void deleteAndInsertProcessTemplateAttachment(String processId, List<ProcessTemplateAttachment> attachmentList) {
        processTemplateMapper.deleteProcessTemplateAttachment(processId);
        processTemplateMapper.insertProcessTemplateAttachment(attachmentList);
    }
    @Transactional
    public void updateSurviveProcess(int status, String processId) {
        surviveProcessMapper.updateSurviveProcess(status, processId);
    }
    @Transactional
    public void deleteProcessAttachment(String processId) {
        processAttachmentMapper.deleteProcessAttachment(processId);
    }

    public void deleteProcessTemplateAttachment(String processId) {
        processTemplateMapper.deleteProcessTemplateAttachment(processId);
    }
    @Transactional
    public void updateData(MatterTask mt) {
        ProcessInfo pi = mt.getProcessInfo();
        int status = pi.getStatus();
        if(Status.HANDLE.getValue() == status){
            updateProcessInfo(pi);
        }else{
            updateCheckProcessInfo(pi);
        }
        if (null != mt.getCheckResult() && ConstantUtil.CHECK_RESULT_PASS == mt.getCheckResult()) {
            updateSurviveProcess(ConstantUtil.PROCESS_END_STATUS, mt.getProcessId());
        } else {
            List<ProcessTemplateAttachment> attachmentList = mt.getProcessInfo().getProcessTemplateAttachmentList();
            if (!CollectionUtils.isEmpty(attachmentList)) {
                deleteAndInsertProcessTemplateAttachment(mt.getProcessId(), attachmentList);
            } else {
                deleteProcessTemplateAttachment(mt.getProcessId());
            }
        }
//        sendMail(pi);
    }
    @Transactional
    public void updateData1(MatterTask mt) {
        updateHandleProcessInfo(mt.getProcessInfo());
        List<ProcessAttachment> attachmentList = mt.getProcessInfo().getProcessAttachmentList();
        if (!CollectionUtils.isEmpty(attachmentList)) {
            deleteAndInsertProcessAttachment(mt.getProcessId(), attachmentList);
        } else {
            deleteProcessAttachment(mt.getProcessId());
        }
//        sendMail(mt.getProcessInfo());
    }

    public ProcessInfo findByProcessId(String processId) {
        return processInfoMapper.findByProcessId(processId);
    }

    /*
     *@param b true:如果确认事项后，修改了事项，需要重新给督导发最新邮件
     */
    public void sendMail(ProcessInfo pi,boolean b) {
        Set<String> to = new HashSet();
        //需要确认，给督导发邮件，不需要确认，给项目负责人和交接人发邮件
        List<User> users = new ArrayList();
        int status = pi.getStatus();
        int type = pi.getType();
        if (Status.CONFIRM.getValue() == status) {
            users=getInspectorUser(pi);
        }
        if (Status.HANDLE.getValue() == status) {
            if(1==pi.getConfirm()){
                users=getHandleUser(pi);
                if(b){
                    users.addAll(getInspectorUser(pi));
                }
            }else{
                users = getInspectorUser(pi);
                users.addAll(getHandleUser(pi));
            }
            //所有给项目人员邮件需发给风控人员
            if(users.size()>0){
                users.addAll(getRiskControlUser());
            }
        }
        users.stream().filter(s -> !StringUtils.isEmpty(s.getEmail()) && s.getEmail().contains("@"))
                .forEach(s -> to.add(s.getEmail()));
        if (to.size() > 0) {
            EmailSendMessage message = new EmailSendMessage();
            message.setTo(to.toArray(new String[to.size()]));
            StringBuffer sb = new StringBuffer();

            if(0 == type){
                sb.append(pi.getCompanyName()).append("—");
            }else{
                sb.append(pi.getBondShortname()).append("—");
            }
            sb.append(pi.getName());
            SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月");
            sb.append("(").append(sf.format(pi.getRemindDate())).append(")");
            message.setSubject(sb.toString());
            int orderIndex = surviveProcessMapper.findOrderIndexByProcessId(pi.getProcessId());
            String text="";
            SimpleDateFormat sf1 = new SimpleDateFormat("yyyy年MM月dd日");
            if(0 == orderIndex){
                text= pi.getContent();
            }else if(1 == orderIndex){
                text = pi.getRightLineContent();
                String table = "<table>";
                List<CustomSubMatter> customSubMatters = new ArrayList();
                customSubMatters = customSubMatterMapper.findByProcessId(pi.getProcessId());
//                if(1==pi.getConfirm() && Status.HANDLE.getValue() == status){
//                    customSubMatters = customSubMatterMapper.findByProcessId(pi.getProcessId());
//                }else{
//                    customSubMatters = customSubMatterMapper.findByProcessId1(pi.getProcessId());
//                }
                for(CustomSubMatter csm : customSubMatters){
//                    text+="子事项"+csm.getOrderIndex()+":"+csm.getName()+"<br/>";
                    table+= "<tr><td valign='top' width='200px'>";
//                    Date subCompleteDate = holidayService.calculateDate(csm.getCompleteDate(), csm.getMailBeforeDay());
                    table += csm.getOrderIndex() + ".(T-" + csm.getCompleteBeforeDay() + ")"+sf1.format(csm.getCompleteDate())+":";
                    table += "</td><td>";
                    table += csm.getContent();
                    table += "</td></tr>";
                }
                text += table+"</table>";
            }else{
                return;
            }
            message.setText(text);
            message.setAttachmentList(pi.getProcessTemplateAttachmentList());
            try {
//                MailThreadPool.getPool().submit(new MailTask(message, mailService));
                mailProducer.sendMq(message);
            } catch (Exception e) {
                log.error("邮件发送错误,processId:" + pi.getProcessId(), e);
            }
        }
    }
    @Transactional
    public void updateNoticeFlag(String processId, int flag) {
        processInfoMapper.updateNoticeFlag(processId, flag);
    }
    @Transactional
    public void insertCustomProcessAndSendMail(SurviveProcess sp) {
        surviveProcessMapper.insert(sp);
        CustomMatter cbm = customMatterService.findById(sp.getCustomId());
        ProcessInfo pi = new ProcessInfo();
        pi.setRemindDate(new Date());
        pi.setCompleteDate(sp.getCompleteDate());
        pi.setType(sp.getType());
        pi.setColor(cbm.getColor());
        if (1 == cbm.getRightLine()) {
            pi.setContent(sp.getContent());
            pi.setMailBeforeDay(sp.getMailBeforeDay());
            pi.setMailFrequency(sp.getMailFrequency());
            pi.setSubName(sp.getSubName());
            pi.setRightLineContent(cbm.getContent());
            pi.setSubBeforeDay(sp.getBeforeDay());
            pi.setSubCompleteBeforeDay(sp.getCompleteBeforeDay());
        } else {
            pi.setContent(cbm.getContent());
            pi.setMailBeforeDay(cbm.getMailBeforeDay());
            pi.setMailFrequency(cbm.getMailFrequency());
        }
        pi.setCompanyName(sp.getCompanyName());
        pi.setBondCode(sp.getBondCode());
        pi.setBondShortname(sp.getBondShortname());
        pi.setDescription(cbm.getDescription());
        pi.setName(cbm.getName());
        pi.setSubName(sp.getSubName());
        pi.setProcessId(sp.getProcessId());
        pi.setShortname(cbm.getShortname());
        pi.setConfirm(cbm.getConfirm());
        pi.setMailUser(cbm.getMailUser());
        List<ProcessTemplateAttachment> processTemplateAttachments = new ArrayList();
        List<CustomAttachment> attachments = cbm.getCustomAttachmentList();
        if (!CollectionUtils.isEmpty(attachments)) {
            for (CustomAttachment ca : attachments) {
                ProcessTemplateAttachment pta = new ProcessTemplateAttachment();
                pta.setProcessId(sp.getProcessId());
                pta.setName(ca.getName());
                pta.setDescription(ca.getDescription());
                pta.setType(ca.getType());
                pta.setUrl(ca.getUrl());
                processTemplateAttachments.add(pta);
            }
        }
        pi.setProcessTemplateAttachmentList(processTemplateAttachments);
        processInfoService.insert(pi);
        if(!CollectionUtils.isEmpty(processTemplateAttachments)) {
            processTemplateMapper.insertProcessTemplateAttachment(processTemplateAttachments);
        }
        int confirm = pi.getConfirm();
        if (ConstantUtil.NEED_CONFIRM == confirm) {
            pi.setStatus(Status.CONFIRM.getValue());
        } else {
            pi.setStatus(Status.HANDLE.getValue());
        }
        updateProcessInfoStatus(pi);
        sendMail(pi,false);
    }

    public ProcessInstance startProcess(SurviveProcess sp) throws Exception {
        identityService.setAuthenticatedUserId("admin");
        Map<String, Object> variables = new HashMap<>();
        variables.put("confirm", sp.getConfirm());
        return runtimeService.startProcessInstanceByKey(ConstantUtil.PROCESS_DEF_KEY, "survive", variables);
    }

    public List<SurviveProcess> findRightLineProcess() {
        return surviveProcessMapper.findRightLineProcess();
    }
    @Transactional
    //不需要确认，如果发行人事项，给发行人下 所有项目负责人/发行人 交接人 发邮件
    //不需要确认，如果项目事项，给项目下项目负责人和发行人 交接人 发邮件
    public List<User> getHandleUser(ProcessInfo pi) {
        int type = pi.getType();
        List<User> users = new ArrayList();
        if (type == 0) {
            if (((pi.getMailUser()) & ConstantUtil.MAIL_MANAGER) == ConstantUtil.MAIL_MANAGER) {
                users.addAll( userService.findByCompany(pi.getCompanyName()));
            }
            if (((pi.getMailUser()) & ConstantUtil.MAIL_LINKMAN) == ConstantUtil.MAIL_LINKMAN) {
                users.addAll(userService.findLinkmanByCompany(pi.getCompanyName()));
            }
        } else if (type == 1) {
            if (((pi.getMailUser()) & ConstantUtil.MAIL_MANAGER) == ConstantUtil.MAIL_MANAGER) {
                users.addAll(userService.findByBond(pi.getBondCode()));
            }
            if (((pi.getMailUser()) & ConstantUtil.MAIL_LINKMAN) == ConstantUtil.MAIL_LINKMAN) {
                users.addAll(userService.findLinkmanByCompany(pi.getCompanyName()));
            }
        }
        return users;
    }
    /*
    *获取督导人员
     */
    public List<User> getInspectorUser(ProcessInfo pi) {
        List<User> users = new ArrayList();
        if (((pi.getMailUser()) & ConstantUtil.MAIL_INSPECTOR) == ConstantUtil.MAIL_INSPECTOR) {
            users.addAll(userService.findByRoleCode(ConstantUtil.INSPECTOR_ROLE));
        }
        return users;
    }
    /*
     *获取风控人员
     */
    public List<User> getRiskControlUser() {
        List<User> users = new ArrayList();
        users.addAll(userService.findByRoleCode(ConstantUtil.RISK_CONTROL_ROLE));
        return users;
    }
    //给项目下项目负责人发邮件
    public List<User> getManagerUser(ProcessInfo pi) {
        int type = pi.getType();
        List<User> users = new ArrayList();
        if (type == 0) {
            if (((pi.getMailUser()) & ConstantUtil.MAIL_MANAGER) == ConstantUtil.MAIL_MANAGER) {
                users.addAll( userService.findByCompany(pi.getCompanyName()));
            }
        } else if (type == 1) {
            if (((pi.getMailUser()) & ConstantUtil.MAIL_MANAGER) == ConstantUtil.MAIL_MANAGER) {
                users.addAll(userService.findByBond(pi.getBondCode()));
            }
        }
        return users;
    }

    public int findSameRemind(SurviveProcess sp) {
        return surviveProcessMapper.findSameRemind(sp);
    }
    public int findSameRemind1(SurviveProcess sp) {
        return surviveProcessMapper1.findSameRemind1(sp);
    }
    //判断是否未触犯 true 未触发，false 已触犯
    public boolean notRemind(SurviveProcess sp) throws Exception {
        boolean b = true;
        int count = 0;
        count = findSameRemind(sp);
        if(count>0){
            b = false;
        }
        return b;
    }

    //判断是否未触犯 true 未触发，false 已触犯  定时查询count 用，不打印日志
    public boolean notRemind1(SurviveProcess sp) throws Exception {
        boolean b = true;
        int count = 0;
        count = findSameRemind1(sp);
        if(count>0){
            b = false;
        }
        return b;
    }
    public int startProcess(List<SurviveProcess> initList) {
        int count = 0;
        if(!CollectionUtils.isEmpty(initList)) {
            for (SurviveProcess sp : initList) {
                try {
                    boolean flag = false;
                    if (0 == sp.getRuleType()) {
                        flag = ruleService.timeHandle(sp);
                    } else if (1 == sp.getRuleType()) {
                        flag = ruleService.beforeHandle(sp);
                    }
                    if (flag && notRemind(sp)) {
                        ProcessInstance processInstance = startProcess(sp);
                        sp.setProcessId(processInstance.getProcessInstanceId());
                        int customId = sp.getCustomId();
                        if (0 == customId) {
                            insertProcessAndSendMail(sp);
                        } else {
                            insertCustomProcessAndSendMail(sp);
                        }
                        count++;
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if (!StringUtils.isEmpty(sp.getProcessId())) {
                        cancelPreocess(sp.getProcessId());
                    }
                    exceptionProcessService.insert(sp);
                }
            }
        }
        return count;
    }
    public int processCount(List<SurviveProcess> initList) {
        int count = 0;
        if(!CollectionUtils.isEmpty(initList)) {
            for (SurviveProcess sp : initList) {
                try {
                    boolean flag = false;
                    if (0 == sp.getRuleType()) {
                        flag = ruleService.timeHandle(sp);
                    } else if (1 == sp.getRuleType()) {
                        flag = ruleService.beforeHandle(sp);
                    }
                    if (flag && notRemind1(sp)) {
                        count++;
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return count;
    }

}
