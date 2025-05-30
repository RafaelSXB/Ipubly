package com.project.Ipubly.Services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.Ipubly.Services.schedulerServices.BaseJob;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.TriggerBuilder;
import org.quartz.CronScheduleBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.LoggerFactory;
import com.project.Ipubly.Services.GeminiApiService;

@Component
public class schedulerServices {

    private static final Logger logger = Logger.getLogger(schedulerServices.class.getName());

    @Autowired
    private TwitterSearchService twitterSearchService;

    @Autowired
    private GeminiApiService geminiApiService;

    @Autowired
    private TwitterPostService twitterPostService;

    @Value("${deepseek.apikey}")
    private String apikey;

    @Value("${deepseek.base-url}")
    private String url;

    public abstract class BaseJob implements Job {
        protected void executeTask(String jobName) {
            logger.info("\n Iniciando tarefa: " + jobName);
            int sleepTime = 1000;
            try {

                String postResult = twitterPostService.postarTweet("Teste de agendamento com Spring AI");
                logger.info("Resultado do post: " + postResult);

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Erro ao pesquisar Tweet", e);
                Thread.currentThread().interrupt();
            }
            logger.info("\n Tarefa " + jobName + " concluída após " + sleepTime + "ms.");
        }
    }

    protected void executeTaskPostGemininiTwitter(String jobName) {
        logger.info("\n Iniciando tarefa: " + jobName);
        try {
            twitterPostService.postarTweet(geminiApiService.generatePostGeminini());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro API DeepSeek", e);
            Thread.currentThread().interrupt();
        }
        logger.info("\n Tarefa profunda " + jobName + " concluída após " + "ms.");
    }

    public class MeuJobTeste extends BaseJob {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            executeTaskPostGemininiTwitter("MeuJobTeste");
        }
    }

    public class TriggerRefresherJob extends BaseJob {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            Scheduler scheduler = context.getScheduler();

            try {
                JobKey targetJobKey = JobKey.jobKey("jobTeste");

                // Remove triggers existentes
                for (TriggerKey triggerKey : scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup())) {
                    if (triggerKey.getName().startsWith("trigger")) {
                        scheduler.unscheduleJob(triggerKey);
                    }
                }

                // Recria novas triggers com horários diferentes
                int execucoes = 3 + (int) (Math.random() * 2);
                for (int i = 0; i < execucoes; i++) {
                    int hour = getRandomHourForSlot(i);
                    int minute = (int) (Math.random() * 60);
                    String cron = String.format("0 %d %d ? * *", minute, hour);

                    logger.info("Nova trigger para jobTeste: " + cron);

                    Trigger trigger = TriggerBuilder.newTrigger()
                            .withIdentity("trigger" + i)
                            .forJob(targetJobKey)
                            .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                            .build();

                    scheduler.scheduleJob(trigger);
                }

            } catch (SchedulerException e) {
                throw new JobExecutionException("Erro ao recriar triggers", e);
            }
        }

        private int getRandomHourForSlot(int slot) {
            switch (slot) {
                case 0:
                    return 5 + (int) (Math.random() * 2);
                case 1:
                    return 10 + (int) (Math.random() * 2);
                case 2:
                    return 16 + (int) (Math.random() * 2);
                case 3:
                    return 20 + (int) (Math.random() * 2);
                default:
                    return 12;
            }
        }
    }
}
