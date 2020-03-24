package org.endcoronavirus.outreach.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.models.LogEntry;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class LogEntriesListAdapter extends RecyclerView.Adapter<LogEntriesListAdapter.ThisViewHolder> {
    private static final String TAG = "LogEntriesListAd...";
    private List<LogEntry> logEntries;
    private java.text.DateFormat dateFormatter;

    public LogEntriesListAdapter(Context context) {
        dateFormatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
    }

    @NonNull
    @Override
    public ThisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.viewholder_log_entry, parent, false);
        return new ThisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThisViewHolder holder, int position) {
        LogEntry logEntry = logEntries.get(position);
        holder.setTimestamp(logEntry.timestamp);
        holder.setDescription(logEntry.description);
    }

    @Override
    public int getItemCount() {
        return logEntries != null ? logEntries.size() : 0;
    }

    public void setLogEntries(List<LogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    class ThisViewHolder extends RecyclerView.ViewHolder {

        private final TextView timestampText;
        private final TextView descriptionText;

        public ThisViewHolder(@NonNull View itemView) {
            super(itemView);
            timestampText = itemView.findViewById(R.id.text_timestamp);
            descriptionText = itemView.findViewById(R.id.text_description);
        }

        public void setTimestamp(Date date) {
            timestampText.setText(dateFormatter.format(date));
        }

        public void setDescription(String description) {
            descriptionText.setText(description);
        }
    }
}
