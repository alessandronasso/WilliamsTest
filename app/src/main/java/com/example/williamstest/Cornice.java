package com.example.williamstest;

import java.io.Serializable;

public class Cornice implements Serializable {

    private String numero;

    public Cornice (String n) {
        numero = n;
    }

    public String getNumero () { return numero; }
}
