package com.ape.apeadmin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class },scanBasePackages = "com.ape.*")
@ComponentScan("com.ape")
@MapperScan("com.**.mapper")
public class ApeAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApeAdminApplication.class, args);
    }

}
