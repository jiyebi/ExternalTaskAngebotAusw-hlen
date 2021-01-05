package com.devk.AngebotAuswaehlen.Model;

import lombok.*;

import java.math.BigDecimal;


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

    private BigDecimal einzelpreis;

    private BigDecimal gesamtpreis;


}
