package com.xxl.job.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.common.pojo.entity.LockInfo;
import com.xxl.job.admin.mapper.LockInfoMapper;
import com.xxl.job.admin.service.LockInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 任务锁 服务实现类
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Service
public class LockInfoServiceImpl extends ServiceImpl<LockInfoMapper, LockInfo> implements LockInfoService {

}
