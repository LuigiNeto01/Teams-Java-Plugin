package com.luigi.projetotimes.Times;

import java.util.ArrayList;
import java.util.List;

public class Time {
    private String nome;
    private int capacidade;
    private List<String> jogadores;

    public Time(String nome, int capacidade) {
        this.nome = nome;
        this.capacidade = capacidade;
        this.jogadores = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public List<String> getJogadores() {
        return jogadores;
    }

    public boolean adicionarJogador(String jogador) {
        if (jogadores.size() < capacidade) {
            jogadores.add(jogador);
            return true;
        }
        return false;
    }

    public boolean removerJogador(String jogador) {
        return jogadores.remove(jogador);
    }
}
