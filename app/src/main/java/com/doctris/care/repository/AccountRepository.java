package com.doctris.care.repository;

import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.doctris.care.client.RetrofitClient;
import com.doctris.care.entities.Account;
import com.doctris.care.domain.AccountResponse;
import com.doctris.care.storage.SharedPrefManager;
import com.doctris.care.utils.LoggerUtil;
import com.doctris.care.utils.RequestBodyUtil;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountRepository {
    private static AccountRepository instance;
    private MutableLiveData<String> status;
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String CONFIRM_PASSWORD = "passwordConfirm";
    private static final String SUCCESS = "success";
    private static final String TOKEN = "token";
    private static final String ERROR = "error";

    public static AccountRepository getInstance() {
        if (instance == null) {
            instance = new AccountRepository();
        }
        return instance;
    }

    public LiveData<String> login(Account account) {
        status = new MutableLiveData<>();
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put(EMAIL, account.getEmail());
        jsonParams.put(PASSWORD, account.getPassword());
        Call<AccountResponse> call = RetrofitClient.getInstance().getApi().login(RequestBodyUtil.createRequestBody(jsonParams));
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call, @NonNull Response<AccountResponse> response) {
                if (response.isSuccessful()) {
                    AccountResponse accountResponse = response.body();
                    assert accountResponse != null;
                    if (accountResponse.getAccount().isVerified()) {
                        SharedPrefManager.getInstance().put(TOKEN, accountResponse.getToken());
                        SharedPrefManager.getInstance().put("account", accountResponse.getAccount());
                        status.setValue(SUCCESS);
                    } else {
                        status.setValue("not verified");
                    }

                } else {
                    status.setValue(ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable t) {
                LoggerUtil.e(t.getMessage());
                status.setValue(t.getMessage());
            }
        });
        return status;
    }

    public LiveData<String> refreshToken() {
        status = new MutableLiveData<>();
        Call<AccountResponse> call = RetrofitClient.getInstance().getApi().refreshToken("User " + SharedPrefManager.getInstance().get(TOKEN, String.class));
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call, @NonNull Response<AccountResponse> response) {
                if (response.isSuccessful()) {
                    AccountResponse accountResponse = response.body();
                    assert accountResponse != null;
                    SharedPrefManager.getInstance().put(TOKEN, accountResponse.getToken());
                    status.setValue(SUCCESS);
                } else {
                    status.setValue(ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable t) {
                LoggerUtil.e(t.getMessage());
                status.setValue(t.getMessage());
            }
        });
        return status;
    }

    public LiveData<String> requestPasswordReset(String email) {
        status = new MutableLiveData<>();
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put(EMAIL, email);
        Call<Account> call = RetrofitClient.getInstance().getApi().requestForgot(RequestBodyUtil.createRequestBody(jsonParams));
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(@NonNull Call<Account> call, @NonNull Response<Account> response) {
                if (response.isSuccessful()) {
                    status.setValue(SUCCESS);
                } else {
                    status.setValue(ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Account> call, @NonNull Throwable t) {
                LoggerUtil.e(t.getMessage());
                status.setValue(t.getMessage());
            }
        });
        return status;
    }

    public LiveData<String> forgot(String token, String password, String confirmPassword) {
        status = new MutableLiveData<>();
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put(TOKEN, token);
        jsonParams.put(PASSWORD, password);
        jsonParams.put(CONFIRM_PASSWORD, confirmPassword);
        Call<AccountResponse> call = RetrofitClient.getInstance().getApi().forgot(RequestBodyUtil.createRequestBody(jsonParams));
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call, @NonNull Response<AccountResponse> response) {
                if (response.isSuccessful()) {
                    status.setValue(SUCCESS);
                } else {
                    status.setValue("token expired");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable t) {
                LoggerUtil.e(t.getMessage());
            }
        });
        return status;
    }

    public LiveData<String> register(String email, String password, String passwordConfirm) {
        status = new MutableLiveData<>();
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put(EMAIL, email);
        jsonParams.put(PASSWORD, password);
        jsonParams.put(CONFIRM_PASSWORD, passwordConfirm);
        Call<AccountResponse> call = RetrofitClient.getInstance().getApi().register(RequestBodyUtil.createRequestBody(jsonParams));
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call, @NonNull Response<AccountResponse> response) {
                if (response.isSuccessful()) {
                    status.setValue(SUCCESS);
                } else {
                    if (response.code() == 400) {
                        status.setValue("email already exists");
                    } else {
                        status.setValue(ERROR);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable t) {
                LoggerUtil.e(t.getMessage());
                status.setValue(t.getMessage());
            }
        });
        return status;
    }

    public void requestVerification(String email) {
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put(EMAIL, email);
        Call<Void> call = RetrofitClient.getInstance().getApi().requestVerification(RequestBodyUtil.createRequestBody(jsonParams));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                // no need to handle response
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                LoggerUtil.e(t.getMessage());
            }
        });
    }

    public LiveData<String> confirmVerification(String token){
        status = new MutableLiveData<>();
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put(TOKEN, token);
        Call<AccountResponse> call = RetrofitClient.getInstance().getApi().verification(RequestBodyUtil.createRequestBody(jsonParams));
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call, @NonNull Response<AccountResponse> response) {
                if (response.isSuccessful()) {
                    status.setValue(SUCCESS);
                } else {
                    status.setValue(ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable t) {
                LoggerUtil.e(t.getMessage());
                status.setValue(t.getMessage());
            }
        });
        return status;
    }

}
