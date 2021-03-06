package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.HistoricActivity;

import java.util.List;

public interface IProcessService {

    /**
     * 查询审批历史列表
     * @param processInstanceId
     * @param historicActivity
     * @return
     */
    List<HistoricActivity> selectHistoryList(String processInstanceId, HistoricActivity historicActivity);

}
