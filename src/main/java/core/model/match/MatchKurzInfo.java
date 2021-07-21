package core.model.match;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.enums.MatchType;
import core.model.enums.MatchTypeExtended;
import core.net.OnlineWorker;
import core.util.HOLogger;
import core.util.HTDatetime;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

import static core.util.StringUtils.getResultString;

public class MatchKurzInfo implements Comparable<Object> {

	/** Name des Teams zu dem die Matchinfo gehört */
	private String m_sGastName = "";

	private String m_sOpponentTeamName;

	/** Name des Teams zu dem die Matchinfo gehört */
	private String m_sHeimName = "";

	private HTDatetime m_matchSchedule;

	/** orders given for this match? */
	private boolean ordersGiven = true;

	/** ID des Teams zu dem die info gehört */
	private int m_iGuestTeamID = -1;

	/** Gast Tore */
	private int m_iGastTore = -1;

	/** ID des Teams zu dem die info gehört */
	private int m_iHomeTeamID = -1;

	/** Heim Tore */
	private int m_iHeimTore = -1;

	/** ID des Matches */
	private int m_iMatchID = -1;

	/** MatchContextId */
	private int iMatchContextId;

	public int getMatchContextId() {
		return iMatchContextId;
	}
	public void setMatchContextId(int iMatchContextId) {
		this.iMatchContextId = iMatchContextId;
	}

	/** TournamentTypeID */
	private int iTournamentTypeID;
	public int getTournamentTypeID() {
		return iTournamentTypeID;
	}
	public void setTournamentTypeID(int iTournamentTypeID) {
		this.iTournamentTypeID = iTournamentTypeID;
	}

	/** Status des Spiels */
	private int m_iMatchStatus = -1;
	
	/** HO user team ID */
	public static int user_team_id = HOVerwaltung.instance().getModel().getBasics().getTeamId();

	private int m_iArenaId = -1;
	private int m_iRegionId = -1;
	private Boolean m_iIsDerby = null;
	private Boolean m_iIsNeutral = null;
	private Weather m_iWeather = Weather.NULL;
	private Weather.Forecast m_iWeatherForecast = Weather.Forecast.NULL;

	// Duration of the match (null if unknown or not played yet)
	private Integer duration;

	// Details of the match, if available
	private Matchdetails matchdetails;

	public final void setArenaId(int id) {this.m_iArenaId=id;}
	public final int getArenaId() {return this.m_iArenaId;}
	public final void setRegionId(int id) {this.m_iRegionId=id;}
	public final int getRegionId(){return this.m_iRegionId;}
	public final void setIsDerby(Boolean is) {this.m_iIsDerby=is;}
	public final boolean isDerby(){return this.m_iIsDerby != null && this.m_iIsDerby.equals(Boolean.TRUE);}
	public final Boolean getIsDerby(){return this.m_iIsDerby;}
	public final void setIsNeutral(Boolean is) {this.m_iIsNeutral=is;}
	public final Boolean getIsNeutral(){return this.m_iIsNeutral;}
	public final boolean isNeutral(){return this.m_iIsNeutral != null && this.m_iIsNeutral.equals(Boolean.TRUE);}
	public void setWeather(Weather w){this.m_iWeather=w;}
	public Weather getWeather(){return m_iWeather;}
	public void setWeatherForecast(Weather.Forecast wf){this.m_iWeatherForecast=wf;}
	public Weather.Forecast getWeatherForecast(){return this.m_iWeatherForecast;}

	/** Typ des Spiels */
	private MatchType m_mtMatchTyp = MatchType.NONE;
	private CupLevel m_mtCupLevel = CupLevel.NONE;
	private CupLevelIndex m_mtCupLevelIndex = CupLevelIndex.NONE;
	private IMatchType m_matchTypeExtended = MatchType.NONE;

	public static final int ONGOING = 3;
	public static final int UPCOMING = 2;
	public static final int FINISHED = 1;


	public boolean isObsolet() {
		return isObsolet;
	}

	public void setisObsolet(boolean obsolet) {
		isObsolet = obsolet;
	}

	private boolean isObsolet = false;  //True if match has been deleted in HT (might happen for some hto integrated game)

	/**
	 * Setter for the ordersGiven property which indicates if orders for this
	 * match are given or not.
	 * 
	 * @param ordersGiven
	 *            <code>true</code> if orders for this match are given,
	 *            <code>false</code> otherwise.
	 */
	public final void setOrdersGiven(boolean ordersGiven) {
		this.ordersGiven = ordersGiven;
	}

	/**
	 * Indicates if orders are given for this match. From HT only supplied for
	 * upcoming matches (haven't started yet).
	 * 
	 * @return <code>true</code> if orders for this match are given,
	 *         <code>false</code> otherwise.
	 */
	public final boolean isOrdersGiven() {
		return ordersGiven;
	}

	public final void setGuestTeamID(int iGuestTeamID) {
		this.m_iGuestTeamID = iGuestTeamID;
	}

	public int getGuestTeamID() {
		return m_iGuestTeamID;
	}

	/**
	 * Setter for property m_sGastName.
	 * 
	 * @param m_sGastName
	 *            New value of property m_sGastName.
	 */
	public final void setGuestTeamName(String m_sGastName) {
		this.m_sGastName = m_sGastName;
	}

	/**
	 * Getter for property m_sGastName.
	 * 
	 * @return Value of property m_sGastName.
	 */
	public final String getGuestTeamName() {
		return m_sGastName;
	}

	public final String getOpponentTeamName(){
		if (m_sOpponentTeamName == null){
			if (isHomeMatch()){
				m_sOpponentTeamName = getGuestTeamName();
			}
			else{
				m_sOpponentTeamName = getHomeTeamName();
			}
		}
		return m_sOpponentTeamName;
	}


	/**
	 * Setter for property m_iGastTore.
	 * 
	 * @param m_iGastTore
	 *            New value of property m_iGastTore.
	 */
	public final void setGuestTeamGoals(int m_iGastTore) {
		this.m_iGastTore = m_iGastTore;
	}

	/**
	 * Getter for property m_iGastTore.
	 * 
	 * @return Value of property m_iGastTore.
	 */
	public final int getGuestGuestGoals() {
		return m_iGastTore;
	}


	public final void setHomeTeamID(int iHomeTeamID) {
		this.m_iHomeTeamID = iHomeTeamID;
	}


	public int getHomeTeamID() {return m_iHomeTeamID;}

	/**
	 * Setter for property m_sHeimName.
	 * 
	 * @param m_sHeimName
	 *            New value of property m_sHeimName.
	 */
	public final void setHomeTeamName(String m_sHeimName) {
		this.m_sHeimName = m_sHeimName;
	}

	/**
	 * Getter for property m_sHeimName.
	 * 
	 * @return Value of property m_sHeimName.
	 */
	public final String getHomeTeamName() {
		return m_sHeimName;
	}

	/**
	 * Setter for property m_iHeimTore.
	 * 
	 * @param m_iHeimTore
	 *            New value of property m_iHeimTore.
	 */
	public final void setHomeTeamGoals(int m_iHeimTore) {
		this.m_iHeimTore = m_iHeimTore;
	}

	/**
	 * Getter for property m_iHeimTore.
	 * 
	 * @return Value of property m_iHeimTore.
	 */
	public final int getHomeTeamGoals() {
		return m_iHeimTore;
	}

	/**
	 * Setter for property m_sMatchDate.
	 * @param sMatchDate New value of property m_sMatchDate.
	 */
	public final void setMatchSchedule(String sMatchDate) {
		m_matchSchedule = new HTDatetime(sMatchDate);
	}

	/**
	 * This function return the match schedule as provided by CHPP in xml files
	 * This function should be avoided
	 */
	@Deprecated
	public final String getMatchScheduleAsString() {
		return m_matchSchedule.getHattrickTimeAsString();
	}

	public ZonedDateTime getMatchSchedule(boolean localized) {
		if (localized) {
			return m_matchSchedule.getUserLocalizedTime();
		}
		else{
			return m_matchSchedule.getHattrickTime();
		}
	}

	/**
	 * Getter for property m_lDatum.
	 * 
	 * @return Value of property m_lDatum.
	 */
	public Timestamp getMatchDateAsTimestamp() {
			return getMatchDateAsTimestamp(false);
	}

	/**
	 * Getter for property m_lDatum.
	 *
	 * @return Value of property m_lDatum.
	 */
	public Timestamp getMatchDateAsTimestamp(boolean localized) {
		if (localized) {
			return m_matchSchedule.getUserLocalizedTimeAsTimestamp();
		}
		else{
			return m_matchSchedule.getHattrickTimeAsTimestamp();
		}
	}

	/**
	 * Setter for property m_iMatchID.
	 * 
	 * @param m_iMatchID
	 *            New value of property m_iMatchID.
	 */
	public void setMatchID(int m_iMatchID) {
		this.m_iMatchID = m_iMatchID;
	}

	// //////////////////////////////////////////////////////////////////////////////
	// Accessor
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Getter for property m_iMatchID.
	 * 
	 * @return Value of property m_iMatchID.
	 */
	public int getMatchID() {
		return m_iMatchID;
	}

	/**
	 * Setter for property m_iMatchStatus.
	 * 
	 * @param m_iMatchStatus
	 *            New value of property m_iMatchStatus.
	 */
	public final void setMatchStatus(int m_iMatchStatus) {
		this.m_iMatchStatus = m_iMatchStatus;
	}

	/**
	 * Getter for property m_iMatchStatus.
	 * 
	 * @return Value of property m_iMatchStatus.
	 */
	public final int getMatchStatus() {
		return m_iMatchStatus;
	}

	/**
	 * Setter for property m_iMatchTyp.
	 * 
	 * @param matchTyp
	 *            New value of property m_iMatchTyp.
	 */
	public void setMatchType(MatchType matchTyp) {
		this.m_mtMatchTyp = matchTyp;
	}

	/**
	 * Getter for property m_iMatchTyp.
	 * 
	 * @return Value of property m_iMatchTyp.
	 */
	public MatchType getMatchType() {
		if ((m_mtMatchTyp == null) || (m_mtMatchTyp == MatchType.NONE)
				|| m_mtMatchTyp == MatchType.CUP && (m_mtCupLevel == null || m_mtCupLevel == CupLevel.NONE)) {
			var m = OnlineWorker.inferMissingMatchType(this);
			this.m_mtMatchTyp = m.getMatchType();
			if (m_mtMatchTyp != MatchType.NONE) {
				this.iMatchContextId = m.getMatchContextId();
				this.m_mtCupLevel = m.getCupLevel();
				this.m_mtCupLevelIndex = m.getCupLevelIndex();
				this.iTournamentTypeID = m.getTournamentTypeID();
				this.isObsolet = m.isObsolet();
				DBManager.instance().storeMatchKurzInfos(new MatchKurzInfo[]{this});
				HOLogger.instance().debug(this.getClass(), String.format("Successfully set MatchType to %s (%s) for match: %s", m_mtMatchTyp.getName(), this.getMatchTypeExtended().getName(), this.getMatchID()));
			} else {
				HOLogger.instance().debug(this.getClass(), String.format("Could not infer MatchType of match: %s", this.getMatchID()));
			}
		}
		return m_mtMatchTyp;
	}

	public void setMatchTypeExtended(IMatchType _matchTypeExtended) {
		this.m_matchTypeExtended = _matchTypeExtended;
	}

	public IMatchType getMatchTypeExtended() {
		if (m_matchTypeExtended == MatchType.NONE) {
			if (this.getMatchType() == MatchType.CUP) {		// reload missing type info
				if (m_mtCupLevel == CupLevel.NATIONALorDIVISIONAL) {
					m_matchTypeExtended = MatchType.CUP;
				} else if (m_mtCupLevel == CupLevel.CONSOLATION) {
					m_matchTypeExtended = MatchTypeExtended.CONSOLANTECUP;
				} else {
					m_matchTypeExtended = switch (m_mtCupLevelIndex) {
						case EMERALD -> MatchTypeExtended.EMERALDCUP;
						case RUBY -> MatchTypeExtended.RUBYCUP;
						case SAPPHIRE -> MatchTypeExtended.SAPPHIRECUP;
						default -> MatchType.NONE;
					};
				}
			} else if (m_mtMatchTyp == MatchType.TOURNAMENTGROUP) {
				TournamentType tournamentType = TournamentType.getById(iTournamentTypeID);
				if (tournamentType == TournamentType.DIVISIONBATTLE) {
					m_matchTypeExtended = MatchTypeExtended.DIVISIONBATTLE;
				}
				else {
					m_matchTypeExtended = m_mtMatchTyp;
				}
			}
			else {
				m_matchTypeExtended = m_mtMatchTyp;
			}
		}
		return m_matchTypeExtended;
	}

	public CupLevel getCupLevel() {
		return m_mtCupLevel;
	}

	public void setCupLevel(CupLevel _CupLevel) {
		this.m_mtCupLevel = _CupLevel;
	}

	public CupLevelIndex getCupLevelIndex() {
		return m_mtCupLevelIndex;
	}

	public void setCupLevelIndex(CupLevelIndex _CupLevelIndex) {
		this.m_mtCupLevelIndex = _CupLevelIndex;
	}

	// --------------------------------------------------------------
	@Override
	public final int compareTo(Object obj) {
		if (obj instanceof MatchKurzInfo) {
			final MatchKurzInfo info = (MatchKurzInfo) obj;

			if (info.getMatchDateAsTimestamp().before(
					this.getMatchDateAsTimestamp())) {
				return -1;
			} else if (info.getMatchDateAsTimestamp().after(
					this.getMatchDateAsTimestamp())) {
				return 1;
			} else {
				return 0;
			}
		}

		return 0;
	}


	public MatchKurzInfo(){}

	public MatchKurzInfo(MatchKurzInfo parent){
		new MatchKurzInfo();
		this.copyFrom(parent);
	}

	/**
	 * Merges the data from the given <code>MatchKurzInfo</code> into this
	 * <code>MatchKurzInfo</code>. This method should be used e.g. when a model
	 * has to be updated with data from a different <code>MatchKurzInfo</code>
	 * instance but and object identity has to be preserved.
	 * 
	 * @param match
	 *            the <code>MatchKurzInfo</code> to get the data from.
	 */
	public void merge(MatchKurzInfo match) {
		if (match.getMatchID() != getMatchID()) {
			throw new IllegalArgumentException(
					"Could not merge matches with different IDs");
		}
		setGuestTeamID(match.getGuestTeamID());
		setGuestTeamName(match.getGuestTeamName());
		setGuestTeamGoals(match.getGuestGuestGoals());
		setHomeTeamID(match.getHomeTeamID());
		setHomeTeamName(match.getHomeTeamName());
		setHomeTeamGoals(match.getHomeTeamGoals());
		setMatchSchedule(match.getMatchScheduleAsString());
		setMatchStatus(match.getMatchStatus());
		setOrdersGiven(match.isOrdersGiven());
		setMatchType(match.getMatchType());
		setCupLevel(match.getCupLevel());
		setCupLevelIndex(match.getCupLevelIndex());
		setWeather(match.getWeather());
		setWeatherForecast(match.getWeatherForecast());
	}


	public void copyFrom(MatchKurzInfo match) {
		setMatchID(match.getMatchID());
		merge(match);
	}

	public final boolean isHomeMatch()
	{
		return m_iHomeTeamID == user_team_id;
	}

	// Return duration of the match in minutes
	// null, if match is not finished or duration is unknown
	public Integer getDuration()
	{
		if ( duration == null && this.isFinished()){
			Matchdetails matchdetails = getMatchdetails();
			if ( matchdetails != null){
				duration = matchdetails.getLastMinute();
			}
		}
		return duration;
	}

	private boolean isFinished() {
		return this.m_iMatchStatus == FINISHED;
	}

	// Set duration of the match in minutes
	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Matchdetails getMatchdetails() {
		if (matchdetails==null){
			matchdetails = Matchdetails.getMatchdetails(getMatchID(), getMatchType());
		}
		return matchdetails;
	}

	// TODO: this one is obsolete. Should be replaced by MatchDetails.isWalkoverMatchWin
	private Boolean isWalkoverMatch;
	// return true, if the opponent team didn't appear. The match was won by 5-0
	public boolean isWalkoverMatch() {
		if ( isWalkoverMatch == null) {
			isWalkoverMatch = false;
			if (getDuration() == 0) {
				// Duration of walk over matches is 0 minutes
				for (var e : getMatchdetails().getHighlights()) {
					if ( e.getMatchEventID() == MatchEvent.MatchEventID.AWAY_TEAM_WALKOVER ) {
						if (this.isHomeMatch()) {
							isWalkoverMatch = true;
						}
						break;
					}
					else if ( e.getMatchEventID() == MatchEvent.MatchEventID.HOME_TEAM_WALKOVER){
						if (!this.isHomeMatch()){
							isWalkoverMatch = true;
						}
						break;
					}
				}
			}
		}
		return isWalkoverMatch;
	}

	public String getResultLong() {
		if ( this.m_iMatchStatus != FINISHED){
			return getResultString(-1,-1,"");
		}
		return getMatchdetails().getResultLong();
	}

	public double getExperienceIncrease(int minutes) {
		double p;
		var _matchType = this.getMatchTypeExtended();
		if (_matchType instanceof MatchType){
			p = switch ((MatchType) _matchType){
				case CUP, QUALIFICATION -> 2d;
				case FRIENDLYNORMAL, FRIENDLYCUPRULES -> .1;
				case INTFRIENDLYCUPRULES, INTFRIENDLYNORMAL -> .2;
				case NATIONALFRIENDLY, LEAGUE -> 1d;
				case MASTERS -> 5d;
				case NATIONALCOMPCUPRULES -> 16d;
				case NATIONALCOMPNORMAL -> 8d;
				case TOURNAMENTGROUP -> getTournamentExperienceFactor(TournamentType.getById(this.iMatchContextId), false);
				case TOURNAMENTPLAYOFF -> getTournamentExperienceFactor(TournamentType.getById(this.iMatchContextId), true);
				default -> 0d;
			};
		} else{
			//case MatchTypeExtended.EMERALDCUP, MatchTypeExtended.RUBYCUP, MatchTypeExtended.SAPPHIRECUP -> .5;
			p = 0.5;
		}
		return minutes * p / 90. / 28.571;
	}

	/**
	 * get experience factor of tournament natianal team matches
	 * TODO: check if playoffs are really tagged by MatchType.TOURNAMENTPLAYOFF
	 * @param tournamentType tournament type of the match, null if no tournament match
	 * @param isPlayOff boolean, true if play off tournament match
	 * @return factor of league match experience increases (3.5)
	 */
	private double getTournamentExperienceFactor(@Nullable TournamentType tournamentType, boolean isPlayOff) {
		if ( tournamentType == null) return 0d;
		if ( tournamentType.isWorldCup()){
			// WM-Spiel	28
			//WM-Halbfinal, WM-Final	56
				if ( isPlayOff){
					return 56/3.5;
				}
				else {
					return 28/3.5;
				}
		}
		else if ( tournamentType.isNationsCup()){
			//Nationenpokal	7
			//Nationenpokal (K.O.)	14
			if ( isPlayOff){
				return 14/3.5;
			}
			else {
				return 7/3.5;
			}
		}
		else if ( tournamentType.isContinentalCup()){
			//Kontinentalmeisterschaftsspiel	14
			//Kontinentalmeisterschaftsspiel (Viertel-, Halbfinal, Final)	21
			if ( isPlayOff){
				return 21/3.5;
			}
			else {
				return 14/3.5;
			}
		}
		else if ( tournamentType.isNTFriendly()){
			//Nationalteam Freundschaftsspiel	3.5
			return 1d;
		}
		return 0d;
	}
}
