package com.devk.AngebotAuswaehlen.Model;

import lombok.*;


@Builder
@Setter@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Angebot {

    private long id;

    private String name;

    private String beschreibung;

    private String adresse;

    private long einzelpreis;

    private long gesamtpreis;


}
