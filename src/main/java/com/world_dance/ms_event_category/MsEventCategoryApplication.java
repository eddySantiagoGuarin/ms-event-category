package com.world_dance.ms_event_category;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = {
        "com.world_dance.ms_event_category",
        "com.world_dance.wd_lib_common"
    },
    excludeName = {
        "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
        "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration"
    }
)
@EnableJpaRepositories(basePackages = {
    "com.world_dance.ms_event_category.repository",
    "com.world_dance.wd_lib_common.repository"
})
@EntityScan(basePackages = {
    "com.world_dance.ms_event_category.entity",
    "com.world_dance.wd_lib_common.entity"
})
public class MsEventCategoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsEventCategoryApplication.class, args);
    }

}