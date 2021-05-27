package com.example.myapplicationtt;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;



public class PlusFragment extends Fragment{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextInputEditText title, v_url,s_url;
    Button plus;
    private PutDataToRVListener putDataToRV;

    public PlusFragment(PutDataToRVListener putDataToRV) {
        this.putDataToRV = putDataToRV;
    }
    public PlusFragment(){
    }

    public static PlusFragment newInstance(String param1, String param2) {
        PlusFragment fragment = new PlusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_plus, container, false);
        title=view.findViewById(R.id.ett);
        v_url=view.findViewById(R.id.etvurl);
        s_url=view.findViewById(R.id.etsturl);
        plus=view.findViewById(R.id.button);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper myDatabaseHelper= new MyDatabaseHelper(view.getContext());
                myDatabaseHelper.addVideo(title.getText().toString().trim(),
                        v_url.getText().toString().trim(),
                        s_url.getText().toString().trim());
                putDataToRV.putDataToArray();
            }
        });
        return view;
    }
}