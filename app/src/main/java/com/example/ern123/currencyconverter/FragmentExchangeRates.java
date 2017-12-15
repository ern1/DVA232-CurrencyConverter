package com.example.ern123.currencyconverter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ernsu on 2017-11-16.
 */

public class FragmentExchangeRates extends Fragment {
    ListView lv;
    SwipeRefreshLayout srl;
    FragmentActivity mActivity;
    Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_exchange_rates, container, false);
        lv = view.findViewById(R.id.ListView);
        srl = view.findViewById(R.id.swipeRefresh);

        mActivity = getActivity();
        mContext = getContext();

        updateExchangeRates();

        srl.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        updateExchangeRates();
                    }
                }
        );

        return view;
    }

    // Hides keyboard when view becomes visible to user
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(mContext.INPUT_METHOD_SERVICE);
            if(imm != null)
                imm.hideSoftInputFromWindow(lv.getWindowToken(), 0);
        }
    }

    public void updateList() {
        // When the method is called from a worker thread, runOnUIThread is needed to access any views
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                DecimalFormat df = new DecimalFormat("#.#####");
                List<HashMap<String, String>> fillMaps = new ArrayList<>();

                for (Map.Entry<String, Double> entry : ExchangeRates.ValuesByName.entrySet()) {

                    HashMap<String, String> map = new HashMap<>();

                    // Get key/value pair as strings
                    String key = entry.getKey();
                    String value = df.format(entry.getValue());

                    map.put("CurrencyPair", key);
                    map.put("ExchangeRate", value);
                    fillMaps.add(map);
                }

                // Sort the map by CurrencyPair
                Collections.sort(fillMaps, new Comparator<Map<String, String>>() {
                    public int compare(Map<String, String> m1, Map<String, String> m2) {
                        return m1.get("CurrencyPair").compareTo(m2.get("CurrencyPair"));
                    }
                });

                // R.id.item1 and R.id.item2 are the columns from R.layout.list_item
                // E.g. values with from columns in fillMaps with "CurrencyPair" are placed in R.id.item1
                SimpleAdapter adapter = new SimpleAdapter(getContext(), fillMaps, R.layout.list_item, new String[] { "CurrencyPair", "ExchangeRate" }, new int[] { R.id.item1, R.id.item2 });
                lv.setAdapter(adapter);
                srl.setRefreshing(false);
            }
        });
    }

    public void updateExchangeRates() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < ExchangeRates.Currencies.length; i++) {
                        URL url = new URL("https://api.fixer.io/latest?base=" + ExchangeRates.Currencies[i] + "");
                        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                        InputStream responseBody = con.getInputStream();

                        BufferedReader streamReader = new BufferedReader(new InputStreamReader(responseBody, "UTF-8"));
                        StringBuilder responseStrBuilder = new StringBuilder();

                        String inputStr;
                        while ((inputStr = streamReader.readLine()) != null)
                            responseStrBuilder.append(inputStr);

                        for (int j = 0; j < ExchangeRates.Currencies.length; j++) {
                            if (!(ExchangeRates.Currencies[i].equals(ExchangeRates.Currencies[j]))) {
                                JSONObject obj = new JSONObject(responseStrBuilder.toString());
                                JSONObject obj2 = obj.getJSONObject("rates");
                                Double ExchangeRate = obj2.getDouble(ExchangeRates.Currencies[j]);
                                ExchangeRates.ValuesByName.put(ExchangeRates.Currencies[i] + "/" + ExchangeRates.Currencies[j], ExchangeRate);
                                //Log.e("daaaadfasdfasdf", ExchangeRates.Currencies[i] + "/" + ExchangeRates.Currencies[j] + "  " + ExchangeRate.toString());
                            }
                        }
                    }
                } catch (Exception ex) {
                    Log.e("CON", ex.getMessage());
                }
                updateList();
            }
        });

    }
}
