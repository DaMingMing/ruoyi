package com.ruoyi.activiti.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.ruoyi.activiti.domain.BizDevelopVo;
import com.ruoyi.activiti.domain.BizSellVo;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.activiti.mapper.BizSellMapper;
import com.ruoyi.activiti.domain.BizSell;
import com.ruoyi.activiti.service.IBizSellService;
import com.ruoyi.common.core.text.Convert;
import org.springframework.util.CollectionUtils;

/**
 * 销售业务Service业务层处理
 * 
 * @author xiaojm
 * @date 2020-03-27
 */
@Service
public class BizSellServiceImpl implements IBizSellService 
{
    @Autowired
    private BizSellMapper bizSellMapper;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private SysUserMapper userMapper;

    /**
     * 查询销售业务
     * 
     * @param id 销售业务ID
     * @return 销售业务
     */
    @Override
    public BizSellVo selectBizSellById(Long id){
        BizSellVo bizSellVo = bizSellMapper.selectBizSellById(id);
        SysUser sysUser = userMapper.selectUserByLoginName(bizSellVo.getApplyUser());
        if (sysUser != null) {
            bizSellVo.setApplyUserName(sysUser.getUserName());
        }
        return bizSellVo;
    }

    /**
     * 查询销售业务列表
     * 
     * @param bizSell 销售业务
     * @return 销售业务
     */
    @Override
    public List<BizSellVo> selectBizSellList(BizSellVo bizSell){
        //return bizSellMapper.selectBizSellList(bizSell);
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();

        // PageHelper 仅对第一个 List 分页
        Page<BizSellVo> list = (Page<BizSellVo>) bizSellMapper.selectBizSellList(bizSell);
        Page<BizSellVo> returnList = new Page<>();
        for (BizSellVo bizSellVo: list) {
            SysUser sysUser = userMapper.selectUserByLoginName(bizSellVo.getCreateBy());
            if (sysUser != null) {
                bizSellVo.setCreateUserName(sysUser.getUserName());
            }
            SysUser sysUser2 = userMapper.selectUserByLoginName(bizSellVo.getApplyUser());
            if (sysUser2 != null) {
                bizSellVo.setApplyUserName(sysUser2.getUserName());
            }
            // 当前环节
            if (StringUtils.isNotBlank(bizSellVo.getInstanceId())) {
                List<Task> taskList = taskService.createTaskQuery()
                        .processInstanceId(bizSellVo.getInstanceId())
//                        .singleResult();
                        .list();    // 例如请假会签，会同时拥有多个任务
                if (!CollectionUtils.isEmpty(taskList)) {
                    Task task = taskList.get(0);
                    bizSellVo.setTaskId(task.getId());
                    bizSellVo.setTaskName(task.getName());
                } else {
                    bizSellVo.setTaskName("已办结");
                }
            } else {
                bizSellVo.setTaskName("未启动");
            }
            returnList.add(bizSellVo);
        }
        returnList.setTotal(CollectionUtils.isEmpty(list) ? 0 : list.getTotal());
        returnList.setPageNum(pageNum);
        returnList.setPageSize(pageSize);
        return returnList;
    }

    @Override
    public ProcessInstance submitApply(BizSellVo entity, String applyUserId) {
        entity.setApplyUser(applyUserId);
        entity.setApplyTime(DateUtils.getNowDate());
        entity.setUpdateBy(applyUserId);
        bizSellMapper.updateBizSell(entity);
        String businessKey = entity.getId().toString(); // 实体类 ID，作为流程的业务 key

        // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
        identityService.setAuthenticatedUserId(applyUserId);

        ProcessInstance processInstance = runtimeService // 启动流程时设置业务 key
                .startProcessInstanceByKey("sell", businessKey);
        String processInstanceId = processInstance.getId();
        entity.setInstanceId(processInstanceId); // 跟请假单关联，建立双向关系
        bizSellMapper.updateBizSell(entity);

        return processInstance;
    }

    @Override
    public List<BizSellVo> findTodoTasks(BizSellVo sellVo, String userId) {
        List<BizSellVo> results = new ArrayList<>();
        List<Task> tasks = new ArrayList<Task>();

        // 根据当前人的ID查询
        List<Task> todoList = taskService.createTaskQuery().processDefinitionKey("sell").taskAssignee(userId).list();

        // 根据当前人未签收的任务
        List<Task> unsignedTasks = taskService.createTaskQuery().processDefinitionKey("sell").taskCandidateUser(userId).list();

        // 合并
        tasks.addAll(todoList);
        tasks.addAll(unsignedTasks);

        // 根据流程的业务ID查询实体并关联
        for (Task task : tasks) {
            String processInstanceId = task.getProcessInstanceId();

            // 条件过滤 1
            if (StringUtils.isNotBlank(sellVo.getInstanceId()) && !sellVo.getInstanceId().equals(processInstanceId)) {
                continue;
            }

            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            String businessKey = processInstance.getBusinessKey();
            BizSellVo sell2 = bizSellMapper.selectBizSellById(new Long(businessKey));

/*            // 条件过滤 2
            if (StringUtils.isNotBlank(develop.getType()) && !leave.getType().equals(leave2.getType())) {
                continue;
            }*/

            sell2.setTaskId(task.getId());
            sell2.setTaskName(task.getName());

            SysUser sysUser = userMapper.selectUserByLoginName(sell2.getApplyUser());
            sell2.setApplyUserName(sysUser.getUserName());

            results.add(sell2);
        }
        return results;
    }

    @Override
    public void complete(BizSellVo sellVo, Boolean saveEntity, String taskId, Map<String, Object> variables) {
        if (saveEntity) {
            bizSellMapper.updateBizSell(sellVo);
        }
        // 只有签收任务，act_hi_taskinst 表的 assignee 字段才不为 null
        taskService.claim(taskId, ShiroUtils.getLoginName());
        taskService.complete(taskId, variables);
    }

    /**
     * 新增销售业务
     * 
     * @param bizSell 销售业务
     * @return 结果
     */
    @Override
    public int insertBizSell(BizSellVo bizSell)
    {
        bizSell.setCreateBy(ShiroUtils.getLoginName());
        bizSell.setCreateTime(DateUtils.getNowDate());
        return bizSellMapper.insertBizSell(bizSell);
    }

    /**
     * 修改销售业务
     * 
     * @param bizSell 销售业务
     * @return 结果
     */
    @Override
    public int updateBizSell(BizSellVo bizSell)
    {
        bizSell.setUpdateTime(DateUtils.getNowDate());
        return bizSellMapper.updateBizSell(bizSell);
    }

    /**
     * 删除销售业务对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteBizSellByIds(String ids)
    {
        return bizSellMapper.deleteBizSellByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除销售业务信息
     * 
     * @param id 销售业务ID
     * @return 结果
     */
    @Override
    public int deleteBizSellById(Long id)
    {
        return bizSellMapper.deleteBizSellById(id);
    }
}
