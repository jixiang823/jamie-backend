package com.jamie.dao;

import com.jamie.vo.TableVo;
import com.jamie.pojo.TestCaseInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TestCaseInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TestCaseInfo row);

    int batchInsert(@Param("caseInfoList") List<TestCaseInfo> caseInfoList); // 避免循环体内操作数据库

    List<Integer> selectIdByBatchNo(String batchNo); // 根据batchNo拿到caseId集合(ASC排列)

    int insertSelective(TestCaseInfo row);

    TestCaseInfo selectByPrimaryKey(Integer id);

    List<TableVo> page(Map<String, Object> queryKeywords); // 分页多条件查询

    int updateByPrimaryKeySelective(TestCaseInfo row);

    int updateByPrimaryKey(TestCaseInfo row);

    int updateBatch(List<TestCaseInfo> testCaseInfos);

}