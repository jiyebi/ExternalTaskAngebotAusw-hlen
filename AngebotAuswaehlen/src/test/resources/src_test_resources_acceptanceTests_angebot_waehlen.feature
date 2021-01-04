  # language: de
  Funktionalität: Aus den geholten Angeboten wird das beste ausgewählt

    Das beste Angebot ist das mit dem niedrigsten Gesamtpreis.
    Gesamtpreis = Grundpreis + (Anzahl der Einheit * Preis pro Einheit)



    Szenario: Wenn kein Angebot geliefert wird, soll abgebrochen werden
      Angenommen es gibt den folgenden Versandauftrag
        | Größe | Anzahl | Geldwert |
        | 5     | 10     | 100      |
      Und es gibt kein Angebot
      Wenn ich den Task "Angebot wählen" aufrufe
      Dann erwarte ich die Fehlermeldung "Es wurde kein Angebot zum Auswerten empfangen".


    Szenario: Wenn nur ein Angebot geliefert wird, dann wird dieses verwendet
      Angenommen es gibt den folgenden Versandauftrag
        | Größe | Anzahl | Geldwert |
        | 5     | 10     | 100      |
      Und es gibt das folgende Angebot
        | id | Grundkosten | Preis pro Anzahl |
        | 1  | 100         | 0.5              |
      Wenn ich den Task "Angebot wählen" aufrufe
      Dann ist dieses das beste Angebot
        | id | Grundkosten | Preis pro Anzahl |
        | 1  | 100         | 0.5              |


    Szenario: Wenn es mehrere Angebote gibt, dann wird das günstigste verwendet
      Angenommen es gibt den folgenden Versandauftrag
        | Größe | Anzahl | Geldwert |
        | 5     | 10     | 100      |
      Und es gibt die folgenden Angebote
        | id | Grundkosten | Preis pro Anzahl |
        | 1  | 1000        | 1                |
        | 2  | 100         | 0.5              |
        | 3  | 3000        | 2                |
      Wenn ich den Task "Angebot wählen" aufrufe
      Dann ist dieses das beste Angebot
        | id | Grundkosten | Preis pro Anzahl |
        | 2  | 100         | 0.5              |
