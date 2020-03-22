package com.ruoyi.activiti.controller;

import java.util.List;

import com.ruoyi.activiti.domain.BizDevelopVo;
import com.ruoyi.activiti.domain.BizLeaveVo;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.activiti.domain.BizDevelop;
import com.ruoyi.activiti.service.IBizDevelopService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

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
