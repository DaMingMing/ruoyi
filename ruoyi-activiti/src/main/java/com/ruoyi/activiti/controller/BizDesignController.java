package com.ruoyi.activiti.controller;

import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.activiti.domain.BizDesignVo;
import com.ruoyi.activiti.domain.BizDevelopVo;
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
import com.ruoyi.activiti.domain.BizDesign;
import com.ruoyi.activiti.service.IBizDesignService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 美工设计业务Controller
 * 
 * @author xiaojm
 * @date 2020-03-29
 */
@Controller
@RequestMapping("/design")
public class BizDesignController extends BaseController
{
    private String prefix = "design";

    @Autowired
    private IBizDesignService bizDesignService;


    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;

    @GetMapping()
    public String design(ModelMap mmap){
        mmap.put("currentUser", ShiroUtils.getSysUser());
        return prefix + "/design";
    }

    /**
     * 查询美工设计业务列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BizDesignVo bizDesign) {
        if (!SysUser.isAdmin(ShiroUtils.getUserId())) {
            bizDesign.setCreateBy(ShiroUtils.getLoginName());
        }
        startPage();
        List<BizDesignVo> list = bizDesignService.selectBizDesignList(bizDesign);
        return getDataTable(list);
    }

    /**
     * 导出美工设计业务列表
     */
    //@RequiresPermissions("activiti:design:export")
    @Log(title = "美工设计业务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BizDesignVo bizDesign)
    {
        List<BizDesignVo> list = bizDesignService.selectBizDesignList(bizDesign);
        ExcelUtil<BizDesignVo> util = new ExcelUtil<BizDesignVo>(BizDesignVo.class);
        return util.exportExcel(list, "design");
    }

    /**
     * 新增美工设计业务
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存美工设计业务
     */
    //@RequiresPermissions("activiti:design:add")
    @Log(title = "美工设计业务", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BizDesignVo bizDesign)
    {
        Long userId = ShiroUtils.getUserId();
        if (SysUser.isAdmin(userId)) {
            return error("提交申请失败：不允许管理员提交申请！");
        }
        return toAjax(bizDesignService.insertBizDesign(bizDesign));
    }

    /**
     * 提交申请
     */
    @Log(title = "美工业务", businessType = BusinessType.UPDATE)
    @PostMapping( "/submitApply")
    @ResponseBody
    public AjaxResult submitApply(Long id) {
        BizDesignVo bizDesign = bizDesignService.selectBizDesignById(id);
        String applyUserId = ShiroUtils.getLoginName();
        bizDesignService.submitApply(bizDesign, applyUserId);
        return success();
    }


    @GetMapping("/designTodo")
    public String todoView() {
        return prefix + "/designTodo";
    }

    /**
     * 我的开发待办列表
     * @param bizDesign
     * @return
     */
    @PostMapping("/designTaskList")
    @ResponseBody
    public TableDataInfo taskList(BizDesignVo bizDesign) {
        startPage();
        List<BizDesignVo> list = bizDesignService.findTodoTasks(bizDesign, ShiroUtils.getLoginName());
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
        BizDesignVo bizDesignVo = bizDesignService.selectBizDesignById(new Long(processInstance.getBusinessKey()));
        mmap.put("bizDesign", bizDesignVo);
        mmap.put("taskId", taskId);
        String verifyName = task.getTaskDefinitionKey().substring(0, 1).toUpperCase() + task.getTaskDefinitionKey().substring(1);
        return prefix + "/task" + verifyName;
    }

    /**
     * 完成任务
     *
     * @return
     */
    @RequestMapping(value = "/complete/{taskId}", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public AjaxResult complete(@PathVariable("taskId") String taskId, @RequestParam(value = "saveEntity", required = false) String saveEntity,
                               @ModelAttribute("preloadDesign") BizDesignVo designVo, HttpServletRequest request) {
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
                taskService.addComment(taskId, designVo.getInstanceId(), comment);
            }
            //设置流程变量产品信息传递到监听器
            if(saveEntityBoolean){
                variables.put("sku",designVo.getSku());
                variables.put("productName",designVo.getProductName());
                variables.put("title",designVo.getTitle());
            }


            bizDesignService.complete(designVo, saveEntityBoolean, taskId, variables);


            return success("任务已完成");
        } catch (Exception e) {
            logger.error("error on complete task {}, variables={}", new Object[]{taskId, variables, e});
            return error("完成任务失败");
        }
    }

    /**
     * 自动绑定页面字段
     */
    @ModelAttribute("preloadDesign")
    public BizDesignVo getDevelop(@RequestParam(value = "id", required = false) Long id, HttpSession session) {
        if (id != null) {
            return bizDesignService.selectBizDesignById(id);
        }
        return new BizDesignVo();
    }

    /**
     * 修改美工设计业务
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        BizDesign bizDesign = bizDesignService.selectBizDesignById(id);
        mmap.put("bizDesign", bizDesign);
        return prefix + "/edit";
    }

    /**
     * 修改保存美工设计业务
     */
    //@RequiresPermissions("activiti:design:edit")
    @Log(title = "美工设计业务", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BizDesignVo bizDesign)
    {
        return toAjax(bizDesignService.updateBizDesign(bizDesign));
    }

    /**
     * 删除美工设计业务
     */
    //@RequiresPermissions("activiti:design:remove")
    @Log(title = "美工设计业务", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(bizDesignService.deleteBizDesignByIds(ids));
    }
}
