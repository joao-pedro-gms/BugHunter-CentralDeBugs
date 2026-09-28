/*
 * João Pedro G M Silva
 * Matrícula: 20251012000740
 * Curso: ADS PUC Goiás
 */
package com.bughunter.centraldebugs;

import com.bughunter.centraldebugs.model.Bug;

import java.util.ArrayList;

/**
 * Verificação da lógica de negócio em JVM pura (sem Android):
 * geração de código BUG-00N, pontuação (gravidade x 10) e contadores.
 * Compilar e rodar:
 *   javac -d /tmp/verificacao app/src/main/java/.../model/Bug.java app/src/test/java/.../VerificacaoLogica.java
 *   java -cp /tmp/verificacao com.bughunter.centraldebugs.VerificacaoLogica
 */
public class VerificacaoLogica {

    private static int verificacoes = 0;

    private static void verificar(String descricao, Object esperado, Object obtido) {
        verificacoes++;
        if (!esperado.equals(obtido)) {
            throw new AssertionError(
                    "FALHOU: " + descricao + " — esperado " + esperado + ", obtido " + obtido);
        }
        System.out.println("OK  " + descricao + " -> " + obtido);
    }

    private static Bug novoBug(String codigo, int gravidade) {
        return new Bug(codigo, "Título de teste", "Módulo de teste",
                "Descrição de teste com mais de dez caracteres.", gravidade, false, 0);
    }

    public static void main(String[] args) {
        System.out.println("== Geração de código BUG-00N ==");
        ArrayList<Bug> bugs = new ArrayList<>();

        String codigo1 = Bug.gerarProximoCodigo(bugs);
        bugs.add(novoBug(codigo1, 3));
        String codigo2 = Bug.gerarProximoCodigo(bugs);
        bugs.add(novoBug(codigo2, 3));
        String codigo3 = Bug.gerarProximoCodigo(bugs);
        bugs.add(novoBug(codigo3, 3));
        verificar("primeiro registro", "BUG-001", codigo1);
        verificar("segundo registro", "BUG-002", codigo2);
        verificar("terceiro registro", "BUG-003", codigo3);

        bugs.remove(2); // remove BUG-003
        String codigo4 = Bug.gerarProximoCodigo(bugs);
        verificar("após remover BUG-003, reutiliza o código livre", "BUG-003", codigo4);
        bugs.add(novoBug(codigo4, 3));

        bugs.remove(1); // remove BUG-002 (BUG-003 continua na lista)
        String codigo5 = Bug.gerarProximoCodigo(bugs);
        verificar("após remover BUG-002 sem duplicar BUG-003", "BUG-004", codigo5);

        bugs.clear();
        verificar("lista vazia volta a BUG-001", "BUG-001", Bug.gerarProximoCodigo(bugs));

        System.out.println();
        System.out.println("== Pontuação: gravidade x 10 para bug corrigido ==");
        Bug gravidade4 = novoBug("BUG-001", 4);
        gravidade4.setCorrigido(true);
        verificar("gravidade 4 corrigido", 40, gravidade4.pontos());
        Bug gravidade5 = novoBug("BUG-002", 5);
        gravidade5.setCorrigido(true);
        verificar("gravidade 5 corrigido", 50, gravidade5.pontos());
        Bug gravidade2 = novoBug("BUG-003", 2);
        gravidade2.setCorrigido(true);
        verificar("gravidade 2 corrigido", 20, gravidade2.pontos());
        Bug aberto = novoBug("BUG-004", 5);
        verificar("bug aberto não pontua", 0, aberto.pontos());

        System.out.println();
        System.out.println("== Contadores do painel ==");
        ArrayList<Bug> lista = new ArrayList<>();
        lista.add(gravidade4); // corrigido, 40 pts
        lista.add(gravidade5); // corrigido, 50 pts
        lista.add(gravidade2); // corrigido, 20 pts
        lista.add(aberto);     // aberto, 0 pts
        lista.add(novoBug("BUG-005", 1)); // aberto, 0 pts
        verificar("total de registros", 5, lista.size());
        verificar("bugs abertos", 2, Bug.contarAbertos(lista));
        verificar("bugs corrigidos", 3, Bug.contarCorrigidos(lista));
        verificar("pontuação total (40 + 50 + 20)", 110, Bug.calcularPontuacao(lista));

        lista.remove(0);
        lista.remove(0);
        lista.remove(0); // remove os 3 corrigidos -> lista só com abertos
        verificar("pontuação zera quando não há corrigidos", 0, Bug.calcularPontuacao(lista));

        System.out.println();
        System.out.println("TODAS AS VERIFICAÇÕES PASSARAM (" + verificacoes + " verificações)");
    }
}
