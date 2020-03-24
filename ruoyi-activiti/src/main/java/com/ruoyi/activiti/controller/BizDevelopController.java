package com.ruoyi.activiti.controller;

import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.activiti.domain.BizDevelopVo;
import com.ruoyi.activiti.domain.BizLeaveVo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.activiti.domain.BizDevelop;
import com.ruoyi.activiti.service.IBizDevelopService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 开发业务Controller
 * 
 * @author xiaojm
 * @date 2020-03-21
 */
@Controller
@RequestMapping("/develop")
public class BizDevelopController extends BaseController
{
    private String prefix = "develop";

    @Autowired
    private IBizDevelopService bizDevelopService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;

    @GetMapping()
    public String develop(ModelMap mmap){
        mmap.put("currentUser", ShiroUtils.getSysUser());
        return prefix + "/develop";
    }

    /**
     * 查询开发业务列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BizDevelopVo bizDevelop) {
        if (!SysUser.isAdmin(ShiroUtils.getUserId())) {
            bizDevelop.setCreateBy(ShiroUtils.getLoginName());
        }
        startPage();
        List<BizDevelopVo> list = bizDevelopService.selectBizDevelopList(bizDevelop);
        return getDataTable(list);
    }

    /**
     * 提交申请
     */
    @Log(title = "开发业务", businessType = BusinessType.UPDATE)
    @PostMapping( "/submitApply")
    @ResponseBody
    public AjaxResult submitApply(Long id) {
        BizDevelopVo developVo = bizDevelopService.selectBizDevelopById(id);
        String applyUserId = ShiroUtils.getLoginName();
        bizDevelopService.submitApply(developVo, applyUserId);
        return success();
    }

    @GetMapping("/developTodo")
    public String todoView() {
        return prefix + "/developTodo";
    }

    /**
     * 我的开发待办列表
     * @param bizDevelop
     * @return
     */
    @PostMapping("/devTaskList")
    @ResponseBody
    public TableDataInfo taskList(BizDevelopVo bizDevelop) {
        startPage();
        List<BizDevelopVo> list = bizDevelopService.findTodoTasks(bizDevelop, ShiroUtils.getLoginName());
        return getDataTable(list);
    }

    /**
     * 加载审批弹窗
     * @param taskId
     * @param mmap
     * @return
     */
    @GetMapping("/showVerifyDialog/{taskId}")
    public String showVerifyDialog(@PathVariable("taskId") String taskId, ModelMap mmap) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        BizDevelopVo bizDevelop = bizDevelopService.selectBizDevelopById(new Long(processInstance.getBusinessKey()));
        mmap.put("bizDevelop", bizDevelop);
        mmap.put("taskId", taskId);
        String verifyName = task.getTaskDefinitionKey().substring(0, 1).toUpperCase() + task.getTaskDefinitionKey().substring(1);
        return prefix + "/task" + verifyName;
    }
    /**
     * 导出开发业务列表
     */
    @Log(title = "开发业务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BizDevelopVo bizDevelop)
    {
        List<BizDevelopVo> list = bizDevelopService.selectBizDevelopList(bizDevelop);
        ExcelUtil<BizDevelopVo> util = new ExcelUtil<BizDevelopVo>(BizDevelopVo.class);
        return util.exportExcel(list, "develop");
    }

    /**
     * 新增开发业务
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存开发业务
     */
    @Log(title = "开发", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BizDevelopVo bizDevelop){
        Long userId = ShiroUtils.getUserId();
        if (SysUser.isAdmin(userId)) {
            return error("提交申请失败：不允许管理员提交申请！");
        }
        return toAjax(bizDevelopService.insertBizDevelop(bizDevelop));
    }


    /**
     * 完成任务
     *
     * @return
     */
    @RequestMapping(value = "/complete/{taskId}", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public AjaxResult complete(@PathVariable("taskId") String taskId, @RequestParam(value = "saveEntity", required = false) String saveEntity,
                               @ModelAttribute("preloadDevelop") BizDevelopVo develop, HttpServletRequest request) {
        boolean saveEntityBoolean = BooleanUtils.toBoolean(saveEntity);
        Map<String, Object> variables = new HashMap<String, Object>();
        Enumeration<String> parameterNames = request.getParameterNames();
        String comment = null;          // 批注
        try {
            while (parameterNames.hasMoreElements()) {
                String parameterName = (String) parameterNames.nextElement();
                if (parameterName.startsWith("p_")) {
                    // 参数结构：p_B_name，p为参数的前缀，B为类型，name为属性名称
                    String[] parameter = parameterName.split("_");
                    if (parameter.length == 3) {
                        String paramValue = request.getParameter(parameterName);
                        Object value = paramValue;
                        if (parameter[1].equals("B")) {
                            value = BooleanUtils.toBoolean(paramValue);
                        } else if (parameter[1].equals("DT")) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            value = sdf.parse(paramValue);
                        } else if (parameter[1].equals("COM")) {
                            comment = paramValue;
                        }
                        variables.put(parameter[2], value);
                    } else {
                        throw new RuntimeException("invalid parameter for activiti variable: " + parameterName);
                    }
                }
            }
            if (StringUtils.isNotEmpty(comment)) {
                identityService.setAuthenticatedUserId(ShiroUtils.getLoginName());
                taskService.addComment(taskId, develop.getInstanceId(), comment);
            }
            bizDevelopService.complete(develop, saveEntityBoolean, taskId, variables);

            return success("任务已完成");
        } catch (Exception e) {
            logger.error("error on complete task {}, variables={}", new Object[]{taskId, variables, e});
            return error("完成任务失败");
        }
    }

    /**
     * 自动绑定页面字段
     */
    @ModelAttribute("preloadDevelop")
    public BizDevelopVo getDevelop(@RequestParam(value = "id", required = false) Long id, HttpSession session) {
        if (id != null) {
            return bizDevelopService.selectBizDevelopById(id);
        }
        return new BizDevelopVo();
    }

    /**
     * 修改开发业务
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        BizDevelop bizDevelop = bizDevelopService.selectBizDevelopById(id);
        mmap.put("bizDevelop", bizDevelop);
        return prefix + "/edit";
    }

    /**
     * 修改保存开发业务
     */
    @Log(title = "开发业务", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BizDevelopVo bizDevelop)
    {
        return toAjax(bizDevelopService.updateBizDevelop(bizDevelop));
    }

    /**
     * 删除开发业务
     */
    @Log(title = "开发业务", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(bizDevelopService.deleteBizDevelopByIds(ids));
    }
}
