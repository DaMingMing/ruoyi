package com.ruoyi.activiti.service;

import com.github.pagehelper.Page;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.StringUtils;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Service
public class ProcessDefinitionService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 分页查询流程定义文件
     * @return
     */
    public Page<com.ruoyi.activiti.domain.ProcessDefinition> listProcessDefinition(com.ruoyi.activiti.domain.ProcessDefinition processDefinition) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();

        Page<com.ruoyi.activiti.domain.ProcessDefinition> list = new Page<>();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.orderByProcessDefinitionId().orderByProcessDefinitionVersion().desc();
        if (StringUtils.isNotBlank(processDefinition.getName())) {
            processDefinitionQuery.processDefinitionNameLike("%" + processDefinition.getName() + "%");
        }
        if (StringUtils.isNotBlank(processDefinition.getKey())) {
            processDefinitionQuery.processDefinitionKeyLike("%" + processDefinition.getKey() + "%");
        }
        if (StringUtils.isNotBlank(processDefinition.getCategory())) {
            processDefinitionQuery.processDefinitionCategoryLike("%" + processDefinition.getCategory() + "%");
        }

        List<ProcessDefinition> processDefinitionList;
        if (pageNum != null && pageSize != null) {
            processDefinitionList = processDefinitionQuery.listPage((pageNum - 1) * pageSize, pageSize);
            list.setTotal(processDefinitionQuery.count());
            list.setPageNum(pageNum);
            list.setPageSize(pageSize);
        } else {
            processDefinitionList = processDefinitionQuery.list();
        }
        for (ProcessDefinition definition: processDefinitionList) {
            com.ruoyi.activiti.domain.ProcessDefinition entity = new com.ruoyi.activiti.domain.ProcessDefinition();
            entity.setId(definition.getId());
            entity.setKey(definition.getKey());
            entity.setName(definition.getName());
            entity.setCategory(definition.getCategory());
            entity.setVersion(definition.getVersion());
            entity.setDescription(definition.getDescription());
            entity.setDeploymentId(definition.getDeploymentId());
            Deployment deployment = repositoryService.createDeploymentQuery()
                    .deploymentId(definition.getDeploymentId())
                    .singleResult();
            entity.setDeploymentTime(deployment.getDeploymentTime());
            list.add(entity);
        }
        return list;
    }

    @Transactional
    public void deployProcessDefinition(String filePath) throws FileNotFoundException {
        if (StringUtils.isNotBlank(filePath)) {
            if (filePath.endsWith(".zip")) {
                ZipInputStream inputStream = new ZipInputStream(new FileInputStream(filePath));
                repositoryService.createDeployment()
                        .addZipInputStream(inputStream)
                        .deploy();
            } else if (filePath.endsWith(".bpmn")) {
                repositoryService.createDeployment()
                        .addInputStream(filePath, new FileInputStream(filePath))
                        .deploy();
            }
        }
    }

    @Transactional
    public int deleteProcessDeploymentByIds(String deploymentIds) {
        String[] deploymentIdsArr = Convert.toStrArray(deploymentIds);
        int counter = 0;
        for (String deploymentId: deploymentIdsArr) {
            List<ProcessInstance> instanceList = runtimeService.createProcessInstanceQuery()
                    .deploymentId(deploymentId)
                    .list();
            if (!CollectionUtils.isEmpty(instanceList)) continue;   // 跳过存在流程实例的流程定义
            repositoryService.deleteDeployment(deploymentId, true); // true 表示级联删除引用，比如 act_ru_execution 数据
            counter++;
        }
        return counter;
    }

}
