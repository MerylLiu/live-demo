package com.swy.live.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

@Configuration
public class LocMyBatisConfig {
    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Bean(name="dataSource")
    public DataSource dataSource() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(dataSourceProperties.getUrl());
        return dataSource;
    }
}
