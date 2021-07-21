package module.series;

import core.db.DBManager;
import core.model.enums.MatchType;
import core.model.enums.RatingsStatistics;

import java.util.HashMap;
import java.util.Map;

public class Model {

	public static class StatisticsEntry {
		private int count;
		private int sum;
		private int max;

		public StatisticsEntry(int v){
			count=1;
			sum=v;
			max=v;
		}

		public void addValue(int v) {
			count++;
			sum += v;
			if (v > max) max = v;
		}

		public int getMax() {
			return max;
		}

		public int getAverage() {
			if (count > 0) return (int) ((double) sum / count + .5);
			return 0;
		}
	}

	private Spielplan currentSeries;
	private String currentTeam;
	private Map<Integer, Map<RatingsStatistics,StatisticsEntry>> leagueStatistics;
	private boolean isStatTypeMax;

	public Spielplan getCurrentSeries() {
		return currentSeries;
	}

	public void setCurrentSeries(Spielplan currentSeries) {
		this.currentSeries = currentSeries;
		this.leagueStatistics = new HashMap<>();
		if (currentSeries == null) return;
		for (var f : this.currentSeries.getEintraege()) {
			if (f.isGameOver()) {
				var matchDetails = DBManager.instance().loadMatchDetails(MatchType.LEAGUE.getId(), f.getMatchId());
				var teamratings = DBManager.instance().loadMatchTeamRating(MatchType.LEAGUE.getId(), f.getMatchId());
				for (var t : teamratings) {
					addLeagueStatistics(t.getTeamId(), RatingsStatistics.POWER_RATINGS, t.getPowerRating());
					addLeagueStatistics(t.getTeamId(), RatingsStatistics.HATSTATS_DEF, matchDetails.getSumHatStatsDefence(t.getTeamId()));
					addLeagueStatistics(t.getTeamId(), RatingsStatistics.HATSTATS_OFF, matchDetails.getSumHatStatsAttack(t.getTeamId()));
					addLeagueStatistics(t.getTeamId(), RatingsStatistics.HATSTATS_MID, matchDetails.getSumHatStatsMidfield(t.getTeamId()));
					addLeagueStatistics(t.getTeamId(), RatingsStatistics.HATSTATS_TOTAL, matchDetails.getHatStats(t.getTeamId()));
				}
			}
		}
	}

	private void addLeagueStatistics(int teamId, RatingsStatistics key, int value) {
		if ( !leagueStatistics.containsKey(teamId) ){
			Map<RatingsStatistics, Model.StatisticsEntry> stat = new HashMap<>();
			stat.put(key, new Model.StatisticsEntry(value));
			leagueStatistics.put(teamId, stat);
		}
		else {
			var stat = leagueStatistics.get(teamId);
			if ( !stat.containsKey(key)){
				stat.put(key, new Model.StatisticsEntry(value));
			}
			else {
				stat.get(key).addValue(value);
			}
		}
	}

	public String getCurrentTeam() {
		return currentTeam;
	}

	public void setCurrentTeam(String currentTeam) {
		this.currentTeam = currentTeam;
	}

	public Map<Integer, Map<RatingsStatistics, StatisticsEntry>> getLeagueStatistics() {
		return leagueStatistics;
	}

	public void setLeagueStatistics(Map<Integer, Map<RatingsStatistics, StatisticsEntry>> leagueStatistics) {
		this.leagueStatistics = leagueStatistics;
	}

	public boolean isStatTypeMax() {
		return isStatTypeMax;
	}

	public void setStatTypeMax(boolean statTypeMax) {
		isStatTypeMax = statTypeMax;
	}
}
