package io.choerodon.test.manager.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestDataLogDTO;

/**
 * @author zhaotianxin
 * @since 2019/11/18
 */
public interface TestDataLogMapper extends BaseMapper<TestDataLogDTO> {
    void batchInsert(@Param("testDataLogDTOList") List<TestDataLogDTO> testDataLogDTOList);
}
