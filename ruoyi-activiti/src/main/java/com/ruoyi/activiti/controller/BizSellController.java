package com.ruoyi.activiti.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.activiti.domain.BizDesignVo;
import com.ruoyi.activiti.domain.BizDevelopVo;
import com.ruoyi.activiti.domain.BizSellVo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
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
import com.ruoyi.activiti.domain.BizSell;
import com.ruoyi.activiti.service.IBizSellService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 销售业务Controller
 * 
 * @author xiaojm
 * @date 2020-03-27
 */
@Controller
@RequestMapping("/sell")
public class BizSellController extends BaseController
{
    private String prefix = "sell";

    @Autowired
    private IBizSellService bizSellService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;

    @GetMapping()
    public String sell(ModelMap mmap) {
        mmap.put("currentUser", ShiroUtils.getSysUser());
        return prefix + "/sell";
    }

    /**
     * 查询销售业务列表
     */

    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BizSellVo bizSell){
        if (!SysUser.isAdmin(ShiroUtils.getUserId())) {
            bizSell.setCreateBy(ShiroUtils.getLoginName());
        }
        startPage();
        List<BizSellVo> list = bizSellService.selectBizSellList(bizSell);
        return getDataTable(list);
    }

    /**
     * 提交申请
     */
    @Log(title = "销售业务", businessType = BusinessType.UPDATE)
    @PostMapping( "/submitApply")
    @ResponseBody
    public AjaxResult submitApply(Long id) {
        BizSellVo sellVo = bizSellService.selectBizSellById(id);
        String applyUserId = ShiroUtils.getLoginName();
        bizSellService.submitApply(sellVo, applyUserId);
        return success();
    }

    @GetMapping("/sellTodo")
    public String todoView() {
        return prefix + "/sellTodo";
    }

    /**
     * 我的销售待办列表
     * @param bizSell
     * @return
     */
    @PostMapping("/sellTaskList")
    @ResponseBody
    public TableDataInfo taskList(BizSellVo bizSell) {
        startPage();
        List<BizSellVo> list = bizSellService.findTodoTasks(bizSell, ShiroUtils.getLoginName());
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
        BizSellVo bizSell = bizSellService.selectBizSellById(new Long(processInstance.getBusinessKey()));
        mmap.put("bizSell", bizSell);
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
                               @ModelAttribute("preloadSell") BizSellVo sellVo, HttpServletRequest request) {
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
                taskService.addComment(taskId, sellVo.getInstanceId(), comment);
            }
            //设置流程变量产品信息传递到监听器
            if(saveEntityBoolean){
                variables.put("sku",sellVo.getSku());
                variables.put("productName",sellVo.getProductName());
                variables.put("title",sellVo.getTitle());
                variables.put("photoNeed",sellVo.getPhotoNeed());
            }


            bizSellService.complete(sellVo, saveEntityBoolean, taskId, variables);


            return success("任务已完成");
        } catch (Exception e) {
            logger.error("error on complete task {}, variables={}", new Object[]{taskId, variables, e});
            return error("完成任务失败");
        }
    }


    /**
     * 完成任务
     *
     * @return
     */
    @RequestMapping(value = "/complete1/{taskId}", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public AjaxResult complete1(@PathVariable("taskId") String taskId,
                                HttpServletRequest request,
                                @RequestPart("file") MultipartFile file,
                                @RequestPart("bizSell") BizSellVo sellVo,
                                @RequestPart("saveEntity") String saveEntity,@RequestPart("p_B_devLeaderApproved") String p_B_devLeaderApproved,@RequestPart("p_COM_comment") String p_COM_comment) {

        // 上传并返回新文件名称
        String fileName = null;
        try {
            fileName = FileUploadUtils.upload(file);
        } catch (IOException e) {
            logger.error("error on add attachment {}, v", new Object[]{file.getName(), e});
            return error("运营计划任务失败");
        }
        sellVo.setAttachment(fileName);

        boolean saveEntityBoolean = BooleanUtils.toBoolean(saveEntity);
        Map<String, Object> variables = new HashMap<String, Object>();

        String comment = null;          // 批注
        if(StringUtils.isNotBlank(p_COM_comment)) {
            comment = p_COM_comment;
            variables.put("comment",comment);
        }

        Object approved = null;          // 审批意见
        if(StringUtils.isNotBlank(p_B_devLeaderApproved)) {
            approved = BooleanUtils.toBoolean(p_B_devLeaderApproved);
            variables.put("devLeaderApproved",approved);
        }

        try {
            if (StringUtils.isNotEmpty(comment)) {
                identityService.setAuthenticatedUserId(ShiroUtils.getLoginName());
                taskService.addComment(taskId, sellVo.getInstanceId(), comment);
            }
            //设置流程变量产品信息传递到监听器
            if(saveEntityBoolean){
                variables.put("sku",sellVo.getSku());
                variables.put("productName",sellVo.getProductName());
                variables.put("title",sellVo.getTitle());
                variables.put("photoNeed",sellVo.getPhotoNeed());
            }


            bizSellService.complete(sellVo, saveEntityBoolean, taskId, variables);

            return success("任务已完成");
        } catch (Exception e) {
            logger.error("error on complete task {}, variables={}", new Object[]{taskId, variables, e});
            return error("完成任务失败");
        }
    }


    /**
     * 自动绑定页面字段
     */
    @ModelAttribute("preloadSell")
    public BizSellVo getSell(@RequestParam(value = "id", required = false) Long id, HttpSession session) {
        if (id != null) {
            return bizSellService.selectBizSellById(id);
        }
        return new BizSellVo();
    }

    /**
     * 导出销售业务列表
     */
    @Log(title = "销售业务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BizSellVo bizSell)
    {
        List<BizSellVo> list = bizSellService.selectBizSellList(bizSell);
        ExcelUtil<BizSellVo> util = new ExcelUtil<BizSellVo>(BizSellVo.class);
        return util.exportExcel(list, "sell");
    }



    /**
     * 新增销售业务
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存销售业务
     */
    @Log(title = "销售业务", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BizSellVo bizSell) {
        Long userId = ShiroUtils.getUserId();
        if (SysUser.isAdmin(userId)) {
            return error("提交申请失败：不允许管理员提交申请！");
        }
        return toAjax(bizSellService.insertBizSell(bizSell));
    }

    /**
     * 修改销售业务
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        BizSell bizSell = bizSellService.selectBizSellById(id);
        mmap.put("bizSell", bizSell);
        return prefix + "/edit";
    }

    /**
     * 修改保存销售业务
     */
    @Log(title = "销售业务", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BizSellVo bizSell)
    {
        return toAjax(bizSellService.updateBizSell(bizSell));
    }

    /**
     * 删除销售业务
     */
    @Log(title = "销售业务", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(bizSellService.deleteBizSellByIds(ids));
    }
}
