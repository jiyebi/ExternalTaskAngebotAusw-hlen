package com.devk.AngebotAuswaehlen.ExternalTaskClientService;

import com.devk.AngebotAuswaehlen.Model.Angebot;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.backoff.ExponentialBackoffStrategy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class HandlerService {
    private Angebot bestOffer = new Angebot();
    ExponentialBackoffStrategy fetchTimer = new ExponentialBackoffStrategy(500L, 2, 500L);
    int maxTasksToFetchWithinOnRequest = 1;

    public ExternalTaskClient getExternalTaskClient(){
       ExternalTaskClient externalTaskClient = ExternalTaskClient
            .create()
            .baseUrl("http://localhost:8080/engine-rest")
            .maxTasks(3).backoffStrategy(fetchTimer)
            .build();
      return externalTaskClient;
}

    public void selectTheBestOffer(){
        log.info("Das beste Angebot wird eingeholt!!!");
          getExternalTaskClient()
            .subscribe("training_angebot_waehlen")
            .handler((externalTask, externalTaskService) -> {

                try {
        String bestOffersJson = externalTask.getVariable("threeBestOffers");
        log.info("this is a test!!!!!!!" + " " + bestOffersJson);
        final ObjectMapper mapper = new ObjectMapper();
       // List<Angebot> threeBestOffersList = mapper.readValue(bestOffersJson, new TypeReference<List<Angebot>>() {});
        List<Angebot> threeBestOffersList = Arrays.asList(mapper.readValue(bestOffersJson, Angebot[].class));
        log.debug("the list of three best offers received by the ExternalTaskClient_AngebotAuswählen!!!!!!" + " " + bestOffersJson );


        for (int i = 0; i<3; i++) { Angebot chosenOffer =
            Collections.min(threeBestOffersList,
            Comparator.comparing(s -> s.getGesamtpreis()));
            bestOffer = chosenOffer; }


        Map<String, Object> myMap = new HashMap<String, Object>();
        myMap.put("bestOffer", bestOffer);
        log.info("Offer with the lowest total cost amongst all three offers is: " + " " + bestOffer.toString());
        externalTaskService.complete(externalTask, myMap);

    } catch (Exception e) {
        log.error("Fehler: ", e);
        externalTaskService.handleFailure(externalTask, externalTask.getId(), e.getMessage(), 1, 100L);
          }

    }).open();

}

}
