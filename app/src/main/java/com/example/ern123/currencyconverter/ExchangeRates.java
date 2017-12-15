package com.example.ern123.currencyconverter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ernsu on 2017-11-16.
 */

public class ExchangeRates{
    static final String[] Currencies = { "EUR", "SEK", "USD", "GBP", "CNY", "JPY", "KRW"};
    static final String[] Countries = { "EU", "SE", "US", "GB", "CN", "JP", "KR"};
    static final Map<String, Double> ValuesByName = new HashMap<>();

    static int GetDefaultCurrency(String CountryCode){
        // The index of the country in the Countries has the same index as it's currency in Currencies
        return Arrays.asList(Countries).indexOf(CountryCode);
    }
}
