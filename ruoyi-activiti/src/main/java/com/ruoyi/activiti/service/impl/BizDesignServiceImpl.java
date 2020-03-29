package com.ruoyi.activiti.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.ruoyi.activiti.domain.BizDesignVo;
import com.ruoyi.activiti.domain.BizDevelopVo;
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
import com.ruoyi.activiti.mapper.BizDesignMapper;
import com.ruoyi.activiti.domain.BizDesign;
import com.ruoyi.activiti.service.IBizDesignService;
import com.ruoyi.common.core.text.Convert;
import org.springframework.util.CollectionUtils;

/**
 * 美工设计业务Service业务层处理
 * 
 * @author xiaojm
 * @date 2020-03-29
 */
@Service
public class BizDesignServiceImpl implements IBizDesignService {
    @Autowired
    private BizDesignMapper bizDesignMapper;

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
     * 查询美工设计业务
     * 
     * @param id 美工设计业务ID
     * @return 美工设计业务
     */
    @Override
    public BizDesignVo selectBizDesignById(Long id) {
        BizDesignVo bizDesignVo = bizDesignMapper.selectBizDesignById(id);
        SysUser sysUser = userMapper.selectUserByLoginName(bizDesignVo.getApplyUser());
        if (sysUser != null) {
            bizDesignVo.setApplyUserName(sysUser.getUserName());
        }
        return bizDesignVo;
    }

    /**
     * 查询美工设计业务列表
     * 
     * @param bizDesign 美工设计业务
     * @return 美工设计业务
     */
    @Override
    public List<BizDesignVo> selectBizDesignList(BizDesignVo bizDesign){
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();

        // PageHelper 仅对第一个 List 分页
        Page<BizDesignVo> list = (Page<BizDesignVo>) bizDesignMapper.selectBizDesignList(bizDesign);
        Page<BizDesignVo> returnList = new Page<>();
        for (BizDesignVo designVo: list) {
            SysUser sysUser = userMapper.selectUserByLoginName(designVo.getCreateBy());
            if (sysUser != null) {
                designVo.setCreateUserName(sysUser.getUserName());
            }
            SysUser sysUser2 = userMapper.selectUserByLoginName(designVo.getApplyUser());
            if (sysUser2 != null) {
                designVo.setApplyUserName(sysUser2.getUserName());
            }
            // 当前环节
            if (StringUtils.isNotBlank(designVo.getInstanceId())) {
                List<Task> taskList = taskService.createTaskQuery()
                        .processInstanceId(designVo.getInstanceId())
//                        .singleResult();
                        .list();    // 例如请假会签，会同时拥有多个任务
                if (!CollectionUtils.isEmpty(taskList)) {
                    Task task = taskList.get(0);
                    designVo.setTaskId(task.getId());
                    designVo.setTaskName(task.getName());
                } else {
                    designVo.setTaskName("已办结");
                }
            } else {
                designVo.setTaskName("未启动");
            }
            returnList.add(designVo);
        }
        returnList.setTotal(CollectionUtils.isEmpty(list) ? 0 : list.getTotal());
        returnList.setPageNum(pageNum);
        returnList.setPageSize(pageSize);
        return returnList;
        //return bizDesignMapper.selectBizDesignList(bizDesign);
    }

    /**
     * 新增美工设计业务
     * 
     * @param bizDesign 美工设计业务
     * @return 结果
     */
    @Override
    public int insertBizDesign(BizDesignVo bizDesign)
    {
        bizDesign.setCreateBy(ShiroUtils.getLoginName());
        bizDesign.setCreateTime(DateUtils.getNowDate());
        return bizDesignMapper.insertBizDesign(bizDesign);
    }

    @Override
    public ProcessInstance submitApply(BizDesignVo entity, String applyUserId) {
        entity.setApplyUser(applyUserId);
        entity.setApplyTime(DateUtils.getNowDate());
        entity.setUpdateBy(applyUserId);
        bizDesignMapper.updateBizDesign(entity);
        String businessKey = entity.getId().toString(); // 实体类 ID，作为流程的业务 key

        // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
        identityService.setAuthenticatedUserId(applyUserId);

        ProcessInstance processInstance = runtimeService // 启动流程时设置业务 key
                .startProcessInstanceByKey("design", businessKey);
        String processInstanceId = processInstance.getId();
        entity.setInstanceId(processInstanceId); // 跟请假单关联，建立双向关系
        bizDesignMapper.updateBizDesign(entity);

        return processInstance;
    }

    @Override
    public List<BizDesignVo> findTodoTasks(BizDesignVo designVo, String userId) {
        List<BizDesignVo> results = new ArrayList<>();
        List<Task> tasks = new ArrayList<Task>();

        // 根据当前人的ID查询
        List<Task> todoList = taskService.createTaskQuery().processDefinitionKey("design").taskAssignee(userId).list();

        // 根据当前人未签收的任务
        List<Task> unsignedTasks = taskService.createTaskQuery().processDefinitionKey("design").taskCandidateUser(userId).list();

        // 合并
        tasks.addAll(todoList);
        tasks.addAll(unsignedTasks);

        // 根据流程的业务ID查询实体并关联
        for (Task task : tasks) {
            String processInstanceId = task.getProcessInstanceId();

            // 条件过滤 1
            if (StringUtils.isNotBlank(designVo.getInstanceId()) && !designVo.getInstanceId().equals(processInstanceId)) {
                continue;
            }

            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            String businessKey = processInstance.getBusinessKey();
            BizDesignVo leave2 = bizDesignMapper.selectBizDesignById(new Long(businessKey));

/*            // 条件过滤 2
            if (StringUtils.isNotBlank(develop.getType()) && !leave.getType().equals(leave2.getType())) {
                continue;
            }*/

            leave2.setTaskId(task.getId());
            leave2.setTaskName(task.getName());

            SysUser sysUser = userMapper.selectUserByLoginName(leave2.getApplyUser());
            leave2.setApplyUserName(sysUser.getUserName());

            results.add(leave2);
        }
        return results;
    }

    @Override
    public void complete(BizDesignVo designVo, Boolean saveEntity, String taskId, Map<String, Object> variables) {
        if (saveEntity) {
            bizDesignMapper.updateBizDesign(designVo);
        }
        // 只有签收任务，act_hi_taskinst 表的 assignee 字段才不为 null
        taskService.claim(taskId, ShiroUtils.getLoginName());
        taskService.complete(taskId, variables);
    }

    /**
     * 修改美工设计业务
     * 
     * @param bizDesign 美工设计业务
     * @return 结果
     */
    @Override
    public int updateBizDesign(BizDesignVo bizDesign)
    {
        bizDesign.setUpdateTime(DateUtils.getNowDate());
        return bizDesignMapper.updateBizDesign(bizDesign);
    }

    /**
     * 删除美工设计业务对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteBizDesignByIds(String ids)
    {
        return bizDesignMapper.deleteBizDesignByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除美工设计业务信息
     * 
     * @param id 美工设计业务ID
     * @return 结果
     */
    @Override
    public int deleteBizDesignById(Long id)
    {
        return bizDesignMapper.deleteBizDesignById(id);
    }
}
