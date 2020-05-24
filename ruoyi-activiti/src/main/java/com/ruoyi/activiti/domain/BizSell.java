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

    /** 附件 */
    @Excel(name = "附件")
    private String attachment;

    /** 附件名称 */
    @Excel(name = "附件名称")
    private String attachmentName;

    /** 价格 */
    @Excel(name = "价格")
    private Double price;

    /** 产品title */
    @Excel(name = "产品title")
    private String product_title;

    /** 月份 */
    @Excel(name = "月份")
    private String sell_month;

    /** 目标销量 */
    @Excel(name = "目标销量")
    private Long sell_count;

    /** 目标销售额 */
    @Excel(name = "目标销售额")
    private Long sell_amount;

    /** 利润率 */
    @Excel(name = "利润率")
    private String sell_profitRate;

    /** 利润 */
    @Excel(name = "利润")
    private Long sell_profit;

    /** 关键词平均竞价 */
    @Excel(name = "关键词平均竞价")
    private Double ad_perPrice;

    /** 预计广告转换率 */
    @Excel(name = "预计广告转换率")
    private String ad_tarnsRate;

    /** 预计广告转换单量 */
    @Excel(name = "预计广告转换单量")
    private Long ad_tarnsCount;

    /** 预计广告点击率 */
    @Excel(name = "预计广告点击率")
    private String ad_clickRate;

    /** 预计点击 */
    @Excel(name = "预计点击")
    private Long ad_click;

    /** 预计曝光率 */
    @Excel(name = "预计曝光率")
    private String ad_exposureRate;

    /** 预计广告费 */
    @Excel(name = "预计广告费")
    private Long ad_fee;

    /** 平均每天广告销量 */
    @Excel(name = "平均每天广告销量")
    private Long ad_perCount;

    /** 平均每天广告点击 */
    @Excel(name = "平均每天广告点击")
    private Long ad_perClick;

    /** 平均每天广告费用 */
    @Excel(name = "平均每天广告费用")
    private Long ad_perFee;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSell_month() {
        return sell_month;
    }

    public void setSell_month(String sell_month) {
        this.sell_month = sell_month;
    }

    public Long getSell_count() {
        return sell_count;
    }

    public void setSell_count(Long sell_count) {
        this.sell_count = sell_count;
    }

    public Long getSell_amount() {
        return sell_amount;
    }

    public void setSell_amount(Long sell_amount) {
        this.sell_amount = sell_amount;
    }

    public String getSell_profitRate() {
        return sell_profitRate;
    }

    public void setSell_profitRate(String sell_profitRate) {
        this.sell_profitRate = sell_profitRate;
    }

    public Long getSell_profit() {
        return sell_profit;
    }

    public void setSell_profit(Long sell_profit) {
        this.sell_profit = sell_profit;
    }

    public Double getAd_perPrice() {
        return ad_perPrice;
    }

    public void setAd_perPrice(Double ad_perPrice) {
        this.ad_perPrice = ad_perPrice;
    }

    public String getAd_tarnsRate() {
        return ad_tarnsRate;
    }

    public void setAd_tarnsRate(String ad_tarnsRate) {
        this.ad_tarnsRate = ad_tarnsRate;
    }

    public Long getAd_tarnsCount() {
        return ad_tarnsCount;
    }

    public void setAd_tarnsCount(Long ad_tarnsCount) {
        this.ad_tarnsCount = ad_tarnsCount;
    }

    public String getAd_clickRate() {
        return ad_clickRate;
    }

    public void setAd_clickRate(String ad_clickRate) {
        this.ad_clickRate = ad_clickRate;
    }

    public Long getAd_click() {
        return ad_click;
    }

    public void setAd_click(Long ad_click) {
        this.ad_click = ad_click;
    }

    public String getAd_exposureRate() {
        return ad_exposureRate;
    }

    public void setAd_exposureRate(String ad_exposureRate) {
        this.ad_exposureRate = ad_exposureRate;
    }

    public Long getAd_fee() {
        return ad_fee;
    }

    public void setAd_fee(Long ad_fee) {
        this.ad_fee = ad_fee;
    }

    public Long getAd_perCount() {
        return ad_perCount;
    }

    public void setAd_perCount(Long ad_perCount) {
        this.ad_perCount = ad_perCount;
    }

    public Long getAd_perClick() {
        return ad_perClick;
    }

    public void setAd_perClick(Long ad_perClick) {
        this.ad_perClick = ad_perClick;
    }

    public Long getAd_perFee() {
        return ad_perFee;
    }

    public void setAd_perFee(Long ad_perFee) {
        this.ad_perFee = ad_perFee;
    }

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
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
