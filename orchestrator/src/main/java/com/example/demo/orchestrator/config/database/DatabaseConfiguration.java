package com.example.demo.orchestrator.config.database;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Primary
    @Bean("dataSource")
    public DataSource getDataSource(){
        HikariDataSource hikariDataSource =  DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url("jdbc:postgresql://localhost:5432/test")
                .username("postgres")
                .password("sourabh")
                .build();
        hikariDataSource.setMaximumPoolSize(40); // Get the maximum pool size
        hikariDataSource.setConnectionTimeout(30000); // Set connection timeout to 30 seconds
        return hikariDataSource;
    }

    @Bean("readerDataSource")
    public DataSource getReaderDataSource(){
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url("jdbc:postgresql://localhost:5432/rssagg")
                .username("postgres")
                .password("sourabh")
                .build();
    }


}
