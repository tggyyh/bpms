package com.innodealing.bpms.controller;

import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.MatterTask;
import com.innodealing.bpms.model.User;
import com.innodealing.bpms.service.InspectorMatterService;
import com.innodealing.bpms.service.MatterTaskService;
import com.innodealing.bpms.service.RoleService;
import com.innodealing.bpms.service.UserService;
import com.innodealing.commons.http.RestResponse;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/inspector-matter")
public class InspectorMatterController {
    private static final Logger logger = LoggerFactory.getLogger(InspectorMatterController.class);
    @Autowired
    private InspectorMatterService inspectorMatterService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RoleService roleService;
    @Autowired
    IdentityService identityService;
    @Autowired
    private MatterTaskService matterTaskService;
    @Autowired
    private UserService userService;

    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(Model model){
        return "app/bpms/inspector-matter/index";
    }
    @RequestMapping(value="/undo-matter", method = RequestMethod.GET)
    public String undoMatter(Model model){
        List<User> userList = userService.findByRoleCode(ConstantUtil.MANAGER_ROLE);
        model.addAttribute("userList", userList);
        return "app/bpms/inspector-matter/undo-matter";
    }
    @RequestMapping(value="/over-matter", method = RequestMethod.GET)
    public String overMatter(Model model){
        List<User> userList = userService.findByRoleCode(ConstantUtil.MANAGER_ROLE);
        model.addAttribute("userList", userList);
        return "app/bpms/inspector-matter/over-matter";
    }
    //查询
    @ResponseBody
    @RequestMapping(value = "/findInspectorMatters", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findInspectorMatters(@RequestBody(required = false) ReqData reqData) {
        return inspectorMatterService.findInspectorMatters(reqData);
    }

    //查询
    @ResponseBody
    @RequestMapping(value = "/findOverMatters", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findOverMatters(@RequestBody(required = false) ReqData reqData) {
        return inspectorMatterService.findOverMatters(reqData);
    }
    @RequestMapping(value="/view-over-matter/{processId}", method = RequestMethod.GET)
    public String viewCheckOverTask(@PathVariable String processId,Model model){
        MatterTask mt =  matterTaskService.findByProcessId(processId);
        model.addAttribute("matterTask",mt);
        return "app/bpms/inspector-trace-matter/view-matter";
    }

    @RequestMapping(value="/view-over-matter/right-line/{processId}", method = RequestMethod.GET)
    public String viewRightLineOverTask(@PathVariable String processId,Model model){
        List<MatterTask> mts =  matterTaskService.findRightLineByProcessId(processId);
        MatterTask matterTask = null;
        for(MatterTask mt:mts){
            if(processId.equals(mt.getProcessId())){
                matterTask = mt;
            }
        }
        model.addAttribute("matterTask",matterTask);
        model.addAttribute("matterTasks",mts);
        return "app/bpms/inspector-trace-matter/view-right-line-matter";
    }

    @RequestMapping(value = "/view-over-matter/info/{pId}/{orderIndex}", method = RequestMethod.GET)
    public String info(Model model,@PathVariable String pId,@PathVariable int orderIndex) {
        model.addAttribute("pId", pId);
        model.addAttribute("orderIndex", orderIndex);
        return "app/bpms/inspector-matter/info";
    }
}
