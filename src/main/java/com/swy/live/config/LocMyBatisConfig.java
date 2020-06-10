package com.swy.live.config;

import com.extm.SqlSessionFactoryBeanEx;
import com.jds.core.dao.BaseDao;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

@Configuration
public class LocMyBatisConfig {
    @javax.annotation.Resource
    BaseDao baseDao;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Bean(name="dataSource")
    public DataSource dataSource() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(dataSourceProperties.getUrl());
        return dataSource;
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBeanEx();
        sqlSessionFactoryBean.setDataSource(dataSource);

        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] mybatisConfigXml = resolver.getResources("classpath*:com/**/cfg/mybatis.cfg.xml");
            sqlSessionFactoryBean.setConfigLocation(mybatisConfigXml[0]);

            Resource[] mybatisMapperXml = resolver.getResources("classpath*:com/**/sql/*.xml");
            sqlSessionFactoryBean.setMapperLocations(mybatisMapperXml);

            SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactoryBean.getObject());
            baseDao.setSqlSessionTemplate(sqlSessionTemplate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sqlSessionFactoryBean;
    }
}
