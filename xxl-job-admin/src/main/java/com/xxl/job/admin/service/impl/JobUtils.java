package com.xxl.job.admin.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class JobUtils {
    public final static ConcurrentHashMap<Integer,List<Integer>> childJobParentIdMap=new ConcurrentHashMap<>();
    public final static ConcurrentHashMap<Integer,List<Integer>> parentIdChildMap=new ConcurrentHashMap<>();


    /**
     * 用于子任务执行日志的parentId字段设值
     * @param childId
     * @param parentId
     */
    public static void putParentId(Integer childId,Integer parentId){
        putToMap(childId, parentId, childJobParentIdMap);
        putToMap(parentId,childId, parentIdChildMap);
    }

    public static Integer getParentId(Integer childId){
        List<Integer> parentIds= childJobParentIdMap.get(childId);
        if(parentIds==null || parentIds.size()==0){
            return null;
        }
        synchronized (parentIds){
            if(parentIds.size()==0){
                return null;
            }
            return parentIds.remove(0);
        }
    }

    /**
     * 移除已执行的子任务
     * @param parentId
     * @param childId
     * @return
     */
    public static boolean removeChildId(Integer parentId,Integer childId){
        List<Integer> childIds= parentIdChildMap.get(parentId);
        if(childIds==null || childIds.size()==0){
            return true;
        }
        synchronized (childIds){
            childIds.remove(childIds.indexOf(childId));
            return childIds.size()==0;
        }
    }

    private static void putToMap(Integer childId, Integer parentId, ConcurrentHashMap<Integer, List<Integer>> map) {
        List<Integer> parentIds=map.get(childId);
        if(parentIds==null){
            parentIds = new ArrayList<>();
            List<Integer> list=map.putIfAbsent(childId,parentIds);
            if(list!=null){
                parentIds=list;
            }
        }
        parentIds.add(parentId);
    }
}
