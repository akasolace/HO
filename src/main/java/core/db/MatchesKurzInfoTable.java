package core.db;

import core.model.match.MatchKurzInfo;
//import core.model.match.MatchKurzInfo2;
import core.model.match.MatchType;
import core.model.match.Weather;
import core.util.HOLogger;
import module.matches.SpielePanel;
import module.matches.statistics.MatchesOverviewCommonPanel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Class used to store in DB, [Matches] Table fetched via CHPP
 */
final class MatchesKurzInfoTable extends AbstractTable {
	final static String TABLENAME = "MATCHESKURZINFO";

	protected MatchesKurzInfoTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[22];
		columns[0] = new ColumnDescriptor("MatchID", Types.INTEGER, false, true); //The globally unique identifier of the match
		columns[1] = new ColumnDescriptor("MatchTyp", Types.INTEGER, false); //Integer defining the type of match
		columns[2] = new ColumnDescriptor("HeimName", Types.VARCHAR, false, 256); // HomeTeamName
		columns[3] = new ColumnDescriptor("HeimID", Types.INTEGER, false);  //HomeTeamID
		columns[4] = new ColumnDescriptor("GastName", Types.VARCHAR, false, 256); // AwayTeamName
		columns[5] = new ColumnDescriptor("GastID", Types.INTEGER, false);  //AwayTeamID
		columns[6] = new ColumnDescriptor("MatchDate", Types.VARCHAR, false, 256); // The start date and time (kick-off) of the match.
		columns[7] = new ColumnDescriptor("HeimTore", Types.INTEGER, false); // The current number of goals in the match for the home team.
		columns[8] = new ColumnDescriptor("GastTore", Types.INTEGER, false); // The current number of goals in the match for the away team
		columns[9] = new ColumnDescriptor("Aufstellung", Types.BOOLEAN, false); // List of boolean value only supplied for upcoming matches of your own team that signifies whether you have given orders or not
		columns[10] = new ColumnDescriptor("Status", Types.INTEGER, false); // Specifying whether the match is FINISHED, ONGOING or UPCOMING
		columns[11] = new ColumnDescriptor("CupLevel", Types.INTEGER, false); // 1 = National/Divisional cup, 2 = Challenger cup, 3 = Consolation cup. 0 if MatchType is not 3
		columns[12] = new ColumnDescriptor("CupLevelIndex", Types.INTEGER, false); // In Challenger cups: 1 = Emerald (start week 2), 2 = Ruby (start week 3), 3 = Sapphire (start week 4). Always 1 for National/Divisional (main cups) and Consolation cups. 0 if MatchType is not 3.
		columns[13] = new ColumnDescriptor("MatchContextId", Types.INTEGER, true); // This will be either LeagueLevelUnitId (for League), CupId (Cup, Hattrick Masters, World Cup and U-20 World Cup), LadderId, TournamentId, or 0 for friendly, qualification, single matches and preparation matches.
		columns[14] = new ColumnDescriptor("TournamentTypeID", Types.INTEGER, true); // 3 = League with playoffs , 4 = Cup
		columns[15] = new ColumnDescriptor("ArenaId", Types.INTEGER, true); //  arena id
		columns[16] = new ColumnDescriptor("RegionId", Types.INTEGER, true); // region id
		columns[17] = new ColumnDescriptor("isDerby", Types.BOOLEAN, true); //
		columns[18] = new ColumnDescriptor("isNeutral", Types.BOOLEAN, true); // 0=false, 1=true, -1=unknown
		columns[19] = new ColumnDescriptor("Weather", Types.INTEGER, true); // 0=rainy, ...
		columns[20] = new ColumnDescriptor("WeatherForecast", Types.INTEGER, true); // 0=happened, ...
		columns[21] = new ColumnDescriptor("Duration", Types.INTEGER, true); // match duration in minutes

	}

    @Override
    protected String[] getCreateIndizeStatements() {
        return new String[]{
                "CREATE INDEX IMATCHKURZINFO_1 ON " + getTableName() + "(" + columns[0].getColumnName() + ")",
                "CREATE INDEX matchkurzinfo_heimid_idx ON "+ getTableName() + "(" + columns[3].getColumnName() + ")",
                "CREATE INDEX matchkurzinfo_gastid_idx ON "+ getTableName() + "(" + columns[5].getColumnName() + ")",
        };
    }

	MatchKurzInfo getMatchesKurzInfo(int teamId, int matchtyp, int statistic,
			boolean home) {

		MatchKurzInfo match = null;
		StringBuilder sql = new StringBuilder(200);
		ResultSet rs = null;
		String column = "";
		String column2 = "";
		try {
			switch (statistic) {
			case MatchesOverviewCommonPanel.HighestVictory:
				column = home ? "(HEIMTORE-GASTTORE) AS DIFF "
						: "(GASTTORE-HEIMTORE) AS DIFF ";
				column2 = home ? ">" : "<";
				break;
			case MatchesOverviewCommonPanel.HighestDefeat:
				column = home ? "(GASTTORE-HEIMTORE) AS DIFF "
						: "(HEIMTORE-GASTTORE) AS DIFF ";
				column2 = home ? "<" : ">";
				break;

			}
			sql.append("SELECT " + getTableName() + ".*,");
			sql.append(column);
			sql.append(" FROM ").append(getTableName());
			sql.append(" WHERE ").append(home ? "HEIMID" : "GASTID")
					.append(" = ");
			sql.append(teamId);
			sql.append(" AND HEIMTORE " + column2 + " GASTTORE ");
			sql.append(getMatchTypWhereClause(matchtyp));

			sql.append(" ORDER BY DIFF DESC ");
			rs = adapter.executeQuery(sql.toString());

			rs.beforeFirst();

			if (rs.next()) {
				match = createMatchKurzInfo(rs);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DB.getMatchesKurzInfo Error" + e);
		}
		return match;
	}

	/**
	 * Wichtig: Wenn die Teamid = -1 ist muss der Matchtyp ALLE_SPIELE sein!
	 * 
	 * @param teamId
	 *            Die Teamid oder -1 für alle
	 * @param matchtyp
	 *            Welche Matches? Konstanten im SpielePanel!
	 * 
	 */
	MatchKurzInfo[] getMatchesKurzInfo(int teamId, int matchtyp, boolean asc) {
		StringBuilder sql = new StringBuilder(100);
		ResultSet rs = null;
		final ArrayList<MatchKurzInfo> liste = new ArrayList<MatchKurzInfo>();

		// Ohne Matchid nur AlleSpiele möglich!
		if ((teamId < 0) && (matchtyp != SpielePanel.ALL_MATCHS)) {
			return new MatchKurzInfo[0];
		}

		try {
			sql.append("SELECT * FROM ").append(getTableName());

			if ((teamId > -1) && (matchtyp != SpielePanel.ALL_MATCHS)
					&& (matchtyp != SpielePanel.OTHER_TEAM_MATCHS)) {
				sql.append(" WHERE ( GastID = " + teamId + " OR HeimID = "
						+ teamId + " )");
			}

			if ((teamId > -1) && (matchtyp == SpielePanel.OTHER_TEAM_MATCHS)) {
				sql.append(" WHERE ( GastID != " + teamId + " AND HeimID != "
						+ teamId + " )");
			}

			// Nur eigene gewählt
			if (matchtyp >= 10) {
				matchtyp = matchtyp - 10;

				sql.append(" AND Status=" + MatchKurzInfo.FINISHED);
			}
			sql.append(getMatchTypWhereClause(matchtyp));

			// nicht desc
			sql.append(" ORDER BY MatchDate");

			if (!asc) {
				sql.append(" DESC");
			}

			rs = adapter.executeQuery(sql.toString());

			rs.beforeFirst();

			while (rs.next()) {
				liste.add(createMatchKurzInfo(rs));
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DB.getMatchesKurzInfo Error" + e);
		}

		// matches = new MatchKurzInfo[liste.size()];
		// Helper.copyVector2Array(liste, matches);

		return liste.toArray(new MatchKurzInfo[liste.size()]);
	}

	MatchKurzInfo[] getMatchesKurzInfoUpComing(int teamId) {
		StringBuilder sql = new StringBuilder(100);
		ResultSet rs = null;
		final ArrayList<MatchKurzInfo> liste = new ArrayList<MatchKurzInfo>();

		// Ohne Matchid nur AlleSpiele möglich!
		if ((teamId < 0)) {
			return new MatchKurzInfo[0];
		}

		try {
			sql.append("SELECT * FROM ").append(getTableName());
			sql.append(" WHERE ( GastID = " + teamId + " OR HeimID = " + teamId + " )");
			sql.append(" AND Status=" + MatchKurzInfo.UPCOMING);
			sql.append(" AND MatchTyp!=" + MatchType.LEAGUE.getId());
			sql.append(" ORDER BY MatchDate DESC");

			rs = adapter.executeQuery(sql.toString());

			rs.beforeFirst();

			while (rs.next()) {
				liste.add(createMatchKurzInfo(rs));
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DB.getMatchesKurzInfo Error" + e);
		}

		return liste.toArray(new MatchKurzInfo[liste.size()]);
	}

	public MatchKurzInfo  getLastMatchesKurzInfo(int teamId) {
		StringBuilder sql = new StringBuilder(100);
		ResultSet rs = null;
		try {
			sql.append("SELECT * FROM ").append(getTableName());
			sql.append(" WHERE ( GastID = " + teamId + " OR HeimID = "
						+ teamId + " )");
			sql.append(" AND Status=" + MatchKurzInfo.FINISHED);
			sql.append(" ORDER BY MatchDate DESC LIMIT 1");
			rs = adapter.executeQuery(sql.toString());
			rs.beforeFirst();
			while (rs.next()) {
				return createMatchKurzInfo(rs);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DB.getMatchesKurzInfo Error" + e);
		}
		return null;
	}


	private StringBuilder getMatchTypWhereClause(int matchtype) {
		StringBuilder sql = new StringBuilder(50);
		switch (matchtype) {
		case SpielePanel.NUR_EIGENE_SPIELE:

			// Nix zu tun, da die teamId die einzige Einschränkung ist
			break;
		case SpielePanel.NUR_EIGENE_PFLICHTSPIELE:
			sql.append(" AND ( MatchTyp=" + MatchType.QUALIFICATION.getId());
			sql.append(" OR MatchTyp=" + MatchType.LEAGUE.getId());
			sql.append(" OR MatchTyp=" + MatchType.CUP.getId() + " )");
			break;
		case SpielePanel.NUR_EIGENE_POKALSPIELE:
			sql.append(" AND MatchTyp=" + MatchType.CUP.getId());
			break;
		case SpielePanel.NUR_EIGENE_LIGASPIELE:
			sql.append(" AND MatchTyp=" + MatchType.LEAGUE.getId());
			break;
		case SpielePanel.NUR_EIGENE_FREUNDSCHAFTSSPIELE:
			sql.append(" AND ( MatchTyp=" + MatchType.FRIENDLYNORMAL.getId());
			sql.append(" OR MatchTyp=" + MatchType.FRIENDLYCUPRULES.getId());
			sql.append(" OR MatchTyp=" + MatchType.INTFRIENDLYCUPRULES.getId());
			sql.append(" OR MatchTyp=" + MatchType.INTFRIENDLYNORMAL.getId() + " )");
			break;
		case SpielePanel.NUR_EIGENE_TOURNAMENTSPIELE:
			sql.append(" AND ( MatchTyp=" + MatchType.TOURNAMENTGROUP.getId());
			sql.append(" OR MatchTyp=" + MatchType.TOURNAMENTPLAYOFF.getId());
			sql.append(" OR MatchTyp=" + MatchType.DIVISIONBATTLE.getId() + " )");
			break; 
		case SpielePanel.ONLY_SECONDARY_CUP:
			sql.append(" AND ( MatchTyp=" + MatchType.EMERALDCUP.getId());
			sql.append(" OR MatchTyp=" + MatchType.RUBYCUP.getId());
			sql.append(" OR MatchTyp=" + MatchType.SAPPHIRECUP.getId());
			sql.append(" OR MatchTyp=" + MatchType.CONSOLANTECUP.getId()+ " )");
			break;
		case SpielePanel.ONLY_QUALIF_MATCHES:
			sql.append(" AND MatchTyp=" + MatchType.QUALIFICATION.getId());
			break;
		}
		return sql;
	}

	private MatchKurzInfo createMatchKurzInfo(ResultSet rs) throws SQLException {
		MatchKurzInfo match = new MatchKurzInfo();
		match.setMatchDate(rs.getString("MatchDate"));
		match.setGastID(rs.getInt("GastID"));
		match.setGastName(DBManager.deleteEscapeSequences(rs
				.getString("GastName")));
		match.setHeimID(rs.getInt("HeimID"));
		match.setHeimName(DBManager.deleteEscapeSequences(rs
				.getString("HeimName")));
		match.setMatchID(rs.getInt("MatchID"));
		match.setMatchContextId(rs.getInt("MatchContextId"));
		match.setTournamentTypeID(rs.getInt("TournamentTypeID"));
		match.setGastTore(rs.getInt("GastTore"));
		match.setHeimTore(rs.getInt("HeimTore"));
		match.setMatchType(MatchType.getById(rs.getInt("MatchTyp")));
		match.setMatchStatus(rs.getInt("Status"));
		match.setOrdersGiven(rs.getBoolean("Aufstellung"));
		match.setArenaId(rs.getInt("ArenaId"));
		match.setRegionId(rs.getInt("RegionId"));
		match.setIsDerby(rs.getBoolean("isDerby"));
		if (rs.wasNull()) match.setIsDerby(null);
		match.setIsNeutral(rs.getBoolean("isNeutral"));
		if (rs.wasNull()) match.setIsNeutral(null);
		match.setWeather(Weather.getById(rs.getInt("Weather")));
		if (rs.wasNull()) match.setWeather(Weather.NULL);
		match.setWeatherForecast(Weather.Forecast.getById(rs.getInt("WeatherForecast")));
		if ( rs.wasNull()) match.setWeatherForecast(Weather.Forecast.NULL);
		match.setDuration(rs.getInt("Duration"));
		if ( rs.wasNull()) match.setDuration(null);
		return match;
	}

	/**
	 * Check if a match is already in the database.
	 */
	boolean isMatchVorhanden(int matchid) {
		boolean vorhanden = false;

		try {
			final String sql = "SELECT MatchId FROM " + getTableName()
					+ " WHERE MatchId=" + matchid;
			final ResultSet rs = adapter.executeQuery(sql);

			rs.beforeFirst();

			if (rs.next()) {
				vorhanden = true;
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DatenbankZugriff.isMatchVorhanden : " + e);
		}

		return vorhanden;
	}

	boolean hasDerbyInfo(int matchId)
	{
		try {
			final String sql = "SELECT isDerby FROM " + getTableName() + " WHERE MatchId=" + matchId;
			final ResultSet rs = adapter.executeQuery(sql);
			rs.beforeFirst();
			if (rs.next()) {
				boolean isDerby = rs.getBoolean(1);
				return !rs.wasNull();
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DatenbankZugriff.hasDerbyInfo : " + e);
		}
		return false;
	}

	boolean hasUnsureWeatherForecast(int matchId)
	{
		try{
			final String sql = "SELECT WeatherForecast FROM " + getTableName() + " WHERE MatchId=" + matchId;
			final ResultSet rs = adapter.executeQuery(sql);
			rs.beforeFirst();
			if (rs.next()) {
				Weather.Forecast forecast = Weather.Forecast.getById(rs.getInt(1));
				if (rs.wasNull()) return true;
				return !forecast.isSure();
			}
		}
		catch(Exception e){
			HOLogger.instance().log(getClass(), "DatenbankZugriff.hasUnsureWeatherForecast : " + e);
		}
		return false;
	}
	// ///////////////////////////////////////////////////////////////////////////////
	// MatchesASP MatchKurzInfo
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Get all matches with a certain status for the given team from the
	 * database.
	 * 
	 * @param teamId
	 *            the teamid or -1 for all matches
	 * @param matchStatus
	 *            the match status (e.g. IMatchKurzInfo.UPCOMING) or -1 to
	 *            ignore this parameter
	 */
	MatchKurzInfo[] getMatchesKurzInfo(final int teamId, final int matchStatus) {

		String sql = "";
		boolean firstCondition = true;

		if (teamId > -1) {
			sql += (firstCondition ? " WHERE" : " AND")
				+ "(GastID=" + teamId + " OR HeimID=" + teamId + ")";
			firstCondition = false;
		}

		if (matchStatus > -1) {
			sql += (firstCondition ? " WHERE" : " AND")
				+ " Status=" + matchStatus;
			firstCondition = false;
		}

		sql += " ORDER BY MatchDate DESC";
		return getMatchesKurzInfo(sql);
	}

	MatchKurzInfo[] getMatchesKurzInfo(final String where) {
		MatchKurzInfo[] matches = new MatchKurzInfo[0];
		String sql = null;
		ResultSet rs = null;
		final Vector<MatchKurzInfo> liste = new Vector<MatchKurzInfo>();

		try {
			sql = "SELECT * FROM " + getTableName() + " " + where;
			rs = adapter.executeQuery(sql);

			while (rs.next()) {
				liste.add(createMatchKurzInfo(rs));
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DB.getMatchesKurzInfo Error" + e);
		}

		matches = new MatchKurzInfo[liste.size()];
		matches = liste.toArray(matches);
		return matches;
	}

	/**
	 * Get all matches for the given team from the database.
	 * 
	 * @param teamId
	 *            the teamid or -1 for all matches
	 */
	MatchKurzInfo[] getMatchesKurzInfo(final int teamId) {
		return getMatchesKurzInfo(teamId, -1);
	}

	/**
	 * Returns the MatchKurzInfo for the match. Returns null if not found.
	 * 
	 * @param matchid
	 *            The ID for the match
	 * @return The kurz info object or null
	 */
	MatchKurzInfo getMatchesKurzInfoByMatchID(int matchid) {

		try {
			final String sql = "SELECT * FROM " + getTableName()
					+ " WHERE MatchId=" + matchid;
			final ResultSet rs = adapter.executeQuery(sql);

			rs.beforeFirst();
			if (rs.next()) {
				return createMatchKurzInfo(rs);
			}
		} catch (SQLException e) {

			HOLogger.instance().error(getClass(),
					"DB.getMatchesKurzInfo Error" + e);
		}

		return null;
	}

	/**
	 * Saves matches into storeMatchKurzInfo table
	 */
	void storeMatchKurzInfos(MatchKurzInfo[] matches) {
		if ( matches == null)return;
		String sql;
		final String[] where = { "MatchID" };
		final String[] werte = new String[1];

		for ( var match : matches){

			werte[0] = "" + match.getMatchID();
			delete(where, werte);

			try {
				sql = "INSERT INTO "
						+ getTableName()
						+ " (  MatchID, MatchContextId, TournamentTypeID, MatchTyp, CupLevel, CupLevelIndex, HeimName, HeimID, GastName, GastID, MatchDate, HeimTore, GastTore, Aufstellung, Status, ArenaId, RegionId, isDerby, isNeutral, Weather, WeatherForecast, Duration ) VALUES(";
				sql += (match.getMatchID()
						+ ","
						+ match.getMatchContextId()
						+ ","
						+ match.getTournamentTypeID()
						+ ","
						+ match.getMatchTyp().getId()
						+ ","
						+ match.getCupLevel().getId()
						+ ","
						+ match.getCupLevelIndex().getId()
						+ ", '"
						+ DBManager.insertEscapeSequences(match.getHeimName())
						+ "', "
						+ match.getHeimID()
						+ ", '"
						+ DBManager.insertEscapeSequences(match.getGastName()) + "', ");
				sql += (match.getGastID() + ", '"
						+ match.getMatchDate() + "', "
						+ match.getHeimTore() + ", "
						+ match.getGastTore() + ", "
						+ match.isOrdersGiven() + ", "
						+ match.getMatchStatus() + ", "
						+ match.getArenaId() + ", "
						+ match.getRegionId() + ", "
						+ match.getIsDerby() + ", "
						+ match.getIsNeutral() + ", "
						+ match.getWeather().getId() + ", "
						+ match.getWeatherForecast().getId() + ", "
						+ match.getDuration()
						+ " )");
				adapter.executeUpdate(sql);
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),
						"DB.storeMatchKurzInfos Error" + e);
				HOLogger.instance().log(getClass(), e);
			}
		}
	}

	void update(MatchKurzInfo match) {
		StringBuilder sql = new StringBuilder()
				.append("UPDATE ").append(getTableName()).append(" SET ")
				.append("HeimName='").append(DBManager.insertEscapeSequences(match.getHeimName()))
				.append("', HeimID=").append(match.getHeimID())
				.append(", GastName='").append(DBManager.insertEscapeSequences(match.getGastName()))
				.append("', GastID=").append(match.getGastID())
				.append(", MatchDate='").append(match.getMatchDate())
				.append("', HeimTore=").append(match.getHeimTore())
				.append(", GastTore=").append(match.getGastTore())
				.append(", Aufstellung=").append(match.isOrdersGiven())
				.append(", Status=").append(match.getMatchStatus())
				.append(", MatchContextId=").append(match.getMatchContextId())
				.append(", TournamentTypeID=").append(match.getTournamentTypeID())
				.append(", CupLevel=").append(match.getCupLevel().getId())
				.append(", CupLevelIndex=").append(match.getCupLevelIndex().getId())
				.append(", ArenaId=").append(match.getArenaId())
				.append(", RegionId=").append(match.getRegionId())
				.append(", isDerby=").append(match.getIsDerby())
				.append(", isNeutral=").append(match.isNeutral())
				.append(", Weather=").append(match.getWeather().getId())
				.append(", WeatherForecast=").append(match.getWeatherForecast().getId())
				.append(", Duration=").append(match.getDuration())
				.append(" WHERE MatchID=").append(match.getMatchID())
				.append(" AND MatchTyp=").append(match.getMatchTyp().getId());
		adapter.executeUpdate(sql.toString());
	}

}
