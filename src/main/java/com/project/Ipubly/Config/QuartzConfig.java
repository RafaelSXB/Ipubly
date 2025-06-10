package com.project.Ipubly.Config;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import com.project.Ipubly.Services.schedulerService.*;

@Configuration
public class QuartzConfig {

/*
    public JobDetail jobDetailTeste() {
        return JobBuilder.newJob(MeuJobTeste.class)
                .withIdentity("jobTeste")
                .storeDurably()
                .build();
    }


    public JobDetail refresherJobDetail() {
        return JobBuilder.newJob(TriggerRefresherJob.class)
                .withIdentity("refresherJob")
                .storeDurably()
                .build();
    }


    public Trigger refresherTrigger(JobDetail refresherJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(refresherJobDetail)
                .withIdentity("refresherTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 0))
                .build();
            }

    public Trigger startTrigger(@Qualifier("refresherJobDetail") JobDetail refreserJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(refreserJobDetail)
                .withIdentity("startTrigger").startNow()
                .build();
    }


    public Trigger startTriggeTester(@Qualifier("jobDetailTeste") JobDetail jobDetailTeste) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetailTeste)
                .withIdentity("startTrigger2").startNow()
                .build();
    }
 */
}
