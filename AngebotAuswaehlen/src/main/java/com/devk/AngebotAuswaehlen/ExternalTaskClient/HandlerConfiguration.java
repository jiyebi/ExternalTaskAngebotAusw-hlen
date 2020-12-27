package com.devk.AngebotAuswaehlen.ExternalTaskClient;

import com.devk.AngebotAuswaehlen.ExternalTaskClientService.HandlerService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class HandlerConfiguration {
    private final Logger logger = LoggerFactory.getLogger(HandlerConfiguration.class);

    private final HandlerService handlerService;

    public HandlerConfiguration(HandlerService handlerService) {
        this.handlerService = handlerService;
    }

    @Bean
    public void createTopicSubscriberHandler() { handlerService.selectTheBestOffer(); }
}