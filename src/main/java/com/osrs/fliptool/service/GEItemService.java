package com.osrs.fliptool.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import com.osrs.fliptool.service.exception.ApiUrlConnectionException;
import com.osrs.fliptool.service.exception.ApiUrlCreationException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class GEItemService {
    private static final String API_HOST_URL = "https://rsbuddy.com/exchange/summary.json";
    private static final String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    private String proxyAddress;
    private int proxyPort;
    private boolean useProxy;

    GEItemService(){
        useProxy = false;
    }

    public GEItemService(String proxyAddress, int proxyPort){
        this.proxyAddress = proxyAddress;
        this.proxyPort = proxyPort;
        useProxy = true;
    }

    public List<GEItem> generateFlipList() throws ApiUrlConnectionException {
        return generateFlipList(Integer.MAX_VALUE, 0);
    }

    public List<GEItem> generateFlipList(int maxItemPrice, int minPercentMargin) throws ApiUrlConnectionException {

        List<GEItem> result = new ArrayList<>();
        JSONObject itemSummary = getItemList(API_HOST_URL, useProxy);

        for(String itemId : itemSummary.keySet()){
            JSONObject jsonItem = itemSummary.getJSONObject(itemId);

            String itemName = jsonItem.getString("name");
            int id = jsonItem.getInt("id");
            int buyAverage = jsonItem.getInt("buy_average");
            int sellAverage = jsonItem.getInt("sell_average");
            int buyQuantity = jsonItem.getInt("buy_quantity");
            int sellQuantity = jsonItem.getInt("sell_quantity");
            boolean isMembers = jsonItem.getBoolean("members");

            if (buyAverage <= maxItemPrice) {
                double profitPercent = getProfitPercent(buyAverage, sellAverage);

                if (profitPercent >= minPercentMargin) {
                    GEItem geitem = new GEItem(itemName,
                                               id,
                                               buyAverage,
                                               sellAverage,
                                               buyQuantity,
                                               sellQuantity,
                                               isMembers);
                    result.add(geitem);
                }
            }
        }

        return result;
    }

    public List<GEItem> generateOfflineFlipList() {
        List<GEItem> geItems = new ArrayList<>();
        geItems.add(new GEItem("Foo", 1, 500, 550, 1000, 2000, true));
        geItems.add(new GEItem("Bar", 2, 100, 750, 2000, 2000, true));
        geItems.add(new GEItem("Baz", 3, 400, 550, 100, 5, true));
        geItems.add(new GEItem("Quix", 4, 500, 550, 1000, 2000, true));
        geItems.add(new GEItem("Will's dong", 1, Integer.MAX_VALUE, 122, 1000, 2000, true));
        geItems.add(new GEItem("test1", 5, 500, 990, 2, 55, true));
        geItems.add(new GEItem("test2", 6, 20000, 30000, 100, 200, true));
        return geItems;
    }

    private int getProfitPercent(int buyAverage, int sellAverage) {
        return (buyAverage > 0)
                ? (Math.abs(sellAverage - buyAverage)/buyAverage) * 100
                : -1;
    }

    private JSONObject getItemList(String urlString, boolean useProxy) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new ApiUrlCreationException("Unable instantiate URL out of provided URL string: " + urlString, e);
        }

        HttpURLConnection connection = null;
        String line;
        StringBuilder responseBuffer = new StringBuilder();
        BufferedReader in;
        try {
            connection = useProxy ? openProxyConnection(url) : (HttpURLConnection) url.openConnection();
            connection.setRequestProperty(HttpHeaders.USER_AGENT, USER_AGENT_VALUE);
            connection.connect();

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = in.readLine()) != null) {
                responseBuffer.append(line);
            }
        } catch (IOException e) {
            throw new ApiUrlConnectionException("Unable to connect to provided URL: " + url, e);
        } finally {
            closeConnection(connection);
        }

        return new JSONObject(responseBuffer.toString());
    }

    private void closeConnection(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }

    private HttpURLConnection openProxyConnection(URL url) throws IOException {
        return (HttpURLConnection)url.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress, proxyPort)));
    }

}
