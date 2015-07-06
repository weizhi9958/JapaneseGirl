package com.zhiz.japanesegirl;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Zhi on 2015/6/20.
 */
public class Give_4X4_Top10_All extends Fragment {

    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> dataList;

    // url to get all products list
    private static String url_all_data = "http://wesley.huhu.tw/japan/get_4x4top10_data.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TABLE = "rank";
    private static final String TAG_NAME = "name";
    private static final String TAG_TIME = "time";

    // products JSONArray
    JSONArray data = null;

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

        dataList = new ArrayList<HashMap<String, String>>();

        new LoadTop10_2x2_Data().execute();
/*
        String[] arr = new String[]{
                "A", "B", "C", "D", "E", "F", "G"
        };
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, arr);
        listView.setAdapter(adapter);
        */
        return v;
    }

    class LoadTop10_2x2_Data extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(v.getContext());
            pDialog.setMessage("資料讀取中，請稍後..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> param = new ArrayList<NameValuePair>();

            try {
                JSONObject json = jParser.makeHttpRequest(url_all_data, "GET", param);
                // Check your log cat for JSON reponse
                Log.d("All Data: ", json.toString());

                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    data = json.getJSONArray(TAG_TABLE);
                    int count=1;
                    for (int i = 0; i < data.length(); i++) {
                        try {
                            JSONObject c = data.getJSONObject(i);
                            String name = new String(c.getString(TAG_NAME).getBytes("ISO-8859-1"), "UTF-8");
                            String time = c.getString(TAG_TIME);

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put("Number",Integer.toString(count));
                            map.put(TAG_NAME,name);
                            map.put(TAG_TIME,time);

                            dataList.add(map);
                            count++;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            Handler mHandler = new Handler();
            mHandler.post(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(v.getContext(),
                            dataList,
                            R.layout.list_item,
                            new String[]{"Number", TAG_NAME, TAG_TIME},
                            new int[]{R.id.txtrank_num, R.id.txtrank_name, R.id.txtrank_time});

                    listView.setAdapter(adapter);
                }
            });

        }
    }



}