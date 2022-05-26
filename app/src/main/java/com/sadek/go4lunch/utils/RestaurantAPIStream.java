package com.sadek.go4lunch.utils;

import com.sadek.go4lunch.model.NearByPlace;
import com.sadek.go4lunch.model.NearByPlacesDetails;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public interface RestaurantAPIStream {

    static Observable<NearByPlace> streamFetchNearByPlace(String location,String type,int radius, String key) {

        RestaurantAPIService restaurantAPIService = RestaurantAPIService.retrofitNearByPlace.create(RestaurantAPIService.class);
        Observable<NearByPlace> result = restaurantAPIService.getNearByPlace(location,type,radius,key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
        return result;
    }

    static Observable<NearByPlacesDetails> streamFetchNearByPlaceDetails(String key, String placeId) {
        RestaurantAPIService restaurantAPIService = RestaurantAPIService.retrofitNearByPlaceDetails.create(RestaurantAPIService.class);
        Observable<NearByPlacesDetails> result = restaurantAPIService.getNearByPlaceDetails(key, placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
        return result;
    }
}
