package com.doctris.care.repository;

import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.doctris.care.client.RetrofitClient;
import com.doctris.care.domain.ListResponse;
import com.doctris.care.entities.Booking;
import com.doctris.care.entities.Patient;
import com.doctris.care.storage.SharedPrefManager;
import com.doctris.care.utils.LoggerUtil;
import com.doctris.care.utils.RequestBodyUtil;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingRepository {
    private static BookingRepository instance;
    private MutableLiveData<String> status;
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    public static BookingRepository getInstance() {
        if (instance == null) {
            instance = new BookingRepository();
        }
        return instance;
    }

    public LiveData<ListResponse<Booking>> getBookingHistory(int page, int perPage, String sort, String filter) {
        MutableLiveData<ListResponse<Booking>> bookings = new MutableLiveData<>();
        status = new MutableLiveData<>();
        Call<ListResponse<Booking>> call = RetrofitClient.getInstance().getApi().getBookingHistory("User " + SharedPrefManager.getInstance().get("token", String.class), page, perPage, sort, filter, "patient, service, doctor");
        call.enqueue(new Callback<ListResponse<Booking>>() {
            @Override
            public void onResponse(@NonNull Call<ListResponse<Booking>> call, @NonNull Response<ListResponse<Booking>> response) {
                if (response.isSuccessful()) {
                    ListResponse<Booking> bookingResponse = response.body();
                    assert bookingResponse != null;
                    bookings.setValue(bookingResponse);
                    status.setValue(SUCCESS);
                } else {
                    status.setValue(ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListResponse<Booking>> call, @NonNull Throwable t) {
                LoggerUtil.e(t.getMessage());
                status.setValue(ERROR);
            }
        });
        return bookings;
    }

    public LiveData<Booking> getBookingById(String id) {
        MutableLiveData<Booking> booking = new MutableLiveData<>();
        status = new MutableLiveData<>();
        Call<Booking> call = RetrofitClient.getInstance().getApi().getBookingHistoryDetail("User " + SharedPrefManager.getInstance().get("token", String.class), id, "patient, service, doctor");
        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(@NonNull Call<Booking> call, @NonNull Response<Booking> response) {
                if (response.isSuccessful()) {
                    Booking bookingResponse = response.body();
                    assert bookingResponse != null;
                    booking.setValue(bookingResponse);
                    status.setValue(SUCCESS);
                } else {
                    status.setValue(ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Booking> call, @NonNull Throwable t) {
                LoggerUtil.e(t.getMessage());
                status.setValue(ERROR);
            }
        });
        return booking;
    }

    public LiveData<Booking> saveBooking(Booking booking) {
        MutableLiveData<Booking> bookingResponse = new MutableLiveData<>();
        status = new MutableLiveData<>();
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("patient", SharedPrefManager.getInstance().get("patient", Patient.class).getId());
        if (booking.getExpand().getService() != null) {
            jsonParams.put("service", booking.getExpand().getService().getId());
            jsonParams.put("price", booking.getExpand().getService().getPrice());
        } else {
            jsonParams.put("doctor", booking.getExpand().getDoctor().getId());
            jsonParams.put("price", booking.getExpand().getDoctor().getPrice());
        }
        jsonParams.put("date_time", booking.getDateTime());
        jsonParams.put("description", booking.getDescription());
        jsonParams.put("payment_method", booking.getPaymentMethod());
        jsonParams.put("payment_status", booking.getPaymentStatus());
        jsonParams.put("booking_status", booking.getBookingStatus());
        jsonParams.put("feedback", booking.isFeedback()? "true" : "false");

        Call<Booking> call = RetrofitClient.getInstance().getApi().saveBooking("User " + SharedPrefManager.getInstance().get("token", String.class), RequestBodyUtil.createRequestBody(jsonParams));
        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(@NonNull Call<Booking> call, @NonNull Response<Booking> response) {
                if (response.isSuccessful()) {
                    Booking booking = response.body();
                    assert booking != null;
                    bookingResponse.setValue(booking);
                    status.setValue(SUCCESS);
                } else {
                    status.setValue(ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Booking> call, @NonNull Throwable t) {
                LoggerUtil.e(t.getMessage());
                status.setValue(ERROR);
            }
        });
        return bookingResponse;
    }

    public void cancelBooking(String id){
        status = new MutableLiveData<>();
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("booking_status", "cancel");
        jsonParams.put("payment_status", "cancel");
        Call<Void> call = RetrofitClient.getInstance().getApi().cancelBooking("User " + SharedPrefManager.getInstance().get("token", String.class), id, RequestBodyUtil.createRequestBody(jsonParams));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    status.setValue(SUCCESS);
                } else {
                    status.setValue(ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                LoggerUtil.e(t.getMessage());
                status.setValue(ERROR);
            }
        });
    }

    public void feedbackBooking(String id){
        status = new MutableLiveData<>();
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("feedback", "true");
        Call<Void> call = RetrofitClient.getInstance().getApi().feedbackBooking("User " + SharedPrefManager.getInstance().get("token", String.class), id, RequestBodyUtil.createRequestBody(jsonParams));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    status.setValue(SUCCESS);
                } else {
                    status.setValue(ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                LoggerUtil.e(t.getMessage());
                status.setValue(ERROR);
            }
        });
    }

}
