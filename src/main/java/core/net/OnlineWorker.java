package core.net;

import core.db.DBManager;
import core.file.ExampleFileFilter;
import core.file.hrf.HRFStringParser;
import core.file.xml.*;
import core.gui.HOMainFrame;
import core.gui.InfoPanel;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.Tournament.TournamentDetails;
import core.model.UserParameter;
import core.model.enums.MatchType;
import core.model.enums.MatchTypeExtended;
import core.model.match.*;
import core.model.misc.Regiondetails;
import core.model.misc.TrainingEvent;
import core.prediction.engine.TeamRatings;
import core.util.HOLogger;
import core.util.Helper;
import core.util.StringUtils;
import module.lineup.Lineup;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * @author thomas.werth
 */
public class OnlineWorker {


	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private OnlineWorker() {
	}

	/**
	 * Get and optionally save HRF
	 *
	 */
	public static boolean getHrf(JDialog parent) {
		// Show wait dialog
		
		boolean ok = true;

		try {
			HOMainFrame homf = HOMainFrame.instance();
			HOVerwaltung hov = HOVerwaltung.instance();

			UserParameter up = core.model.UserParameter.instance();

			String hrf = null;
			try {
				hrf = ConvertXml2Hrf.createHrf();
				if (hrf == null) {
					return false;
				}
				
			} catch (IOException e) {
				// Info
				String msg = getLangString("Downloadfehler")
						+ " : Error converting xml 2 HRF. Corrupt/Missing Data : ";
				setInfoMsg(msg, InfoPanel.FEHLERFARBE);
				Helper.showMessage(parent, msg + "\n" + e.toString() + "\n", getLangString("Fehler"),
						JOptionPane.ERROR_MESSAGE);
				ok = false;
			}

			if (hrf != null) {
				if (hrf.contains("playingMatch=true")) {
					HOMainFrame.instance().resetInformation();
					JOptionPane.showMessageDialog(parent, getLangString("NO_HRF_Spiel"),
							getLangString("NO_HRF_ERROR"), 1);
				} else if (hrf.contains("NOT AVAILABLE")) {
					HOMainFrame.instance().resetInformation();
					JOptionPane.showMessageDialog(parent, getLangString("NO_HRF_ERROR"),
							getLangString("NO_HRF_ERROR"), 1);
				} else {
					// Create HOModel from the hrf data
					HOModel homodel = HRFStringParser.parse(hrf);
					if (homodel == null) {
						// Info
						setInfoMsg(getLangString("Importfehler"), InfoPanel.FEHLERFARBE);
						// Error
						Helper.showMessage(parent, getLangString("Importfehler"),
								getLangString("Fehler"), JOptionPane.ERROR_MESSAGE);
					} else {
						// save the model in the database
						homodel.saveHRF();

						homodel.setFixtures(hov.getModel().getFixtures());

						// Add old players to the model
						homodel.setFormerPlayers(DBManager.instance().getAllSpieler());
						// Only update when the model is newer than existing
						if (HOVerwaltung.isNewModel(homodel)) {
							// Reimport Skillup
							DBManager.instance().checkSkillup(homodel);
							// Show
							hov.setModel(homodel);
							// Recalculate Training
							// Training->Subskill calculation
							//TrainingManager.instance().refreshTrainingWeeks();
							//homodel.calcSubskills(); // moved behind the download of matches
//							LineupsComparisonHistoryPanel.setHRFAufstellung(
//									homodel.getLineup(), homodel.getPreviousLineup());
//							LineupsComparisonHistoryPanel
//									.setAngezeigteAufstellung(new LineupCBItem(
//											getLangString("AktuelleAufstellung"), homodel
//													.getLineup()));

							// reset value of TS, confidence in Lineup Settings Panel after data download
							HOMainFrame.instance().getLineupPanel().getLineupSettingsPanel().backupRealGameSettings();

						}
						// Info
						setInfoMsg(getLangString("HRFErfolg"));

						saveHRFToFile(parent,hrf);
					}
				}
			}
		} finally {
			HOMainFrame.instance().resetInformation();
		}
		return ok;
	}

	/**
	 * saugt das Archiv
	 *
	 * @param teamId
	 *            null falls unnötig sonst im Format 2004-02-01
	 * @param firstDate
	 *            null falls unnötig sonst im Format 2004-02-01
	 * @param store
	 *            True if matches are to be downloaded and stored. False if only
	 *            a match list is wanted.
	 *
	 * @return The list of MatchKurzInfo. This can be null on error, or empty.
	 */
	public static List<MatchKurzInfo> getMatchArchive(int teamId, Date firstDate, boolean store) {

		List<MatchKurzInfo> allMatches = new ArrayList<>();
		GregorianCalendar tempBeginn = new GregorianCalendar();
		tempBeginn.setTime(firstDate);
		GregorianCalendar tempEnd = new GregorianCalendar();
		tempEnd.setTimeInMillis(tempBeginn.getTimeInMillis());
		tempEnd.add(Calendar.MONTH, 3);

		GregorianCalendar endDate = new GregorianCalendar();
		if (!tempEnd.before(endDate)) {
			tempEnd.setTime(endDate.getTime());
		}

		// Show wait Dialog
		showWaitInformation(1);

		try {
			String matchesString;

			while (tempBeginn.before(endDate)) {
				try {
					showWaitInformation(10);
					matchesString = MyConnector.instance().getMatchesArchive(teamId, tempBeginn.getTime(),
							tempEnd.getTime());
					showWaitInformation(20);
				} catch (Exception e) {
					// Info
					String msg = getLangString("Downloadfehler")
							+ " : Error fetching MatchArchiv : ";
					setInfoMsg(msg, InfoPanel.FEHLERFARBE);
					Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
							JOptionPane.ERROR_MESSAGE);
					showWaitInformation(0);
					return null;
				}

				showWaitInformation(40);
				List<MatchKurzInfo> matches = XMLMatchArchivParser
						.parseMatchesFromString(matchesString);

				// Add the new matches to the list of all matches
				allMatches.addAll(matches);

				// Zeitfenster neu setzen
				tempBeginn.add(Calendar.MONTH, 3);
				tempEnd.add(Calendar.MONTH, 3);

				if (!tempEnd.before(endDate)) {
					tempEnd.setTime(endDate.getTime());
				}
			}

			// Store in the db if store is true
			if (store && (allMatches.size() > 0)) {

				showWaitInformation(80);
				DBManager.instance().storeMatchKurzInfos(allMatches.toArray(new MatchKurzInfo[0]));

				// Store full info for all matches
				for (MatchKurzInfo match : allMatches) {
					// if match is available and match is finished
					if ((DBManager.instance().isMatchInDB(match.getMatchID(), match.getMatchType()))
							&& (match.getMatchStatus() == MatchKurzInfo.FINISHED)) {
						downloadMatchData(match.getMatchID(), match.getMatchType(), true);
					}
				}
			}
		} finally {
			showWaitInformation(0);
		}
		return allMatches;
	}

	/**
	 * Downloads a match with the given criteria and stores it in the database.
	 * If a match is already in the db, and refresh is false, nothing is
	 * downloaded.
	 *
	 * @param matchid
	 *            ID for the match to be downloaded
	 * @param matchType
	 *            matchType for the match to be downloaded.
	 * @param refresh
	 *            If true the match will always be downloaded.
	 *
	 * @return true if the match is in the db afterwards
	 */
	public static boolean downloadMatchData(int matchid, MatchType matchType, boolean refresh) {
		MatchKurzInfo info;
		if (DBManager.instance().isMatchInDB(matchid, matchType)) {
			info = DBManager.instance().getMatchesKurzInfoByMatchID(matchid, matchType);
		}
		else {
			info = new MatchKurzInfo();
			info.setMatchID(matchid);
			info.setMatchType(matchType);
		}
		return downloadMatchData(info, refresh);
	}

	public static boolean downloadMatchData(MatchKurzInfo info, boolean refresh)
	{
		if (info.isObsolet()){
			return true;
		}

		HOLogger.instance().debug(OnlineWorker.class, "Get Lineup : " + info.getMatchID());

		int matchID = info.getMatchID();
		if (matchID < 0) {
			return false;
		}

		showWaitInformation(1);
		// Only download if not present in the database, or if refresh is true or if match not oboslet
		if (refresh
				|| !DBManager.instance().isMatchInDB(matchID, info.getMatchType())
				|| DBManager.instance().hasUnsureWeatherForecast(matchID)
				|| !DBManager.instance().isMatchLineupInDB(info.getMatchType(), matchID)
		) {
			try {
				Matchdetails details;

				// If ids not found, download matchdetails to obtain them.
				// Highlights will be missing.
				// ArenaId==0 in division battles
				boolean newInfo = info.getHomeTeamID()<=0 || info.getGuestTeamID()<=0;
				Weather.Forecast weatherDetails = info.getWeatherForecast();
				boolean bWeatherKnown = ((weatherDetails != null) && weatherDetails.isSure());
				if ( newInfo || !bWeatherKnown) {

					showWaitInformation(10);
					details = downloadMatchDetails(matchID, info.getMatchType(), null);
					if ( details != null) {
						info.setHomeTeamID(details.getHomeTeamId());
						info.setGuestTeamID(details.getGuestTeamId());
						info.setArenaId(details.getArenaID());
						info.setMatchSchedule(details.getMatchDate().toString());
						int wetterId = details.getWetterId();
						if (wetterId != -1) {
							info.setMatchStatus(MatchKurzInfo.FINISHED);
							info.setWeather(Weather.getById(details.getWetterId()));
							info.setWeatherForecast(Weather.Forecast.HAPPENED);
						} else if (info.getArenaId() > 0) {
							info.setRegionId(details.getRegionId());

							if (!info.getWeatherForecast().isSure()) {
								Regiondetails regiondetails = getRegionDetails(info.getRegionId());
								if ( regiondetails != null) {
									SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
									java.sql.Timestamp matchDate = info.getMatchDateAsTimestamp();
									java.sql.Timestamp weatherDate = regiondetails.getFetchDatum();
									String wdate = fmt.format(weatherDate);
									String mdate = fmt.format(matchDate);
									if (mdate.equals(wdate)) {
										info.setWeatherForecast(Weather.Forecast.TODAY);
										info.setWeather(regiondetails.getWeather());
									} else {
										Calendar c = Calendar.getInstance();
										c.setTime(fmt.parse(wdate));
										c.add(Calendar.DATE, 1);
										if (fmt.format(c.getTime()).equals(mdate)) {
											info.setWeatherForecast(Weather.Forecast.TOMORROW);
										} else {
											info.setWeatherForecast((Weather.Forecast.UNSURE));
										}
										info.setWeather(regiondetails.getWeatherTomorrow());
									}
								}
							}
						}

						// get the other team
						int otherId;
						if (info.isHomeMatch()) {
							otherId = info.getGuestTeamID();
						} else {
							otherId = info.getHomeTeamID();
						}
						if (otherId > 0) {
							Map<String, String> otherTeam = getTeam(otherId);
							info.setIsDerby(getRegionId(otherTeam) == HOVerwaltung.instance().getModel().getBasics().getRegionId());
							info.setIsNeutral(info.getArenaId() != HOVerwaltung.instance().getModel().getStadium().getArenaId()
									&& info.getArenaId() != getArenaId(otherTeam));
							DBManager.instance().storeTeamLogoInfo(otherId, getLogoURL(otherTeam), null);

						} else {
							// Verlegenheitstruppe 08/15
							info.setIsDerby(false);
							info.setIsNeutral(false);
						}
					}
				}

				MatchLineup lineup;
				boolean success;
				if ( (info.getMatchStatus() == MatchKurzInfo.FINISHED) && (! info.isObsolet())) {
					lineup = downloadMatchlineup(matchID, info.getMatchType(), info.getHomeTeamID(), info.getGuestTeamID());
					if (lineup == null) {
						if ( !isSilentDownload()) {
							String msg = getLangString("Downloadfehler")
									+ " : Error fetching Matchlineup :";
							// Info
							setInfoMsg(msg, InfoPanel.FEHLERFARBE);
							Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
									JOptionPane.ERROR_MESSAGE);
						}
						return false;
					}

					downloadTeamRatings(matchID, info.getMatchType(), info.getHomeTeamID());
					downloadTeamRatings(matchID, info.getMatchType(), info.getGuestTeamID());

					// Get details with highlights.
					showWaitInformation(10);
					details = downloadMatchDetails(matchID, info.getMatchType(), lineup);

					if (details == null) {
						HOLogger.instance().error(OnlineWorker.class,
								"Error downloading match. Details is null: " + matchID);
						return false;
					}
					info.setDuration(details.getLastMinute());
					info.setGuestTeamGoals(details.getGuestGoals());
					info.setHomeTeamGoals(details.getHomeGoals());
					info.setGuestTeamID(lineup.getGuestTeamId());
					info.setGuestTeamName(lineup.getGuestTeamName());
					info.setHomeTeamID(lineup.getHomeTeamId());
					info.setHomeTeamName(lineup.getHomeTeamName());
					success = DBManager.instance().storeMatch(info, details, lineup);
				}
				else{
					// Update arena and region ids
					MatchKurzInfo[] matches = {info};
					DBManager.instance().storeMatchKurzInfos(matches);
					success = true;
				}
				if (!success) {
					showWaitInformation(0);
					return false;
				}
			} catch (Exception ex) {
				HOLogger.instance().error(OnlineWorker.class,
						"downloadMatchData:  Error in downloading match: " + ex);
				showWaitInformation(0);
				return false;
			}
		}
		showWaitInformation(0);
		return true;
	}

	private static void downloadTeamRatings(int matchID, MatchType matchType, int teamID) {
		try {
			var xml = MyConnector.instance().getTeamdetails(teamID);
			var teamrating = new MatchTeamRating(matchID, matchType, XMLTeamDetailsParser.parseTeamdetailsFromString(xml, teamID));
			DBManager.instance().storeTeamRatings(teamrating);
		} catch (Exception e) {
			String msg = getLangString("Downloadfehler") + " : Error fetching Team ratings :";
			// Info
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private static Map<String, String> getTeam(int teamId)
	{
		String str = MyConnector.instance().fetchTeamDetails(teamId);
		return XMLTeamDetailsParser.parseTeamdetailsFromString(str, teamId);
	}

	private static int getRegionId(Map<String, String> team) {
		String  str = team.get("RegionID");
		if ( str != null ) return Integer.parseInt(str);
		return 0;
	}

	private  static int getArenaId(Map<String, String> team)
	{
		String str = team.get("ArenaID");
		if ( str != null ) return Integer.parseInt(str);
		return 0;
	}


	public static String getLogoURL(Map<String, String> team)
	{
		String str = team.get("LogoURL");
		if ( (str == null) || (str.equals(""))) {
			return null;
		}
		else {
			return str;
		}
	}

	/**
	 * Loads the data for the given match from HT and updates the data for this
	 * match in the DB.
	 *
	 * @param teamId
	 *            the id of the team
	 * @param match
	 *            the match to update
	 * @return a new MatchKurzInfo object with the current data from HT or null
	 *         if match could not be downloaded.
	 */
	public static @Nullable MatchKurzInfo updateMatch(int teamId, MatchKurzInfo match) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(match.getMatchDateAsTimestamp());
		cal.add(Calendar.MINUTE, 1);
		// At the moment, HT does not support getting a single match.
		List<MatchKurzInfo> matches = getMatches(teamId, cal.getTime());
		for (MatchKurzInfo m : matches) {
			if (m.getMatchID() == match.getMatchID()) {
				DBManager.instance().updateMatchKurzInfo(m);
				return m;
			}
		}
		return null;
	}

	/**
	 * Gets the most recent and upcoming matches for a given teamId and up to a
	 * specific date. Nothing is stored to DB.
	 *
	 * @param teamId
	 *            the id of the team.
	 * @param date
	 *            last date (+time) to get matches to.
	 * @return the most recent and upcoming matches up to the given date.
	 */
	public static List<MatchKurzInfo> getMatches(int teamId, Date date) {
		String matchesString = null;
		try {
			matchesString = MyConnector.instance().getMatches(teamId, true, date);
		} catch (IOException e) {
			Helper.showMessage(HOMainFrame.instance(), getLangString("Downloadfehler")
					+ " : Error fetching matches : " + e, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			HOLogger.instance().log(OnlineWorker.class, e);
		}

		if (!StringUtils.isEmpty(matchesString)) {
			return XMLMatchesParser.parseMatchesFromString(matchesString);
		}

		return new ArrayList<>();
	}

	/**
	 * Download information about a given tournament
	 */

	public static TournamentDetails getTournamentDetails(int tournamentId) {
		TournamentDetails oTournamentDetails = null;
		String tournamentString = "";

		try {
			tournamentString = MyConnector.instance().getTournamentDetails(tournamentId);
		} catch (IOException e) {
			Helper.showMessage(HOMainFrame.instance(), getLangString("Downloadfehler")
							+ " : Error fetching Tournament Details : " + e, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			HOLogger.instance().log(OnlineWorker.class, e);
		}

		if (!StringUtils.isEmpty(tournamentString)) {
			oTournamentDetails = XMLTournamentDetailsParser.parseTournamentDetailsFromString(tournamentString);  // TODO: create function parseTournamentDetailsFromString
		}

		return oTournamentDetails;
	}

	/**
	 * saugt den Spielplan
	 *
	 * @param teamId
	 *            angabe der Saison ( optinal &lt; 1 für aktuelle
	 * @param forceRefresh
	 * @param store
	 *            true if the full match details are to be stored, false if not.
	 * @param upcoming
	 *            true if upcoming matches should be included
	 *
	 * @return The list of MatchKurzInfos found or null if an exception
	 *         occurred.
	 */
	public static List<MatchKurzInfo> getMatches(int teamId, boolean forceRefresh, boolean store,
			boolean upcoming) {
		String matchesString;
		List<MatchKurzInfo> matches = new ArrayList<>();
		boolean bOK = false;
		showWaitInformation(10);

		try {
			matchesString = MyConnector.instance().getMatches(teamId, forceRefresh, upcoming);
			bOK = (matchesString != null && matchesString.length() > 0);
			if (bOK)
				showWaitInformation(50);
			else
				showWaitInformation(0);
		} catch (Exception e) {
			String msg = getLangString("Downloadfehler") + " : Error fetching matches: "
					+ e.getMessage();
			// Info
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			HOLogger.instance().log(OnlineWorker.class, e);
			showWaitInformation(0);
			return null;
		}
		if (bOK) {
			matches.addAll(XMLMatchesParser.parseMatchesFromString(matchesString));

			// Store in DB if store is true
			if (store) {
				showWaitInformation(80);

				matches = FilterUserSelection(matches);
				DBManager.instance().storeMatchKurzInfos(matches.toArray(new MatchKurzInfo[0]));

				showWaitInformation(100);

				// Automatically download additional match infos (lineup + arena)
				for (MatchKurzInfo match : matches) {
					int curMatchId = match.getMatchID();
					boolean refresh = !DBManager.instance().isMatchInDB(curMatchId, match.getMatchType())
							|| (match.getMatchStatus() != MatchKurzInfo.FINISHED && DBManager.instance().hasUnsureWeatherForecast(curMatchId))
							|| !DBManager.instance().isMatchLineupInDB(match.getMatchType(), curMatchId);

					if (refresh) {
						// No lineup or arenaId in DB
						boolean result = downloadMatchData(curMatchId, match.getMatchType(), refresh);
						if (!result) {
							break;
						}
					}
				}
			}
		}
		showWaitInformation(0);
		return matches;
	}

	public static List<MatchKurzInfo> FilterUserSelection(List<MatchKurzInfo> matches) {
		ArrayList<MatchKurzInfo> ret = new ArrayList<>();
		for (MatchKurzInfo m: matches) {
			switch (m.getMatchType()) {
				case INTSPIEL:
				case NATIONALCOMPNORMAL:
				case NATIONALCOMPCUPRULES:
				case NATIONALFRIENDLY:
				case PREPARATION:
				case LEAGUE:
				case QUALIFICATION:
				case CUP:
				case FRIENDLYNORMAL:
				case FRIENDLYCUPRULES:
				case INTFRIENDLYNORMAL:
				case INTFRIENDLYCUPRULES:
				case MASTERS:
					if (UserParameter.instance().downloadCurrentMatchlist) {
						ret.add(m);
					}
					break;
				case TOURNAMENTGROUP:
					// this is TOURNAMENTGROUP but more specifically a division battle
					if (m.getMatchTypeExtended() == MatchTypeExtended.DIVISIONBATTLE){
						if (UserParameter.instance().downloadDivisionBattleMatches) {
							// we add the game only if user selected division battle category
							ret.add(m);
						}
					}
					else{
						// this is TOURNAMENTGROUP but not a division battle
						if (UserParameter.instance().downloadTournamentGroupMatches) {
							ret.add(m);
						}
					}
					break;
				case TOURNAMENTPLAYOFF:
					if (UserParameter.instance().downloadTournamentPlayoffMatches) {
						ret.add(m);
					}
					break;
				case SINGLE:
					if (UserParameter.instance().downloadSingleMatches) {
						ret.add(m);
					}
					break;
				case LADDER:
					if (UserParameter.instance().downloadLadderMatches) {
						ret.add(m);
					}
					break;
				default:
					HOLogger.instance().warning(OnlineWorker.class, "Unknown Matchtyp:" + m.getMatchType() + ". Is not downloaded!");
					break;
			}
		}
		return ret;
	}

	/**
	 * Download match lineup
	 *
	 * @param matchId
	 * 			Match Id
	 * @param matchType
	 * 			MatchType
	 * @param teamId1
	 * 			Id of first team to include to the returned lineup
	 * @param teamId2
	 * 			Optional id of second team
	 * @return
	 * 			MatchLineup containing specified teams
	 */
	private static MatchLineup downloadMatchlineup(int matchId, MatchType matchType, int teamId1,
												   int teamId2) {
		boolean bOK = false;
		MatchLineup lineUp1 = null;
		MatchLineup lineUp2 = null;

		// Wait Dialog zeigen
		showWaitInformation(10);

		// Lineups holen
		lineUp1 = downloadMatchLineup(matchId, teamId1, matchType);
		if (lineUp1 != null) {
			showWaitInformation(50);
			if (teamId2 > 0)
				lineUp2 = downloadMatchLineup(matchId, teamId2, matchType);

			// Merge the two
			if ((lineUp2 != null)) {
				if (!lineUp1.isHomeTeamLoaded())
					lineUp1.setHomeTeam(lineUp2.getHomeTeam());
				else if (!lineUp1.isGuestTeamLoaded())
					lineUp1.setGuestTeam(lineUp2.getGuestTeam());
			} else {
				// Get the 2nd lineup
				if (!lineUp1.isHomeTeamLoaded()) {
					lineUp2 = downloadMatchLineup(matchId, lineUp1.getHomeTeamId(), matchType);
					if (lineUp2 != null)
						lineUp1.setHomeTeam(lineUp2.getHomeTeam());
				} else {
					lineUp2 = downloadMatchLineup(matchId, lineUp1.getGuestTeamId(), matchType);
					if (lineUp2 != null)
						lineUp1.setGuestTeam(lineUp2.getGuestTeam());
				}
			}
		}
		showWaitInformation(0);
		return lineUp1;
	}

	/**
	 * Get the Fixtures list
	 *
	 * @param season
	 *            - The season, -1 for current
	 * @param leagueID
	 *            - The ID of the league to get the fixtures for
	 *
	 * @return true on sucess, false on failure
	 */
	public static boolean getSpielplan(int season, int leagueID) {
		boolean bOK = false;
		String leagueFixtures;
		HOVerwaltung hov = HOVerwaltung.instance();
		try {
			showWaitInformation(10);
			leagueFixtures = MyConnector.instance().getLeagueFixtures(season, leagueID);
			bOK = (leagueFixtures != null && leagueFixtures.length() > 0);
			showWaitInformation(50);
		} catch (Exception e) {
			HOLogger.instance().log(OnlineWorker.class, e);
			String msg = getLangString("Downloadfehler") + " : Error fetching leagueFixture: "
					+ e.getMessage();
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			showWaitInformation(0);
			return false;
		}
		if (bOK) {
			HOModel hom = hov.getModel();
			hom.setFixtures(XMLSpielplanParser.parseSpielplanFromString(leagueFixtures));
			showWaitInformation(70);
			// Save to DB
			hom.saveFixtures();
			showWaitInformation(90);
		}
		showWaitInformation(0);
		return bOK;
	}

	protected static void showWaitInformation(int i) {
		if (HOMainFrame.launching.get()) return;
		String info;
		if ( i <=0 ){
			HOMainFrame.instance().resetInformation();
		}
		else{
			HOMainFrame.instance().setWaitInformation(i);
		}
	}

	/**
	 * Uploads the given order to Hattrick
	 *
	 * @param matchId
	 *            The id of the match in question. If left at 0 the match ID
	 *            from the model will be used (next match).
	 * @param lineup
	 *            The lineup object to be uploaded
	 * @return A string response with any error message
	 */

	public static String uploadMatchOrder(int matchId, MatchType matchType, Lineup lineup) {
		String result;
		String orders = lineup.toJson();
		try {
			result = MyConnector.instance().setMatchOrder(matchId, HOVerwaltung.instance().getModel().getBasics().getTeamId(), matchType, orders);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return result;
	}


	/**
	 * Try to recover missing matchType information by querying HT with different source system and returning first result
	 *
	 * @param _match the match id
	 * @return the match type
	 */
	public static MatchKurzInfo inferMissingMatchType(MatchKurzInfo _match) {
		String matchDetails;
		Matchdetails details;
		var conn = MyConnector.instance();
		conn.setSilentDownload(true);



		try {
			matchDetails = conn.downloadMatchdetails(_match.getMatchID(), MatchType.LEAGUE);
			if((matchDetails != null) && (! matchDetails.equals(""))) {
				details = XMLMatchdetailsParser.parseMatchdetailsFromString(matchDetails, null);
			}
			else{
				details = null;
			}
			if (details != null) {
				if (details.getHomeTeamId() == _match.getHomeTeamID()) {
					_match.setMatchType(details.getMatchType());
					_match.setMatchContextId(details.getMatchContextId());
					_match.setCupLevel(details.getCupLevel());
					_match.setCupLevelIndex(details.getCupLevelIndex());
					conn.setSilentDownload(false);
					return _match;
				}
			}

			matchDetails = conn.downloadMatchdetails(_match.getMatchID(), MatchType.LADDER);
			if((matchDetails != null) && (! matchDetails.equals(""))) {
				details = XMLMatchdetailsParser.parseMatchdetailsFromString(matchDetails, null);
			}
			else{
				details = null;
			}
			if (details != null) {
				if (details.getHomeTeamId() == _match.getHomeTeamID()) {
					_match.setMatchType(details.getMatchType());
					_match.setMatchContextId(details.getMatchContextId());
					_match.setCupLevel(details.getCupLevel());
					_match.setCupLevelIndex(details.getCupLevelIndex());
					conn.setSilentDownload(false);
					return _match;
				}
			}

			matchDetails = conn.downloadMatchdetails(_match.getMatchID(), MatchType.YOUTHLEAGUE);
			if((matchDetails != null) && (! matchDetails.equals(""))) {
				details = XMLMatchdetailsParser.parseMatchdetailsFromString(matchDetails, null);
			}
			else{
				details = null;
			}
			if (details != null) {
				if (details.getHomeTeamId() == _match.getHomeTeamID()) {
					_match.setMatchType(details.getMatchType());
					_match.setMatchContextId(details.getMatchContextId());
					_match.setCupLevel(details.getCupLevel());
					_match.setCupLevelIndex(details.getCupLevelIndex());
					conn.setSilentDownload(false);
					return _match;
				}
			}
			_match.setMatchType(MatchType.TOURNAMENTGROUP);
			_match.setTournamentTypeID(TournamentType.DIVISIONBATTLE.getId());
			_match.setisObsolet(true);
			conn.setSilentDownload(false);
			return _match;
		}
		catch (Exception e) {
			HOLogger.instance().error(OnlineWorker.class, "can't infer MatchType of match: " + _match.getMatchID());
			_match.setMatchType(MatchType.NONE);
			conn.setSilentDownload(false);
			return _match;
		}
	}




	private static Matchdetails downloadMatchDetails(int matchID, MatchType matchType, MatchLineup lineup) {
		String matchDetails;
		Matchdetails details;

		try {
			matchDetails = MyConnector.instance().downloadMatchdetails(matchID, matchType);
			if (matchDetails.length() == 0) {
				HOLogger.instance().warning(OnlineWorker.class, "Unable to fetch details for match " + matchID);
				return null;
			}
			showWaitInformation(20);
			details = XMLMatchdetailsParser.parseMatchdetailsFromString(matchDetails, lineup);
			showWaitInformation(40);
			if (details == null) {
				HOLogger.instance().warning(OnlineWorker.class, "Unable to fetch details for match " + matchID);
				return null;
			}
			String arenaString = MyConnector.instance().downloadArena(details.getArenaID());
			showWaitInformation(50);
			String regionIdAsString = XMLArenaParser.parseArenaFromString(arenaString).get("RegionID");
			details.setRegionId(Integer.parseInt(regionIdAsString));
		} catch (Exception e) {
			String msg = getLangString("Downloadfehler") + ": Error fetching Matchdetails XML.: ";
			// Info
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"), JOptionPane.ERROR_MESSAGE);
			showWaitInformation(0);
			return null;
		}
		showWaitInformation(0);
		return details;
	}

	public static MatchLineup downloadMatchLineup(int matchID, int teamID, MatchType matchType) {
		String matchLineup;
		MatchLineup lineUp=null;
		boolean bOK;
		try {
			matchLineup = MyConnector.instance().downloadMatchLineup(matchID, teamID, matchType);
			bOK = (matchLineup != null && matchLineup.length() > 0);
		} catch (Exception e) {
			String msg = getLangString("Downloadfehler") + " : Error fetching Matchlineup :";
			// Info
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (bOK) {
			lineUp = XMLMatchLineupParser.parseMatchLineupFromString(matchLineup);
		}
		return lineUp;
	}



	/**
	 * Get all lineups for MatchKurzInfos, if they're not there already
	 */
	public static void getAllLineups(@Nullable Integer nbGames) {

		final MatchKurzInfo[] infos;

		if (nbGames == null){
			infos = DBManager.instance().getMatchesKurzInfo(-1);
		}
		else{
			infos = DBManager.instance().getPlayedMatchInfo(nbGames, false, false).toArray(new MatchKurzInfo[0]);
		}

		boolean bOK;
		for (MatchKurzInfo info : infos) {
			int curMatchId = info.getMatchID();
			if ((!(info.isObsolet())) && (!DBManager.instance().isMatchLineupInDB(info.getMatchType(), curMatchId))) {
				if (info.getMatchStatus() == MatchKurzInfo.FINISHED) {
					bOK = downloadMatchData(curMatchId, info.getMatchType(), false);
					if (!bOK) {
						HOLogger.instance().error(OnlineWorker.class, "Error fetching Match: " + curMatchId);
						break;
					}
				}
			}
		}
	}

	/**
	 *
	 * @param matchId
	 *            The match ID for the match to download
	 * @param matchType
	 *            The matchTyp for the match to download
	 * @return The Lineup object with the downloaded match data
	 */
	public static Lineup getLineupbyMatchId(int matchId, MatchType matchType) {

		try {
			String xml = MyConnector.instance().getMatchOrder(matchId, matchType, HOVerwaltung.instance().getModel().getBasics().getTeamId());
			
			if (!StringUtils.isEmpty(xml)) {
				Map<String, String> map = XMLMatchOrderParser.parseMatchOrderFromString(xml);
				String trainerID = "-1";
				try
				{
					trainerID = String.valueOf(HOVerwaltung.instance().getModel().getTrainer()
						.getPlayerID());
					
				}
				catch (Exception e)
				{	
					//It is possible that NTs struggle here.
				}
				String lineupData = ConvertXml2Hrf.createLineUp(trainerID, map);
				return new Lineup(getProperties(lineupData));
			}
		} catch (Exception e) {
			String msg = getLangString("Downloadfehler") + " : Error fetching Matchorder :";
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			HOLogger.instance().error(OnlineWorker.class, e.getMessage());
		}

		return null;
	}

	public static Regiondetails getRegionDetails(int regionId)
	{
		try {
			String xml = MyConnector.instance().getRegion(regionId);
			if ( !StringUtils.isEmpty(xml)){
				return new Regiondetails(XMLRegionParser.parseRegionDetailsFromString(xml));
			}
		}
		catch(Exception e){
			String msg = getLangString("Downloadfehler") + " : Error fetching region details :";
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			HOLogger.instance().error(OnlineWorker.class, e.getMessage());
		}
		return null;
	}

	public static List<TrainingEvent> getTrainingEvents(int playerId ){
		try{
			String xml = MyConnector.instance().getTrainingEvents(playerId);
			return XMLTrainingEventsParser.parseTrainingEvents(xml);
		}
		catch(Exception e){
			String msg = getLangString("Downloadfehler") + " : Error fetching training events :";
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			HOLogger.instance().error(OnlineWorker.class, e.getMessage());
		}
		return null;
	}

	private static Properties getProperties(String data) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes("UTF-8"));
		InputStreamReader isr = new InputStreamReader(bis, "UTF-8");
		BufferedReader hrfReader = new BufferedReader(isr);
		Properties properties = new Properties();

		// Lose the first line
		hrfReader.readLine();
		while (hrfReader.ready()) {
			String lineString = hrfReader.readLine();
			// Ignore empty lines
			if (!StringUtils.isEmpty(lineString)) {
				int indexEqualsSign = lineString.indexOf('=');
				if (indexEqualsSign > 0) {
					properties.setProperty(
							lineString.substring(0, indexEqualsSign).toLowerCase(
									java.util.Locale.ENGLISH),
							lineString.substring(indexEqualsSign + 1));
				}
			}
		}
		return properties;
	}

	/**
	 * Shows a file chooser asking for the location for the HRF file and saves
	 * it to the location chosen by the user.
	 *
	 * @param hrfData
	 *            the HRF data as string
	 * @throws IOException
	 */
	private static void saveHRFToFile(JDialog parent, String hrfData) {
		setInfoMsg(getLangString("HRFSave"));

		File path = new File(UserParameter.instance().hrfImport_HRFPath);
		File file = new File(path, getHRFFileName());
		// Show dialog if path not set or the file already exists
		if (UserParameter.instance().showHRFSaveDialog || !path.exists() || !path.isDirectory()
				|| file.exists()) {
			file = askForHRFPath(parent, file);
		}

		if ((file != null) && (file.getPath() != null)) {
			// Save Path
			UserParameter.instance().hrfImport_HRFPath = file.getParentFile().getAbsolutePath();

			// File exists?
			int value = JOptionPane.OK_OPTION;
			if (file.exists()) {
				value = JOptionPane.showConfirmDialog(HOMainFrame.instance(),
						getLangString("overwrite"), HOVerwaltung.instance().getLanguageString("confirmation.title"), JOptionPane.YES_NO_OPTION);
			}

			// Save
			if (value == JOptionPane.OK_OPTION) {
				try {
					saveFile(file.getPath(), hrfData);
				} catch (IOException e) {
					Helper.showMessage(HOMainFrame.instance(),
							HOVerwaltung.instance().getLanguageString("Show_SaveHRF_Failed") + " " + file.getParentFile()  +".\nError: " + e.getMessage(),
							getLangString("Fehler"), JOptionPane.ERROR_MESSAGE);
				}
			} else {
				// Canceled
				setInfoMsg(getLangString("HRFAbbruch"), InfoPanel.FEHLERFARBE);
			}
		}

	}

	/**
	 * Gets a HRF file name, based on the current date.
	 *
	 * @return the HRF file name.
	 */
	private static String getHRFFileName() {
		GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
		StringBuilder builder = new StringBuilder();
		
		builder.append(HOVerwaltung.instance().getModel().getBasics().getTeamId());
		builder.append('-');
		
		builder.append(calendar.get(Calendar.YEAR));
		builder.append('-');
		int month = calendar.get(Calendar.MONTH) + 1;
		if (month < 10) {
			builder.append('0');
		}
		builder.append(month);
		builder.append('-');
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if (day < 10) {
			builder.append('0');
		}
		builder.append(day);
		builder.append(".hrf");
		String name = calendar.get(Calendar.YEAR) + "-" + month + "-" + day + ".hrf";
		return builder.toString();
	}

	/**
	 * Shows a file chooser dialog to ask the user for the location to save the
	 * HRF file.
	 *
	 * @param file
	 *            the recommendation for the file name/location.
	 * @return the file location choosen by the user or null if the canceled the
	 *         dialog.
	 */
	private static File askForHRFPath(JDialog parent, File file) {
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setDialogTitle(getLangString("FileExport"));
		ExampleFileFilter filter = new ExampleFileFilter();
		filter.addExtension("hrf");
		filter.setDescription(HOVerwaltung.instance().getLanguageString("filetypedescription.hrf"));
		fileChooser.setFileFilter(filter);
		File path = file.getParentFile();
		if (path.exists() && path.isDirectory()) {
			fileChooser.setCurrentDirectory(path);
		}
		fileChooser.setSelectedFile(file);
		int returnVal = fileChooser.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			// File doesn't end with .hrf?
			if (!f.getPath().endsWith(".hrf")) {
				f = new java.io.File(file.getAbsolutePath() + ".hrf");
			}
			return f;
		}
		return null;
	}

	/**
	 * Convenience method for
	 * HOMainFrame.instance().getInfoPanel().setLangInfoText(msg);
	 *
	 * @param msg
	 *            the message to show
	 */
	private static void setInfoMsg(String msg) {
		HOMainFrame.instance().setInformation(msg);
	}

	/**
	 * Convenience method for
	 * HOMainFrame.instance().getInfoPanel().setLangInfoText(msg, color);
	 *
	 * @param msg
	 *            the message to show
	 * @param color
	 *            the color
	 */
	private static void setInfoMsg(String msg, Color color) {
		HOMainFrame.instance().setInformation(msg, color);
	}

	/**
	 * Convenience method for HOVerwaltung.instance().getLanguageString(key)
	 *
	 * @param key
	 *            the key for the language string
	 * @return the string for the current language
	 */
	private static String getLangString(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}

	/**
	 * Save the passed in data to the passed in file
	 *
	 * @param fileName
	 *            Name of the file to save the data to
	 * @param content
	 *            The content to write to the file
	 *
	 * @return The saved file
	 * @throws IOException
	 */
	private static File saveFile(String fileName, String content) throws IOException {
		File outFile = new File(fileName);
		if (outFile.exists()) {
			outFile.delete();
		}
		outFile.createNewFile();
		OutputStreamWriter outWrit = new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8");
		BufferedWriter out = new BufferedWriter(outWrit);
		out.write(content);
		out.newLine();
		out.close();
		return outFile;
	}

	public static boolean isSilentDownload() {
		return MyConnector.instance().isSilentDownload();
	}

	public static void setSilentDownload(boolean silentDownload) {
		MyConnector.instance().setSilentDownload(silentDownload);
	}

	final static long oneDay = 24L*60L*60L*1000L;
	final static long threeMonths = 3L*30L*oneDay;

	public static void downloadMissingYouthMatchData(HOModel model, Timestamp dateSince) {
		var youthteamid = model.getBasics().getYouthTeamId();
		var lastStoredYouthMatchDate = DBManager.instance().getLastYouthMatchDate();

		if ( dateSince == null || lastStoredYouthMatchDate != null && lastStoredYouthMatchDate.after(dateSince) ){
			// if there are no youth matches in database, take the limit from arrival date of 'oldest' youth players
			dateSince = lastStoredYouthMatchDate;
		}

		for ( Timestamp dateUntil = null; dateSince != null; dateSince = dateUntil) {
			if (dateSince.before(new Timestamp(System.currentTimeMillis()- threeMonths))){
				dateUntil = new Timestamp(dateSince.getTime() + threeMonths);
			}
			else {
				dateUntil = null;	// until now
			}
			var mc = MyConnector.instance();
			try {
				var xml = mc.getMatchesArchive(SourceSystem.YOUTH, youthteamid, dateSince, dateUntil);
				var youthMatches = XMLMatchArchivParser.parseMatchesFromString(xml);
				for ( var match: youthMatches){
					MatchLineup lineup = downloadMatchlineup(match.getMatchID(), match.getMatchType(), match.getHomeTeamID(), match.getGuestTeamID());
					if (lineup != null) {
						var details = downloadMatchDetails(match.getMatchID(), match.getMatchType(), lineup);
						//var lineup = downloadMatchlineup(match.getMatchID(), match.getMatchType(), match.getHeimID(), match.getGastID());
						DBManager.instance().storeMatchDetails(details);
						DBManager.instance().storeMatchLineup(lineup, youthteamid);
						lineup.setMatchDetails(details);
						model.addYouthMatchLineup(lineup);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
