package com.techela.symbiosis.admin.ui.home;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import java.util.Map;

public class HomeFragment extends Fragment {
    HashMap<String, Map<String, String>> userAnswers = new HashMap<>();
    HashMap<String, String> userUIDmap = new HashMap<>();
    ArrayList<String> uidList = new ArrayList<>();
    HashMap<String, String> userAnswersCount = new HashMap<>();
    HashMap<String, String> realAnswers = new HashMap<>();
    ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        listView = root.findViewById(R.id.quiz_answers_list);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if (ds.getKey().equals("QuizAnswers")) {
                        for (DataSnapshot quizAnswersSnapshot : ds.getChildren()) {
                            String uid = quizAnswersSnapshot.getKey();
                            uidList.add(uid);
                            HashMap<String, String> ans = new HashMap<>();
                            for (DataSnapshot answers : quizAnswersSnapshot.getChildren()) {
                                ans.put(answers.getKey(), answers.getValue().toString());
                            }

                            userAnswers.put(uid, ans);
                            Log.d("test", "onDataChange: "+ userAnswers);
                        }
                    } else if (ds.getKey().equals("QuizRealAnswers")) {
                        for (DataSnapshot realAnswersSnapshot : ds.getChildren()) {
                            realAnswers.put(realAnswersSnapshot.getKey(), realAnswersSnapshot.getValue().toString());
                        }
                    } else if (ds.getKey().equals("Usernames")) {
                        for (DataSnapshot users: ds.getChildren()) {
                            userUIDmap.put(users.getKey(), users.getValue().toString());
                        }
                    }
                }
                tallyAnswers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    private void displayResults() {
        ListViewAdapter adapter = new ListViewAdapter(userAnswersCount);
        listView.setAdapter(adapter);
    }

    private void tallyAnswers() {
        for (int i = 0; i < userAnswers.size(); i++) {
            HashMap answer = (HashMap)userAnswers.get(uidList.get(i));
            int correctAnswer = 0;
            for (String key: realAnswers.keySet()) {
                if (answer.containsKey(key)) {
                    Log.d("test", "tallyAnswers: "+ answer.get(key) + ", " +realAnswers.get(key));
                    if (answer.get(key).equals(realAnswers.get(key))) {
                        correctAnswer++;
                    }
                }
            }
            userAnswersCount.put(userUIDmap.get(uidList.get(i)), String.valueOf(correctAnswer));
        }
        Log.d("test", "tallyAnswers: "+ userAnswersCount);
        displayResults();
    }
}