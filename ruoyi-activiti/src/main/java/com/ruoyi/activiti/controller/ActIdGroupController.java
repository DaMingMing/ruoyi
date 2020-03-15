package com.ruoyi.activiti.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.activiti.domain.ActIdGroup;
import com.ruoyi.activiti.service.IActIdGroupService;
import com.ruoyi.activiti.service.IActIdUserService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程用户组Controller
 *
 * @author Xianlu Tech
 * @date 2019-10-02
 */
@Controller
@RequestMapping("/group")
public class ActIdGroupController extends BaseController
{
    private String prefix = "group";

    @Autowired
    private IActIdGroupService actIdGroupService;

    @Autowired
    private IActIdUserService actIdUserService;

    @Autowired
    private IdentityService identityService;

    @GetMapping()
    public String group()
    {
        return prefix + "/group";
    }

    /**
     * 查询流程用户组列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ActIdGroup actIdGroup)
    {
        startPage();
        List<ActIdGroup> list = actIdGroupService.selectActIdGroupList(actIdGroup);
        return getDataTable(list);
    }

    /**
     * 导出流程用户组列表
     */
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ActIdGroup actIdGroup)
    {
        List<ActIdGroup> list = actIdGroupService.selectActIdGroupList(actIdGroup);
        ExcelUtil<ActIdGroup> util = new ExcelUtil<ActIdGroup>(ActIdGroup.class);
        return util.exportExcel(list, "group");
    }

    /**
     * 新增流程用户组
     */
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        mmap.put("users", actIdUserService.selectActIdUserList(null));
        return prefix + "/add";
    }

    /**
     * 新增保存流程用户组
     */
    @Log(title = "流程用户组", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ActIdGroup actIdGroup) {
        int rows = actIdGroupService.insertActIdGroup(actIdGroup);
        String[] userIds = actIdGroup.getUserIds();
        for (String userId: userIds) {
            identityService.createMembership(userId, actIdGroup.getId());
        }
        return toAjax(rows);
    }

    /**
     * 修改流程用户组
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap) {
        ActIdGroup actIdGroup = actIdGroupService.selectActIdGroupById(id);
        mmap.put("actIdGroup", actIdGroup);
        mmap.put("users", actIdGroupService.selectUserByGroupId(id));
        return prefix + "/edit";
    }

    /**
     * 修改保存流程用户组
     */
    @Log(title = "流程用户组", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ActIdGroup actIdGroup) {
        int rows = actIdGroupService.updateActIdGroup(actIdGroup);
        String[] userIds = actIdGroup.getUserIds();
        List<User> userList = identityService.createUserQuery().memberOfGroup(actIdGroup.getId()).list();
        // 先删后增
        userList.forEach(existUser -> {
            identityService.deleteMembership(existUser.getId(), actIdGroup.getId());
        });
        for (String userId: userIds) {
            identityService.createMembership(userId, actIdGroup.getId());
        }
        return toAjax(rows);
    }

    /**
     * 删除流程用户组
     */
    @Log(title = "流程用户组", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(actIdGroupService.deleteActIdGroupByIds(ids));
    }

}
