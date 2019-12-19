package com.innodealing.bpms.common.model;

public class ConstantUtil {
    public static final Integer PAGE_NUM=0;
    public static final Integer PAGE_SIZE=10;
    //流程业务定义KEY
    public static final String PROCESS_DEF_KEY="survive";
    //审核通过
    public static final int CHECK_RESULT_PASS=1;
    //流程结束状态
    public static final int PROCESS_END_STATUS=1;
    //流程取消状态
    public static final int PROCESS_CANCEL_STATUS=2;
    //流程删除状态
    public static final int PROCESS_DELETE_STATUS=4;

    //上传路径
    public static final String FILE_URL = "/bpms/attachment/";

    //督导角色
    public static final String INSPECTOR_ROLE = "inspector_check";
    //项目负责人角色
    public static final String MANAGER_ROLE = "manager_handle";
    //风控人员角色
    public static final String RISK_CONTROL_ROLE = "risk_control";
    //需要督导确认
    public static final int NEED_CONFIRM=1;
    //不需要督导确认
    public static final int NO_NEED_CONFIRM=0;
    //邮件发送 1发行人对接人，2,项目负责人 4督导人员
    public static final int MAIL_LINKMAN=1;
    public static final int MAIL_MANAGER=2;
    public static final int MAIL_INSPECTOR=4;
    //邮箱线程数
    public static final int MAIL_THREAD_COUNT=10;
}
