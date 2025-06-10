package com.example.myfinal;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Base URL dari Quotable API
    private static final String BASE_URL = "https://api.quotable.io/";
    private static Retrofit retrofit = null;

    // Metode untuk mendapatkan instance ApiService
    public static QuoteApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(QuoteApiService.class);
    }
}