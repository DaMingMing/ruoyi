package com.ruoyi.activiti.mapper;

import com.ruoyi.activiti.domain.BizDesign;
import com.ruoyi.activiti.domain.BizDesignVo;

import java.util.List;

/**
 * 美工设计业务Mapper接口
 * 
 * @author xiaojm
 * @date 2020-03-29
 */
public interface BizDesignMapper 
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
     * 修改美工设计业务
     * 
     * @param bizDesign 美工设计业务
     * @return 结果
     */
    public int updateBizDesign(BizDesignVo bizDesign);

    /**
     * 删除美工设计业务
     * 
     * @param id 美工设计业务ID
     * @return 结果
     */
    public int deleteBizDesignById(Long id);

    /**
     * 批量删除美工设计业务
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizDesignByIds(String[] ids);
}
