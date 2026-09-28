/*
 * João Pedro G M Silva
 * Matrícula: 20251012000740
 * Curso: ADS PUC Goiás
 */
package com.bughunter.centraldebugs.model;

import java.util.List;
import java.util.Locale;

/**
 * Modelo de um bug registrado no aplicativo.
 * Não depende de classes Android para facilitar a verificação da lógica em JVM.
 */
public class Bug {

    private final String codigo;
    private final String titulo;
    private final String modulo;
    private final String descricao;
    private final int gravidade; // de 1 a 5
    private final int imagemRes;  // identificador da imagem (recurso do cartão)
    private boolean corrigido;    // status de resolução

    public Bug(String codigo, String titulo, String modulo, String descricao,
               int gravidade, boolean corrigido, int imagemRes) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.modulo = modulo;
        this.descricao = descricao;
        this.gravidade = gravidade;
        this.corrigido = corrigido;
        this.imagemRes = imagemRes;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getModulo() {
        return modulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getGravidade() {
        return gravidade;
    }

    public int getImagemRes() {
        return imagemRes;
    }

    public boolean isCorrigido() {
        return corrigido;
    }

    public void setCorrigido(boolean corrigido) {
        this.corrigido = corrigido;
    }

    /**
     * Gera o próximo código no formato BUG-001, BUG-002 ... a partir do maior
     * código já presente na lista. Nunca repete um código em uso: ao remover
     * registros, o próximo código continua consistente com o que existe.
     */
    public static String gerarProximoCodigo(List<Bug> existentes) {
        int maior = 0;
        for (Bug bug : existentes) {
            String partes = bug.getCodigo();
            int inicioDigitos = partes.indexOf('-') + 1;
            if (inicioDigitos <= 0) {
                continue;
            }
            try {
                int numero = Integer.parseInt(partes.substring(inicioDigitos));
                if (numero > maior) {
                    maior = numero;
                }
            } catch (NumberFormatException ignored) {
                // código fora do padrão BUG-00N: ignora
            }
        }
        return String.format(Locale.US, "BUG-%03d", maior + 1);
    }

    /** Pontos de um bug: gravidade x 10 quando está corrigido, 0 caso aberto. */
    public int pontos() {
        return corrigido ? gravidade * 10 : 0;
    }

    /** Pontuação total do caçador de bugs: soma dos pontos dos bugs corrigidos. */
    public static int calcularPontuacao(List<Bug> bugs) {
        int pontuacao = 0;
        for (Bug bug : bugs) {
            pontuacao += bug.pontos();
        }
        return pontuacao;
    }

    public static int contarCorrigidos(List<Bug> bugs) {
        int corrigidos = 0;
        for (Bug bug : bugs) {
            if (bug.isCorrigido()) {
                corrigidos++;
            }
        }
        return corrigidos;
    }

    public static int contarAbertos(List<Bug> bugs) {
        return bugs.size() - contarCorrigidos(bugs);
    }
}
