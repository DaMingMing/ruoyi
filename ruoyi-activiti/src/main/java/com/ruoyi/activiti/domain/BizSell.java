package com.ruoyi.activiti.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 销售业务对象 biz_sell
 * 
 * @author xiaojm
 * @date 2020-03-27
 */
public class BizSell extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String productName;

    /** SKU */
    @Excel(name = "SKU")
    private String sku;

    /** 市场调研 */
    @Excel(name = "市场调研")
    private String marketResearch;

    /** 卖点分析 */
    @Excel(name = "卖点分析")
    private String sellingPointAna;

    /** 痛点分析 */
    @Excel(name = "痛点分析")
    private String painPointAna;

    /** 销售策略 */
    @Excel(name = "销售策略")
    private String sellStrategy;

    /** keywords */
    @Excel(name = "keywords")
    private String keywords;

    /** 5point */
    @Excel(name = "5point")
    private String fivepoint;

    /** description */
    @Excel(name = "description")
    private String description;

    /** 图需 */
    @Excel(name = "图需")
    private String photoNeed;

    /** 运营计划 */
    @Excel(name = "运营计划")
    private String businessPlan;

    /** 流程实例ID */
    @Excel(name = "流程实例ID")
    private String instanceId;

    /** 申请人 */
    @Excel(name = "申请人")
    private String applyUser;

    /** 申请时间 */
    @Excel(name = "申请时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date applyTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setProductName(String productName) 
    {
        this.productName = productName;
    }

    public String getProductName() 
    {
        return productName;
    }
    public void setSku(String sku) 
    {
        this.sku = sku;
    }

    public String getSku() 
    {
        return sku;
    }
    public void setMarketResearch(String marketResearch) 
    {
        this.marketResearch = marketResearch;
    }

    public String getMarketResearch() 
    {
        return marketResearch;
    }
    public void setSellingPointAna(String sellingPointAna) 
    {
        this.sellingPointAna = sellingPointAna;
    }

    public String getSellingPointAna() 
    {
        return sellingPointAna;
    }
    public void setPainPointAna(String painPointAna) 
    {
        this.painPointAna = painPointAna;
    }

    public String getPainPointAna() 
    {
        return painPointAna;
    }
    public void setSellStrategy(String sellStrategy) 
    {
        this.sellStrategy = sellStrategy;
    }

    public String getSellStrategy() 
    {
        return sellStrategy;
    }
    public void setKeywords(String keywords) 
    {
        this.keywords = keywords;
    }

    public String getKeywords() 
    {
        return keywords;
    }
    public void setFivepoint(String fivepoint) 
    {
        this.fivepoint = fivepoint;
    }

    public String getFivepoint() 
    {
        return fivepoint;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setPhotoNeed(String photoNeed) 
    {
        this.photoNeed = photoNeed;
    }

    public String getPhotoNeed() 
    {
        return photoNeed;
    }
    public void setBusinessPlan(String businessPlan) 
    {
        this.businessPlan = businessPlan;
    }

    public String getBusinessPlan() 
    {
        return businessPlan;
    }
    public void setInstanceId(String instanceId) 
    {
        this.instanceId = instanceId;
    }

    public String getInstanceId() 
    {
        return instanceId;
    }
    public void setApplyUser(String applyUser) 
    {
        this.applyUser = applyUser;
    }

    public String getApplyUser() 
    {
        return applyUser;
    }
    public void setApplyTime(Date applyTime) 
    {
        this.applyTime = applyTime;
    }

    public Date getApplyTime() 
    {
        return applyTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("productName", getProductName())
            .append("sku", getSku())
            .append("marketResearch", getMarketResearch())
            .append("sellingPointAna", getSellingPointAna())
            .append("painPointAna", getPainPointAna())
            .append("sellStrategy", getSellStrategy())
            .append("keywords", getKeywords())
            .append("fivepoint", getFivepoint())
            .append("description", getDescription())
            .append("photoNeed", getPhotoNeed())
            .append("businessPlan", getBusinessPlan())
            .append("instanceId", getInstanceId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("applyUser", getApplyUser())
            .append("applyTime", getApplyTime())
            .toString();
    }
}
