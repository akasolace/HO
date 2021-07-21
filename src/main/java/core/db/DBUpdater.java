package core.db;

import core.model.enums.DBDataSource;
import core.model.enums.MatchType;
import core.model.match.SourceSystem;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import javax.swing.JOptionPane;
import core.HO;
import module.youth.YouthPlayer;

final class DBUpdater {
	JDBCAdapter m_clJDBCAdapter;
	DBManager dbManager;

	void setDbManager(DBManager dbManager) {
		this.dbManager = dbManager;
	}

	void updateDB(int DBVersion) {
		// Just add new version cases in the switch..case part
		// and leave the old ones active, so also users which
		// have skipped a version get their database updated.

		this.m_clJDBCAdapter = dbManager.getAdapter();

		int version = ((UserConfigurationTable) dbManager.getTable(UserConfigurationTable.TABLENAME)).getDBVersion();

		if (version != DBVersion) {
			try {
				switch (version) {
					default:
						// Unsupported database version
						// We upgrade database from version 300 (HO 3.0)
						HOLogger.instance().log(getClass(), "DB version " + version + " is too old");
						try {
							JOptionPane.showMessageDialog(null,
									"DB is too old.\nPlease update first to HO! 3.0", "Error",
									JOptionPane.ERROR_MESSAGE);
						} catch (Exception e) {
							HOLogger.instance().log(getClass(), e);
						}
						System.exit(0);
						break;

					// hint: fall through (no breaks) is intended
					case 301:
						updateDBv300();  // Bug#509 requires another update run of v300
						updateDBv301(DBVersion);
					case 302:
					case 399:
						updateDBv400(DBVersion);
					case 400:
					case 499:
						updateDBv500(DBVersion);
					case 500:
				}

			} catch (Exception e) {
				HOLogger.instance().log(getClass(), e);
			}
		} else {
			HOLogger.instance().log(getClass(), "No DB update necessary.");
		}
	}

	private void updateDBv500(int dbVersion) throws SQLException {
		// Upgrade legacy FINANZEN table to new ECONOMY Table (since HO 5.0)
		if (!tableExists(EconomyTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Datum RENAME TO FetchedDate");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Supporter RENAME TO SupportersPopularity");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Sponsoren RENAME TO SponsorsPopularity");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Finanzen RENAME TO Cash");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinSponsoren RENAME TO IncomeSponsors");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinZuschauer RENAME TO IncomeSpectators");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinZinsen RENAME TO IncomeFinancial");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinSonstiges RENAME TO IncomeTemporary");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinGesamt RENAME TO IncomeSum");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostSpieler RENAME TO CostsPlayers");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostTrainer RENAME TO CostsStaff");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostStadion RENAME TO CostsArena");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostJugend RENAME TO CostsYouth");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostZinsen RENAME TO CostsFinancial");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostSonstiges RENAME TO CostsTemporary");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostGesamt RENAME TO CostsSum");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN GewinnVerlust RENAME TO ExpectedWeeksTotal");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinSponsoren RENAME TO LastIncomeSponsors");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinZuschauer RENAME TO LastIncomeSpectators");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinZinsen RENAME TO LastIncomeFinancial");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinSonstiges RENAME TO LastIncomeTemporary");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinGesamt RENAME TO LastIncomeSum");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostSpieler RENAME TO LastCostsPlayers");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostTrainer RENAME TO LastCostsStaff");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostStadion RENAME TO LastCostsArena");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostJugend RENAME TO LastCostsYouth");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostZinsen RENAME TO LastCostsFinancial");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostSonstiges RENAME TO LastCostsTemporary");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostGesamt RENAME TO LastCostsSum");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteGewinnVerlust RENAME TO LastWeeksTotal");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN ExpectedCash INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN IncomeSoldPlayers INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN IncomeSoldPlayersCommission INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN CostsBoughtPlayers INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN CostsArenaBuilding INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastIncomeSoldPlayers INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastIncomeSoldPlayersCommission INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastCostsBoughtPlayers INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastCostsArenaBuilding INTEGER DEFAULT 0");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE FINANZEN RENAME TO ECONOMY");
		}

		AbstractTable matchDetailsTable = dbManager.getTable(MatchDetailsTable.TABLENAME);
		matchDetailsTable.tryAddColumn("HomeFormation", "VARCHAR (5)");
		matchDetailsTable.tryAddColumn("AwayFormation", "VARCHAR (5)");

		AbstractTable basicsTable = dbManager.getTable(BasicsTable.TABLENAME);
		basicsTable.tryAddColumn("YouthTeamName", "VARCHAR (127)");
		basicsTable.tryAddColumn("YouthTeamID", "INTEGER");

		AbstractTable matchLineupPlayerTable = dbManager.getTable(MatchLineupPlayerTable.TABLENAME);
		matchLineupPlayerTable.tryAddColumn("StartSetPieces", "BOOLEAN");

		AbstractTable matchHighlightsTable = dbManager.getTable(MatchHighlightsTable.TABLENAME);
		matchHighlightsTable.tryAddColumn("MatchDate", "TIMESTAMP");

		if (!tableExists(YouthTrainingTable.TABLENAME)) {
			dbManager.getTable(YouthTrainingTable.TABLENAME).createTable();
			dbManager.getTable(YouthPlayerTable.TABLENAME).createTable();
			dbManager.getTable(YouthScoutCommentTable.TABLENAME).createTable();
		}

		AbstractTable youthplayerTable = dbManager.getTable(YouthPlayerTable.TABLENAME);
		for (var skill : YouthPlayer.skillIds) {
			youthplayerTable.tryAddColumn(skill + "Top3", "BOOLEAN");
		}

		if (!tableExists(TeamsLogoTable.TABLENAME)) {
			dbManager.getTable(TeamsLogoTable.TABLENAME).createTable();
		}

		// Upgrade TRAINING table =================================================================================
		if (columnExistsInTable("COACH_LEVEL", TrainingsTable.TABLENAME)) {
			HOLogger.instance().debug(getClass(), "Upgrade of training table was already performed ... process skipped !");
		} else {
			// Step 1. Add new columns in TRAINING table ===========================
			var trainingTable = dbManager.getTable(TrainingsTable.TABLENAME);
			trainingTable.tryAddColumn("COACH_LEVEL", "INTEGER");
			trainingTable.tryAddColumn("TRAINING_ASSISTANTS_LEVEL", "INTEGER");
			trainingTable.tryAddColumn("SOURCE", "INTEGER");
			trainingTable.tryAddColumn("TRAINING_DATE", "TIMESTAMP");

			// Step 2. Migrate existing entries ==================================================================
			var trainings = new ArrayList<int[]>();
			final String statement = "SELECT * FROM " + TrainingsTable.TABLENAME;
			ResultSet rs = m_clJDBCAdapter.executeQuery(statement);
			try {
				if (rs != null) {
					rs.beforeFirst();
					while (rs.next()) {
						var training = new int[]{
								rs.getInt("week"),
								rs.getInt("year")
						};
						trainings.add(training);
					}
				}

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.from(ZoneOffset.UTC));
				for (var training : trainings) {
					// Convert year, week to Date
					int dayOfWeek = 1;  // 1-7, locale-dependent such as Sunday-Monday in US.
					WeekFields weekFields = WeekFields.of(Locale.GERMANY);
					LocalDate ld = LocalDate.now()
							.withYear(training[1])
							.with(weekFields.weekOfYear(), training[0])
							.with(weekFields.dayOfWeek(), dayOfWeek);

					String dateString = formatter.format(ld);

					// find hrf of that training week
					// COTrainer from VEREIN,HRF_ID
					// TRAINER from SPIELER,HRF_ID && TRAINER>0
					// TrainingDate from XTRA,HRF_ID
					String sql = "select TRAININGDATE,COTRAINER,TRAINER FROM XTRADATA " +
							"INNER JOIN VEREIN ON VEREIN.HRF_ID=XTRADATA.HRF_ID " +
							"INNER JOIN SPIELER ON SPIELER.HRF_ID=XTRADATA.HRF_ID AND TRAINER>0 " +
							"WHERE TRAININGDATE>'" +
							dateString +
							"' LIMIT 1";

					rs = m_clJDBCAdapter.executeQuery(sql);
					if (rs != null) {
						rs.next();
						var trainingDate = rs.getTimestamp("TRAININGDATE");
						var coTrainer = rs.getInt("COTRAINER");
						var trainer = rs.getInt("TRAINER");

						// update new columns
						String update = "update " + TrainingsTable.TABLENAME +
								" SET" +
								" TRAINING_DATE='" +
								trainingDate +
								"',  TRAINING_ASSISTANTS_LEVEL=" +
								coTrainer +
								", COACH_LEVEL=" +
								trainer +
								", SOURCE=" +
								DBDataSource.MANUAL.getValue() +
								" WHERE YEAR=" +
								training[1] +
								" AND WEEK=" +
								training[0];

						m_clJDBCAdapter.executeUpdate(update);
					}
				}
			} catch (Exception e) {
				HOLogger.instance().error(getClass(), "Error when trying to migrate existing entries of TRAININGS table: " + e);
			}

			// Step 3. Finalize upgrade of Training table structure ===============================
			trainingTable.tryChangeColumn("COACH_LEVEL", "NOT NULL");
			trainingTable.tryChangeColumn("TRAINING_ASSISTANTS_LEVEL", "NOT NULL");
			trainingTable.tryChangeColumn("TRAINING_DATE", "NOT NULL");
			trainingTable.tryChangeColumn("SOURCE", "NOT NULL");
			trainingTable.tryRenameColumn("TYP", "TRAINING_TYPE");
			trainingTable.tryRenameColumn("INTENSITY", "TRAINING_INTENSITY");
			trainingTable.tryRenameColumn("STAMINATRAININGPART", "STAMINA_SHARE");
			trainingTable.tryDeleteColumn("YEAR");
			trainingTable.tryDeleteColumn("WEEK");
		}

		// Upgrade FutureTraining table ======================================================================================
		if (columnExistsInTable("COACH_LEVEL", FutureTrainingTable.TABLENAME)) {
			HOLogger.instance().debug(getClass(), "Upgrade of FutureTraining table was already performed ... process skipped !");
		}
		else {
			// Step 1. Add new columns in FUTURETRAININGS table ===========================================================
			var futureTrainingTable = dbManager.getTable(FutureTrainingTable.TABLENAME);
			futureTrainingTable.tryAddColumn("COACH_LEVEL", "INTEGER");
			futureTrainingTable.tryAddColumn("TRAINING_ASSISTANTS_LEVEL", "INTEGER");
			futureTrainingTable.tryAddColumn("TRAINING_DATE", "TIMESTAMP");
			futureTrainingTable.tryAddColumn("SOURCE", "INTEGER");


			// Step 2: update columns with non-null values to ensure NOT NULL clauses can be called
			// we store week and season information for future treatment
			String sql = "UPDATE " + FutureTrainingTable.TABLENAME +
					" SET TRAINING_DATE=timestamp('1900-01-01'), TRAINING_ASSISTANTS_LEVEL=WEEK, COACH_LEVEL=SEASON, SOURCE=" +
					DBDataSource.MANUAL.getValue() + " WHERE TRUE";
			m_clJDBCAdapter.executeUpdate(sql);

			// Step 3. Finalize upgrade of FUTURETRAININGS table structure ===============================
			futureTrainingTable.tryChangeColumn("COACH_LEVEL", "NOT NULL");
			futureTrainingTable.tryChangeColumn("TRAINING_ASSISTANTS_LEVEL", "NOT NULL");
			futureTrainingTable.tryChangeColumn("TRAINING_DATE", "NOT NULL");
			futureTrainingTable.tryChangeColumn("SOURCE", "NOT NULL");
			futureTrainingTable.tryRenameColumn("TYPE", "TRAINING_TYPE");
			futureTrainingTable.tryRenameColumn("INTENSITY", "TRAINING_INTENSITY");
			futureTrainingTable.tryRenameColumn("STAMINATRAININGPART", "STAMINA_SHARE");
			futureTrainingTable.tryDeleteColumn("SEASON");
			futureTrainingTable.tryDeleteColumn("WEEK");
		}

		// SourceSystem does NOT exist in HO4, but HeimName does
		if (columnExistsInTable("HeimName", MatchLineupTable.TABLENAME)) {

			HOLogger.instance().debug(getClass(), "Upgrading DB structure SourceSystem/MatchType .... ");
			var matchLineupTable = dbManager.getTable(MatchLineupTable.TABLENAME);
			var matchesKurzInfoTable = dbManager.getTable(MatchesKurzInfoTable.TABLENAME);
			var matchIFATable = dbManager.getTable(IfaMatchTable.TABLENAME);

			matchesKurzInfoTable.tryDropPrimaryKey();
			matchLineupTable.tryDropPrimaryKey();
			matchDetailsTable.tryDropPrimaryKey();
			matchIFATable.tryDropPrimaryKey();

			// Update primary key from matchID => (matchID, MATCHTYP) because doublons might otherwise exists
			m_clJDBCAdapter.executeQuery("ALTER TABLE MATCHESKURZINFO ADD PRIMARY KEY (MATCHID, MATCHTYP)");
			m_clJDBCAdapter.executeQuery("ALTER TABLE MATCHLINEUP ADD PRIMARY KEY (MATCHID, MATCHTYP)");

			matchLineupTable.tryDeleteColumn("SourceSystem");
			matchLineupTable.tryDeleteColumn("HeimName");
			matchLineupTable.tryDeleteColumn("HeimID");
			matchLineupTable.tryDeleteColumn("GastName");
			matchLineupTable.tryDeleteColumn("GastID");
			matchLineupTable.tryDeleteColumn("FetchDate");
			matchLineupTable.tryDeleteColumn("MatchDate");
			matchLineupTable.tryDeleteColumn("ArenaID");
			matchLineupTable.tryDeleteColumn("ArenaName");

			dbManager.getTable(AufstellungTable.TABLENAME).tryDeleteColumn("SourceSystem");
			dbManager.getTable(MatchHighlightsTable.TABLENAME).tryDeleteColumn("SourceSystem");
			dbManager.getTable(MatchLineupTeamTable.TABLENAME).tryDeleteColumn("SourceSystem");
			dbManager.getTable(MatchSubstitutionTable.TABLENAME).tryDeleteColumn("SourceSystem");
			dbManager.getTable(MatchDetailsTable.TABLENAME).tryDeleteColumn("SourceSystem");
			dbManager.getTable(MatchLineupPlayerTable.TABLENAME).tryDeleteColumn("SourceSystem");

			dbManager.getTable(IfaMatchTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");
			dbManager.getTable(MatchDetailsTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");
			dbManager.getTable(MatchHighlightsTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");
			dbManager.getTable(MatchLineupTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");
			dbManager.getTable(MatchLineupPlayerTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");
			dbManager.getTable(MatchLineupTeamTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");
			dbManager.getTable(MatchOrderTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");
			dbManager.getTable(MatchSubstitutionTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");
			dbManager.getTable(YouthTrainingTable.TABLENAME).tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0");

			// Correct history of MATCHESKURZINFO  ==============================================
			String sql = """
					UPDATE MATCHESKURZINFO
					SET MATCHTYP =
					    CASE MATCHTYP
					        WHEN 1001 THEN 3
					        WHEN 1002 THEN 3
					        WHEN 1003 THEN 3
					        WHEN 1004 THEN 3
					        WHEN 1101 THEN 50
					        ELSE -1
					    END
					WHERE
					    MATCHTYP IN (1001, 1002, 1003, 1004, 1101)""";

			m_clJDBCAdapter.executeQuery(sql);

			// Set MatchType in all table but YouthTable from entry in MATCHESKURZINFO =============================
			// use match lineup table to fix match types, since the lineup table holds the youth matches too
			// the types in lineup table seems to be the correct one - at least in my database (ws) - no fake types of sapphire cup and co.
			//List<String> lTables = List.of("IFA_MATCHES", "MATCHDETAILS", "MATCHLINEUP", "MATCHHIGHLIGHTS", "MATCHLINEUPPLAYER", "MATCHLINEUPTEAM",
			//		"MATCHORDER", "MATCHSUBSTITUTION");

			copyMatchTypes("MATCHESKURZINFO", "IFA_MATCHES");
			copyMatchTypes("MATCHESKURZINFO", "MATCHORDER");		// no lineup available yet for match orders
			copyMatchTypes("MATCHLINEUP", "MATCHDETAILS");
			copyMatchTypes("MATCHLINEUP", "MATCHHIGHLIGHTS");
			copyMatchTypes("MATCHLINEUP", "MATCHLINEUPPLAYER");
			copyMatchTypes("MATCHLINEUP", "MATCHLINEUPTEAM");
			copyMatchTypes("MATCHLINEUP", "MATCHSUBSTITUTION");
			copyMatchTypes("MATCHLINEUP", "YOUTHTRAINING");

			// Update primary key from matchID => (matchID, MATCHTYP) because doublons might otherwise exists
			m_clJDBCAdapter.executeQuery("ALTER TABLE IFA_MATCHES ADD PRIMARY KEY (MATCHID, MATCHTYP)");
			m_clJDBCAdapter.executeQuery("ALTER TABLE MATCHDETAILS ADD PRIMARY KEY (MATCHID, MATCHTYP)");

			HOLogger.instance().debug(getClass(), "Upgrade of DB structure SourceSystem/MatchType is complete ! ");
		}

		if (!columnExistsInTable("LAST_MATCH_TYPE", "SPIELER")) {
			m_clJDBCAdapter.executeQuery("ALTER TABLE SPIELER ADD COLUMN LAST_MATCH_TYPE INTEGER ");
		}

		// Delete corrupt entries (wrong week numbers) from TA_PLAYER table
		var hrfTable = (HRFTable)dbManager.getTable(HRFTable.TABLENAME);
		var hrf = hrfTable.getLatestHrf();
		if ( hrf.isOK() ) {
			m_clJDBCAdapter.executeUpdate("DELETE FROM " + TAPlayerTable.TABLENAME
					+ " WHERE WEEK> (SELECT SAISON*16+SPIELTAG-1 FROM " + basicsTable.getTableName() + " WHERE HRF_ID=" + hrf.getHrfId() + ")");
		}

		dbManager.getTable(MatchesKurzInfoTable.TABLENAME).tryAddColumn("isObsolete", "BOOLEAN");

		updateDBVersion(dbVersion, 500);
	}

	private void copyMatchTypes(String fromTable, String toTable) {
		String sql = "UPDATE " + toTable + " t1 SET MATCHTYP = (SELECT MK.MATCHTYP FROM " + fromTable +" MK WHERE t1.MATCHID = MK.MATCHID)";
		m_clJDBCAdapter.executeQuery(sql);
		sql = "UPDATE " + toTable + " SET MATCHTYP = 0 WHERE MATCHTYP IS NULL";
		m_clJDBCAdapter.executeQuery(sql);
	}

	private void updateDBv400(int dbVersion) throws SQLException {
		// Delete existing values to provide sane defaults.
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'spielerUebersichtsPanel_horizontalRightSplitPane'");
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'aufstellungsPanel_verticalSplitPane'");
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'aufstellungsPanel_horizontalRightSplitPane'");
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'aufstellungsPanel_horizontalLeftSplitPane'");

		if (!columnExistsInTable("SeasonOffset", BasicsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE BASICS ADD COLUMN SeasonOffset INTEGER");
		}

		if (!columnExistsInTable("Duration", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN Duration INTEGER ");
		}
		if (!columnExistsInTable("MatchPart", MatchHighlightsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN MatchPart INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN EventVariation INTEGER ");
		}
		if (!columnExistsInTable("HomeGoal0", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal0 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal1 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal2 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal3 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal4 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal0 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal1 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal2 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal3 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal4 INTEGER ");
		}

		if (!columnExistsInTable("NAME", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN NAME VARCHAR (100) ");
		}

		// use defaults player formula from defaults.xml by resetting the value in the database
		try {
			AbstractTable faktorenTab = dbManager.getTable(FaktorenTable.TABLENAME);
			if (faktorenTab != null) {
				faktorenTab.dropTable();
				faktorenTab.createTable();
			}
		} catch (SQLException throwables) {
			HOLogger.instance().error(getClass(), "updateDBv400:  Faktoren table could not be reset");
			throwables.printStackTrace();
		}


		resetUserColumns();

		//create FuturePlayerTrainingTable
		if (!tableExists(FuturePlayerTrainingTable.TABLENAME)) {
			dbManager.getTable(FuturePlayerTrainingTable.TABLENAME).createTable();
		}

		updateDBVersion(dbVersion, 400);
	}

	private void updateDBv301(int dbVersion) throws SQLException {

		m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ALTER COLUMN isDerby SET DATA TYPE BOOLEAN");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ALTER COLUMN isNeutral SET DATA TYPE BOOLEAN");

		if (!columnExistsInTable("EVENT_INDEX", MatchHighlightsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN EVENT_INDEX INTEGER");
		}

		if (!columnExistsInTable("INJURY_TYPE", MatchHighlightsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN INJURY_TYPE TINYINT");
		}

		if (columnExistsInTable("TYP", MatchHighlightsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("UPDATE MATCHHIGHLIGHTS SET MATCH_EVENT_ID = (TYP * 100) + SUBTYP WHERE MATCH_EVENT_ID IS NULL");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS DROP TYP");
		}

		if (!columnExistsInTable("LastMatchDate", SpielerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LastMatchDate VARCHAR (100)");
		}
		if (!columnExistsInTable("LastMatchRating", SpielerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LastMatchRating INTEGER");
		}
		if (!columnExistsInTable("LastMatchId", SpielerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LastMatchId INTEGER");
		}

		Arrays.asList("HEIMTORE", "GASTTORE", "SUBTYP").forEach(s -> {
			try {
				if (columnExistsInTable(s, MatchHighlightsTable.TABLENAME)) {
					m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS DROP " + s);
				}
			} catch (SQLException e) {
				HOLogger.instance().log(getClass(), e);
			}
		});

		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchdetails_heimid_idx ON MATCHDETAILS (HEIMID)");
		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchdetails_gastid_idx ON MATCHDETAILS (GASTID)");
		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchkurzinfo_heimid_idx ON MATCHESKURZINFO (HEIMID)");
		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchkurzinfo_gastid_idx ON MATCHESKURZINFO (GASTID)");
		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchhighlights_teamid_idx ON MATCHHIGHLIGHTS (TEAMID)");
		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchhighlights_eventid_idx ON MATCHHIGHLIGHTS (MATCH_EVENT_ID)");

		Arrays.asList("GlobalRanking", "LeagueRanking", "RegionRanking", "PowerRating").forEach(s -> {
			try {
				if (!columnExistsInTable(s, VereinTable.TABLENAME)) {
					m_clJDBCAdapter.executeUpdate(String.format("ALTER TABLE VEREIN ADD COLUMN %s INTEGER", s));
				}
			} catch (SQLException e) {
				HOLogger.instance().log(getClass(), e);
			}
		});

		Arrays.asList("TWTrainer", "Physiologen").forEach(s -> {
			try {
				if (columnExistsInTable(s, VereinTable.TABLENAME)) {
					m_clJDBCAdapter.executeUpdate("ALTER TABLE VEREIN DROP " + s);
				}
			} catch (SQLException e) {
				HOLogger.instance().log(getClass(), e);
			}
		});

		updateDBVersion(dbVersion, 301);

	}

	private void updateDBv300() throws SQLException {
		// HO 3.0

		// delete old divider locations
		m_clJDBCAdapter
				.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='teamAnalyzer_LowerLefSplitPane'");
		m_clJDBCAdapter
				.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='teamAnalyzer_UpperLeftSplitPane'");
		m_clJDBCAdapter
				.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='teamAnalyzer_MainSplitPane'");

		//store ArenaId into MATCHESKURZINFO table
		if (!columnExistsInTable("ArenaId", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN ArenaId INTEGER");
		}

		//store RegionId into MATCHESKURZINFO table
		if (!columnExistsInTable("RegionId", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN RegionId INTEGER");
		}

		//store Weather into MATCHESKURZINFO table
		if (!columnExistsInTable("Weather", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN Weather INTEGER");
		}

		//store WeatherForecast into MATCHESKURZINFO table
		if (!columnExistsInTable("WeatherForecast", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN WeatherForecast INTEGER");
		}

		//store isDerby into MATCHESKURZINFO table
		if (!columnExistsInTable("isDerby", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN isDerby BOOLEAN");
		}

		//store isNeutral into MATCHESKURZINFO table
		if (!columnExistsInTable("isNeutral", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN isNeutral BOOLEAN");
		}

		//store Salary into TA_PLAYER table
		if (!columnExistsInTable("SALARY", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN SALARY INTEGER");
		}

		//store Stamina  into TA_PLAYER table
		if (!columnExistsInTable("STAMINA", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN STAMINA INTEGER");
		}

		//store MotherClubBonus  into TA_PLAYER table
		if (!columnExistsInTable("MOTHERCLUBBONUS", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN MOTHERCLUBBONUS BOOLEAN");
		}

		//store Loyalty  into TA_PLAYER table
		if (!columnExistsInTable("LOYALTY", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN LOYALTY INTEGER");
		}

		//store RATINGINDIRECTSETPIECESATT  into MATCHDETAILS table
		if (!columnExistsInTable("RATINGINDIRECTSETPIECESATT", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN RATINGINDIRECTSETPIECESATT INTEGER");
		}

		//store RATINGINDIRECTSETPIECESDEF  into MATCHDETAILS table
		if (!columnExistsInTable("RATINGINDIRECTSETPIECESDEF", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN RATINGINDIRECTSETPIECESDEF INTEGER");
		}

		//store FirstName, Nickname  into Playertable
		if (!columnExistsInTable("FirstName", SpielerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN FirstName VARCHAR (100)");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN NickName VARCHAR (100)");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ALTER COLUMN Name RENAME TO LastName");
		}

		// Delete league plans which are not of our own team
		try {
			// find own league plans
			int teamId = getTeamId();
			// select saison,ligaid from paarung where heimid=520472 group by saison,ligaid
			HashMap<Integer, Integer> ownLeaguePlans = new HashMap<>();
			ResultSet rs = m_clJDBCAdapter.executeQuery("select saison,ligaid from paarung where heimid=" + teamId + " group by saison,ligaid");
			if (rs != null) {
				while (rs.next()) {
					int saison = rs.getInt(1);
					int league = rs.getInt(2);
					ownLeaguePlans.put(saison, league);
				}
				rs.close();
			}
			// delete entries in SPIELPLAN and PAARUNG which are not from own team
			rs = m_clJDBCAdapter.executeQuery("select saison,ligaid from spielplan");
			if (rs != null) {
				while (rs.next()) {
					int saison = rs.getInt(1);
					int league = rs.getInt(2);
					if (!ownLeaguePlans.containsKey(saison) || ownLeaguePlans.get(saison) != league) {
						// league is not our own one
						m_clJDBCAdapter.executeUpdate("DELETE FROM spielplan WHERE ligaid=" + league + " and saison=" + saison);
						m_clJDBCAdapter.executeUpdate("DELETE FROM paarung WHERE ligaid=" + league + " and saison=" + saison);
					}
				}
				rs.close();
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
		HOLogger.instance().info(DBUpdater.class, "updateDBv300() successfully completed");
	}

	private void updateDBVersion(int DBVersion, int version) {
		if (version < DBVersion) {
			if (!HO.isDevelopment()) {
				HOLogger.instance().info(DBUpdater.class, "Update to " + version + " done. Updating DBVersion");
				dbManager.saveUserParameter("DBVersion", version);
			} else {
				HOLogger.instance().debug(DBUpdater.class, "Update to " + version + " done but this is a development version so DBVersion will remain unchanged");
			}
		} else if (version == DBVersion) {
			if (!HO.isDevelopment()) {
				HOLogger.instance().info(DBUpdater.class, "Update complete, setting DBVersion to " + version);
				dbManager.saveUserParameter("DBVersion", version);
			} else {
				HOLogger.instance().debug(DBUpdater.class, "Update to " + version + " complete but this is a development version so DBVersion will remain unchanged");
			}
		} else {
			HOLogger.instance().error(DBUpdater.class,
					"Error trying to set DB version to unidentified value:  " + version
							+ " (isDevelopment=" + HO.isDevelopment() + ")");
		}
	}

	private int getTeamId() {
		try {
			ResultSet rs = m_clJDBCAdapter.executeQuery("select teamid from basics limit 1");
			if (rs != null) {
				rs.first();
				int ret = rs.getInt(1);
				rs.close();
				return ret;
			}
		} catch (SQLException e) {
			HOLogger.instance().log(getClass(), e);
		}
		return 0;
	}

	private boolean columnExistsInTable(String columnName, String tableName) throws SQLException {
		String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME = '"
				+ tableName.toUpperCase()
				+ "' AND COLUMN_NAME = '"
				+ columnName.toUpperCase()
				+ "'";
		ResultSet rs = this.m_clJDBCAdapter.executeQuery(sql);
		if (rs != null) return rs.next();
		return false;
	}

	private boolean tableExists(String tableName) throws SQLException {
		String sql = "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME = '"
				+ tableName.toUpperCase() + "'";
		ResultSet rs = this.m_clJDBCAdapter.executeQuery(sql);
		if (rs != null) return rs.next();
		return false;
	}

	private void resetUserColumns() {
		HOLogger.instance().debug(getClass(), "Resetting player overview rows.");
		String sql = "DELETE FROM USERCOLUMNS WHERE COLUMN_ID BETWEEN 2000 AND 3000";
		m_clJDBCAdapter.executeQuery(sql);

		HOLogger.instance().debug(getClass(), "Resetting lineup overview rows.");
		sql = "DELETE FROM USERCOLUMNS WHERE COLUMN_ID BETWEEN 3000 AND 4000";
		m_clJDBCAdapter.executeQuery(sql);
	}
}
