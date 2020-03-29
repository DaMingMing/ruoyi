package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.BizDevelopVo;
import com.ruoyi.activiti.domain.BizSell;
import com.ruoyi.activiti.domain.BizSellVo;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.List;
import java.util.Map;

/**
 * 销售业务Service接口
 * 
 * @author xiaojm
 * @date 2020-03-27
 */
public interface IBizSellService 
{
    /**
     * 查询销售业务
     * 
     * @param id 销售业务ID
     * @return 销售业务
     */
    public BizSellVo selectBizSellById(Long id);

    /**
     * 查询销售业务列表
     * 
     * @param bizSell 销售业务
     * @return 销售业务集合
     */
    public List<BizSellVo> selectBizSellList(BizSellVo bizSell);

    /**
     * 启动流程
     * @param entity
     * @param applyUserId
     * @return
     */
    ProcessInstance submitApply(BizSellVo entity, String applyUserId);

    /**
     * 查询我的待办列表
     * @param userId
     * @return
     */
    List<BizSellVo> findTodoTasks(BizSellVo sellVo, String userId);

    /**
     * 完成任务
     * @param sellVo
     * @param saveEntity
     * @param taskId
     * @param variables
     */
    void complete(BizSellVo sellVo, Boolean saveEntity, String taskId, Map<String, Object> variables);

    /**
     * 新增销售业务
     * 
     * @param bizSell 销售业务
     * @return 结果
     */
    public int insertBizSell(BizSellVo bizSell);

    /**
     * 修改销售业务
     * 
     * @param bizSell 销售业务
     * @return 结果
     */
    public int updateBizSell(BizSellVo bizSell);

    /**
     * 批量删除销售业务
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizSellByIds(String ids);

    /**
     * 删除销售业务信息
     * 
     * @param id 销售业务ID
     * @return 结果
     */
    public int deleteBizSellById(Long id);
}
