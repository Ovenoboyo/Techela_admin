package com.techela.symbiosis.admin.ui.dashboard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.symbiosis.techela.admin.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardFragment extends Fragment {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    ArrayList<List<String>> expandableListTitle;
    HashMap<List<String>, List<String>> expandableListDetail = new HashMap<>();
    ProgressDialog mProgressDialog;
    ArrayList<String> keys = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Getting data...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();

        expandableListView = (ExpandableListView) root.findViewById(R.id.expandableListView);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference quizQuestionsNode = rootRef.child("Quiz");

        quizQuestionsNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d("test", "onDataChange: "+ds.getKey());
                    keys.add(ds.getKey());
                    String question = ds.child("question").getValue().toString();
                    ArrayList<String> options = new ArrayList<>();
                    for (DataSnapshot optionsShot : ds.getChildren()) {
                        if (optionsShot.getKey().matches(".*option.*")) {
                            options.add(optionsShot.getValue().toString());
                        }
                    }
                    ArrayList<String> questionList = new ArrayList();
                    questionList.add(question);
                    questionList.add(ds.getKey());
                    expandableListDetail.put(questionList, options);
                }


                expandableListTitle = new ArrayList<List<String>>(expandableListDetail.keySet());
                expandableListAdapter = new CustomListViewAdapter(DashboardFragment.this.getContext(), expandableListTitle, expandableListDetail);
                expandableListView.setAdapter(expandableListAdapter);

                FloatingActionButton fab = root.findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditDialogFragment(-1);
                    }
                });

                expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getContext(),
                                "long click",
                                Toast.LENGTH_SHORT).show();
                        showEditDialogFragment(ExpandableListView.getPackedPositionGroup(id));
                        return false;
                    }
                });

                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    private void showEditDialogFragment(int id) {
        Bundle bundle = new Bundle();
        if (id != -1) {
            List<String> options = expandableListDetail.get(expandableListTitle.get(id));
            bundle.putString("pos", expandableListTitle.get(id).get(1));
            bundle.putStringArrayList("optionsList", (ArrayList<String>)options);
            bundle.putString("question", expandableListTitle.get(id).get(0));
        }
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        EditDialogFragment editDialogFragment = new EditDialogFragment();
        editDialogFragment.setArguments(bundle);
        editDialogFragment.show(ft, "EditDialogFragment");
        ft.addToBackStack("EditDialogFragment");
    }
}