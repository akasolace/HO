package core.net;


import com.github.scribejava.core.model.*;
import core.file.xml.XMLCHPPPreParser;
import core.file.xml.XMLTeamDetailsParser;
import core.gui.CursorToolkit;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.enums.MatchType;
import core.model.match.SourceSystem;
import core.net.login.OAuthDialog;
import core.net.login.ProxyDialog;
import core.net.login.ProxySettings;
import core.util.HOLogger;
import core.util.Helper;
import core.util.IOUtils;
import core.util.StringUtils;
import core.util.XMLUtils;
import org.jetbrains.annotations.Nullable;
import tool.updater.VersionInfo;
import core.HO;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import javax.swing.JOptionPane;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.w3c.dom.Document;
import java.util.Base64;


public class MyConnector {
	private final static SimpleDateFormat HT_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final String htUrl = "https://chpp.hattrick.org/chppxml.ashx";
	public static String m_sIDENTIFIER = "HO! Hattrick Organizer V" + HO.VERSION;
	private static MyConnector m_clInstance;
	private final static String VERSION_TRAINING = "2.1";
	private final static String VERSION_MATCHORDERS = "3.0";
	private final static String VERSION_MATCHORDERS_NT = "2.1";
	private final static String VERSION_MATCHLINEUP = "2.0";
	private final static String VERSION_MATCHDETAILS = "3.0";
	private final static String VERSION_TEAM_DETAILS = "3.5";
	private final static String VERSION_PLAYERS = "2.5";
	private final static String VERSION_YOUTHPLAYERLIST = "1.1";
	private final static String VERSION_WORLDDETAILS = "1.8";
	private final static String VERSION_TOURNAMENTDETAILS = "1.0";
	private final static String VERSION_LEAGUE_DETAILS = "1.5";
	private final static String CONSUMER_KEY = ">Ij-pDTDpCq+TDrKA^nnE9";
	private final static String CONSUMER_SECRET = "2/Td)Cprd/?q`nAbkAL//F+eGD@KnnCc>)dQgtP,p+p";
	private ProxySettings proxySettings;
	private final OAuth10aService m_OAService;
	private OAuth1AccessToken m_OAAccessToken;
	private static boolean DEBUGSAVE = false;

	private boolean silentDownload = false;

	/**
	 * Creates a new instance of MyConnector.
	 */
	private MyConnector() {
		m_OAService = new ServiceBuilder(Helper.decryptString(CONSUMER_KEY))
						.apiSecret(Helper.decryptString(CONSUMER_SECRET))
						.build(HattrickAPI.instance());


		m_OAAccessToken = createOAAccessToken();
	}

	private OAuth1AccessToken createOAAccessToken() {
		return new OAuth1AccessToken(Helper.decryptString(UserParameter.instance().AccessToken),
				Helper.decryptString(UserParameter.instance().TokenSecret));
	}

	/**
	 * Get the MyConnector instance.
	 */
	public static MyConnector instance() {
		if (m_clInstance == null) {
			m_clInstance = new MyConnector();
		}
		return m_clInstance;
	}

	public static boolean hasInternetAccess(){
		try {
			URL url = new URL("http://www.hattrick.org");
			URLConnection connection = url.openConnection();
			connection.connect();
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}

	/**
	 * Sets the DEBUGSAVE flag. Setting the flag to true will save downloaded
	 * CHPP files.
	 * 
	 * @param debugSave
	 *            true to save downloaded CHPP files, false otherwise.
	 */
	public static void setDebugSave(boolean debugSave) {
		DEBUGSAVE = debugSave;
	}

	public static String getResourceSite() {
		return getPluginSite();
	}

	public static String getHOSite() {
		return "http://ho1.sourceforge.net/";
	}

	public static String getPluginSite() {
		return getHOSite() + "onlinefiles";
	}


	/**
	 * Fetch a specific arena
	 * 
	 * @param arenaId
	 *            id of the arena to fetch (-1 = our arena)
	 * @return arena xml
	 * 
	 * @throws IOException
	 */
	public String downloadArena(int arenaId) {
		String url = htUrl + "?file=arenadetails";
		if (arenaId > 0) {
			url += "&arenaID=" + arenaId;
		}
		return getCHPPWebFile(url);
	}

	/**
	 * Fetch a specific region
	 *
	 * @param regionId
	 *            id of the region to fetch
	 * @return regiondetails xml
	 *
	 * @throws IOException
	 */
	public String getRegion(int regionId) {
		String url = htUrl + "?file=regiondetails";
		if (regionId > 0) {
			url += "&regionID=" + regionId;
		}
		return getCHPPWebFile(url);
	}

	/**
	 * Fetch training events of player
	 *
	 * @param playerId
	 *            id of the region to fetch
	 * @return training events xml
	 *
	 * @throws IOException
	 */
	public String getTrainingEvents(int playerId){
		String url = htUrl + "?file=trainingevents&playerID=" + playerId;
		return getCHPPWebFile(url);
	}

	/**
	 * holt die Finanzen
	 */
	public String getEconomy(int teamId){
		String url = htUrl + "?file=economy&version=1.3&teamId=" + teamId;
		return getCHPPWebFile(url);
	}

	// ///////////////////////////////////////////////////////////////////////////////
	// get-XML-Files
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * downloads an xml File from hattrick Behavior has changed with oauth, but
	 * we try to convert old syntaxes.
	 * 
	 * @param file
	 *            ex. = "?file=leaguedetails&[leagueLevelUnitID = integer]"
	 * 
	 * @return the complete file as String
	 */
	public String getHattrickXMLFile(String file){
		String url;

		// An attempt at solving old syntaxes.

		if (file.contains("chppxml.axd")) {
			file = file.substring(file.indexOf("?"));
		} else if (file.contains(".asp")) {
			String s = file.substring(0, file.indexOf("?")).replace(".asp", "")
					.replace("/common/", "");
			file = "?file=" + s + "&" + file.substring(file.indexOf("?") + 1);
		}

		url = htUrl + file;
		return getCHPPWebFile(url);
	}

	/**
	 * lädt die Tabelle
	 */
	public String getLeagueDetails(String leagueUnitId) {
		String url = htUrl + "?file=leaguedetails&version=" + VERSION_LEAGUE_DETAILS + "&leagueLevelUnitID=" + leagueUnitId;
		return getCHPPWebFile(url);
	}

	/**
	 * lädt den Spielplan
	 */
	public String getLeagueFixtures(int season, int leagueID){
		String url = htUrl + "?file=leaguefixtures";
		if (season > 0) {
			url += "&season=" + season;
		}
		if (leagueID > 0) {
			url += "&leagueLevelUnitID=" + leagueID;
		}
		return getCHPPWebFile(url);
	}

	/**
	 * Fetches matches from Hattrick's matches archive (see 'matchesarchive' in
	 * Hattrick's CHPP API documentation) for the given team and a specified
	 * period of time.
	 * 
	 * @param teamId
	 *            the ID of the team to fetch the matches for.
	 * @param firstDate
	 *            the first date of the period of time.
	 * @param lastDate
	 *            the last date of the period of time.
	 * @return the a string containing the matches data in XML format.
	 * @throws IOException
	 *             if an io-error occurred when fetching the matches.
	 */
	public String getMatchesArchive(int teamId, Date firstDate, Date lastDate){
		StringBuilder url = new StringBuilder();
		url.append(htUrl).append("?file=matchesarchive&version=1.4");

		if (teamId > 0) {
			url.append("&teamID=").append(teamId);
		}

		if (firstDate != null) {
			url.append("&FirstMatchDate=").append(HT_FORMAT.format(firstDate));
		}

		if (lastDate != null) {
			url.append("&LastMatchDate=").append(HT_FORMAT.format(lastDate));
		}
		url.append("&includeHTO=true");
		return getCHPPWebFile(url.toString());
	}

	public String getMatchesArchive(SourceSystem sourceSystem, int teamId, Date firstDate, Date lastDate) throws IOException {
		StringBuilder url = new StringBuilder();
		url.append(htUrl).append("?file=matchesarchive&version=1.4");

		if (teamId > 0) {
			url.append("&teamID=").append(teamId);
		}

		if (firstDate != null) {
			url.append("&FirstMatchDate=").append(HT_FORMAT.format(firstDate));
		}

		if (lastDate != null) {
			url.append("&LastMatchDate=").append(HT_FORMAT.format(lastDate));
		}
		if ( sourceSystem == SourceSystem.HTOINTEGRATED) url.append("&includeHTO=true");
		else if ( sourceSystem == SourceSystem.YOUTH) url.append("&isYouth=true");

		return getCHPPWebFile(url.toString());
	}

	/**
	 * Get information about a tournament. This is only available for the current season.
	 */
	public String getTournamentDetails(int tournamentId) throws IOException{
		String url = htUrl + "?file=tournamentdetails&version=" + VERSION_TOURNAMENTDETAILS + "&tournamentId=" + tournamentId;
		if (url == "") {
			HOLogger.instance().error(getClass(), "getTournamentDetails: could not download information for required tournament: "+tournamentId);
			throw new IOException("getTournamentDetails: could not download information for required tournament: "+tournamentId);
		}
		return getCHPPWebFile(url);
	}

	/**
	 * lädt die Aufstellungsbewertung zu einem Spiel
	 */
	public String downloadMatchLineup(int matchId, int teamId, MatchType matchType) {
		String url = htUrl + "?file=matchlineup&version=" + VERSION_MATCHLINEUP;

		if (matchId > 0) {
			url += ("&matchID=" + matchId);
		}

		// Had to remove check for negative team ID. Street teams used in cup have that. 
		url += ("&teamID=" + teamId);
		
		url += "&sourceSystem=" + matchType.getSourceString();

		return getCHPPWebFile(url);
	}

	/**
	 * lädt die Aufstellungsbewertung zu einem Spiel
	 */
	public String getRatingsPrediction(int matchId, int teamId, MatchType matchType) {
		String url = htUrl + "?file=matchorders&version=" + VERSION_MATCHORDERS;
		url += "&actionType=predictratings";

		if (matchId > 0) {
			url += ("&matchID=" + matchId);
		}

		url += ("&teamID=" + teamId);

		url += "&sourceSystem=" + matchType.getSourceString();

		return getCHPPWebFile(url);
	}

	/**
	 * Fetches the match order xml from Hattrick
	 * 
	 * @param matchId
	 *            The match id to fetch the lineup for
	 * @param matchType
	 *            The match type connected to the match
	 * @return The api content (xml)
	 * @throws IOException
	 */
	public String getMatchOrder(int matchId, MatchType matchType, int teamId) {
		String url = htUrl + "?file=matchorders&matchID=" + matchId + "&sourceSystem=" + matchType.getSourceString();
		if (HOVerwaltung.instance().getModel().getBasics().isNationalTeam()) {
			url += "&version=" + VERSION_MATCHORDERS_NT;
		}
		else {
			url += "&version=" + VERSION_MATCHORDERS + "&teamId=" + teamId;
		}
		return getCHPPWebFile(url);
	}

	/**
	 * Sets the match order with the provided content to the provided match.
	 * 
	 * @param matchId
	 *            The match id to upload the order to
	 * @param matchType
	 *            The match type of the match to upload the order to
	 * @param orderString
	 *            The string with the actual orders. See the CHPP API
	 *            documentation.
	 * @return the result xml from the upload
	 * @throws IOException
	 */
	public String setMatchOrder(int matchId, int teamId, MatchType matchType, String orderString)
			throws IOException {
		StringBuilder urlpara = new StringBuilder();
		if (HOVerwaltung.instance().getModel().getBasics().isNationalTeam()) {
			urlpara.append("?file=matchorders&version=").append(VERSION_MATCHORDERS_NT);
		}
		else
		{
			urlpara.append("?file=matchorders&version=").append(VERSION_MATCHORDERS);
			if (teamId>0) {
				urlpara.append("&teamId=").append(teamId);
			}
		}

		if (matchId > 0) {
			urlpara.append("&matchID=").append(matchId);
		}
		urlpara.append("&actionType=setmatchorder");
		urlpara.append("&sourceSystem=" + matchType.getSourceString());

		Map<String, String> paras = new HashMap<>();
		paras.put("lineup", orderString);
		String result = readStream(postWebFileWithBodyParameters(htUrl + urlpara, paras, true,
				"set_matchorder"));
		String sError = XMLCHPPPreParser.getError(result);
		if (sError.length() > 0) {
			throw new RuntimeException(sError);
		}
		return result;
	}

	/**
	 * lädt die Aufstellungsbewertung zu einem Spiel
	 */
	public String downloadMatchdetails(int matchId, MatchType matchType) throws IOException {
		String url = htUrl + "?file=matchdetails&version=" + VERSION_MATCHDETAILS;
		if (matchId > 0) {
			url += ("&matchID=" + matchId);
		}
		url += "&sourceSystem=" + matchType.getSourceString();
		url += "&matchEvents=true";
		return getCHPPWebFile(url);
	}

	/**
	 * Gets the most recent and upcoming matches for a given teamId and up to a
	 * specific date.
	 * 
	 * @param teamId
	 *            the id of the team.
	 * @param forceRefresh
	 *            <code>true</code> if cache should be refreshed,
	 *            <code>false</code> otherwise.
	 * @param date
	 *            last date (+time) to get matches to.
	 * @return a string containing the xml data for the downloaded matches (to
	 *         be used for MatchKurzInfo).
	 * @throws IOException
	 *             if an IO error occurs during download.
	 */
	public String getMatches(int teamId, boolean forceRefresh, Date date) throws IOException {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(htUrl).append("?file=matches&version=2.8");
		urlBuilder.append("&teamID=").append(teamId);
		if (forceRefresh) {
			urlBuilder.append("&actionType=refreshCache");
		}
		urlBuilder.append("&LastMatchDate=");
		String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		urlBuilder.append(URLEncoder.encode(dateString, StandardCharsets.UTF_8));
		return getCHPPWebFile(urlBuilder.toString());
	}

	/**
	 * Get Matches
	 */
	public String getMatches(int teamId, boolean forceRefresh, boolean upcoming) throws IOException {
		String url = htUrl + "?file=matches&version=2.8";

		if (teamId > 0) {
			url += "&teamID=" + teamId;
		}
		if (forceRefresh) {
			url += "&actionType=refreshCache";
		}

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		if (upcoming) {
			cal.add(java.util.Calendar.MONTH, 5);
		}
		// Paranoia against inaccurate system clock.
		cal.add(java.util.Calendar.DAY_OF_MONTH, 1);

		url += "&LastMatchDate=";
		url += new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

		return getCHPPWebFile(url);
	}

	/**
	 * Get Players
	 */
	public String getPlayers(int teamId) throws IOException {
		String url = htUrl + "?file=players&version=" + VERSION_PLAYERS+"&includeMatchInfo=true&teamID=" + teamId;
		return getCHPPWebFile(url);
	}

	public String downloadYouthPlayers(int youthteamId) throws IOException {
		String url = htUrl + "?file=youthplayerlist&version=" + VERSION_YOUTHPLAYERLIST +"&actionType=unlockskills&showScoutCall=true&showLastMatch=true&youthTeamID=" + youthteamId;
		return getCHPPWebFile(url);
	}

	/**
	 * Get Staff
	 */
	
	public String getStaff(int teamId) throws IOException {
		String url = htUrl + "?file=stafflist&version=1.0&teamId=" + teamId;
		return getCHPPWebFile(url);
	}
	
	/**
	 * holt die Teamdetails
	 */
	public String getTeamdetails(int teamId) throws IOException {
		String url = htUrl + "?file=teamdetails&version=3.5";
		if (teamId > 0) {
			url += ("&teamID=" + teamId);
		}

		return getCHPPWebFile(url);
	}

	/**
	 * Get the training XML data.
	 */
	public String getTraining(int teamId) throws IOException {
		final String url = htUrl + "?file=training&version=" + VERSION_TRAINING + "&teamId=" + teamId;

		return getCHPPWebFile(url);
	}

	/**
	 * Get the transfer data for a player
	 */
	public String getTransfersForPlayer(int playerId) {
		final String url = htUrl + "?file=transfersPlayer&playerID=" + playerId;

		return getCHPPWebFile(url);
	}

	/**
	 * holt die Vereinsdaten
	 */
	public String getVerein(int teamId) throws IOException {
		final String url = htUrl + "?file=club&version=1.5&teamId=" + teamId;
		return getCHPPWebFile(url);
	}

	/**
	 * Get the content of a web page in one string.
	 */
	private String getWebPage(String surl, boolean showError) throws IOException {
		final InputStream resultingInputStream = getNonCHPPWebFile(surl, showError);
		return readStream(resultingInputStream);
	}

	/**
	 * holt die Weltdaten
	 */
	public String getWorldDetails(int leagueId) throws IOException {
		String url = htUrl + "?file=worlddetails&version=" + VERSION_WORLDDETAILS;
		if (leagueId > 0)
			url += "&leagueID=" + leagueId;
		return getCHPPWebFile(url);
	}

	// ///////////////////////////////////////////////////////////////////////////////
	// Update Checker
	// //////////////////////////////////////////////////////////////////////////////
	public VersionInfo getVersion(String url) {
		BufferedReader br = null;
		InputStream is = null;
		try {
			is = getNonCHPPWebFile(url, false);
			if (is != null) {
				br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
				VersionInfo ret = new VersionInfo();
				String line;

				while ((line = br.readLine()) != null) {
					int pos = line.indexOf("=");
					if (pos > 0) {
						String key = line.substring(0, pos).trim();
						String val = line.substring(pos + 1).trim();
						ret.setValue(key, val);
					}
				}
				if (ret.isValid()) {
					return ret;
				}
			} else {
				HOLogger.instance().log(getClass(), "Unable to connect to the update server (HO).");
			}
		} catch (Exception e) {
			HOLogger.instance()
					.log(getClass(), "Unable to connect to the update server (HO): " + e);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(is);
		}
		return null;
	}


	public VersionInfo getLatestStableVersion() {
		return getVersion("https://akasolace.github.io/HO/lateststable.html");
	}

	public VersionInfo getLatestVersion() {
		return getVersion("https://akasolace.github.io/HO/latest.html");
	}

	public VersionInfo getLatestBetaVersion() {
		return getVersion("https://akasolace.github.io/HO/latestbeta.html");
	}


	// ///////////////////////////////////////////////////////////////////////////////
	// Proxy
	// //////////////////////////////////////////////////////////////////////////////
	public void enableProxy(ProxySettings proxySettings) {
		this.proxySettings = proxySettings;
		if (this.proxySettings != null && this.proxySettings.isUseProxy()) {
			System.getProperties().setProperty("https.proxyHost", proxySettings.getProxyHost());
			System.getProperties().setProperty("https.proxyPort",
					String.valueOf(proxySettings.getProxyPort()));
			System.getProperties().setProperty("http.proxyHost", proxySettings.getProxyHost());
			System.getProperties().setProperty("http.proxyPort",
					String.valueOf(proxySettings.getProxyPort()));
		} else {
			System.getProperties().remove("https.proxyHost");
			System.getProperties().remove("https.proxyPort");
			System.getProperties().remove("http.proxyHost");
			System.getProperties().remove("http.proxyPort");
		}
	}

	/**
	 * Get the region id for a certain team.
	 */
	public String fetchRegionID(int teamId) {
		String xml = fetchTeamDetails(teamId);
		if ( xml.length()>0){
			return XMLTeamDetailsParser.fetchRegionID(xml);
		}
		return "-1";
	}


	public String fetchTeamDetails(int teamId)
	{
		try {
			String xmlFile = htUrl + "?file=teamdetails&version=" + VERSION_TEAM_DETAILS + "&teamID=" + teamId;
			return getCHPPWebFile(xmlFile);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
		return "";
	}

	public InputStream getFileFromWeb(String url, boolean displaysettingsScreen) {
		if (displaysettingsScreen) {
			// Show Screen
			new ProxyDialog(HOMainFrame.instance());
		}
		return getNonCHPPWebFile(url, false);
	}


	/**
	 * Get a web page using a URLconnection.
	 */
	private String getCHPPWebFile(String surl) {
		String returnString = "";
		OAuthDialog authDialog = null;
		Response response = null;
		int iResponse = 200;
		boolean tryAgain = true;
		try {
			while (tryAgain) {
				OAuthRequest request = new OAuthRequest(Verb.GET, surl);
				infoHO(request);
				if (m_OAAccessToken == null || m_OAAccessToken.getToken().length() == 0) {
					iResponse = 401;
				} else {
					m_OAService.signRequest(m_OAAccessToken, request);
					response = m_OAService.execute(request);
					iResponse = response.getCode();
				}
				switch (iResponse) {
				case 200:
				case 201:
					// We are done!
					returnString = readStream(getResultStream(response));
					if (DEBUGSAVE) {
						saveCHPP(surl, returnString);
					}
					String sError = XMLCHPPPreParser.getError(returnString);
					if (sError.length() > 0) {
						throw new RuntimeException(sError);
					}
					tryAgain = false;
					break;
				case 401:
					if ( !silentDownload) {
						if (authDialog == null) {

							HOMainFrame mainFrame = null;

							// If the main frame is not in the process of loading, use it,
							// otherwise use null frame.

							if (!HOMainFrame.launching.get()) {
								mainFrame = HOMainFrame.instance();
							}

							// disable WaitCursor to unblock GUI
							if (mainFrame != null) {
								CursorToolkit.stopWaitCursor(mainFrame.getRootPane());
							}
							authDialog = new OAuthDialog(mainFrame, m_OAService, "");
						}
						authDialog.setVisible(true);
						// A way out for a user unable to authorize for some reason
						if (authDialog.getUserCancel() == true) {
							return null;
						}
						m_OAAccessToken = authDialog.getAccessToken();
						if (m_OAAccessToken == null) {
							m_OAAccessToken = createOAAccessToken();
						}
					}
					else {
						throw new RuntimeException("HTTP Response Code 401: CHPP Connection failed.");
					}
					break;
				case 407:
					throw new RuntimeException(
							"HTTP Response Code 407: Proxy authentication required.");
				default:
					throw new RuntimeException("HTTP Response Code: " + iResponse);
				}
			}
		} catch (Exception sox) {

			if ( !silentDownload) {
				HOLogger.instance().error(getClass(), sox);
				JOptionPane.showMessageDialog(null,
						sox.getMessage() + "\n\n" + "URL:" + surl + "\n",
						HOVerwaltung.instance().getLanguageString("Fehler"),
						JOptionPane.ERROR_MESSAGE);
			}
			returnString = "";
		}
		return returnString;
	}

	private @Nullable InputStream getNonCHPPWebFile(String surl, boolean showErrorMessage) {
		InputStream returnStream = null;
		try {
			Response response;
			OAuthRequest request = new OAuthRequest(Verb.GET, surl);
			infoHO(request);
			response = m_OAService.execute(request);
			int iResponse = response.getCode();
			returnStream = switch (iResponse) {
				case 200, 201 -> getResultStream(response);
				case 404 -> throw new RuntimeException("Download Update Error: code 404: the following page does not exist: " + surl);
				case 407 -> throw new RuntimeException("Download Update Error: code 407: Proxy authentication required.");
				default -> throw new RuntimeException("Download Update Error: code: " + iResponse); };
		}
		catch (Exception sox) {
			HOLogger.instance().error(getClass(), sox);
			if (showErrorMessage)
				JOptionPane.showMessageDialog(null, sox.getMessage() + "\nURL: " + surl,
						HOVerwaltung.instance().getLanguageString("Fehler"),
						JOptionPane.ERROR_MESSAGE);
		}
		return returnStream;
	}


	/**
	 * Post a web file containing single value in the body (no key)
	 *
	 * @param surl
	 *            the full url with parameters
	 * @param bodyParas
	 *            A hash map of string, string where key is parameter key and value is parameter value
	 * @param showErrorMessage
	 *            Whether to show message on error or not
	 * @param scope
	 *            The scope of the request is required, if no scope, put "".
	 *            Example: "set_matchorder".
	 */
	public InputStream postWebFileWithBodyParameters(String surl, Map<String, String> bodyParas,
													 boolean showErrorMessage, String scope) {

		OAuthDialog authDialog = null;
		Response response = null;
		int iResponse;
		boolean tryAgain = true;
		try {
			while (tryAgain) {
				OAuthRequest request = new OAuthRequest(Verb.POST, surl);
				for (Map.Entry<String, String> entry : bodyParas.entrySet()) {
					request.addBodyParameter(entry.getKey(), entry.getValue());
				}
				infoHO(request);
				request.addHeader("Content-Type", "application/x-www-form-urlencoded");
				if (m_OAAccessToken == null || m_OAAccessToken.getToken().length() == 0) {
					iResponse = 401;
				} else {
					m_OAService.signRequest(m_OAAccessToken, request);
					response = m_OAService.execute(request);
					iResponse = response.getCode();
				}
				switch (iResponse) {
					case 200:
					case 201:
						// We are done!
						return getResultStream(response);
					case 401:
						// disable WaitCursor to unblock GUI
						CursorToolkit.stopWaitCursor(HOMainFrame.instance().getRootPane());
						if (authDialog == null) {
							authDialog = new OAuthDialog(HOMainFrame.instance(), m_OAService, scope);
						}
						authDialog.setVisible(true);
						// A way out for a user unable to authorize for some reason
						if (authDialog.getUserCancel() == true) {
							return null;
						}
						m_OAAccessToken = authDialog.getAccessToken();
						if (m_OAAccessToken == null) {
							m_OAAccessToken = new OAuth1AccessToken(
									Helper.decryptString(core.model.UserParameter.instance().AccessToken),
									Helper.decryptString(core.model.UserParameter.instance().TokenSecret));
						}
						// Try again...
						break;
					case 407:
						throw new RuntimeException(
								"Download Error\nHTTP Response Code 407: Proxy authentication required.");
					default:
						throw new RuntimeException("Download Error\nHTTP Response Code: " + iResponse);
				}
			}
		} catch (Exception sox) {
			HOLogger.instance().error(getClass(), sox);
			if (showErrorMessage) {
				JOptionPane.showMessageDialog(null, sox.getMessage() + "\nURL: " + surl,
						HOVerwaltung.instance().getLanguageString("Fehler"),
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return null;
	}

	private InputStream getResultStream(Response response) throws IOException {
		InputStream resultingInputStream = null;
		if (response != null) {
			String encoding = response.getHeader("Content-Encoding");
			if ((encoding != null) && encoding.equalsIgnoreCase("gzip")) {
				resultingInputStream = new GZIPInputStream(response.getStream());
			} else if ((encoding != null) && encoding.equalsIgnoreCase("deflate")) {
				resultingInputStream = new InflaterInputStream(response.getStream(), new Inflater(
						true));
//				HOLogger.instance().log(getClass(), " Read Deflated.");
			} else {
				resultingInputStream = response.getStream();
//				HOLogger.instance().log(getClass(), " Read Normal.");
			}
		}
		return resultingInputStream;
	}

	private String readStream(InputStream stream) throws IOException {
		StringBuilder builder = new StringBuilder();
		if (stream != null) {
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(stream,
					StandardCharsets.UTF_8));
			String line = bufferedreader.readLine();
			if (line != null) {
				builder.append(line);
				while ((line = bufferedreader.readLine()) != null) {
					builder.append('\n');
					builder.append(line);
				}
			}
			bufferedreader.close();
		}
		return builder.toString();
	}

	// ///////////////////////////////////////////////////////////////////////////////
	// Identifikation
	// //////////////////////////////////////////////////////////////////////////////

	private void infoHO(OAuthRequest request) {
		request.addHeader("accept-language", "en");
//		request.setConnectionKeepAlive(true);
//		request.setConnectTimeout(60, TimeUnit.SECONDS);
		request.addHeader("accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, */*");
		request.addHeader("accept-encoding", "gzip, deflate");
		request.addHeader("user-agent", m_sIDENTIFIER);

		// ProxyAuth hier einbinden da diese Funk immer aufgerufen wird
		if (this.proxySettings != null && this.proxySettings.isAuthenticationNeeded()) {
			final String pw = this.proxySettings.getUsername() + ":"
					+ this.proxySettings.getPassword();
			final String epw = new String(Base64.getEncoder().encode(pw.getBytes()));
			request.addHeader("Proxy-Authorization", "Basic " + epw);
		}
	}

	/**
	 * Save downloaded data to a temp-file for debugging purposes.
	 * 
	 * @param url
	 *            the url where the content was downloaded from
	 * @param content
	 *            the content to save
	 */
	private void saveCHPP(String url, String content) {
		File outDir = new File("tmp");
		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		String xmlName = null;
		try {
			Document doc = XMLUtils.createDocument(content);
			xmlName = XMLUtils.getTagData(doc, "FileName");
			if (xmlName != null && xmlName.indexOf('.') != -1) {
				xmlName = xmlName.substring(0, xmlName.lastIndexOf('.'));
			}
		} catch (Exception ex) {
			HOLogger.instance().error(getClass(), ex);
		}

		Date downloadDate = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss-S");
		String outFileName = df.format(downloadDate) + ".txt";
		if (!StringUtils.isEmpty(xmlName)) {
			outFileName = xmlName + "_" + outFileName;
		}
		File outFile = new File(outDir, outFileName);

		df = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");
		StringBuilder builder = new StringBuilder();
		builder.append("Downloaded at ").append(df.format(downloadDate)).append('\n');
		builder.append("From ").append(url).append("\n\n");
		builder.append(content);

		try {
			IOUtils.writeToFile(builder.toString(), outFile, "UTF-8");
		} catch (Exception e) {
			HOLogger.instance().error(MyConnector.class, e);
		}
	}


	public boolean isSilentDownload() {
		return silentDownload;
	}

	public void setSilentDownload(boolean silentDownload) {
		this.silentDownload = silentDownload;
	}

    public String getYouthMatchesSince(Timestamp dateOfLastMatchInDb) {

		try {
			return getMatchesArchive(SourceSystem.YOUTH, HOVerwaltung.instance().getModel().getBasics().getYouthTeamId(), dateOfLastMatchInDb, null);
		}
		catch ( Exception e){

		}
		return null;
    }
}
