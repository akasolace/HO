package module.series.statistics;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import core.model.enums.RatingsStatistics;
import core.module.config.ModuleConfig;
import core.util.HOLogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.tls.HandshakeCertificates;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;

@Deprecated
public class DataDownloader {

    private final static String ALLTID_SERVER_BASEURL = "https://hattid.com/api";
    private final static String POWERRATING_ENDPOINT  = "/leagueUnit/%s/teamPowerRatings?page=0&pageSize=8&sortBy=power_rating&sortDirection=asc&statType=statRound&statRoundNumber=%s&season=%s";
    private final static String HATSTATS_ENDPOINT     = "/leagueUnit/%s/teamHatstats?page=0&pageSize=8&sortBy=hatstats&sortDirection=asc&statType=%s&season=%s";

    /** HTTP Header requested by Hattid. */
    private final static String HATTID_REQUEST_SOURCE = "Hattid-Request-Source";
    /** Value for Hattid Header request. */
    private final static String HATTID_REQUEST_SOURCE_VALUE = "HO!";


    // Singleton.
    private DataDownloader() {
    }

    private static DataDownloader instance = null;

    public static DataDownloader instance() {
        if (instance == null) {
            instance = new DataDownloader();
        }

        return instance;
    }


    /**
     * Fetch league statistics (power rating and HatStats) from Alltid website for display in League panel
     */
    public Map<Integer, Map<RatingsStatistics, Integer>> fetchLeagueStatistics(int iLeagueID, int iHTWeek, int iHTSeason){

        Map<Integer, Map<RatingsStatistics, Integer>> resultsMap = new HashMap<>();

        Map<Integer, Integer> powerRatings = fetchLeagueTeamPowerRatings(iLeagueID, iHTWeek, iHTSeason);
        if (powerRatings == null) {
            return resultsMap;
        }
        Map<Integer, Map<RatingsStatistics, Integer>> hatStatsMax = fetchLeagueTeamHatStats(iLeagueID, iHTSeason, "max");
        Map<Integer, Map<RatingsStatistics, Integer>> hatStatsAvg = fetchLeagueTeamHatStats(iLeagueID, iHTSeason, "avg");

        if ( hatStatsMax != null && hatStatsAvg != null) {
            Set<Integer> teamIDs = new HashSet<>(powerRatings.keySet());
            teamIDs.addAll(hatStatsMax.keySet());
            teamIDs.addAll(hatStatsAvg.keySet());

            for (Integer teamID : teamIDs) {
                Map<RatingsStatistics, Integer> teamStats = new HashMap<>();

                if (powerRatings.containsKey(teamID)) {
                    teamStats.put(RatingsStatistics.POWER_RATINGS, powerRatings.get(teamID));
                }

                if (hatStatsMax.containsKey(teamID)) {
                    hatStatsMax.get(teamID).forEach(
                            (key, value) -> teamStats.merge(key, value, (v1, v2) -> v1));
                }

                if (hatStatsAvg.containsKey(teamID)) {
                    hatStatsAvg.get(teamID).forEach(
                            (key, value) -> teamStats.merge(key, value, (v1, v2) -> v1));
                }

                resultsMap.put(teamID, teamStats);
            }
        }
        return resultsMap;
    }

    public Map<Integer, Map<RatingsStatistics, Integer>> fetchLeagueTeamHatStats(int iLeagueID, int iHTSeason, String dataType) {

        Map<Integer, Map<RatingsStatistics, Integer>> result = new HashMap<>();

        try {
            final OkHttpClient client = initializeHttpsClient();
            String url = ALLTID_SERVER_BASEURL + String.format(HATSTATS_ENDPOINT, iLeagueID, dataType, iHTSeason);

            if (client != null) {
                Request request = createRequest(url);
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyAsString = response.body().string();
                    Gson gson = new Gson();
                    JsonObject output = gson.fromJson(bodyAsString, JsonObject.class);

                    response.close();

                    for (var entity : output.getAsJsonArray("entities")) {
                        Map<RatingsStatistics, Integer> teamStat = new HashMap<>();
                        JsonObject teamSortingKey = (JsonObject)((JsonObject) entity).get("teamSortingKey");
                        int iTeamID = teamSortingKey.get("teamId").getAsInt();

                        int rating = ((JsonObject) entity).get("hatStats").getAsInt();
//                        teamStat.put(RatingsStatistics.getCode("total", dataType), rating);

                        rating = ((JsonObject) entity).get("midfield").getAsInt();
//                        teamStat.put(RatingsStatistics.getCode("mid", dataType), rating * 3);

                        rating = ((JsonObject) entity).get("defense").getAsInt();
//                        teamStat.put(RatingsStatistics.getCode("def", dataType), rating);

                        rating = ((JsonObject) entity).get("attack").getAsInt();
//                        teamStat.put(RatingsStatistics.getCode("off", dataType), rating);

                        result.put(iTeamID, teamStat);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            HOLogger.instance().error(
                    DataDownloader.class,
                    "Error fetching data from Alltid for league team hatstats: " + e.getMessage()
            );
            return null;
        }


        return result;
    }

    private Request createRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader(HATTID_REQUEST_SOURCE, HATTID_REQUEST_SOURCE_VALUE)
                .build();
    }


    public Map<Integer, Integer> fetchLeagueTeamPowerRatings(int iLeagueID, int iHTWeek, int iHTSeason) {

        Map<Integer, Integer> result = new HashMap<>();

        try {
            final OkHttpClient client = initializeHttpsClient();
            String url = ALLTID_SERVER_BASEURL + String.format(POWERRATING_ENDPOINT, iLeagueID, iHTWeek, iHTSeason);

            if (client != null) {
                Request request = createRequest(url);
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyAsString = response.body().string();
                    Gson gson = new Gson();
                    JsonObject output = gson.fromJson(bodyAsString, JsonObject.class);

                    response.close();

                    for (var entity : output.getAsJsonArray("entities")) {
                        int iTeamID = ((JsonObject) ((JsonObject) entity).get("teamSortingKey")).get("teamId").getAsInt();
                        int iPowerRating = ((JsonObject) entity).get("powerRating").getAsInt();
                        result.put(iTeamID, iPowerRating);
                    }
                }
            }

        } catch (Exception e) {
            HOLogger.instance().error(
                    DataDownloader.class,
                    "Error fetching data from Alltid for league team power ratings: " + e.getMessage()
            );
            return null;
        }


        return result;
    }

    private OkHttpClient initializeHttpsClient() throws Exception {

        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            char[] keystoreCred = new String(Base64.getDecoder().decode("aGVsbG9oYXR0cmljaw==")).toCharArray();
            final InputStream trustStoreStream = this.getClass().getClassLoader().getResourceAsStream("truststore.jks");

            keyStore.load(trustStoreStream, keystoreCred);
            Certificate letsEncryptCert = keyStore.getCertificate("letsencrypt");
            Certificate globalsignCert = keyStore.getCertificate("globalsign r3");

            HandshakeCertificates certificates = new HandshakeCertificates.Builder()
                    .addTrustedCertificate((X509Certificate) letsEncryptCert)
                    .addTrustedCertificate((X509Certificate) globalsignCert)
                    .addPlatformTrustedCertificates() // Why does this not work??
                    .build();

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .sslSocketFactory(certificates.sslSocketFactory(), certificates.trustManager());

            int proxyPort = 3000;
            String proxyHost = "localhost";

            if (ModuleConfig.instance().getBoolean("PromotionStatus_DebugProxy", false)) {
                builder = builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
            }

            return builder.build();

        } catch (Exception e) {
            HOLogger.instance().error(
                    DataDownloader.class,
                    "Error creating OkHttp client: " + e.getMessage()
            );
            return null;
        }
    }
}
