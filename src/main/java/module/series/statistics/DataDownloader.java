package module.series.statistics;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import core.module.config.ModuleConfig;
import core.util.HOLogger;
import module.series.promotion.HttpDataSubmitter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.*;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class DataDownloader {

    private final static String ALLTID_SERVER_BASEURL = "https://hattid.com/api";
    private final static String POWERRATING_ENDPOINT = "/leagueUnit/%s/teamPowerRatings?page=0&pageSize=8&sortBy=power_rating&sortDirection=asc&statType=statRound&statRoundNumber=%s&season=%s";

    // Singleton.
    private DataDownloader() {}

    private static DataDownloader instance = null;

    public static DataDownloader instance() {
        if (instance == null) {
            instance = new DataDownloader();
        }

        return instance;
    }

    public List<Integer> fetchLeagueTeamPowerRatings(int iLeagueID, int iHTWeek, int iHTSeason) {
        try {
            final OkHttpClient client = initializeHttpsClient();

            String url = ALLTID_SERVER_BASEURL + String.format(POWERRATING_ENDPOINT, iLeagueID, iHTWeek, iHTSeason);


            System.out.println(url);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            List<Integer> supportedLeagues = new ArrayList<>();

            if (response.isSuccessful()) {
                String bodyAsString = response.body().string();
                Gson gson = new Gson();
                JsonObject output = gson.fromJson(bodyAsString, JsonObject.class);

                System.out.println(output);

                response.close();
                return supportedLeagues;
            }

            response.close();
        } catch (Exception e) {
            HOLogger.instance().error(
                    HttpDataSubmitter.class,
                    "Error fetching data from Alltid for league team power ratings: " + e.getMessage()
            );
        }

        return Collections.emptyList();
    }

    private OkHttpClient initializeHttpsClient() throws Exception {
        char[] keystoreCred = new String(Base64.getDecoder().decode("aGVsbG9oYXR0cmljaw==")).toCharArray();
        final InputStream trustStoreStream = this.getClass().getClassLoader().getResourceAsStream("truststore.jks");

        final KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(trustStoreStream, keystoreCred);

        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, keystoreCred);
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);

        final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        final X509TrustManager trustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];

        int proxyPort = 3000;
        String proxyHost = "localhost";

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager);

        if (ModuleConfig.instance().getBoolean("PromotionStatus_DebugProxy", false)) {
            builder = builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
        }

        return builder.build();
    }
}
