package com.techela.symbiosis.admin.ui.dashboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.symbiosis.techela.admin.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class EditDialogFragment extends DialogFragment {
    private Context mContext;
    private String pos = "", questionTitle = "";
    private EditText question;
    private Spinner category;
    private RecyclerView list;
    private ArrayList<String> optionsList = new ArrayList<>();
    private CustomRecyclerViewInput listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setCancelable(true);

        pos = Objects.requireNonNull(getArguments()).getString("pos");
        optionsList = getArguments().getStringArrayList("optionsList");
        if (optionsList == null) {
            optionsList = new ArrayList<String>();
        }
        questionTitle = getArguments().getString("question");

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.edit_dialog, null);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext);

        question = view.findViewById(R.id.question);
        question.setText(questionTitle);
        Button add = view.findViewById(R.id.add_btn);

        builder.setView(view);
        builder.setTitle(getString(R.string.edit));

        listAdapter = new CustomRecyclerViewInput(getContext(), optionsList);
        list = view.findViewById(R.id.custom_list);
        list.setAdapter(listAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(RecyclerView.VERTICAL);
        list.setLayoutManager(llm);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAdapter.addItem();
                listAdapter.notifyItemInserted(listAdapter.getItemCount()+1);
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();

        TextView save = view.findViewById(R.id.save);
        TextView delete = view.findViewById(R.id.delete);

        if (pos == null) {
            delete.setVisibility(View.GONE);
        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        save.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
            builder1.setTitle(getString(R.string.confirm));
            builder1.setTitle(getString(R.string.areyousure));

            builder1.setPositiveButton("Yes", (dialog12, which) -> saveText());
            builder1.setNegativeButton("No", (dialog1, which) -> {
            });
            AlertDialog alertDialog = builder1.create();
            alertDialog.show();
        });

//        delete.setOnClickListener(v -> {
//            RemoveItem(pos, Objects.requireNonNull(getContext()));
//        });
        return dialog;
    }

    private void delete() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference questionsNode;
        if (pos != null) {
            questionsNode = database.getReference("Quiz").child(pos);
        } else {
            questionsNode = database.getReference("Quiz").child(UUID.randomUUID().toString());

        }

        questionsNode.removeValue();
    }

    private void saveText() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference questionsNode;
        String uuid = UUID.randomUUID().toString();
        if (pos != null) {
            questionsNode = database.getReference("Quiz").child(pos);
        } else {
            questionsNode = database.getReference("Quiz").child(uuid);

        }

        final DatabaseReference realAnswersNode = database.getReference("QuizRealAnswers").child(uuid);

        Map<String, String> optionsMap = new HashMap<>();
        String questionS = question.getText().toString().trim();
        optionsMap.put("question", questionS);
        String firstOption = "";
        for (int i = 0; i < listAdapter.getItemCount(); i++) {
            RecyclerView.ViewHolder holder = list.findViewHolderForAdapterPosition(i);
            if(holder == null) {
                holder = listAdapter.holderHashMap.get(i);
            }
            EditText option = holder.itemView.findViewById(R.id.edit_option);
            if (!option.getText().toString().equals("")) {
                optionsMap.put("options" + i, option.getText().toString().trim());
            }
            if (i == 0) {
                firstOption = option.getText().toString().trim();
            }
        }

        realAnswersNode.setValue(firstOption);

        questionsNode.setValue(optionsMap);
    }
}
