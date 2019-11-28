package io.choerodon.test.manager.infra.mapper;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestCycleDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleMapper extends Mapper<TestCycleDTO> {

    List<TestCycleDTO> query(@Param("projectId") Long projectId, @Param("versionIds") Long[] versionId, @Param("assignedTo") Long assignedTo);

    List<TestCycleDTO> queryOneCycleBar(@Param("cycleId") Long cycleId);

    /**
     * 获取version下的所有循环Id
     *
     * @param versionIds
     * @return
     */
    List<Long> selectCyclesInVersions(@Param("versionIds") Long[] versionIds);

    /**
     * 验证version下是否有重名cycle
     *
     * @param testCycleDTO
     * @return
     */
    Long validateCycle(TestCycleDTO testCycleDTO);

    List<TestCycleDTO> queryChildCycle(@Param("dto") TestCycleDTO testCycleDTO);

    List<TestCycleDTO> queryCycleInVersion(@Param("dto") TestCycleDTO testCycleDTO);

    List<TestCycleDTO> queryByIds(@Param("cycleIds") List<Long> cycleIds);

    void updateAuditFields(@Param("cycleIds") Long[] cycleId, @Param("userId") Long userId, @Param("date") Date date);

    String getCycleLastedRank(@Param("versionId") Long versionId);

    String getFolderLastedRank(@Param("cycleId") Long cycleId);

    Long getCycleCountInVersion(@Param("versionId") Long versionId);

    Long getFolderCountInCycle(@Param("cycleId") Long cycleId);

    List<TestCycleDTO> queryChildFolderByRank(@Param("cycleId") Long cycleId);

//    void deleteByType();

    List<TestCycleDTO> selectRepeat();

    void deleteRepeat(@Param("projectId") Long projectId, @Param("versionId") Long versionId, @Param("folderId") Long folderId);

    List<TestCycleDTO> listByPlanIds(@Param("planIds") List<Long> planIds);
    List<Long> selectVersionId();

    void deleteRepeat(@Param("versionId") Long versionId, @Param("folderId") Long folderId);

    void fixPlanId(@Param("versionId") Long versionId, @Param("planId") Long planId);
}
