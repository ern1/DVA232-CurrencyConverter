package com.example.ern123.currencyconverter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by ernsu on 2017-11-16.
 */

public class FragmentCurrencyConverter extends Fragment implements View.OnClickListener {
    private Spinner SpinnerFrom;
    private Spinner SpinnerTo;
    private EditText EditTextInput;
    private TextView TextViewAmounts;
    private AlphaAnimation ButtonClick;
    private Context mContext;

    private void ConvertCurrency(){
        if (!(EditTextInput.getText().toString().matches(""))) {
            // Get all the values from the views
            String CurrencyTypeFrom = SpinnerFrom.getSelectedItem().toString();
            String CurrencyTypeTo = SpinnerTo.getSelectedItem().toString();
            Double ExchangeAmountIn = Double.parseDouble(EditTextInput.getText().toString());
            Double ExchangeRate = 1.0;

            // Get exchange rate only if the currencies are different
            if (!(CurrencyTypeFrom.equals(CurrencyTypeTo))){
                String CurrencyPair = CurrencyTypeFrom + "/" + CurrencyTypeTo;
                ExchangeRate = ExchangeRates.ValuesByName.get(CurrencyPair);
            }

            // Temporary
            if(ExchangeRate == null){
                TextViewAmounts.setText(getString(R.string.currency_pair_not_supported));
                return;
            }

            // Calculate amount and display it in TextView
            Double ExchangeAmountOut = ExchangeAmountIn * ExchangeRate;
            DecimalFormat df = new DecimalFormat("#.##");
            String output = df.format(ExchangeAmountIn) + " " + CurrencyTypeFrom + " = " + df.format(ExchangeAmountOut) + " " + CurrencyTypeTo;
            TextViewAmounts.setText(output);
        }
        else{
            TextViewAmounts.setText("");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_currency_converter, container, false);

        mContext = getContext();

        // Initialize view variables
        SpinnerFrom = view.findViewById(R.id.spinnerFromCurrency);
        SpinnerTo = view.findViewById(R.id.spinnerToCurrency);
        EditTextInput = view.findViewById(R.id.editTextInput);
        TextViewAmounts = view.findViewById(R.id.textViewAmounts);

        // Set animation for swap button
        ButtonClick = new AlphaAnimation(1f, 0.7f);
        ButtonClick.setDuration(300);

        // Set spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, ExchangeRates.Currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        SpinnerFrom.setAdapter(adapter);
        SpinnerTo.setAdapter(adapter);
        SpinnerFrom.setSelection(ExchangeRates.GetDefaultCurrency(GetLocation()));


        // Set listeners for button(s)
        Button b1 = view.findViewById(R.id.SwapButton);
        b1.setOnClickListener(this);

        // Listeners for spinners
        SpinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { ConvertCurrency(); }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ConvertCurrency();
            }
        });

        SpinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { ConvertCurrency(); }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ConvertCurrency();
            }
        });

        EditTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { ConvertCurrency(); }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { ConvertCurrency(); }
            @Override
            public void afterTextChanged(Editable s) {
                ConvertCurrency();
            }
        });

        return view;
    }

    // Removes EditText input error (if there is one) when view becomes visible to user
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(EditTextInput != null){
                EditTextInput.setError(null);
            }
        }
    }

    // Return a 2-letter country code
    public String GetLocation(){
        try {
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
            return tm.getNetworkCountryIso().toUpperCase(); // Returns the ISO country code equivalent of the current registered operator's Mobile Country Code. Should even work without SIM.
        }
        catch (Exception e) {
            Log.e("tm ex", e.getMessage());
        }

        // In case of exception, return country from locale instead
        return Resources.getSystem().getConfiguration().locale.getCountry();
        //return mContext.getResources().getConfiguration().locale.getCountry(); // Same return value as above but need context, so cannot be used in a static context.
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.SwapButton:
                // Add animation to button
                view.startAnimation(ButtonClick);

                // Swap currencies
                int From = SpinnerFrom.getSelectedItemPosition();
                int To = SpinnerTo.getSelectedItemPosition();
                SpinnerFrom.setSelection(To);
                SpinnerTo.setSelection(From);

                break;
        }
    }


}
