package com.flextalk.we.cmmn.configure;

import com.flextalk.we.cmmn.util.RoutingDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static com.flextalk.we.cmmn.util.DataSourceType.*;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.flextalk.we"})
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class DataSourceConfig {

    @Bean(name = "masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "slaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "routingDataSource")
    public DataSource routingDataSource() {
        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSources = new HashMap<>();
        dataSources.put(MASTER, masterDataSource());
        dataSources.put(SLAVE, slaveDataSource());
        routingDataSource.setDefaultTargetDataSource(masterDataSource());
        routingDataSource.setTargetDataSources(dataSources);
        return routingDataSource;
    }

    @Primary
    @Bean(name = "dataSource")
    @DependsOn({"masterDataSource", "slaveDataSource"})
    public DataSource dataSource() {
        return new LazyConnectionDataSourceProxy(routingDataSource());
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier(value = "masterDataSource") DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }
}
