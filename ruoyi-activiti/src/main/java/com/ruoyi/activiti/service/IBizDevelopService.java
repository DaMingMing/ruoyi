package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.BizDevelop;
import com.ruoyi.activiti.domain.BizDevelopVo;
import com.ruoyi.activiti.domain.BizLeaveVo;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.List;

/**
 * 开发业务Service接口
 * 
 * @author xiaojm
 * @date 2020-03-21
 */
public interface IBizDevelopService 
{
    /**
     * 查询开发业务
     * 
     * @param id 开发业务ID
     * @return 开发业务
     */
    public BizDevelopVo selectBizDevelopById(Long id);

    /**
     * 查询开发业务列表
     * 
     * @param bizDevelop 开发业务
     * @return 开发业务集合
     */
    public List<BizDevelopVo> selectBizDevelopList(BizDevelopVo bizDevelop);

    /**
     * 启动流程
     * @param entity
     * @param applyUserId
     * @return
     */
    ProcessInstance submitApply(BizDevelopVo entity, String applyUserId);

    /**
     * 新增开发业务
     * 
     * @param bizDevelop 开发业务
     * @return 结果
     */
    public int insertBizDevelop(BizDevelopVo bizDevelop);

    /**
     * 修改开发业务
     * 
     * @param bizDevelop 开发业务
     * @return 结果
     */
    public int updateBizDevelop(BizDevelopVo bizDevelop);

    /**
     * 查询我的待办列表
     * @param userId
     * @return
     */
    List<BizDevelopVo> findTodoTasks(BizDevelopVo leave, String userId);

    /**
     * 批量删除开发业务
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizDevelopByIds(String ids);

    /**
     * 删除开发业务信息
     * 
     * @param id 开发业务ID
     * @return 结果
     */
    public int deleteBizDevelopById(Long id);
}
