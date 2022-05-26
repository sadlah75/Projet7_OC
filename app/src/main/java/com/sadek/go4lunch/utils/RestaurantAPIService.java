package com.sadek.go4lunch.utils;

import com.sadek.go4lunch.BuildConfig;
import com.sadek.go4lunch.model.NearByPlace;
import com.sadek.go4lunch.model.NearByPlacesDetails;
import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantAPIService {

    @GET("api/place/nearbysearch/json?sensor=true&key=" + BuildConfig.GOOGLE_API_KEY)
    Observable<NearByPlace> getNearByPlace(@Query("location") String location,
                                           @Query("type") String type,
                                           @Query("radius") int radius,
                                           @Query("key") String key);

    @GET("api/place/details/json?key=" + BuildConfig.GOOGLE_API_KEY)
    Observable<NearByPlacesDetails> getNearByPlaceDetails(@Query("key") String key,
                                                          @Query("place_id") String placeId);

    Retrofit retrofitNearByPlace = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory((RxJava2CallAdapterFactory.create()))
            .build();

    Retrofit retrofitNearByPlaceDetails = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory((RxJava2CallAdapterFactory.create()))
            .build();
}
