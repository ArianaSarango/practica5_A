package com.unl.automata.service;

import com.unl.automata.model.Automata;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AutomataService {

    // ALGORITMO: Construcción de Subconjuntos (Conversión AFND a AFD)
    // Conversión AFND a AFD

    public Automata convertirAFNDaAFD(Automata afnd) {
        Automata afd = new Automata(afnd.nombre + "_AFD");
        Map<Set<String>, String> nombres = new HashMap<>();
        Queue<Set<String>> cola = new LinkedList<>();

        Set<String> inicial = Collections.singleton(afnd.estadoInicial);
        cola.add(inicial);
        nombres.put(inicial, "S" + nombres.size());
        afd.estadoInicial = nombres.get(inicial);

        while (!cola.isEmpty()) {
            Set<String> actual = cola.poll();
            String nomActual = nombres.get(actual);

            // Si el conjunto actual contiene un estado final del AFND, el estado del AFD es
            // final
            for (String sub : actual) {
                if (afnd.estadosFinales.contains(sub))
                    afd.estadosFinales.add(nomActual);
            }

            for (String sim : afnd.alfabeto) {
                Set<String> destino = new HashSet<>();
                for (String s : actual) {
                    if (afnd.transiciones.containsKey(s) && afnd.transiciones.get(s).containsKey(sim)) {
                        destino.addAll(afnd.transiciones.get(s).get(sim));
                    }
                }
                if (!destino.isEmpty()) {
                    if (!nombres.containsKey(destino)) {
                        nombres.put(destino, "S" + nombres.size());
                        cola.add(destino);
                    }
                    afd.agregarTransicion(nomActual, sim, nombres.get(destino));
                }
            }
        }
        return afd;
    }

    // ALGORITMO: Minimización de AFD (Particiones)
    // Reduce el número de estados del AFD al mínimo posible manteniendo la
    // equivalencia.

    public Automata minimizarAFD(Automata afd) {
        if (afd.estados.isEmpty())
            return afd;

        // Partición inicial: Estados Finales vs No Finales
        List<Set<String>> particiones = new ArrayList<>();
        Set<String> finales = new HashSet<>(afd.estadosFinales);
        Set<String> noFinales = new HashSet<>(afd.estados);
        noFinales.removeAll(finales);

        if (!finales.isEmpty())
            particiones.add(finales);
        if (!noFinales.isEmpty())
            particiones.add(noFinales);

        boolean cambio = true;
        while (cambio) {
            cambio = false;
            List<Set<String>> nuevaParticion = new ArrayList<>();

            for (Set<String> grupo : particiones) {
                if (grupo.size() <= 1) {
                    nuevaParticion.add(grupo);
                    continue;
                }

                Map<String, Set<String>> subGrupos = new HashMap<>();
                for (String estado : grupo) {
                    StringBuilder clave = new StringBuilder();
                    for (String sim : afd.alfabeto) {
                        String destino = afd.transiciones.getOrDefault(estado, Collections.emptyMap())
                                .getOrDefault(sim, Collections.singleton("TRAP"))
                                .iterator().next();

                        // Encontrar a qué índice de partición pertenece el destino
                        int idxDestino = -1;
                        for (int i = 0; i < particiones.size(); i++) {
                            if (particiones.get(i).contains(destino)) {
                                idxDestino = i;
                                break;
                            }
                        }
                        clave.append(sim).append(":").append(idxDestino).append("|");
                    }
                    subGrupos.computeIfAbsent(clave.toString(), k -> new HashSet<>()).add(estado);
                }

                if (subGrupos.size() > 1)
                    cambio = true;
                nuevaParticion.addAll(subGrupos.values());
            }
            particiones = nuevaParticion;
        }

        // Reconstruir el autómata minimizado
        Automata min = new Automata(afd.nombre + "_MIN");
        Map<String, String> mapeo = new HashMap<>();

        for (int i = 0; i < particiones.size(); i++) {
            String nuevoEstado = "M" + i;
            for (String viejo : particiones.get(i)) {
                mapeo.put(viejo, nuevoEstado);
                if (viejo.equals(afd.estadoInicial))
                    min.estadoInicial = nuevoEstado;
                if (afd.estadosFinales.contains(viejo))
                    min.estadosFinales.add(nuevoEstado);
            }
        }

        for (Set<String> grupo : particiones) {
            String origen = grupo.iterator().next();
            String nuevoOrigen = mapeo.get(origen);
            Map<String, Set<String>> trans = afd.transiciones.get(origen);
            if (trans != null) {
                trans.forEach((sim, destinos) -> {
                    String nuevoDestino = mapeo.get(destinos.iterator().next());
                    if (nuevoDestino != null)
                        min.agregarTransicion(nuevoOrigen, sim, nuevoDestino);
                });
            }
        }

        return min;
    }

    // ALGORITMO: Simulación de ejecución (Evaluación de cadenas)
    // Verifica si una cadena pertenece al lenguaje definido por el autómata.

    public boolean acepta(Automata aut, String cadena) {
        Set<String> actuales = new HashSet<>();
        actuales.add(aut.estadoInicial);
        for (char c : cadena.toCharArray()) {
            String s = String.valueOf(c);
            Set<String> siguientes = new HashSet<>();
            for (String est : actuales) {
                if (aut.transiciones.containsKey(est) && aut.transiciones.get(est).containsKey(s)) {
                    siguientes.addAll(aut.transiciones.get(est).get(s));
                }
            }
            actuales = siguientes;
        }
        for (String est : actuales)
            if (aut.estadosFinales.contains(est))
                return true;
        return false;
    }
}