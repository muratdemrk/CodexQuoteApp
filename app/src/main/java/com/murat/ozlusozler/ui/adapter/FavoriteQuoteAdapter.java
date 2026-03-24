package com.murat.ozlusozler.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.murat.ozlusozler.R;
import com.murat.ozlusozler.data.local.FavoriteQuoteEntity;
import com.murat.ozlusozler.databinding.ItemFavoriteQuoteBinding;
import com.murat.ozlusozler.util.CategoryMapper;

import java.util.ArrayList;
import java.util.List;

public class FavoriteQuoteAdapter extends RecyclerView.Adapter<FavoriteQuoteAdapter.FavoriteQuoteViewHolder> {

    public interface OnFavoriteActionListener {
        void onRemoveClick(FavoriteQuoteEntity entity);
    }

    private final List<FavoriteQuoteEntity> items = new ArrayList<>();
    private final OnFavoriteActionListener listener;

    public FavoriteQuoteAdapter(OnFavoriteActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<FavoriteQuoteEntity> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteQuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteQuoteBinding binding = ItemFavoriteQuoteBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new FavoriteQuoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteQuoteViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FavoriteQuoteViewHolder extends RecyclerView.ViewHolder {

        private final ItemFavoriteQuoteBinding binding;

        FavoriteQuoteViewHolder(ItemFavoriteQuoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(FavoriteQuoteEntity entity, OnFavoriteActionListener listener) {
            Context context = binding.getRoot().getContext();
            binding.textQuote.setText(entity.getQuoteText());
            binding.textAuthor.setText(context.getString(R.string.quote_author_prefix, entity.getAuthor()));
            binding.textCategory.setText(context.getString(
                    R.string.quote_category_prefix,
                    CategoryMapper.getDisplayCategory(context, entity.getCategoryKey())
            ));
            binding.buttonRemove.setOnClickListener(view -> listener.onRemoveClick(entity));
        }
    }
}

