package com.xxl.job.admin.core.util;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.core.util.FancyTreeNode;

import java.util.ArrayList;
import java.util.List;

public class FileTypeMapUtil {

    /**
     * 单个任务对象转换为树节点对象。
     * 如果有下级节点，将逐级转换
     *
     * @param paramXxlJobInfo
     * @return FancyTree节点数组
     */
    public static FancyTreeNode toTreeNode(XxlJobInfo paramXxlJobInfo) {
        FancyTreeNode curTreeNode = new FancyTreeNode();

        if (paramXxlJobInfo != null) {

            curTreeNode.setKey(paramXxlJobInfo.getId()+"");
            curTreeNode.setTitle(paramXxlJobInfo.getJobDesc());
            curTreeNode.setParent(paramXxlJobInfo.getChildJobId());

            if (paramXxlJobInfo.hasChildren()) {
                curTreeNode.setFolder(true);

                List<FancyTreeNode> childNodeList = new ArrayList<FancyTreeNode>();
                for (XxlJobInfo childFileType : paramXxlJobInfo.getChildren()) {
                    childNodeList.add(toTreeNode(childFileType));
                }

                curTreeNode.setChildren(childNodeList);
            }
        }

        return curTreeNode;
    }

    /**
     * 将文件类型对象集合转换为树节点对象集合。 如果有下级节点，将逐级转换
     *
     * @param paramFileTypeList
     * @return  FancyTree节点数组
     */
    public static List<FancyTreeNode> toTreeNodeList(List<XxlJobInfo> paramFileTypeList) {

        List<FancyTreeNode> curNodeList = new ArrayList<FancyTreeNode>();

        if (paramFileTypeList != null) {
            for (XxlJobInfo childFileType : paramFileTypeList) {
                curNodeList.add(toTreeNode(childFileType));
            }
        }

        return curNodeList;
    }


}

