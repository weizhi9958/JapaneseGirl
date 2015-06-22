package com.zhiz.japanesegirl;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Zhi on 2015/6/20.
 */
public class Fragment1 extends Fragment {
    private ListView listView;
    private View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        v = inflater.inflate(R.layout.fragment1_list, container, false);
        listView = (ListView) v.findViewById(R.id.listView);
        String[] arr = new String[]{
                "A", "B", "C", "D", "E", "F", "G"
        };
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, arr);
        listView.setAdapter(adapter);
        return v;
    }
}