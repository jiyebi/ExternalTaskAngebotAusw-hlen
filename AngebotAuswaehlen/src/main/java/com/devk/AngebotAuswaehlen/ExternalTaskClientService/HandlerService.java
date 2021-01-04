package com.devk.AngebotAuswaehlen.ExternalTaskClientService;

import com.devk.AngebotAuswaehlen.Model.Angebot;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.backoff.ExponentialBackoffStrategy;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class HandlerService implements ExternalTaskHandler {
    private Angebot bestOffer = new Angebot();
    private final String logString = "The best Offer is:";
    final ExponentialBackoffStrategy fetchTimer = new ExponentialBackoffStrategy(500L, 2, 500L);
    int maxTasksToFetchWithinOnRequest = 1;


    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        log.info("Das beste Angebot wird eingeholt!!!");
                    try {
                        String bestOffersJson = externalTask.getVariable("threeBestOffers");
                        log.info("BestOffersJson as Json String received...\n" + " " + bestOffersJson);
                        final ObjectMapper mapper = new ObjectMapper();
                        List<Angebot> threeBestOffersList = Arrays.asList(mapper.readValue(bestOffersJson, Angebot[].class));
                        log.debug("BestOffersJson Json String converted to a List containing offers..." );

                        Map<String, Object> myMap = new HashMap<>();
                        switch (threeBestOffersList.size()){
                            case 0:
                                log.info("offer list is empty!");
                                externalTaskService.handleFailure(externalTask, externalTask.getId(), "Es wurde kein Angebot zum Auswerten empfangen", 0, 1000L);
                                break;
                            case 1:
                                log.info(logString + " " + threeBestOffersList.get(0).toString());
                                myMap.put("bestOffer", threeBestOffersList.get(0));
                                externalTaskService.complete(externalTask, myMap);
                                break;
                            default:
                                Angebot chosenOffer =
                                        Collections.min(threeBestOffersList,
                                                Comparator.comparing(angebot -> angebot.getGesamtpreis()));
                                bestOffer = chosenOffer; }
                        myMap.put("bestOffer", bestOffer);
                        log.info(logString + " " + bestOffer.toString());
                        externalTaskService.complete(externalTask, myMap);

                    } catch (Exception e) {
                        log.error("Fehler: ", e);
                        externalTaskService.handleFailure(externalTask, externalTask.getId(), e.getMessage(), 1, 100L);
                    }

                }
    }


