package core.model.match;

import core.model.enums.MatchType;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static core.file.xml.XMLManager.xmlValue2Hash;

public class MatchTeamRating {

    private int matchId;
    private MatchType matchTyp;
    private int teamId;
    private int fanclubSize;
    private int powerRating;
    private int globalRanking;
    private int leagueRanking;
    private int regionRanking;
    private int numberOfVictories;
    private int numberOfUndefeated;

    public MatchTeamRating(ResultSet rs) throws SQLException {
        this.matchId = rs.getInt("MatchId");
        this.matchTyp = MatchType.getById(rs.getInt("MatchTyp"));
        this.teamId = rs.getInt("TeamId");
        this.fanclubSize = rs.getInt("FanclubSize");
        this.powerRating = rs.getInt("Powerrating");
        this.globalRanking = rs.getInt("GlobalRanking");
        this.leagueRanking = rs.getInt("LeagueRanking");
        this.regionRanking = rs.getInt("RegionRanking");
        this.numberOfVictories = rs.getInt("NumberOfVictories");
        this.numberOfUndefeated = rs.getInt("NumberOfUndefeated");
    }

    public MatchTeamRating(int matchID, MatchType matchType, Map<String, String> download) {
        this.matchId=matchID;
        this.matchTyp=matchType;
        this.teamId = getInt( download, "TeamID");
        this.fanclubSize = getInt(download, "FanclubSize");
        this.powerRating = getInt(download, "PowerRating");
        this.globalRanking = getInt(download,  "GlobalRanking");
        this.leagueRanking = getInt(download,  "LeagueRanking");
        this.regionRanking = getInt(download,  "RegionRanking");
        this.numberOfUndefeated = getInt(download, "NumberOfUndefeated");
        this.numberOfVictories = getInt(download, "NumberOfVictories");
    }

    private int getInt(Map<String, String> download, String key) {
        try {
            var s = download.get(key);
            if (s != null && s.length() > 0) return Integer.parseInt(s);
        } catch (Exception e) {
            HOLogger.instance().warning(getClass(), "getInt: " + e);
        }
        return -1;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public MatchType getMatchTyp() {
        return matchTyp;
    }

    public void setMatchTyp(MatchType matchTyp) {
        this.matchTyp = matchTyp;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getFanclubSize() {
        return fanclubSize;
    }

    public void setFanclubSize(int fanclubSize) {
        this.fanclubSize = fanclubSize;
    }

    public int getPowerRating() {
        return powerRating;
    }

    public void setPowerRating(int powerRating) {
        this.powerRating = powerRating;
    }

    public int getGlobalRanking() {
        return globalRanking;
    }

    public void setGlobalRanking(int globalRanking) {
        this.globalRanking = globalRanking;
    }

    public int getLeagueRanking() {
        return leagueRanking;
    }

    public void setLeagueRanking(int leagueRanking) {
        this.leagueRanking = leagueRanking;
    }

    public int getRegionRanking() {
        return regionRanking;
    }

    public void setRegionRanking(int regionRanking) {
        this.regionRanking = regionRanking;
    }

    public int getNumberOfVictories() {
        return numberOfVictories;
    }

    public void setNumberOfVictories(int numberOfVictories) {
        this.numberOfVictories = numberOfVictories;
    }

    public int getNumberOfUndefeated() {
        return numberOfUndefeated;
    }

    public void setNumberOfUndefeated(int numberOfUndefeated) {
        this.numberOfUndefeated = numberOfUndefeated;
    }
}
