package io.choerodon.test.manager.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.api.dto.ProductVersionDTO
import io.choerodon.agile.api.dto.ProductVersionPageDTO
import io.choerodon.core.domain.Page
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.TestCycleDTO
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE
import io.choerodon.test.manager.infra.mapper.TestCycleMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestCycleControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    TestCaseService testCaseService

    @Autowired
    TestCycleMapper testCycleMapper

    @Shared
    def projectId = 1L
    @Shared
    def versionId = 1L
    @Shared
    List<TestCycleDTO> testCycleDTOS = new  ArrayList<>()



    def "Insert"() {
        given:
        def res = testCycleMapper.selectAll()

        TestCycleDTO testCycleDTO1 = new TestCycleDTO()
        testCycleDTO1.setCycleName("testCycleInsert")
        testCycleDTO1.setVersionId(versionId)
        testCycleDTO1.setType(TestCycleE.CYCLE)
        testCycleDTO1.setObjectVersionNumber(1L)

        testCycleDTOS.add(testCycleDTO1)

        TestCycleDTO testCycleDTO2 = new TestCycleDTO()
        testCycleDTO2.setCycleName("testFolderInsert")
        testCycleDTO2.setFolderId(11L)
        testCycleDTO2.setVersionId(versionId)
        testCycleDTO2.setType(TestCycleE.FOLDER)
        testCycleDTO2.setObjectVersionNumber(1L)

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle', testCycleDTOS.get(0),TestCycleDTO, projectId)
        then:
        entity.statusCode.is2xxSuccessful()
        and:
        entity.body != null
        entity.body.folderId == null
        entity.body.type == TestCycleE.CYCLE
        and:
        testCycleDTOS.get(0).setCycleId(entity.getBody().getCycleId())
        and:
        testCycleDTO2.setParentCycleId(testCycleDTOS.get(0).getCycleId())
        testCycleDTOS.add(testCycleDTO2)

        when:
        entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle', testCycleDTOS.get(1),TestCycleDTO, projectId)
        then:
        entity.statusCode.is2xxSuccessful()
        and:
        entity.body != null
        entity.body.folderId == 11L
        entity.body.type == TestCycleE.FOLDER
        and:
        testCycleDTOS.get(1).setCycleId(entity.getBody().getCycleId())
    }

    def "Update"() {
        given:
        testCycleDTOS.get(0).setCycleName("testCycleUpdate")
        testCycleDTOS.get(1).setCycleName("testFolderUpdate")

        when:
        HttpEntity<TestCycleDTO> requestEntity = new HttpEntity<TestCycleDTO>(testCycleDTOS.get(0), null)
        def entity = restTemplate.exchange('/v1/projects/{project_id}/cycle',
                HttpMethod.PUT, requestEntity, TestCycleDTO, projectId)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.getCycleName() == "testCycleUpdate"
        entity.body.type == TestCycleE.CYCLE

        when:
        requestEntity = new HttpEntity<TestCycleDTO>(testCycleDTOS.get(1), null)
        entity = restTemplate.exchange('/v1/projects/{project_id}/cycle',
                HttpMethod.PUT, requestEntity, TestCycleDTO, projectId)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.getCycleName() == "testFolderUpdate"
        entity.body.type == TestCycleE.FOLDER
    }

    def "QueryOne"() {
        when:
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/cycle/query/one/{cycleId}',TestCycleDTO,projectId, testCycleDTOS.get(0).getCycleId())
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.cycleName == "testCycleUpdate"
    }

    def "GetTestCycle"() {
        given:
        ProductVersionDTO productVersionDTO = new ProductVersionDTO()
        productVersionDTO.setVersionId(1L)
        productVersionDTO.setStatusName("testCycle")
        productVersionDTO.setName("testCycle")

        ProductVersionDTO productVersionDTO2 = new ProductVersionDTO()
        productVersionDTO2.setVersionId(2L)
        productVersionDTO2.setName("testCycle")

        Map map = new HashMap()
        map.put(1L,productVersionDTO)
        map.put(2L,productVersionDTO2)

        when:
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/cycle/query",JSONObject.class,projectId,testCycleDTOS.get(0).getCycleId())
        then:
        1*testCaseService.getVersionInfo(_)>>map
        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject jsonObject = entity.body

        expect:
        !jsonObject.isEmpty()

        when:
        entity = restTemplate.getForEntity("/v1/projects/{project_id}/cycle/query",JSONObject.class,projectId,testCycleDTOS.get(0).getCycleId())
        then:
        1*testCaseService.getVersionInfo(_)>>new HashMap<>()
        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject jsonObject2 = entity.body

        expect:
        jsonObject2.isEmpty()
    }

    def "GetTestCycleVersion"() {
        given:
        Map<String, Object> searchParamMap = new HashMap<>()
        searchParamMap.put("cycleName", "发布11")

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/query/version', searchParamMap,Page.class, 12L)
        then: '返回值'
        1 * testCaseService.getTestCycleVersionInfo(_, _) >> new ResponseEntity<Page<ProductVersionPageDTO>>(HttpStatus.OK)
    }

    def "CloneCycle"() {
        given:
        TestCycleDTO testCycleDTO = new TestCycleDTO()
        testCycleDTO.setVersionId(99L)
        testCycleDTO.setCycleName("cloneCycleTest")

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/clone/folder/{cycleId}', testCycleDTO,TestCycleDTO, testCycleDTOS.get(0).getCycleId(),projectId)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.versionId == 99L
        entity.body.cycleName == "cloneCycleTest"
    }

    def "CloneFolder"() {
        given:
        TestCycleDTO testCycleDTO = new TestCycleDTO()
        testCycleDTO.setCycleName("cloneCycleFolderTest")

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/clone/folder/{cycleId}', testCycleDTO,TestCycleDTO, testCycleDTOS.get(1).getCycleId(),projectId)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.cycleName == "cloneCycleFolderTest"
    }

    def "GetFolderByCycleId"() {
        given:
        TestCycleDTO testCycleDTO = new TestCycleDTO()
        testCycleDTO.setCycleName("cloneCycleFolderTest")

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/query/folder/cycleId/{cycleId}', null,List, projectId,testCycleDTOS.get(0).getCycleId())
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.size() == 1
        List<TestCycleDTO> list = entity.body
        list.get(0).cycleName == "testFolderUpdate"
    }

    def "SynchroFolder"() {
        given:
        TestCycleDTO testCycleDTO = new TestCycleDTO()
        testCycleDTO.setCycleName("cloneCycleFolderTest")

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/synchro/folder/{folderId}/in/{cycleId}', null,boolean, projectId,testCycleDTOS.get(1).getCycleId(),testCycleDTOS.get(1).getFolderId())
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
    }
//
//    def "SynchroFolder1"() {
//    }
//
//    def "SynchroFolderInVersion"() {
//    }
//
//    def "Delete"() {
//    }
}