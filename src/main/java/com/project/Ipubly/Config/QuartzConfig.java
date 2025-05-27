package com.project.Ipubly.Config;

import java.util.ArrayList;
import java.util.List;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.project.Ipubly.Services.schedulerServices.*;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail jobDetailTeste() {
        return JobBuilder.newJob(MeuJobTeste.class)
                .withIdentity("jobTeste")
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail refresherJobDetail() {
        return JobBuilder.newJob(TriggerRefresherJob.class)
                .withIdentity("refresherJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger refresherTrigger(JobDetail refresherJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(refresherJobDetail)
                .withIdentity("refresherTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 0)) // meia-noite
                .build();
            }
    @Bean
    public Trigger startTrigger(@Qualifier("refresherJobDetail") JobDetail refreserJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(refreserJobDetail)
                .withIdentity("startTrigger").startNow() // Inicia imediatamente
                .build();
    }

    @Bean
    public Trigger startTriggeTester(@Qualifier("jobDetailTeste") JobDetail jobDetailTeste) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetailTeste)
                .withIdentity("startTrigger2").startNow() // Inicia imediatamente
                .build();
    }
 
}
