package com.deighd.school58.utils;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.deighd.school58.balance.Balance;
import com.deighd.school58.balance.BalanceResponse;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Connection {
    private static final String TAG = "Connection";

    public BalanceResponse getBalanceResponse(String cardId) {
        BalanceResponse balanceResponse = new BalanceResponse();
        ArrayList<Balance> balances = new ArrayList<>();
        try {
            final String url = "http://xn--58-6kc3bfr2e.xn--p1ai/ajax/";
            Log.d(TAG, "getBalance. Get url data " + url);

            String fields = "card=" + cardId +
                    "&" + "act=FreeCheckBalance";
            Response response = doPost(url, fields);
            if(!response.isSuccessful()) {
                return null;
            }

            String responseBodyRaw = response.body().string();
            JSONObject responseBodyJson = new JSONObject(responseBodyRaw);
            if(responseBodyJson.has("text") && responseBodyJson.has("type")) {
                String type = responseBodyJson.getString("type");
                if(type.equals("error")) {
                    balanceResponse.setError(true);
                    balanceResponse.setErrorDescription(responseBodyJson.getString("text"));
                }

                String htmlString = responseBodyJson.getString("text");
                Document html = Jsoup.parse(htmlString);
                Elements htmlBalances = html.body().getElementsByClass("type_balance");

                for(Element htmlBalance : htmlBalances) {
                    Balance balance = new Balance();

                    String balanceName = htmlBalance
                            .getElementsByClass("name")
                            .get(0)
                            .getElementsByTag("span")
                            .text();
                    balance.setName(balanceName);

                    String[] balanceResidueParts = htmlBalance
                            .getElementsByClass("residue")
                            .get(0)
                            .text()
                            .split(" ");
                    Float balanceResidue = Float.valueOf(balanceResidueParts[0]);
                    balance.setResidue(balanceResidue);

                    String balanceCurrency = balanceResidueParts[1];
                    balance.setCurrency(balanceCurrency);

                    balances.add(balance);
                }
            }
            balanceResponse.setBalances(balances);
            return balanceResponse;
        } catch (Exception e) {
            Log.d(TAG, "Get balance error " + e.getMessage());
            return null;
        }
    }

    private Response doPost(String desiredUrl, String urlParameters) {
        return doHttpDataUrlConnectionAction(desiredUrl, "POST", urlParameters);
    }

    @Nullable
    private Response doHttpDataUrlConnectionAction(
            String desiredUrl,
            @NotNull String method,
            String urlParameters
    )
    {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            Request.Builder builder = new Request.Builder();

            builder.url(desiredUrl);

            builder.addHeader("Accept", "application/json");
            builder.addHeader("Content-Type", "application/x-www-form-urlencoded");

            if (method.equals("POST")) {
                RequestBody body = RequestBody.create(urlParameters.getBytes(StandardCharsets.UTF_8));
                builder.post(body);
            }

            Request request = builder.build();
            Response response;

            response = client.newCall(request).execute();
            return response;
        }  catch (Exception e) {
            Log.d(TAG, "Connection error " + e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }
}
