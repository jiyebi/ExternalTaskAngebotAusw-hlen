package com.devk.AngebotAuswaehlen.ExternalTaskClient;

import com.devk.AngebotAuswaehlen.ExternalTaskClientService.HandlerService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class HandlerConfiguration {

    private final HandlerService handlerService;

    public HandlerConfiguration(HandlerService handlerService) {
        this.handlerService = handlerService;
    }

    @Bean
    public void createTopicSubscriberHandler() {

        // Erstellen des Workers
            ExternalTaskClient externalTaskClient = ExternalTaskClient.create() //
                    // Url vom Worker
                    .baseUrl("http://localhost:8080/engine-rest")
                    // Maximale parallele Tasks
                    .maxTasks(5)
                    .build();

            externalTaskClient
                    // Topic vom Worker
                    .subscribe("training_angebot_waehlen")
                    // Aufruf vom Service
                    .handler(handlerService)
                    // Starten des Workers
                    .open();

            log.info("Camunda Worker is Ready!");
        }

    }
