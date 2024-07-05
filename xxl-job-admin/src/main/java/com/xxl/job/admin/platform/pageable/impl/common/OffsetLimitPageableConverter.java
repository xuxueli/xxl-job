package com.xxl.job.admin.platform.pageable.impl.common;

import com.xxl.job.admin.platform.pageable.DatabasePageable;
import com.xxl.job.admin.platform.pageable.IDatabasePageableConverter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ice2Faith
 * @date 2024/7/5 11:19
 * @desc
 */
@Component
public class OffsetLimitPageableConverter implements IDatabasePageableConverter {
    public static final Set<String> SUPPORT_TYPES= Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    "mysql",
                    "postgre",
                    "gbase",
                    "h2",
                    "dm",
                    "kingbase"
            ))
    );
    @Override
    public boolean supportDatabase(String type) {
        return SUPPORT_TYPES.contains(type);
    }

    @Override
    public DatabasePageable converter(int start, int length) {
        return new DatabasePageable(start,length);
    }
}
