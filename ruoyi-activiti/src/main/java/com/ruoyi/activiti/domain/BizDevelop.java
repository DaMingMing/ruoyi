package com.ruoyi.activiti.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 开发业务对象 biz_develop
 * 
 * @author xiaojm
 * @date 2020-03-21
 */
public class BizDevelop extends BaseEntity
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

    /** 卖点 */
    @Excel(name = "卖点")
    private String sellingPoint;

    /** 痛点 */
    @Excel(name = "痛点")
    private String painPoint;

    /** 流程实例ID */
    @Excel(name = "流程实例ID")
    private String instanceId;

    /** 申请人 */
    @Excel(name = "申请人")
    private String applyUser;

    /** 申请时间 */
    @Excel(name = "申请时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date applyTime;

    /** 附件 */
    @Excel(name = "附件")
    private String attachment;


    /** 附件名称 */
    @Excel(name = "附件名称")
    private String attachmentName;

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
    public void setSellingPoint(String sellingPoint) 
    {
        this.sellingPoint = sellingPoint;
    }

    public String getSellingPoint() 
    {
        return sellingPoint;
    }
    public void setPainPoint(String painPoint) 
    {
        this.painPoint = painPoint;
    }

    public String getPainPoint() 
    {
        return painPoint;
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

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("productName", getProductName())
            .append("sku", getSku())
            .append("sellingPoint", getSellingPoint())
            .append("painPoint", getPainPoint())
            .append("instanceId", getInstanceId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("applyUser", getApplyUser())
            .append("applyTime", getApplyTime())
            .append("attachment", getAttachment())
            .append("attachmentName", getAttachmentName())
            .toString();
    }
}
