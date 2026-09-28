/*
 * João Pedro G M Silva
 * Matrícula: 20251012000740
 * Curso: ADS PUC Goiás
 */
package com.bughunter.centraldebugs.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bughunter.centraldebugs.R;
import com.bughunter.centraldebugs.databinding.ItemBugBinding;
import com.bughunter.centraldebugs.model.Bug;

import java.util.List;

/**
 * Adapter da lista de bugs. Os cliques encaminham o próprio objeto Bug,
 * nunca uma posição guardada.
 */
public class BugAdapter extends RecyclerView.Adapter<BugAdapter.BugViewHolder> {

    /** Evento de toque simples: alterna o status do bug. */
    public interface OnBugClickListener {
        void onBugClicado(Bug bug);
    }

    /** Evento de toque longo: remove o bug da lista. */
    public interface OnBugLongClickListener {
        void onBugClicadoLongo(Bug bug);
    }

    private final List<Bug> bugs;
    private OnBugClickListener ouvinteClique;
    private OnBugLongClickListener ouvinteCliqueLongo;

    public BugAdapter(List<Bug> bugs) {
        this.bugs = bugs;
    }

    public void setOnBugClickListener(OnBugClickListener ouvinteClique) {
        this.ouvinteClique = ouvinteClique;
    }

    public void setOnBugLongClickListener(OnBugLongClickListener ouvinteCliqueLongo) {
        this.ouvinteCliqueLongo = ouvinteCliqueLongo;
    }

    @NonNull
    @Override
    public BugViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBugBinding binding = ItemBugBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BugViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BugViewHolder holder, int position) {
        holder.vincular(bugs.get(position));
    }

    @Override
    public int getItemCount() {
        return bugs.size();
    }

    /** ViewHolder com View Binding do layout reutilizável do cartão. */
    public class BugViewHolder extends RecyclerView.ViewHolder {

        private final ItemBugBinding binding;

        public BugViewHolder(ItemBugBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Sempre reescreve TODOS os elementos visuais do cartão (textos, imagem,
         * cores, status). Como os cartões são reciclados, nada é herdado de
         * outro registro durante a rolagem.
         */
        void vincular(Bug bug) {
            Context contexto = binding.getRoot().getContext();
            boolean corrigido = bug.isCorrigido();

            // Textos
            binding.textoCodigo.setText(bug.getCodigo());
            binding.textoTitulo.setText(bug.getTitulo());
            binding.textoModulo.setText(contexto.getString(R.string.card_modulo_format, bug.getModulo()));
            binding.textoDescricao.setText(bug.getDescricao());

            String[] niveis = contexto.getResources().getStringArray(R.array.niveis_gravidade);
            String nivel = niveis[bug.getGravidade() - 1];
            binding.textoGravidade.setText(
                    contexto.getString(R.string.gravidade_formato, bug.getGravidade(), nivel));
            binding.textoGravidade.setTextColor(ContextCompat.getColor(contexto, corDaGravidade(bug.getGravidade())));

            // Imagem de identificação da gravidade + contentDescription
            binding.imagemSeveridade.setImageResource(bug.getImagemRes());
            binding.imagemSeveridade.setContentDescription(
                    contexto.getString(R.string.imagem_gravidade_descricao, bug.getGravidade(), nivel));

            // Status: texto, fundo do selo e cores
            binding.textoStatus.setText(corrigido ? R.string.status_corrigido : R.string.status_aberto);
            binding.textoStatus.setBackgroundResource(
                    corrigido ? R.drawable.fundo_badge_corrigido : R.drawable.fundo_badge_aberto);
            binding.textoStatus.setTextColor(ContextCompat.getColor(contexto,
                    corrigido ? R.color.badge_corrigido_texto : R.color.badge_aberto_texto));

            // Cartão: cor de fundo e borda verde quando corrigido
            binding.cartaoBug.setCardBackgroundColor(ContextCompat.getColor(contexto,
                    corrigido ? R.color.cartao_corrigido : R.color.cartao_aberto));
            binding.cartaoBug.setStrokeColor(ContextCompat.getColor(contexto, R.color.verde_neon));
            float densidade = contexto.getResources().getDisplayMetrics().density;
            binding.cartaoBug.setStrokeWidth(corrigido ? Math.round(2 * densidade) : 0);

            // Título: cor sempre reatribuída e riscado quando corrigido
            int flagsTitulo = binding.textoTitulo.getPaintFlags();
            if (corrigido) {
                flagsTitulo |= Paint.STRIKE_THRU_TEXT_FLAG;
            } else {
                flagsTitulo &= ~Paint.STRIKE_THRU_TEXT_FLAG;
            }
            binding.textoTitulo.setPaintFlags(flagsTitulo);
            binding.textoTitulo.setTextColor(ContextCompat.getColor(contexto,
                    corrigido ? R.color.texto_secundario : R.color.texto_principal));

            // Cores fixas reatribuídas a cada bind (não herdam de outro cartão)
            binding.textoCodigo.setTextColor(ContextCompat.getColor(contexto, R.color.verde_neon));
            binding.textoModulo.setTextColor(ContextCompat.getColor(contexto, R.color.texto_secundario));
            binding.textoDescricao.setTextColor(ContextCompat.getColor(contexto, R.color.texto_secundario));

            // Clique e clique longo repassam o próprio objeto Bug
            binding.cartaoBug.setOnClickListener(v -> {
                if (ouvinteClique != null) {
                    ouvinteClique.onBugClicado(bug);
                }
            });
            binding.cartaoBug.setOnLongClickListener(v -> {
                if (ouvinteCliqueLongo != null) {
                    ouvinteCliqueLongo.onBugClicadoLongo(bug);
                    return true;
                }
                return false;
            });
        }

        private int corDaGravidade(int gravidade) {
            if (gravidade <= 2) {
                return R.color.gravidade_baixa;
            }
            if (gravidade == 3) {
                return R.color.gravidade_media;
            }
            return R.color.gravidade_alta;
        }
    }
}
