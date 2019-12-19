//package com.innodealing.bpms.service.process;
//
//import com.innodealing.bpms.common.model.ConstantUtil;
//import com.innodealing.bpms.common.model.Status;
//import com.innodealing.bpms.mail.MailService;
//import com.innodealing.bpms.model.ProcessInfo;
//import com.innodealing.bpms.model.User;
//import com.innodealing.bpms.service.ProcessService;
//import com.innodealing.bpms.service.UserService;
//import org.activiti.engine.RuntimeService;
//import org.activiti.engine.delegate.DelegateExecution;
//import org.activiti.engine.delegate.JavaDelegate;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ConfirmService implements JavaDelegate {
//    private static final Logger logger = LoggerFactory.getLogger(ConfirmService.class);
//    @Autowired
//    private ProcessService processService;
//    @Autowired
//    private MailService mailService;
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private RuntimeService runtimeService;
//    @Override
//    public void execute(DelegateExecution execution) {
//        long begin = System.currentTimeMillis();
//        String processId =execution.getProcessInstanceId();
//        runtimeService.suspendProcessInstanceById(processId);
//        ProcessInfo pi = null;
//            pi=processService.findByProcessId(processId);
//        int confirm = pi.getConfirm();
//        int type = pi.getType();
//        execution.setVariable("confirm" ,confirm);
//        int mailUser = pi.getMailUser();
//        List<String> to = new ArrayList<>();
//        //需要确认，给督导发邮件
//        if(1==confirm){
//            pi.setStatus(Status.CONFIRM.getValue());
//            List<User> inspectors = userService.findByRoleCode(ConstantUtil.INSPECTOR_ROLE);
//            inspectors.forEach(s->to.add(s.getEmail()));
//            mailService.sendMessage((String[]) to.toArray(),pi.getName(),pi.getContent(),pi.getProcessTemplateAttachmentList());
//        }else if(type==0){
//            pi.setStatus(Status.HANDLE.getValue());
//            //不需要确认，如果发行人事项，给发行人下 所有项目负责人和发行人 交接人 发邮件
//        }else if(type==1){
//            pi.setStatus(Status.HANDLE.getValue());
//            //不需要确认，如果项目事项，给项目下项目负责人和发行人 交接人 发邮件
//        }
//        processService.updateProcessInfoStatus(pi);
//        logger.info("自动节点结束："+pi+",耗时:"+(System.currentTimeMillis()-begin));
//    }
//
//}