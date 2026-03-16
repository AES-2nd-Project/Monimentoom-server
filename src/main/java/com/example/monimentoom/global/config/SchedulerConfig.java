package com.example.monimentoom.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class SchedulerConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.setScheduler(scheduledExecutor());
    }

    @Bean(destroyMethod = "shutdown")   // 앱 재시작할 때 스레드 남아있지 않도록 처리
    public ExecutorService scheduledExecutor() {
        return Executors.newScheduledThreadPool(1);
    }
}