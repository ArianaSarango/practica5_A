package com.unl.automata.model;

import java.util.*;

/**
 * Clase que representa un Autómata Finito (AFND o AFD).
 * Define los componentes de la 5-tupla formal (Q, Sigma, q0, F, delta).
 */
public class Automata {
    public String nombre;
    public Set<String> estados = new HashSet<>(); // Conjunto Q: Todos los estados del autómata
    public Set<String> alfabeto = new HashSet<>(); // Conjunto Σ: Símbolos permitidos
    public String estadoInicial; // Estado q0: Punto de inicio de la ejecución
    public Set<String> estadosFinales = new HashSet<>(); // Conjunto F: Estados de aceptación/finalización
    public Map<String, Map<String, Set<String>>> transiciones = new HashMap<>(); // Función δ: Transiciones entre estados

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