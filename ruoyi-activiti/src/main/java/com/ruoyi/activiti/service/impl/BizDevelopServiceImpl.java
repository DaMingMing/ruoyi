package com.ruoyi.activiti.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.ruoyi.activiti.domain.BizDevelopVo;
import com.ruoyi.activiti.domain.BizLeaveVo;
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
import com.ruoyi.activiti.mapper.BizDevelopMapper;
import com.ruoyi.activiti.domain.BizDevelop;
import com.ruoyi.activiti.service.IBizDevelopService;
import com.ruoyi.common.core.text.Convert;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 开发业务Service业务层处理
 * 
 * @author xiaojm
 * @date 2020-03-21
 */
@Service
public class BizDevelopServiceImpl implements IBizDevelopService 
{
    @Autowired
    private BizDevelopMapper bizDevelopMapper;

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
     * 查询开发业务
     * 
     * @param id 开发业务ID
     * @return 开发业务
     */
    @Override
    public BizDevelopVo selectBizDevelopById(Long id) {
        BizDevelopVo leave = bizDevelopMapper.selectBizDevelopById(id);
        SysUser sysUser = userMapper.selectUserByLoginName(leave.getApplyUser());
        if (sysUser != null) {
            leave.setApplyUserName(sysUser.getUserName());
        }
        return leave;
    }

    /**
     * 查询开发业务列表
     * 
     * @param bizDevelop 开发业务
     * @return 开发业务
     */
    @Override
    public List<BizDevelopVo> selectBizDevelopList(BizDevelopVo bizDevelop){
        //return bizDevelopMapper.selectBizDevelopList(bizDevelop);

        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();

        // PageHelper 仅对第一个 List 分页
        Page<BizDevelopVo> list = (Page<BizDevelopVo>) bizDevelopMapper.selectBizDevelopList(bizDevelop);
        Page<BizDevelopVo> returnList = new Page<>();
        for (BizDevelopVo developVo: list) {
            SysUser sysUser = userMapper.selectUserByLoginName(developVo.getCreateBy());
            if (sysUser != null) {
                developVo.setCreateUserName(sysUser.getUserName());
            }
            SysUser sysUser2 = userMapper.selectUserByLoginName(developVo.getApplyUser());
            if (sysUser2 != null) {
                developVo.setApplyUserName(sysUser2.getUserName());
            }
            // 当前环节
            if (StringUtils.isNotBlank(developVo.getInstanceId())) {
                List<Task> taskList = taskService.createTaskQuery()
                        .processInstanceId(developVo.getInstanceId())
//                        .singleResult();
                        .list();    // 例如请假会签，会同时拥有多个任务
                if (!CollectionUtils.isEmpty(taskList)) {
                    Task task = taskList.get(0);
                    developVo.setTaskId(task.getId());
                    developVo.setTaskName(task.getName());
                } else {
                    developVo.setTaskName("已办结");
                }
            } else {
                developVo.setTaskName("未启动");
            }
            returnList.add(developVo);
        }
        returnList.setTotal(CollectionUtils.isEmpty(list) ? 0 : list.getTotal());
        returnList.setPageNum(pageNum);
        returnList.setPageSize(pageSize);
        return returnList;
    }

    @Override
    public ProcessInstance submitApply(BizDevelopVo entity, String applyUserId) {
        entity.setApplyUser(applyUserId);
        entity.setApplyTime(DateUtils.getNowDate());
        entity.setUpdateBy(applyUserId);
        bizDevelopMapper.updateBizDevelop(entity);
        String businessKey = entity.getId().toString(); // 实体类 ID，作为流程的业务 key

        // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
        identityService.setAuthenticatedUserId(applyUserId);

        ProcessInstance processInstance = runtimeService // 启动流程时设置业务 key
                .startProcessInstanceByKey("develop", businessKey);
        String processInstanceId = processInstance.getId();
        entity.setInstanceId(processInstanceId); // 跟请假单关联，建立双向关系
        bizDevelopMapper.updateBizDevelop(entity);

        return processInstance;
    }

    /**
     * 新增开发业务
     * 
     * @param bizDevelop 开发业务
     * @return 结果
     */
    @Override
    public int insertBizDevelop(BizDevelopVo bizDevelop){
        bizDevelop.setCreateBy(ShiroUtils.getLoginName());
        bizDevelop.setCreateTime(DateUtils.getNowDate());
        return bizDevelopMapper.insertBizDevelop(bizDevelop);
    }

    /**
     * 修改开发业务
     * 
     * @param bizDevelop 开发业务
     * @return 结果
     */
    @Override
    public int updateBizDevelop(BizDevelopVo bizDevelop) {

        bizDevelop.setUpdateTime(DateUtils.getNowDate());
        return bizDevelopMapper.updateBizDevelop(bizDevelop);
    }

    /**
     * 查询开发待办任务
     */
    @Override
    @Transactional(readOnly = true)
    public List<BizDevelopVo> findTodoTasks(BizDevelopVo develop, String userId) {
        List<BizDevelopVo> results = new ArrayList<>();
        List<Task> tasks = new ArrayList<Task>();

        // 根据当前人的ID查询
        List<Task> todoList = taskService.createTaskQuery().processDefinitionKey("develop").taskAssignee(userId).list();

        // 根据当前人未签收的任务
        List<Task> unsignedTasks = taskService.createTaskQuery().processDefinitionKey("develop").taskCandidateUser(userId).list();

        // 合并
        tasks.addAll(todoList);
        tasks.addAll(unsignedTasks);

        // 根据流程的业务ID查询实体并关联
        for (Task task : tasks) {
            String processInstanceId = task.getProcessInstanceId();

            // 条件过滤 1
            if (StringUtils.isNotBlank(develop.getInstanceId()) && !develop.getInstanceId().equals(processInstanceId)) {
                continue;
            }

            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            String businessKey = processInstance.getBusinessKey();
            BizDevelopVo leave2 = bizDevelopMapper.selectBizDevelopById(new Long(businessKey));

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

    /**
     * 删除开发业务对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteBizDevelopByIds(String ids)
    {
        return bizDevelopMapper.deleteBizDevelopByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除开发业务信息
     * 
     * @param id 开发业务ID
     * @return 结果
     */
    @Override
    public int deleteBizDevelopById(Long id)
    {
        return bizDevelopMapper.deleteBizDevelopById(id);
    }

    /**
     * 完成任务
     * @param developVo
     * @param saveEntity
     * @param taskId
     * @param variables
     */
    @Override
    public void complete(BizDevelopVo developVo, Boolean saveEntity, String taskId, Map<String, Object> variables) {
        if (saveEntity) {
            bizDevelopMapper.updateBizDevelop(developVo);
        }
        // 只有签收任务，act_hi_taskinst 表的 assignee 字段才不为 null
        taskService.claim(taskId, ShiroUtils.getLoginName());
        taskService.complete(taskId, variables);
    }
}
