package com.xxl.job.admin.core.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * jpa 主键生成
 *
 * @author songyinyin
 * @date 2020/3/21 下午 03:49
 */
public class XxlJobGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Serializable id = session.getEntityPersister(null, object)
                .getClassMetadata().getIdentifier(object, session);
        // 当id没有设置时，自动生成
        return (id == null || "".equals(id)) ? XxlJobIdWorker.getId() : id;
    }
}
