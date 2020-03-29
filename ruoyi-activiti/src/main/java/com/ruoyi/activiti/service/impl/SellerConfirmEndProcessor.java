package com.ruoyi.activiti.service.impl;

import com.ruoyi.activiti.domain.BizLeaveVo;
import com.ruoyi.activiti.domain.BizSellVo;
import com.ruoyi.activiti.service.IBizLeaveService;
import com.ruoyi.activiti.service.IBizSellService;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <b>监听器使用范例</b>：开发流程销售确认后处理器
 * <p>
 * 设置销假时间
 * </p>
 * <p>
 * 使用Spring代理，可以注入Bean，管理事物
 * </p>
 *
 * @author HenryYan
 */
@Component
@Transactional
public class SellerConfirmEndProcessor implements TaskListener {

    private static final long serialVersionUID = 1L;

    @Autowired
    IBizLeaveService bizLeaveService;


    @Autowired
    private IBizSellService bizSellService;

    @Autowired
    private SysUserMapper userMapper;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.activiti.engine.delegate.TaskListener#notify(org.activiti.engine.delegate
     * .DelegateTask)
     */
    public void notify(DelegateTask delegateTask) {
/*        BizLeaveVo leave = bizLeaveService.selectBizLeaveById(new Long(delegateTask.getExecution().getProcessInstanceBusinessKey()));
        Object realityStartTime = delegateTask.getVariable("realityStartTime");
        leave.setRealityStartTime((Date) realityStartTime);
        Object realityEndTime = delegateTask.getVariable("realityEndTime");
        leave.setRealityEndTime((Date) realityEndTime);
        bizLeaveService.updateBizLeave(leave);*/
        //String loginName = ShiroUtils.getLoginName();

        //自动触发销售流程
        //1、往销售业务表插入数据
        BizSellVo bizSell = new BizSellVo();
        String assignee = delegateTask.getAssignee();
        String sku = (String) delegateTask.getVariable("sku");
        String productName = (String)delegateTask.getVariable("productName");
        String title = (String)delegateTask.getVariable("title");
        bizSell.setSku(sku);
        bizSell.setTitle(title);
        bizSell.setProductName(productName);
        int i = bizSellService.insertBizSell(bizSell);
        //2、启动销售流程
        SysUser sysUser = userMapper.selectUserByLoginName(assignee);
        if (sysUser != null) {
            bizSell.setApplyUserName(sysUser.getUserName());
        }
        String applyUserId = ShiroUtils.getLoginName();
        bizSellService.submitApply(bizSell, applyUserId);
        System.out.println(sku + "------------------notify----------------" + productName);
    }

}
