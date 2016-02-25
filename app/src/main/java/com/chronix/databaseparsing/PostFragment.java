package com.chronix.databaseparsing;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends android.app.Fragment {

    private static final String LINK="http://192.168.1.4/post.php?";
    private EditText brand;
    private EditText model;
    private EditText year;
    private EditText price;
    private EditText description;
    private Button post;

    private String brandString;
    private String modelString;
    private String yearString;
    private String priceString;
    private String descriptionString;
    String email = "sanchit.samuel@live.com";

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_post, container, false);
        brand = (EditText) layout.findViewById(R.id.brand);
        model = (EditText) layout.findViewById(R.id.model);
        year = (EditText) layout.findViewById(R.id.year);
        price = (EditText) layout.findViewById(R.id.price);
        description = (EditText) layout.findViewById(R.id.description);
        post = (Button) layout.findViewById(R.id.post_button);

        brandString = brand.getText().toString();
        modelString = model.getText().toString();
        yearString = year.getText().toString();
        priceString = price.getText().toString();
        descriptionString = description.getText().toString();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (isValid(brandString) && isValid(modelString) && isValid(yearString) && isValid(priceString) && isValid(descriptionString))
                    attempPost(email, brandString, modelString, yearString, priceString, descriptionString);
            }
        });
        return layout;
    }

    void attempPost(String e, String b, String m, String y, String p, String d) {
        Post post = new Post(e, b, m, y, p, d);
        post.execute((Void) null);
    }

    boolean isValid(String s) {
        return s != null;
    }

    class Post extends AsyncTask<Void, Void, Void> {

        private String emailTask;
        private String brandTask;
        private String modelTask;
        private String yearTask;
        private String priceTask;
        private String descriptionTask;
        private OkHttpClient client;

        Post(String e, String b, String m, String y, String p, String d) {
            emailTask = e;
            brandTask = b;
            modelTask = m;
            yearTask = y;
            priceTask = p;
            descriptionTask = d;
            client = new OkHttpClient();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Request emailCheckRequest = new Request.Builder()
                    .url(LINK+"email="+emailTask+"&brand="+brandTask+"&model="+modelTask+"&year="+yearTask+
                            "&price="+priceTask+"&description="+descriptionTask)
                    .build();
            Log.i("info", LINK+"email="+emailTask+"&brand="+brandTask+"&model="+modelTask+"&year="+yearTask+
                    "&price="+priceTask+"&description="+descriptionTask);
            Response emailCheckResponse = null;
            try {
                emailCheckResponse = client.newCall(emailCheckRequest).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!emailCheckResponse.isSuccessful()) try {
                throw new IOException("Unexpected code " + emailCheckResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
