package com.whdcks3.portfolio.gory_server.config;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class EnvConfig {
    private final Dotenv dotenv = Dotenv.load();

    @PostConstruct
    public void init() {
        System.setProperty("spring.datasource.url",
                "jdbc:mysql://" + dotenv.get("DB_HOST") + ":" + dotenv.get("DB_PORT") + "/" + dotenv.get("DB_NAME"));
        System.setProperty("spring.datasource.username", dotenv.get("DB_USER"));
        System.setProperty("spring.datasource.password", dotenv.get("DB_PASSWORD"));

        System.setProperty("aws.s3.bucket=gory", dotenv.get("AWS_BUCKET"));
        System.setProperty("aws.s3.region", dotenv.get("AWS_REGION"));
        System.setProperty("aws.s3.access-key", dotenv.get("AWS_ACCESS_KEY"));
        System.setProperty("aws.s3.secret-key", dotenv.get("AWS_SECRET_KEY"));
    }
}
