package com.example.myfinal;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteApiService {

    @GET("random")
    Call<Quote> getRandomQuote();
}