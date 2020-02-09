package com.techela.symbiosis.admin.ui.dashboard;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.recyclerview.widget.RecyclerView;


import com.symbiosis.techela.admin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CustomRecyclerViewInput extends RecyclerView.Adapter<CustomRecyclerViewInput.RecyclerViewHolder> {
    private int count = 1;
    private final Context context;
    private ArrayList<String> optionsList = new ArrayList<>();
    public final HashMap<Integer, RecyclerView.ViewHolder> holderHashMap = new HashMap<>();

    public CustomRecyclerViewInput(Context context, ArrayList<String> optionsList) {
        this.context = context;
        this.optionsList = optionsList;
        if (!optionsList.isEmpty()) {
            count = optionsList.size();
        }

    }

    @Override
    public void onViewDetachedFromWindow(RecyclerViewHolder holder) {
        holderHashMap.put(holder.getAdapterPosition(),holder);
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerViewHolder holder) {
        holderHashMap.remove(holder.getAdapterPosition());
        super.onViewAttachedToWindow(holder);

    }
    @Override
    public CustomRecyclerViewInput.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomRecyclerViewInput.RecyclerViewHolder holder, int position) {
        if (position < optionsList.size()) {
            holder.option.setText(optionsList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return count;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void addItem() {
        if (count < 4) {
            count++;
        }
    }

    public void removeItem() {
        if (count > 1) {
            count--;
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        final EditText option;

        RecyclerViewHolder(View view) {
            super(view);
            option = view.findViewById(R.id.edit_option);

        }
    }
}
