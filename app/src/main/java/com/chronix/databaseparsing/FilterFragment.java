package com.chronix.databaseparsing;


import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterFragment extends android.app.Fragment {

    private static final String LINK_FILTER_VALUES = "http://192.168.1.4/filter.php";
    private ArrayAdapter<String> categoriesAdapter;
    private ListView categoryListView;
    private ArrayAdapter<String> valuesAdaper;
    private ListView valuesListView;
    private String[] categories;
    private ProgressBar waitForValuesProgressBar;
    private JsonFilterValues jsonFilterValues;
    private GetFilterValues getFilterValues;
    private TextView valuesTextView;
    private Adapter adapter;
    private TextView reference;
    private ArrayList<String> brand = null;
    private ArrayList<String> model = null;
    private Button clear;
    private Button apply;
    public FilterFragment() {
        // Required empty public constructor
    }

    void setUpAdapter(String name) {
        adapter = null;
        valuesTextView.setText(name);
        switch (name) {
            case "Brand":
                reference.setText(name);
                //valuesAdaper = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_checked, jsonFilterValues.getBrand());
                //Log.i("info", String.valueOf(jsonFilterValues.getBrand().size()));
                adapter = new Adapter(getActivity(), jsonFilterValues.getBrand());
                break;
            case "Model":
                reference.setText(name);
                //valuesAdaper = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_checked, jsonFilterValues.getModel());
                adapter = new Adapter(getActivity(), jsonFilterValues.getModel());
                break;
            case "Year":
                reference.setText(name);
                List<String> year = generateYear(jsonFilterValues.getYear_max(), jsonFilterValues.getYear_min());
                //valuesAdaper = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_checked, year);
                adapter = new Adapter(getActivity(), year);
                break;
            case "Price":
                reference.setText(name);
                List<String> price = generatePrice(jsonFilterValues.getPrice_max(), jsonFilterValues.getPrice_min());
                //valuesAdaper = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_checked, price);
                adapter = new Adapter(getActivity(), price);
                break;
            default:
        }
        valuesListView.setAdapter(adapter);
    }

    List<String> generateYear(String maxYear, String minYear) {
        int maxYearInt = Integer.parseInt(maxYear);
        int minYearInt = Integer.parseInt(minYear);
        List<String> year = new ArrayList<>();
        int i = maxYearInt;

        while (i > minYearInt) {
            String temp = i-2+"-"+String.valueOf(i);
            //Log.i("info", temp);
            year.add(temp);
            i = i-2;
        }
        year.add(i - 2 + "-" + String.valueOf(i));
        return year;
    }

    List<String> generatePrice(String maxPrice, String minPrice) {
        int maxPriceInt = Integer.parseInt(maxPrice);
        int minPriceInt = Integer.parseInt(minPrice);
        List<String> price = new ArrayList<>();
        int i = maxPriceInt;

        while (i > minPriceInt) {
            String temp = i-20000+"-"+String.valueOf(i);
            //Log.i("info", temp);
            price.add(temp);
            i = i-20000;
        }
        price.add(i-20000+"-"+String.valueOf(i));
        return price;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categories = new String[] {"Brand", "Model", "Year", "Price"};
        categoriesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item, categories);
        getFilterValues = new GetFilterValues();
        getFilterValues.execute((Void) null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_filter, container, false);
        reference = (TextView) layout.findViewById(R.id.reference);
        clear = (Button) layout.findViewById(R.id.clear);
        apply = (Button) layout.findViewById(R.id.apply);
        brand = new ArrayList<>();
        model = new ArrayList<>();
        categoryListView = (ListView) layout.findViewById(R.id.filter_category);
        valuesListView = (ListView) layout.findViewById(R.id.filter_values);
        waitForValuesProgressBar = (ProgressBar) layout.findViewById(R.id.wait_for_values);
        valuesTextView = (TextView) layout.findViewById(R.id.values_textView);
        categoryListView.setAdapter(categoriesAdapter);
        getActivity().setTitle("Filters");

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showProgressCircle(true);
                //Toast.makeText(getActivity(), String.valueOf(position)+" "+((TextView)view).getText().toString(), Toast.LENGTH_SHORT).show();
                setUpAdapter(((TextView) view).getText().toString());
                showProgressCircle(false);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brand.clear();
                model.clear();
                setUpAdapter(reference.getText().toString());
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return layout;
    }

    public void showProgressCircle(final boolean show) {

        if (!show) {        // hide progress bar
            waitForValuesProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            valuesListView.setVisibility(show ? View.GONE : View.VISIBLE);
        } else {        // show progress bar
            waitForValuesProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            valuesListView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    class Adapter extends BaseAdapter {

        List<String> list;
        LayoutInflater inflater;
        Context context;
        CheckBox checkBox;

        public Adapter(Context contextParent, List<String> stringArrayList) {
            list = stringArrayList;
            inflater = (LayoutInflater) contextParent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            context = contextParent;
        }
        @Override
        public int getCount() {
            //Log.i("info", String.valueOf(list.size()));
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            v = inflater.inflate(R.layout.listview_layout, parent, false);
            checkBox = (CheckBox) v.findViewById(R.id.cbBox);
            checkBox.setText(list.get(position));
            if(brand.contains(checkBox.getText().toString()))
                checkBox.setChecked(true);
            if(model.contains(checkBox.getText().toString()))
                checkBox.setChecked(true);
            //v.setOnClickListener(new onItemClickListener(position));


            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(getActivity(), reference.getText().toString() + ": " + buttonView.getText(), Toast.LENGTH_SHORT).show();
                        if (reference.getText().toString() == "Brand")
                            brand.add(buttonView.getText().toString());
                        if (reference.getText().toString() == "Model")
                            model.add(buttonView.getText().toString());
                    }
                }
            });
            return v;
        }
    }

    class GetFilterValues extends AsyncTask<Void, Void, Response> {

        private OkHttpClient client;
        private Gson gson;

        GetFilterValues() {
            client = new OkHttpClient();
            gson = new Gson();
        }

        Response run() throws Exception {
            //Log.i("info", "in run");
            Request filterValues = new Request.Builder()
                    .url(LINK_FILTER_VALUES)
                    .build();
            Response filterValuesResponse = client.newCall(filterValues).execute();
            if (!filterValuesResponse.isSuccessful())
                throw new IOException("Unexpected code " + filterValuesResponse);
            return filterValuesResponse;
        }
        @Override
        protected Response doInBackground(Void... params) {
            Response response = null;
            try {
                response = run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    jsonFilterValues = gson.fromJson(response.body().string(), JsonFilterValues.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class JsonFilterValues {

        /**
         * price_max : 80000
         * price_min : 40000
         * year_max : 2014
         * year_min : 2012
         * brand : ["Bajaj","Yamaha","Honda"]
         * model : ["Pulsur 200NS","R15","Shine"]
         */

        private String price_max;
        private String price_min;
        private String year_max;
        private String year_min;
        private List<String> brand;
        private List<String> model;

        public void setPrice_max(String price_max) {
            this.price_max = price_max;
        }

        public void setPrice_min(String price_min) {
            this.price_min = price_min;
        }

        public void setYear_max(String year_max) {
            this.year_max = year_max;
        }

        public void setYear_min(String year_min) {
            this.year_min = year_min;
        }

        public void setBrand(List<String> brand) {
            this.brand = brand;
        }

        public void setModel(List<String> model) {
            this.model = model;
        }

        public String getPrice_max() {
            return price_max;
        }

        public String getPrice_min() {
            return price_min;
        }

        public String getYear_max() {
            return year_max;
        }

        public String getYear_min() {
            return year_min;
        }

        public List<String> getBrand() {
            return brand;
        }

        public List<String> getModel() {
            return model;
        }
    }
}
