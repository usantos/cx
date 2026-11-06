package br.gov.caixa.loterias.apostas.debug.adapter;

import android.graphics.Typeface;
import android.text.Spanned;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class PayloadChunkAdapter
        extends RecyclerView.Adapter<PayloadChunkAdapter.Holder> {

    private static final int CHUNK_SIZE = 8000;

    private final CharSequence payload;

    public PayloadChunkAdapter(CharSequence payload) {
        this.payload = payload != null
                ? payload
                : "";
    }

    @Override
    public Holder onCreateViewHolder(
            ViewGroup parent,
            int viewType) {

        TextView textView = new TextView(parent.getContext());

        RecyclerView.LayoutParams params =
                new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        textView.setLayoutParams(params);
        textView.setTextSize(12);
        textView.setTypeface(Typeface.MONOSPACE);
        textView.setTextColor(0xFF444444);
        textView.setPadding(24, 16, 24, 16);
        textView.setBackgroundColor(0xFFF5F5F5);

        return new Holder(textView);
    }

    @Override
    public void onBindViewHolder(
            Holder holder,
            int position) {

        int start = position * CHUNK_SIZE;

        int end = Math.min(
                start + CHUNK_SIZE,
                payload.length()
        );

        CharSequence chunk;

        if (payload instanceof Spanned) {

            chunk = ((Spanned) payload)
                    .subSequence(start, end);

        } else {

            chunk = payload.subSequence(start, end);
        }

        holder.text.setText(chunk);
    }

    @Override
    public int getItemCount() {

        if (payload.length() == 0) {
            return 0;
        }

        return (int) Math.ceil(
                payload.length() / (double) CHUNK_SIZE
        );
    }

    static class Holder
            extends RecyclerView.ViewHolder {

        TextView text;

        Holder(TextView itemView) {
            super(itemView);
            text = itemView;
        }
    }
}