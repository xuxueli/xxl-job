package com.xxl.job.admin.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.*;

import java.util.*;

/**
 * Author: Antergone
 * Date: 2017/6/29
 */
@Configuration
public class DataSourceConfig {

    @Bean(destroyMethod = "close")
    @ConfigurationProperties(prefix = "xxl.job.db")
    public ComboPooledDataSource comboPooledDataSource() {
        return new ComboPooledDataSource();
    }

//    @Bean
//    @Qualifier("txAdvice")
//    public TransactionInterceptor transactionInterceptor(PlatformTransactionManager manager) {
//
//        MethodMapTransactionAttributeSource mt1 = new MethodMapTransactionAttributeSource();
//        mt1.addTransactionalMethod("detail*", new DefaultTransactionAttribute(Propagation.SUPPORTS.value()));
//
//        MethodMapTransactionAttributeSource mt2 = new MethodMapTransactionAttributeSource();
//        mt2.addTransactionalMethod("visit*", new DefaultTransactionAttribute(Propagation.SUPPORTS.value()));
//
//        MethodMapTransactionAttributeSource mt3 = new MethodMapTransactionAttributeSource();
//        mt3.addTransactionalMethod("get*", new DefaultTransactionAttribute(Propagation.SUPPORTS.value()));
//
//        MethodMapTransactionAttributeSource mt4 = new MethodMapTransactionAttributeSource();
//        mt4.addTransactionalMethod("find*", new DefaultTransactionAttribute(Propagation.SUPPORTS.value()));
//
//        MethodMapTransactionAttributeSource mt5 = new MethodMapTransactionAttributeSource();
//        mt5.addTransactionalMethod("check*", new DefaultTransactionAttribute(Propagation.SUPPORTS.value()));
//
//        MethodMapTransactionAttributeSource mt6 = new MethodMapTransactionAttributeSource();
//        mt6.addTransactionalMethod("list*", new DefaultTransactionAttribute(Propagation.SUPPORTS.value()));
//
//        MethodMapTransactionAttributeSource mt7 = new MethodMapTransactionAttributeSource();
//        RollbackRuleAttribute rule = new RollbackRuleAttribute("Exception");
//        RuleBasedTransactionAttribute attribute = new RuleBasedTransactionAttribute(Propagation.REQUIRED.value(),Collections.singletonList(rule));
//        mt7.addTransactionalMethod("list*", attribute);
//
//        TransactionAttributeSource[] sources = new TransactionAttributeSource[]{mt1, mt2, mt3, mt4, mt5, mt6, mt7};
//
//        TransactionInterceptor ti = new TransactionInterceptor();
//        ti.setTransactionManager(manager);
//        ti.setTransactionAttributeSources(sources);
//        return ti;
//    }
}
