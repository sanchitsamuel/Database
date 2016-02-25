package com.chronix.databaseparsing;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends android.app.Fragment {

    RecyclerView mListRecyclerView;
    private RecyclerListAdapter mRecyclerListAdapter;
    public ListFragment() {
    }

    public static List <RecyclerList> recyclerListData() {
        List <RecyclerList> list = new ArrayList<>();
        int[] icons = {R.drawable.ic_brand, R.drawable.ic_model};
        String[] brandModel = {"KTM Duke 200", "Yamaha R15"};
        String[] year = {"2014, 2012"};

        for (int i = 0; i < icons.length && i < brandModel.length && i < year.length; i++) {
            RecyclerList current = new RecyclerList();
            current.brandModel = brandModel[i];
            current.imageID = icons[i];
            current.year = year[i];
            list.add(current);
        }
        return list;
    }

    @Override
    public void onResume() {
        super.onResume();
        // getFragmentManager()
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Bike DB");
        View layout = inflater.inflate(R.layout.fragment_list, container, false);
        mListRecyclerView = (RecyclerView) layout.findViewById(R.id.fragment_recycler_view);
        mRecyclerListAdapter = new RecyclerListAdapter(getActivity(), recyclerListData());
        mListRecyclerView.setAdapter(mRecyclerListAdapter);
        mListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return layout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }
}