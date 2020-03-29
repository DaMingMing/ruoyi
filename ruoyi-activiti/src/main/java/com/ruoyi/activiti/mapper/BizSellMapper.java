package com.ruoyi.activiti.mapper;

import com.ruoyi.activiti.domain.BizSell;
import com.ruoyi.activiti.domain.BizSellVo;

import java.util.List;

/**
 * 销售业务Mapper接口
 * 
 * @author xiaojm
 * @date 2020-03-27
 */
public interface BizSellMapper 
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
     * 删除销售业务
     * 
     * @param id 销售业务ID
     * @return 结果
     */
    public int deleteBizSellById(Long id);

    /**
     * 批量删除销售业务
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizSellByIds(String[] ids);
}
