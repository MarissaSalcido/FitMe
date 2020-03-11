package com.example.fitme;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        loadData(view, savedInstanceState);

    }

    //calculates the BMR according to the formulas from here:
    // (https://www.everydayhealth.com/weight/boost-weight-loss-by-knowing-your-bmr.aspx)
    // assumes weight in pounds, height in inches, and age in years
    public double calcBMR(long weight, long height, long age, String gender){

        double BMR;
        if (gender.toLowerCase().equals("male"))
            BMR = 66 + (6.23 * weight) + (12.7 * height) - (6.8 * age);
        else
            BMR = 655 + (4.35 * weight) + (4.7 * height) - (4.7 * age);

        return BMR;
    }

    //loads data from the FireStore db into the profile strings displayed in the app
    public void loadData(final View view, @Nullable Bundle savedInstanceState){

        final String UUID = ((MainActivity)getActivity()).get_uuid(getContext());
        FirebaseFirestore db = ((MainActivity)getActivity()).getFS();

        db.collection("users").document(UUID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()){

                            Map<String,Object> data = document.getData();

                            long weight = (long)data.get("weight");
                            long height = (long)data.get("height");
                            long age = (long)data.get("age");
                            String gender = (String)data.get("gender");
                            double BMR = calcBMR(weight, height, age, gender);

                            //update the viewable text with the values from FireBase
                            ((TextView)view.findViewById(R.id.name)).setText((String)data.get("name"));

                            ((TextView)view.findViewById(R.id.age)).setText(String.format("%d yrs.", age));

                            ((TextView)view.findViewById(R.id.gender)).setText((String)gender);

                            ((TextView)view.findViewById(R.id.height)).setText(String.format("%d in.", height));

                            ((TextView)view.findViewById(R.id.weight)).setText(String.format("%d lbs.", weight));

                            ((TextView)view.findViewById(R.id.bmr)).setText(String.format("%.1f", BMR));

                            System.out.println("Successfully loaded data for profile view from Firestore");

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
    }

}
