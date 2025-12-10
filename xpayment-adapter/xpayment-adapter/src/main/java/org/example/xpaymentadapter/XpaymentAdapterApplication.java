package org.example.xpaymentadapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableKafka
@EnableRetry
@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
public class XpaymentAdapterApplication {
    public static void main(String[] args) {
        SpringApplication.run(XpaymentAdapterApplication.class, args);
    }
}
