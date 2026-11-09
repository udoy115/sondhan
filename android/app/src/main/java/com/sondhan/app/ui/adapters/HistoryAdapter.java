package com.sondhan.app.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sondhan.app.R;
import com.sondhan.app.models.SearchHistoryItem;
import com.sondhan.app.models.Source;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public interface OnDeleteClickListener {
        void onDelete(SearchHistoryItem item);
    }

    private final List<SearchHistoryItem> items = new ArrayList<>();
    private final Set<String> expandedIds = new HashSet<>();
    private final OnDeleteClickListener deleteListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault());

    public HistoryAdapter(OnDeleteClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setItems(List<SearchHistoryItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void removeItem(SearchHistoryItem item) {
        int index = items.indexOf(item);
        if (index >= 0) {
            items.remove(index);
            notifyItemRemoved(index);
        }
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_card, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        SearchHistoryItem item = items.get(position);
        Context context = holder.itemView.getContext();

        // 1. Verdict pill and Confidence
        String verdict = item.getVerdict() != null ? item.getVerdict().toUpperCase(Locale.ROOT) : "UNVERIFIED";
        int verdictColor;
        String verdictLabel;

        if ("TRUE".equals(verdict)) {
            verdictLabel = "✅ TRUE";
            verdictColor = ContextCompat.getColor(context, R.color.verdict_true);
            holder.tvHistVerdict.setBackgroundResource(R.drawable.bg_pill_badge);
        } else if ("FALSE".equals(verdict)) {
            verdictLabel = "❌ FALSE";
            verdictColor = ContextCompat.getColor(context, R.color.verdict_false);
            holder.tvHistVerdict.setBackgroundResource(R.drawable.bg_pill_badge);
        } else if ("MISLEADING".equals(verdict)) {
            verdictLabel = "⚠️ MISLEADING";
            verdictColor = ContextCompat.getColor(context, R.color.verdict_misleading);
            holder.tvHistVerdict.setBackgroundResource(R.drawable.bg_pill_badge);
        } else {
            verdictLabel = "❓ UNVERIFIED";
            verdictColor = ContextCompat.getColor(context, R.color.verdict_unverified);
            holder.tvHistVerdict.setBackgroundResource(R.drawable.bg_pill_badge);
        }

        holder.tvHistVerdict.setText(verdictLabel);
        holder.tvHistVerdict.setTextColor(verdictColor);
        holder.tvHistConfidence.setText(item.getConfidence() + "%");
        holder.tvHistConfidence.setTextColor(verdictColor);

        // 2. URL if applicable
        if (item.getOriginalInput() != null &&
                (item.getOriginalInput().startsWith("http://") || item.getOriginalInput().startsWith("https://"))) {
            holder.tvHistUrl.setText(item.getOriginalInput());
            holder.tvHistUrl.setVisibility(View.VISIBLE);
        } else {
            holder.tvHistUrl.setVisibility(View.GONE);
        }

        // 3. Claim
        holder.tvHistClaim.setText(item.getClaim());

        // 4. Date
        if (item.getCreatedAt() != null) {
            holder.tvHistDate.setText(dateFormat.format(item.getCreatedAt()));
        } else {
            holder.tvHistDate.setText("Recent");
        }

        // 5. Expand / Collapse State
        boolean isExpanded = expandedIds.contains(item.getId());
        holder.layoutHistDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.tvHistChevron.setText(isExpanded ? "∧" : "∨");

        holder.itemView.setOnClickListener(v -> {
            if (isExpanded) {
                expandedIds.remove(item.getId());
            } else {
                expandedIds.add(item.getId());
            }
            notifyItemChanged(position);
        });

        // 6. Delete action
        holder.btnDeleteHist.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(item);
            }
        });

        // 7. Explanation
        holder.tvHistExplanation.setText(item.getExplanation());

        // 8. AI Summary
        if (item.getSummary() != null && !item.getSummary().trim().isEmpty()) {
            holder.tvHistSummaryTitle.setVisibility(View.VISIBLE);
            holder.tvHistSummary.setText(item.getSummary());
            holder.tvHistSummary.setVisibility(View.VISIBLE);
        } else {
            holder.tvHistSummaryTitle.setVisibility(View.GONE);
            holder.tvHistSummary.setVisibility(View.GONE);
        }

        // 9. Sources
        holder.layoutHistSources.removeAllViews();
        if (item.getSources() != null && !item.getSources().isEmpty()) {
            for (Source s : item.getSources()) {
                View chip = LayoutInflater.from(context).inflate(R.layout.item_source_chip, holder.layoutHistSources, false);
                TextView tvTitle = chip.findViewById(R.id.tvSourceTitle);
                tvTitle.setText(s.getTitle());
                chip.setOnClickListener(v -> {
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(s.getUrl())));
                    } catch (Exception ignored) {}
                });
                holder.layoutHistSources.addView(chip);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistVerdict;
        TextView tvHistConfidence;
        TextView tvHistChevron;
        TextView tvHistUrl;
        TextView tvHistClaim;
        TextView tvHistDate;
        ImageButton btnDeleteHist;
        LinearLayout layoutHistDetails;
        TextView tvHistExplanation;
        TextView tvHistSummaryTitle;
        TextView tvHistSummary;
        LinearLayout layoutHistSources;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistVerdict = itemView.findViewById(R.id.tvHistVerdict);
            tvHistConfidence = itemView.findViewById(R.id.tvHistConfidence);
            tvHistChevron = itemView.findViewById(R.id.tvHistChevron);
            tvHistUrl = itemView.findViewById(R.id.tvHistUrl);
            tvHistClaim = itemView.findViewById(R.id.tvHistClaim);
            tvHistDate = itemView.findViewById(R.id.tvHistDate);
            btnDeleteHist = itemView.findViewById(R.id.btnDeleteHist);
            layoutHistDetails = itemView.findViewById(R.id.layoutHistDetails);
            tvHistExplanation = itemView.findViewById(R.id.tvHistExplanation);
            tvHistSummaryTitle = itemView.findViewById(R.id.tvHistSummaryTitle);
            tvHistSummary = itemView.findViewById(R.id.tvHistSummary);
            layoutHistSources = itemView.findViewById(R.id.layoutHistSources);
        }
    }
}
