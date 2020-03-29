package com.ruoyi.activiti.service.impl;

import com.ruoyi.activiti.domain.BizDesign;
import com.ruoyi.activiti.domain.BizDesignVo;
import com.ruoyi.activiti.domain.BizSellVo;
import com.ruoyi.activiti.service.IBizDesignService;
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

/**
 * 销售领导确认后处理器
 * @author xiaojm
 */
@Component
@Transactional
public class SellerLeaderVerifyEndProcessor implements TaskListener {

    private static final long serialVersionUID = 1L;

    @Autowired
    private IBizDesignService bizDesignService;

    @Autowired
    private SysUserMapper userMapper;


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
        BizDesignVo bizDesignVo = new BizDesignVo();
        String assignee = delegateTask.getAssignee();
        String sku = (String) delegateTask.getVariable("sku");
        String productName = (String)delegateTask.getVariable("productName");
        String title = (String)delegateTask.getVariable("title");
        String photoNeed = (String)delegateTask.getVariable("photoNeed");

        bizDesignVo.setSku(sku);
        bizDesignVo.setTitle(title);
        bizDesignVo.setProductName(productName);
        bizDesignVo.setPhotoNeed(photoNeed);
        int i = bizDesignService.insertBizDesign(bizDesignVo);

        //2、启动销售流程
        SysUser sysUser = userMapper.selectUserByLoginName(assignee);
        if (sysUser != null) {
            bizDesignVo.setApplyUserName(sysUser.getUserName());
        }
        //String applyUserId = ShiroUtils.getLoginName();
        bizDesignService.submitApply(bizDesignVo, assignee);
        System.out.println(sku + "------------------notify----------------" + productName);
    }

}
