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

@Component
public class schedulerServices {

    private static final Logger logger = Logger.getLogger(schedulerServices.class.getName());

    @Autowired
    private TwitterSearchService twitterSearchService;

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

                // Simula o envio de um tweet
                String postResult = twitterPostService.postarTweet("Teste de agendamento com Spring AI");
                logger.info("Resultado do post: " + postResult);

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Erro ao pesquisar Tweet", e);
                Thread.currentThread().interrupt();
            }
            logger.info("\n Tarefa " + jobName + " concluída após " + sleepTime + "ms.");
        }

        protected void executeTaskDeep(String jobName) {
            logger.info("\n Iniciando tarefa profunda: " + jobName);
            int sleepTime = 1000;
            try {
                 RestTemplate restTemplate = new RestTemplate();
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", "deepseek-chat");
                requestBody.put("max_tokens", 1000);
                requestBody.put("reset_context", true);  


             
                 List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "Crie o tweet."),
Map.of("role", "user", "content", 
    "Gere um post criativo para redes sociais (Twitter/X) sem carater especial, sem \\\"\\\"\\\",  tema FITNESS com as propriedades abaixo:\n" +
    "1. Texto de 280 caracteres com chamada para ação.  \n" +
    "2. Hashtags misturando tendências e nicho\n" +
    "3. Tom e Estilo STORYTELLING e OPINIÃO POLÊMICA para gerar engajamento.\n" +
    "4. Evite clichês genéricos;\n" +
    "5. 100% HUMANO\n\n" +
    "Me retorne apenas o TEXTO do tweet, sem formatação especial, sem aspas ou caracteres especiais, apenas o conteúdo do tweet. " +
    "Não inclua emojis ou links. Apenas o texto do tweet, sem formatação especial, sem aspas ou caracteres especiais, apenas o conteúdo do tweet."
)
                
        );
                requestBody.put("messages", messages);
                requestBody.put("temperature", 0.4);
                requestBody.put("max_tokens", 200);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(apikey);
                
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

                ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
          
                if (response.getStatusCode() == HttpStatus.OK) {
                    Map<String, Object> responseBody = response.getBody();
                     List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                      Map<String, Object> firstChoice = choices.get(0);
                      Map<String, String> message = (Map<String, String>) firstChoice.get("message");
                    logger.info("Resultado da API DeepSeek: " + message.get("content"));

                    twitterPostService.postarTweet(message.get("content"));
                } else {
                    logger.warning("Erro na chamada da API DeepSeek: " + response.getStatusCode());
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Erro API DeepSeek", e);
                Thread.currentThread().interrupt();
            }
            logger.info("\n Tarefa profunda " + jobName + " concluída após " + sleepTime + "ms.");
        }
    }

    public class MeuJobTeste extends BaseJob {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            executeTaskDeep("MeuJobTeste");
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
            int execucoes = 3 + (int)(Math.random() * 2);
            for (int i = 0; i < execucoes; i++) {
                int hour = getRandomHourForSlot(i);
                int minute = (int)(Math.random() * 60);
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
            case 0: return 5 + (int)(Math.random() * 2);
            case 1: return 10 + (int)(Math.random() * 2);
            case 2: return 16 + (int)(Math.random() * 2);
            case 3: return 20 + (int)(Math.random() * 2);
            default: return 12;
        }
    }
    }
}
