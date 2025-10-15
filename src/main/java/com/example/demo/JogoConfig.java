package com.example.demo;

import java.io.Serializable;

public class JogoConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private String dificuldade = "Médio";
    private String resolucao = "1280x720";
    private double volume = 0.7;
    private String personagemEscolhido = null;
    private int faseAtual = 1;

    public JogoConfig() {}

    // --- Getters e Setters ---
    public String getDificuldade() { return dificuldade; }
    public void setDificuldade(String dificuldade) { this.dificuldade = dificuldade; }
    public String getResolucao() { return resolucao; }
    public void setResolucao(String resolucao) { this.resolucao = resolucao; }
    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }
    public String getPersonagemEscolhido() { return personagemEscolhido; }
    public void setPersonagemEscolhido(String personagemEscolhido) { this.personagemEscolhido = personagemEscolhido; }
    public int getFaseAtual() { return faseAtual; }
    public void setFaseAtual(int faseAtual) { this.faseAtual = faseAtual; }

    @Override
    public String toString() {
        return "Configurações Salvas:\n" +
                "Dificuldade: " + dificuldade + "\n" +
                "Resolução: " + resolucao + "\n" +
                "Volume: " + (int)(volume * 100) + "%\n" +
                "Personagem: " + (personagemEscolhido != null ? personagemEscolhido : "Nenhum") + "\n" +
                "Fase Atual: " + faseAtual;
    }
}