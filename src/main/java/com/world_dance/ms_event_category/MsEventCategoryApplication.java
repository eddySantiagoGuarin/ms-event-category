package com.world_dance.ms_event_category;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = {
    MongoAutoConfiguration.class, 
    MongoDataAutoConfiguration.class
})
@EntityScan(basePackages = {
    "com.world_dance.ms_event_category",
    "com.world_dance.wd_lib_common.entity"
})
@EnableJpaRepositories(basePackages = {
    "com.world_dance.ms_event_category",
    "com.world_dance.wd_lib_common.repository"
})
public class MsEventCategoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsEventCategoryApplication.class, args);
    }
}