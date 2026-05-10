package com.unl.automata.model;

import java.util.*;

public class Automata {
    public String nombre;
    public Set<String> estados = new HashSet<>();
    public Set<String> alfabeto = new HashSet<>();
    public String estadoInicial;
    public Set<String> estadosFinales = new HashSet<>();
    public Map<String, Map<String, Set<String>>> transiciones = new HashMap<>();

    public Automata(String nombre) { this.nombre = nombre; }

    public void agregarTransicion(String origen, String simbolo, String destino) {
        this.estados.add(origen);
        this.estados.add(destino);
        this.alfabeto.add(simbolo);
        this.transiciones.computeIfAbsent(origen, k -> new HashMap<>())
                        .computeIfAbsent(simbolo, k -> new HashSet<>())
                        .add(destino);
    }
}