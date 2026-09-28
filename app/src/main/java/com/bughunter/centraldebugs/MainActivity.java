/*
 * João Pedro G M Silva
 * Matrícula: 20251012000740
 * Curso: ADS PUC Goiás
 */
package com.bughunter.centraldebugs;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bughunter.centraldebugs.adapter.BugAdapter;
import com.bughunter.centraldebugs.databinding.ActivityMainBinding;
import com.bughunter.centraldebugs.model.Bug;

import java.util.ArrayList;

/**
 * Tela única: cadastro de bugs, painel de acompanhamento e lista (RecyclerView).
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final ArrayList<Bug> bugs = new ArrayList<>();
    private BugAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lista: LinearLayoutManager, fora de qualquer ScrollView
        adaptador = new BugAdapter(bugs);
        adaptador.setOnBugClickListener(this::alternarStatusBug);
        adaptador.setOnBugLongClickListener(this::removerBug);
        binding.listaBugs.setLayoutManager(new LinearLayoutManager(this));
        binding.listaBugs.setAdapter(adaptador);

        // Gravidade: SeekBar de 1 a 5 com leitura em tempo real
        binding.seekGravidade.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                atualizarTextoGravidade(progress + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        binding.botaoRegistrarBug.setOnClickListener(v -> tentarRegistrarBug());

        atualizarTextoGravidade(gravidadeSelecionada());
        recalcularPainel();
    }

    /** Valida o formulário e registra o bug quando tudo está válido. */
    private void tentarRegistrarBug() {
        String titulo = binding.campoTitulo.getText().toString().trim();
        String modulo = binding.campoModulo.getText().toString().trim();
        String descricao = binding.campoDescricao.getText().toString().trim();

        binding.layoutTitulo.setError(null);
        binding.layoutModulo.setError(null);
        binding.layoutDescricao.setError(null);

        boolean tituloValido = !titulo.isEmpty();
        if (!tituloValido) {
            binding.layoutTitulo.setError(getString(R.string.erro_titulo));
        }
        boolean moduloValido = !modulo.isEmpty();
        if (!moduloValido) {
            binding.layoutModulo.setError(getString(R.string.erro_modulo));
        }
        boolean descricaoValida = descricao.length() >= 10;
        if (!descricaoValida) {
            binding.layoutDescricao.setError(getString(R.string.erro_descricao));
        }
        if (!tituloValido || !moduloValido || !descricaoValida) {
            return;
        }

        int gravidade = gravidadeSelecionada();
        Bug novoBug = new Bug(
                Bug.gerarProximoCodigo(bugs),
                titulo,
                modulo,
                descricao,
                gravidade,
                false,
                imagemDaGravidade(gravidade));
        bugs.add(novoBug);

        int posicaoInserida = bugs.size() - 1;
        adaptador.notifyItemInserted(posicaoInserida);
        binding.listaBugs.scrollToPosition(posicaoInserida);

        limparFormulario();
        recalcularPainel();
    }

    /** Toque no cartão: alterna entre aberto e corrigido. */
    private void alternarStatusBug(Bug bug) {
        bug.setCorrigido(!bug.isCorrigido());
        int posicao = bugs.indexOf(bug);
        if (posicao >= 0) {
            adaptador.notifyItemChanged(posicao);
        }
        recalcularPainel();
    }

    /** Toque longo no cartão: remove o registro e informa qual foi removido. */
    private void removerBug(Bug bug) {
        int posicao = bugs.indexOf(bug);
        if (posicao < 0) {
            return;
        }
        bugs.remove(posicao);
        adaptador.notifyItemRemoved(posicao);
        recalcularPainel();
        Toast.makeText(this,
                getString(R.string.bug_removido_format, bug.getCodigo(), bug.getTitulo()),
                Toast.LENGTH_LONG).show();
    }

    /** Recalcula total, abertos, corrigidos, pontuação e a mensagem de lista vazia. */
    private void recalcularPainel() {
        binding.valorTotal.setText(String.valueOf(bugs.size()));
        binding.valorAbertos.setText(String.valueOf(Bug.contarAbertos(bugs)));
        binding.valorCorrigidos.setText(String.valueOf(Bug.contarCorrigidos(bugs)));
        binding.valorPontos.setText(getString(R.string.pontos_formato, Bug.calcularPontuacao(bugs)));
        binding.mensagemListaVazia.setVisibility(bugs.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private int gravidadeSelecionada() {
        return binding.seekGravidade.getProgress() + 1; // SeekBar vai de 0 a 4
    }

    private void atualizarTextoGravidade(int gravidade) {
        String[] niveis = getResources().getStringArray(R.array.niveis_gravidade);
        String nivel = niveis[gravidade - 1];
        binding.textoGravidadeAtual.setText(getString(R.string.gravidade_formato, gravidade, nivel));
        int corRecurso = gravidade <= 2
                ? R.color.gravidade_baixa
                : gravidade == 3
                ? R.color.gravidade_media
                : R.color.gravidade_alta;
        binding.textoGravidadeAtual.setTextColor(ContextCompat.getColor(this, corRecurso));
    }

    private int imagemDaGravidade(int gravidade) {
        if (gravidade <= 2) {
            return R.drawable.ic_gravidade_baixa;
        }
        if (gravidade == 3) {
            return R.drawable.ic_gravidade_media;
        }
        return R.drawable.ic_gravidade_alta;
    }

    private void limparFormulario() {
        binding.campoTitulo.setText("");
        binding.campoModulo.setText("");
        binding.campoDescricao.setText("");
        binding.seekGravidade.setProgress(2); // volta para a gravidade padrão: 3 (média)
    }
}
