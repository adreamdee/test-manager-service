package io.choerodon.test.manager.api.vo;

import java.util.List;

/**
 * @author: 25499
 * @date: 2019/11/14 11:25
 * @description:
 */
public class TestTreeIssueFolderVO {
    private List<Long> rootIds;
    private List<TestTreeFolderVO> treeFolder;

    public TestTreeIssueFolderVO(List<Long> rootIds, List<TestTreeFolderVO> treeFolder) {
        this.rootIds = rootIds;
        this.treeFolder = treeFolder;
    }

    public List<Long> getRootIds() {
        return rootIds;
    }

    public void setRootIds(List<Long> rootIds) {
        this.rootIds = rootIds;
    }

    public List<TestTreeFolderVO> getTreeFolder() {
        return treeFolder;
    }

    public void setTreeFolder(List<TestTreeFolderVO> treeFolder) {
        this.treeFolder = treeFolder;
    }

    public TestTreeIssueFolderVO() {
    }
}
