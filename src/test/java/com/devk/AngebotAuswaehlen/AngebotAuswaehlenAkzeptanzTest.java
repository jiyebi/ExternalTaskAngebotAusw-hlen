package com.devk.AngebotAuswaehlen;

import com.devk.AngebotAuswaehlen.ExternalTaskClient.HandlerConfiguration;
import com.devk.AngebotAuswaehlen.ExternalTaskClientService.HandlerService;
import com.devk.AngebotAuswaehlen.Model.Angebot;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.de.Angenommen;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Und;
import io.cucumber.java.de.Wenn;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.equalTo;

public class AngebotAuswaehlenAkzeptanzTest {

    ExternalTask mockExternalTask = Mockito.mock(ExternalTask.class);
    ExternalTaskService mockExternalTaskService = Mockito.mock(ExternalTaskService.class);

    ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

    HandlerService handlerService = new HandlerService();


    @Angenommen("es gibt den folgenden Versandauftrag")
    public void es_gibt_den_folgenden_versandauftrag(DataTable dataTable) {
        List<Map<String, String>> dataMapList = dataTable.asMaps(String.class, String.class);
        long Groesse = Long.parseLong(dataMapList.get(0).get("Größe"));
        long Anzahl = Long.parseLong(dataMapList.get(0).get("Anzahl"));
        double Geldwert = Double.parseDouble(dataMapList.get(0).get("Geldwert"));
        Mockito.when(mockExternalTask.getVariable("Größe")).thenReturn(Groesse);
        Mockito.when(mockExternalTask.getVariable("Anzahl")).thenReturn(Anzahl);
        Mockito.when(mockExternalTask.getVariable("Geldwert")).thenReturn(Geldwert);

    }
    @Und("es gibt kein Angebot")
    public void es_gibt_kein_angebot() {
        Mockito.when(mockExternalTask.getVariable("threeBestOffers")).thenReturn(new ArrayList<Angebot>());
    }

    @Wenn("ich den Task {string} aufrufe")
    public void ich_den_task_aufrufe(String string) {
    Mockito.when(mockExternalTask.getId()).thenReturn("i'am an ID");
    Mockito.doNothing().when(mockExternalTaskService).complete(Mockito.eq(mockExternalTask), captor.capture());
    handlerService.execute(mockExternalTask,mockExternalTaskService);

    }

    @Dann("erwarte ich die Fehlermeldung {string}.")
    public void erwarte_ich_die_fehlermeldung(String string) {
    Mockito.verify(mockExternalTaskService, Mockito.times(1))
            .handleFailure(Mockito.eq(mockExternalTask), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyLong());
    }

    @Und("es gibt das folgende Angebot")
    @Und("es gibt die folgenden Angebote")
    public void esGibtDieFolgendenAngebote(DataTable dataTable) {
        List<Map<String, String>> dataMapList = dataTable.asMaps(String.class, String.class);

        List<Angebot> angebotList = new ArrayList<>();

        dataMapList.forEach(stringStringMap -> {
            long id = Long.parseLong(stringStringMap.get("id"));
            BigDecimal Grundkosten = new BigDecimal(stringStringMap.get("Grundkosten"));
            BigDecimal PreisProAnzahl = new BigDecimal(stringStringMap.get("Preis pro Anzahl"));

            Angebot angebot = Angebot.builder().id(id).einzelpreis(PreisProAnzahl).gesamtpreis(Grundkosten).build();

            angebotList.add(angebot);
        });

        Mockito.when(mockExternalTask.getVariable("threeBestOffers")).thenReturn(angebotList);

    }

    @Dann("ist dieses das beste Angebot")
    public void istDiesesDasBesteAngebot(DataTable dataTable) {
        List<Map<String, String>> dataMapList = dataTable.asMaps(String.class, String.class);
        long id = Long.parseLong(dataMapList.get(0).get("id"));
        BigDecimal grundkosten = new BigDecimal(dataMapList.get(0).get("Grundkosten"));
        BigDecimal preisProAnzahl = new BigDecimal(dataMapList.get(0).get("Preis pro Anzahl"));



        Angebot angebot = Angebot.builder().id(id).einzelpreis(preisProAnzahl).gesamtpreis(grundkosten).build();

        Mockito.verify(mockExternalTaskService, Mockito.times(1)).complete(Mockito.eq(mockExternalTask), Mockito.anyMap());

        Map<String, Object> args = captor.getValue();

        assertThat(args.containsKey("bestOffer"), equalTo(true));

        assertThat(((Angebot) args.get("bestOffer")).getId(), equalTo(angebot.getId()));
        assertThat(((Angebot) args.get("bestOffer")).getEinzelpreis(), equalTo(angebot.getEinzelpreis()));
        assertThat(((Angebot) args.get("bestOffer")).getGesamtpreis(), equalTo(angebot.getGesamtpreis()));

    }

}
