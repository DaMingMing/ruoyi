package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.BizDesign;
import com.ruoyi.activiti.domain.BizDesignVo;
import com.ruoyi.activiti.domain.BizDevelopVo;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.List;
import java.util.Map;

/**
 * 美工设计业务Service接口
 * 
 * @author xiaojm
 * @date 2020-03-29
 */
public interface IBizDesignService 
{
    /**
     * 查询美工设计业务
     * 
     * @param id 美工设计业务ID
     * @return 美工设计业务
     */
    public BizDesignVo selectBizDesignById(Long id);

    /**
     * 查询美工设计业务列表
     * 
     * @param bizDesign 美工设计业务
     * @return 美工设计业务集合
     */
    public List<BizDesignVo> selectBizDesignList(BizDesignVo bizDesign);

    /**
     * 新增美工设计业务
     * 
     * @param bizDesign 美工设计业务
     * @return 结果
     */
    public int insertBizDesign(BizDesignVo bizDesign);


    /**
     * 启动流程
     * @param entity
     * @param applyUserId
     * @return
     */
    ProcessInstance submitApply(BizDesignVo entity, String applyUserId);

    /**
     * 查询我的待办列表
     * @param userId
     * @return
     */
    List<BizDesignVo> findTodoTasks(BizDesignVo designVo, String userId);

    /**
     * 完成任务
     * @param designVo
     * @param saveEntity
     * @param taskId
     * @param variables
     */
    void complete(BizDesignVo designVo, Boolean saveEntity, String taskId, Map<String, Object> variables);

    /**
     * 修改美工设计业务
     * 
     * @param bizDesign 美工设计业务
     * @return 结果
     */
    public int updateBizDesign(BizDesignVo bizDesign);

    /**
     * 批量删除美工设计业务
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizDesignByIds(String ids);

    /**
     * 删除美工设计业务信息
     * 
     * @param id 美工设计业务ID
     * @return 结果
     */
    public int deleteBizDesignById(Long id);
}
