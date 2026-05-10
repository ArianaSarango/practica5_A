package com.unl.automata.controller;

import com.unl.automata.model.Automata;
import com.unl.automata.service.AutomataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/practica")
@CrossOrigin(origins = "*")
public class AutomataController {

    @Autowired
    private AutomataService service;

    /**
     * Endpoint principal que orquesta la transformación y evaluación de todos los ejercicios.
     * Recibe una lista de cadenas desde el frontend y devuelve los resultados de los 3 casos.
     */
    @PostMapping("/ejecutar-todo")
    public Map<String, Object> ejecutarTodo(@RequestBody List<String> pruebas) {
        Map<String, Object> respuestaFinal = new LinkedHashMap<>();

        // --- 1. CASO COMPRADOR (H, S, C) ---
        Automata compradorAFND = crearCompradorAFND();
        Automata compradorAFD = service.convertirAFNDaAFD(compradorAFND);
        Automata compradorMin = service.minimizarAFD(compradorAFD);

        // --- 2. CASO CIBERSEGURIDAD (A, C, K) ---
        Automata idsAFND = crearIDSAFND();
        Automata idsAFD = service.convertirAFNDaAFD(idsAFND);
        Automata idsMin = service.minimizarAFD(idsAFD);

        // --- 3. CASO BIOINFORMÁTICA (K, G, X, F) ---
        Automata bioAFND = crearBioAFND();
        Automata bioAFD = service.convertirAFNDaAFD(bioAFND);
        Automata bioMin = service.minimizarAFD(bioAFD);

        respuestaFinal.put("Comprador", correrPruebas(compradorAFND, compradorAFD, compradorMin, pruebas));
        respuestaFinal.put("Ciberseguridad", correrPruebas(idsAFND, idsAFD, idsMin, pruebas));
        respuestaFinal.put("Bioinformatica", correrPruebas(bioAFND, bioAFD, bioMin, pruebas));

        return respuestaFinal;
    }

    /**
     * Ejecuta las pruebas sobre los 3 autómatas (AFND, AFD y Mínimo) para verificar equivalencia.
     */
    private List<Map<String, Object>> correrPruebas(Automata nfa, Automata dfa, Automata min, List<String> cadenas) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        for (String s : cadenas) {
            Map<String, Object> r = new HashMap<>();
            r.put("cadena", s);
            r.put("nfa", service.acepta(nfa, s));
            r.put("dfa", service.acepta(dfa, s));
            r.put("min", service.acepta(min, s));
            resultados.add(r);
        }
        return resultados;
    }

    // --- DEFINICIONES DE LOS EJERCICIOS (AFND ORIGINALES) ---

    private Automata crearCompradorAFND() {
        Automata a = new Automata("Comprador_AFND");
        a.estadoInicial = "q0";
        a.agregarTransicion("q0", "H", "q1");
        a.agregarTransicion("q1", "S", "q1");
        a.agregarTransicion("q1", "S", "q2");
        a.agregarTransicion("q2", "C", "q3");
        a.estadosFinales.add("q3");
        return a;
    }

    private Automata crearIDSAFND() {
        Automata a = new Automata("IDS_AFND");
        a.estadoInicial = "q0";
        a.agregarTransicion("q0", "A", "q0");
        a.agregarTransicion("q0", "A", "q1");
        a.agregarTransicion("q1", "C", "q2");
        a.agregarTransicion("q2", "K", "q3");
        a.estadosFinales.add("q3");
        return a;
    }

    private Automata crearBioAFND() {
        Automata a = new Automata("Bio_AFND");
        a.estadoInicial = "q0";
        a.agregarTransicion("q0", "K", "q1");
        a.agregarTransicion("q1", "G", "q2");
        a.agregarTransicion("q2", "X", "q2");
        a.agregarTransicion("q2", "F", "q3");
        a.estadosFinales.add("q3");
        return a;
    }
}