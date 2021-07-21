package core.model.match;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.enums.MatchType;
import core.model.misc.Basics;
import core.net.OnlineWorker;
import core.util.HOLogger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static core.util.StringUtils.getResultString;


public class Matchdetails implements core.model.match.IMatchDetails {

    private SourceSystem sourceSystem;
    private String m_sArenaName = "";
    private String m_sGastName = "";
    private String m_sHeimName = "";
    private String m_sMatchreport = "";
    private Timestamp m_clFetchDatum;
    private Timestamp m_clSpielDatum;
    private ArrayList<MatchEvent> m_vHighlights;
    private int m_iArenaID = -1;
    private int m_iGastId = -1;

    //-1pic,0=nor,1=mots
    private int m_iGuestEinstellung;
    private int m_iGuestGoals;
    private int m_iGuestLeftAtt;
    private int m_iGuestLeftDef;
    private int m_iGuestMidAtt;
    private int m_iGuestMidDef;
    private int m_iGuestMidfield;
    private int m_iGuestRightAtt;
    private int m_iGuestRightDef;
    private int m_iGuestHatStats;
    private int m_iGuestTacticSkill;
    private int m_iGuestTacticType;
    private int m_iHeimId = -1;

    // Match Type used in case match is reloaded, i.e matchType in Database was incorrect
    private MatchType m_MatchTyp = MatchType.NONE;

    //-1pic,0=nor,1=mots, -1000 Unbekannt
    private int m_iHomeEinstellung;

    //Ratings
    private int m_iHomeGoals;
    private int m_iHomeLeftAtt;
    private int m_iHomeLeftDef;
    private int m_iHomeMidAtt;
    private int m_iHomeMidDef;
    private int m_iHomeMidfield;
    private int m_iHomeRightAtt;
    private int m_iHomeRightDef;
    private int m_iHomeHatStats;
    private int m_iHomeTacticSkill;
    private int m_iHomeTacticType;
    private int m_iMatchID = -1;

    //0=Regen,1=BewÃ¶lkt,2=wolkig,3=Sonne
    private int m_iWetterId = -1;

    private int m_iZuschauer;

    private CupLevel m_mtCupLevel = CupLevel.NONE;
    private CupLevelIndex m_mtCupLevelIndex = CupLevelIndex.NONE;
    /**
     * MatchContextId
     */
    private int iMatchContextId;

    public int getMatchContextId() {
        return iMatchContextId;
    }

    public void setMatchContextId(int iMatchContextId) {
        this.iMatchContextId = iMatchContextId;
    }

    /**
     * TournamentTypeID
     */
    private int iTournamentTypeID;

    public int getTournamentTypeID() {
        return iTournamentTypeID;
    }

    public void setTournamentTypeID(int iTournamentTypeID) {
        this.iTournamentTypeID = iTournamentTypeID;
    }

    /**
     * Spectators in category Terraces, is 0 if not our home match
     **/
    private int soldTerraces = -1;

    /**
     * Spectators in category Basic, is 0 if not our home match
     **/
    private int soldBasic = -1;

    /**
     * Spectators in category Roof, is 0 if not our home match
     **/
    private int soldRoof = -1;

    /**
     * Spectators in category VIP, is 0 if not our home match
     **/
    private int soldVIP = -1;

    private int m_iRegionId;
    private int ratingIndirectSetPiecesAtt = -1;
    private int ratingIndirectSetPiecesDef = -1;

    public ArrayList<Injury> getM_Injuries() {
        return m_Injuries;
    }

    public void setM_Injuries(ArrayList<Injury> m_Injuries) {
        this.m_Injuries = m_Injuries;
    }

    public ArrayList<Injury> m_Injuries = new ArrayList<>();

    /*
    goals of each part [0..4] of the match (see MatchEvent.MatchPartId)
     */
    private Integer[] homeGoalsInParts;
    private Integer[] guestGoalsInParts;

    private String homeFormation;
    private String awayFormation;

    public String getResultInPart(MatchEvent.MatchPartId part) {
        if (homeGoalsInParts != null) {
            return getResultString(homeGoalsInParts[part.getValue()], guestGoalsInParts[part.getValue()], "");
        }
        return "";
    }

    public String getResultAfterPart(MatchEvent.MatchPartId part) {
        if (homeGoalsInParts != null) {
            int home = 0;
            int guest = 0;
            for (var p = MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED.getValue(); p <= part.getValue(); p++) {
                if (homeGoalsInParts[p] != null) {
                    home += homeGoalsInParts[p];
                    guest += guestGoalsInParts[p];
                }
            }
            return getResultString(home, guest, "");
        }
        if (part == MatchEvent.MatchPartId.SECOND_HALF && this.getLastMinute() < 110 ||
                part == MatchEvent.MatchPartId.OVERTIME && this.getLastMinute() < 121 ||
                part == MatchEvent.MatchPartId.PENALTY_CONTEST) {
            return getResult();
        }
        return "";
    }

    public String getResult() {
        if (this.getFetchDatum() != null) {
            return getResultString(this.m_iHomeGoals, this.m_iGuestGoals, "");
        }
        return getResultString(-1, -1, "");
    }

    // Return match result extension information as abbreviation string
    // e. g. a.p. means after penalties. a.e. after extension
    public String getResultExtensionAbbreviation() {
        return getResultExtensionAbbreviation(getLastMinuteFromPartInfo());
    }

    private int getLastMinuteFromPartInfo() {
        if (homeGoalsInParts != null) {
            if (homeGoalsInParts[4] != null) return 130;
            if (homeGoalsInParts[3] != null) return 120;
            return 90;
        }
        return getLastMinute();
    }

    static String afterPenalties = HOVerwaltung.instance().getLanguageString("ls.match.after.penalties.abbreviation");
    static String afterExtension = HOVerwaltung.instance().getLanguageString("ls.match.after.extension.abbreviation");

    public static String getResultExtensionAbbreviation(Integer duration) {
        if (duration != null) {
            if (duration > 120)
                return afterPenalties;
            if (duration > 110)
                return afterExtension;
        }
        return "";
    }

    // results like : 2 - 4 a.p.
    public String getResultEx() {
        return getResultString(this.m_iHomeGoals, this.m_iGuestGoals, getResultExtensionAbbreviation());
    }

    // results like : 1 - 1 (2 - 4 a.p.)
    public String getResultLong() {
        String res = getResultAfterPart(MatchEvent.MatchPartId.SECOND_HALF);
        if (getLastMinuteFromPartInfo() > 110) {
            res += " (" + getResultEx() + ")";
        }
        return res;
    }

    /*
    Reloading match details is real expensive, so there is a limit of maximum reloads per session
    Reasons to reload match details:
    - match details do not contain highlights
    - highlights do not contain match part (column is introduced with #561 in HO V4.0)
     */
    private static int maxMatchdetailsReloadsPerSession = 10;

    // Enable explicit download of match details (e. g. invoked by user clicks)
    public static Matchdetails getMatchdetails(int matchId, MatchType type, boolean force) {
        if (force && maxMatchdetailsReloadsPerSession == 0) {
            maxMatchdetailsReloadsPerSession = 1;
        }
        return getMatchdetails(matchId, type);
    }

    public static Matchdetails getMatchdetails(int matchId, MatchType type) {
        var ret = DBManager.instance().loadMatchDetails(type.getId(), matchId);
        ret.setMatchType(type);
        ret.setSourceSystem(type.getSourceSystem());
        return ret;
    }

    public void setRatingIndirectSetPiecesAtt(int ratingIndirectSetPiecesAtt) {
        this.ratingIndirectSetPiecesAtt = ratingIndirectSetPiecesAtt;
    }

    public void setRatingIndirectSetPiecesDef(int ratingIndirectSetPiecesDef) {
        this.ratingIndirectSetPiecesDef = ratingIndirectSetPiecesDef;
    }

    public int getRatingIndirectSetPiecesDef() {
        return ratingIndirectSetPiecesDef;
    }

    public int getRatingIndirectSetPiecesAtt() {
        return ratingIndirectSetPiecesAtt;
    }

    public int getLastMinute() {
        var highlights = getHighlights();
        if (highlights != null && highlights.size() > 0) {
            return highlights.get(highlights.size() - 1).getMinute();
        }
        return -1;
    }

    public Integer getHomeGoalsInPart(MatchEvent.MatchPartId matchPartId) {
        if (homeGoalsInParts == null) {
            InitGoalsInParts();
        }
        return homeGoalsInParts[matchPartId.getValue()];
    }

    public Integer getGuestGoalsInPart(MatchEvent.MatchPartId matchPartId) {
        if (guestGoalsInParts == null) {
            InitGoalsInParts();
        }
        return guestGoalsInParts[matchPartId.getValue()];
    }

    private void InitGoalsInParts() {
        int totalHome = 0;
        int totalGuest = 0;
        int lastPart = 0;
        homeGoalsInParts = new Integer[MatchEvent.MatchPartId.values().length];
        guestGoalsInParts = new Integer[MatchEvent.MatchPartId.values().length];
        for (var event : getHighlights()) {
            int part = 0;

            var partId = event.getMatchPartId();
            if (partId == MatchEvent.MatchPartId.PENALTY_CONTEST &&
                    event.isEndOfMatchEvent()) {
                // HO shifted match end event of extra time part (3) to part 4
                break;
            }

            if (partId != null) part = partId.getValue();
            if (homeGoalsInParts[part] == null) {
                homeGoalsInParts[part] = 0;
                guestGoalsInParts[part] = 0;
                lastPart = part;
            }
            var eventId = event.getMatchEventID().getValue();
            if (eventId > 99 && eventId < 200 ||
                    eventId > 54 && eventId < 58
            ) {
                // goal
                if (event.getTeamID() == this.m_iHeimId ||
                        event.getTeamID() <= 0 && this.m_iHeimId <= 0) { // Verlegenheitstruppe has id < 0 are stored as 0 in event
                    homeGoalsInParts[part]++;
                    totalHome++;
                } else {
                    guestGoalsInParts[part]++;
                    totalGuest++;
                }
            } else if (eventId == MatchEvent.MatchEventID.HOME_TEAM_WALKOVER.getValue()) {
                guestGoalsInParts[0] = 5;
                totalGuest = 5;
            } else if (eventId == MatchEvent.MatchEventID.AWAY_TEAM_WALKOVER.getValue()) {
                homeGoalsInParts[0] = 5;
                totalHome = 5;
            }
        }
        verifyGoalsInParts(lastPart, totalHome, totalGuest);
    }

    /**
     * Hattrick bug links own goals to wrong team.
     * In this case the goals in  parts evaluated from match events needs correction.
     * If match event result differs from match details end result, all goal will be shifted to the last
     * mentioned match part.
     *
     * @param lastPart   last mentioned match part
     * @param totalHome  number of home goals counted by match events
     * @param totalGuest number of guest goals counted by match events
     */
    private void verifyGoalsInParts(int lastPart, int totalHome, int totalGuest) {
        var homeEnd = this.getHomeGoals();
        var guestEnd = this.getGuestGoals();
        if (totalHome != homeEnd || totalGuest != guestEnd) {
            for (int i = 0; i < lastPart; i++) {
                if (homeGoalsInParts[i] != null) {
                    homeGoalsInParts[i] = 0;
                    guestGoalsInParts[i] = 0;
                }
            }
            homeGoalsInParts[lastPart] = homeEnd;
            guestGoalsInParts[lastPart] = guestEnd;
        }
    }

    public void setHomeGoalsInPart(Integer[] homeGoalsInPart) {
        this.homeGoalsInParts = homeGoalsInPart;
    }

    public void setGuestGoalsInPart(Integer[] guestGoalsInPart) {
        this.guestGoalsInParts = guestGoalsInPart;
    }

    public SourceSystem getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(SourceSystem sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public int getHatStats(int teamId) {
        if (teamId == this.m_iHeimId) {
            return this.getHomeHatStats();
        } else if (teamId == this.m_iGastId) {
            return this.getAwayHatStats();
        }
        return 0;
    }

    public int getSumHatStatsMidfield(int teamId) {
        if (teamId == this.m_iHeimId) {
            return 3*this.getHomeMidfield();
        } else if (teamId == this.m_iGastId) {
            return 3*this.getGuestMidfield();
        }
        return 0;
    }

    public int getSumHatStatsAttack(int teamId) {
        if (teamId == this.m_iHeimId) {
            return this.getHomeLeftAtt() + this.getHomeMidAtt() + this.getHomeRightAtt();
        } else if (teamId == this.m_iGastId) {
            return this.getGuestLeftAtt() + this.getGuestMidAtt() + this.getHomeRightAtt();
        }
        return 0;
    }

    public int getSumHatStatsDefence(int teamId) {
        if (teamId == this.m_iHeimId) {
            return this.getHomeLeftDef() + this.getHomeMidDef() + this.getHomeRightDef();
        } else if (teamId == this.m_iGastId) {
            return this.getGuestLeftDef() + this.getGuestMidDef() + this.getHomeRightDef();
        }
        return 0;
    }

    public enum eInjuryType {
        NA(0), BRUISE(1), INJURY(2);
        private final int value;

        public int getValue() {
            return value;
        }

        eInjuryType(final int newValue) {
            value = newValue;
        }

        public static eInjuryType fromInteger(int x) {
            return switch (x) {
                case 0 -> NA;
                case 1 -> BRUISE;
                case 2 -> INJURY;
                default -> null;
            };
        }

    }

    public static class Injury {
        public int getInjuryPlayerID() {
            return InjuryPlayerID;
        }

        public eInjuryType getInjuryType() {
            return InjuryType;
        }

        public int getInjuryMinute() {
            return InjuryMinute;
        }


        int InjuryPlayerID;
        int InjuryTeamID;
        eInjuryType InjuryType;
        int InjuryMinute;
        int MatchPart;

        public Injury(int injuryPlayerID, int injuryTeamID, int _injuryType, int injuryMinute, int matchPart) {
            InjuryPlayerID = injuryPlayerID;
            InjuryTeamID = injuryTeamID;
            InjuryMinute = injuryMinute;
            MatchPart = matchPart;
            if (_injuryType == eInjuryType.BRUISE.value) {
                InjuryType = eInjuryType.BRUISE;
            } else if (_injuryType == eInjuryType.INJURY.value) {
                InjuryType = eInjuryType.INJURY;
            } else {
                HOLogger.instance().log(getClass(), "Injury type not recognized !!");
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////
    //Konstruktor
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new instance of Matchdetails
     */
    public Matchdetails() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Gibt den Namen zu einer BewertungzurÃ¼ck
     */
    public static String getNameForEinstellung(int einstellung) {
        return switch (einstellung) {
            case EINSTELLUNG_PIC -> HOVerwaltung.instance().getLanguageString("ls.team.teamattitude.playitcool");
            case EINSTELLUNG_NORMAL -> HOVerwaltung.instance().getLanguageString("ls.team.teamattitude.normal");
            case EINSTELLUNG_MOTS -> HOVerwaltung.instance().getLanguageString("ls.team.teamattitude.matchoftheseason");
            default -> HOVerwaltung.instance().getLanguageString("Unbestimmt");
        };
    }

    ////////////////////////////////////////////////////////////////////////////////
    //Static
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Gibt den Namen zu einer BewertungzurÃ¼ck
     */
    public static String getNameForTaktik(int taktikTyp) {

        return switch (taktikTyp) {
            case TAKTIK_NORMAL -> HOVerwaltung.instance().getLanguageString("ls.team.tactic.normal");
            case TAKTIK_PRESSING -> HOVerwaltung.instance().getLanguageString("ls.team.tactic.pressing");
            case TAKTIK_KONTER -> HOVerwaltung.instance().getLanguageString("ls.team.tactic.counter-attacks");
            case TAKTIK_MIDDLE -> HOVerwaltung.instance().getLanguageString("ls.team.tactic.attackinthemiddle");
            case TAKTIK_WINGS -> HOVerwaltung.instance().getLanguageString("ls.team.tactic.attackonwings");
            case TAKTIK_CREATIVE -> HOVerwaltung.instance().getLanguageString("ls.team.tactic.playcreatively");
            case TAKTIK_LONGSHOTS -> HOVerwaltung.instance().getLanguageString("ls.team.tactic.longshots");
            default -> HOVerwaltung.instance().getLanguageString("Unbestimmt");
        };
    }

    // get the used tactic name in short form
    public static String getShortTacticName(int type) {
        HOVerwaltung hov = HOVerwaltung.instance();

        return switch (type) {
            case IMatchDetails.TAKTIK_NORMAL -> hov.getLanguageString("ls.team.tactic_short.normal");
            case IMatchDetails.TAKTIK_PRESSING -> hov.getLanguageString("ls.team.tactic_short.pressing");
            case IMatchDetails.TAKTIK_KONTER -> hov.getLanguageString("ls.team.tactic_short.counter-attacks");
            case IMatchDetails.TAKTIK_MIDDLE -> hov.getLanguageString("ls.team.tactic_short.attackinthemiddle");
            case IMatchDetails.TAKTIK_WINGS -> hov.getLanguageString("ls.team.tactic_short.attackonwings");
            case IMatchDetails.TAKTIK_LONGSHOTS -> hov.getLanguageString("ls.team.tactic_short.longshots");
            case IMatchDetails.TAKTIK_CREATIVE -> hov.getLanguageString("ls.team.tactic_short.playcreatively");
            default -> HOVerwaltung.instance().getLanguageString("Unbestimmt");
        };
    }

    public final int getGuestHalfTimeGoals() {
        ArrayList<MatchEvent> highLights = getHighlights();
        if (highLights != null) {
            for (MatchEvent iMatchHighlight : highLights) {
                if (iMatchHighlight.getMatchEventCategory() == 0 && iMatchHighlight.getiMatchEventID() == 45)
                    return iMatchHighlight.getGastTore();
            }
        }
        return -1;
    }

    public final int getHomeHalfTimeGoals() {
        ArrayList<MatchEvent> highLights = getHighlights();
        if (highLights != null) {
            for (MatchEvent iMatchHighlight : highLights) {
                if (iMatchHighlight.getMatchEventCategory() == 0 && iMatchHighlight.getiMatchEventID() == 45)
                    return iMatchHighlight.getHeimTore();
            }
        }
        return -1;
    }

    /**
     * Setter for property m_iArenaID.
     *
     * @param m_iArenaID New value of property m_iArenaID.
     */
    public final void setArenaID(int m_iArenaID) {
        this.m_iArenaID = m_iArenaID;
    }

    /**
     * Getter for property m_iArenaID.
     *
     * @return Value of property m_iArenaID.
     */
    public final int getArenaID() {
        return m_iArenaID;
    }

    /**
     * Setter for property m_sArenaName.
     *
     * @param m_sArenaName New value of property m_sArenaName.
     */
    public final void setArenaName(java.lang.String m_sArenaName) {
        this.m_sArenaName = m_sArenaName;
    }

    /**
     * Getter for property m_sArenaName.
     *
     * @return Value of property m_sArenaName.
     */
    public final java.lang.String getArenaName() {
        return m_sArenaName;
    }

    /**
     * Setter for property m_clFetchDatum.
     *
     * @param m_clFetchDatum New value of property m_clFetchDatum.
     */
    public final void setFetchDatum(java.sql.Timestamp m_clFetchDatum) {
        this.m_clFetchDatum = m_clFetchDatum;
    }

    /**
     * Getter for property m_clFetchDatum.
     *
     * @return Value of property m_clFetchDatum.
     */
    public final java.sql.Timestamp getFetchDatum() {
        return m_clFetchDatum;
    }

    /**
     * Getter for property m_lDatum.
     */
    public final boolean setFetchDatumFromString(String date) {
        m_clFetchDatum = Basics.parseHattrickDate(date);
        return m_clFetchDatum != null;
        /*
        try {
            //Hattrick
            final java.text.SimpleDateFormat simpleFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    java.util.Locale.GERMANY);

            m_clFetchDatum = new java.sql.Timestamp(simpleFormat.parse(date).getTime());
        } catch (Exception e) {
            try {
                //Hattrick
                final java.text.SimpleDateFormat simpleFormat = new java.text.SimpleDateFormat("yyyy-MM-dd",
                        java.util.Locale.GERMANY);

                m_clFetchDatum = new java.sql.Timestamp(simpleFormat.parse(date).getTime());
            } catch (Exception ex) {
                HOLogger.instance().log(getClass(), ex);
                m_clFetchDatum = null;
                return false;
            }
        }

        return true;*/
    }

    /**
     * Method that extract the formation from the full match comment
     *
     * @param home true for home lineup, false for away
     * @return The formation type 4-4-2 or 5-3-2 etc
     */
    public final String getFormation(boolean home) {
        return home ? homeFormation : awayFormation;
    }

    public void setHomeFormation(String homeFormation) {
        this.homeFormation = homeFormation;
    }

    public void setAwayFormation(String awayFormation) {
        this.awayFormation = awayFormation;
    }

    /**
     * Setter for property m_iGastId.
     *
     * @param m_iGastId New value of property m_iGastId.
     */
    public final void setGastId(int m_iGastId) {
        this.m_iGastId = m_iGastId;
    }

    /**
     * Getter for property m_iGastId.
     *
     * @return Value of property m_iGastId.
     */
    public final int getGuestTeamId() {
        return m_iGastId;
    }

    /**
     * Setter for property m_sGastName.
     *
     * @param m_sGastName New value of property m_sGastName.
     */
    public final void setGastName(java.lang.String m_sGastName) {
        this.m_sGastName = m_sGastName;
    }

    /**
     * Getter for property m_sGastName.
     *
     * @return Value of property m_sGastName.
     */
    public final java.lang.String getGuestTeamName() {
        return m_sGastName;
    }

    /**
     * Setter for property m_iGuestEinstellung.
     *
     * @param m_iGuestEinstellung New value of property m_iGuestEinstellung.
     */
    public final void setGuestEinstellung(int m_iGuestEinstellung) {
        this.m_iGuestEinstellung = m_iGuestEinstellung;
    }

    /**
     * Getter for property m_iGuestEinstellung.
     *
     * @return Value of property m_iGuestEinstellung.
     */
    public final int getGuestEinstellung() {
        return m_iGuestEinstellung;
    }

    /**
     * Gibt die GesamtstÃ¤rke zurÃ¼ck, Absolut oder durch die Anzahl der Summanten geteilt
     */
    public final float getGuestGesamtstaerke(boolean absolut) {
        double summe = 0d;
        summe += getGuestLeftDef();
        summe += getGuestMidDef();
        summe += getGuestRightDef();
        summe += (getGuestMidfield() * 3);
        summe += getGuestLeftAtt();
        summe += getGuestMidAtt();
        summe += getGuestRightAtt();

        if (!absolut) {
            summe /= 9d;
        }

        return (float) summe;
    }

    /**
     * Setter for property m_iGuestGoals.
     *
     * @param m_iGuestGoals New value of property m_iGuestGoals.
     */
    public final void setGuestGoals(int m_iGuestGoals) {
        this.m_iGuestGoals = m_iGuestGoals;
    }

    /**
     * Getter for property m_iGuestGoals.
     *
     * @return Value of property m_iGuestGoals.
     */
    public final int getGuestGoals() {
        return m_iGuestGoals;
    }

    /**
     * Setter for property m_iGuestLeftAtt.
     *
     * @param m_iGuestLeftAtt New value of property m_iGuestLeftAtt.
     */
    public final void setGuestLeftAtt(int m_iGuestLeftAtt) {
        this.m_iGuestLeftAtt = m_iGuestLeftAtt;
    }

    /**
     * Getter for property m_iGuestLeftAtt.
     *
     * @return Value of property m_iGuestLeftAtt.
     */
    public final int getGuestLeftAtt() {
        return m_iGuestLeftAtt;
    }

    /**
     * Setter for property m_iGuestLeftDef.
     *
     * @param m_iGuestLeftDef New value of property m_iGuestLeftDef.
     */
    public final void setGuestLeftDef(int m_iGuestLeftDef) {
        this.m_iGuestLeftDef = m_iGuestLeftDef;
    }

    /**
     * Getter for property m_iGuestLeftDef.
     *
     * @return Value of property m_iGuestLeftDef.
     */
    public final int getGuestLeftDef() {
        return m_iGuestLeftDef;
    }

    /**
     * Setter for property m_iGuestMidAtt.
     *
     * @param m_iGuestMidAtt New value of property m_iGuestMidAtt.
     */
    public final void setGuestMidAtt(int m_iGuestMidAtt) {
        this.m_iGuestMidAtt = m_iGuestMidAtt;
    }

    /**
     * Getter for property m_iGuestMidAtt.
     *
     * @return Value of property m_iGuestMidAtt.
     */
    public final int getGuestMidAtt() {
        return m_iGuestMidAtt;
    }

    /**
     * Setter for property m_iGuestMidDef.
     *
     * @param m_iGuestMidDef New value of property m_iGuestMidDef.
     */
    public final void setGuestMidDef(int m_iGuestMidDef) {
        this.m_iGuestMidDef = m_iGuestMidDef;
    }

    /**
     * Getter for property m_iGuestMidDef.
     *
     * @return Value of property m_iGuestMidDef.
     */
    public final int getGuestMidDef() {
        return m_iGuestMidDef;
    }

    /**
     * Setter for property m_iGuestMidfield.
     *
     * @param m_iGuestMidfield New value of property m_iGuestMidfield.
     */
    public final void setGuestMidfield(int m_iGuestMidfield) {
        this.m_iGuestMidfield = m_iGuestMidfield;
    }

    /**
     * Getter for property m_iGuestMidfield.
     *
     * @return Value of property m_iGuestMidfield.
     */
    public final int getGuestMidfield() {
        return m_iGuestMidfield;
    }

    /**
     * Setter for property m_iGuestRightAtt.
     *
     * @param m_iGuestRightAtt New value of property m_iGuestRightAtt.
     */
    public final void setGuestRightAtt(int m_iGuestRightAtt) {
        this.m_iGuestRightAtt = m_iGuestRightAtt;
    }

    /**
     * Getter for property m_iGuestRightAtt.
     *
     * @return Value of property m_iGuestRightAtt.
     */
    public final int getGuestRightAtt() {
        return m_iGuestRightAtt;
    }

    /**
     * Setter for property m_iGuestRightDef.
     *
     * @param m_iGuestRightDef New value of property m_iGuestRightDef.
     */
    public final void setGuestRightDef(int m_iGuestRightDef) {
        this.m_iGuestRightDef = m_iGuestRightDef;
    }

    /**
     * Getter for property m_iGuestRightDef.
     *
     * @return Value of property m_iGuestRightDef.
     */
    public final int getGuestRightDef() {
        return m_iGuestRightDef;
    }

    /**
     * Getter for property m_iGuestHatStats.
     *
     * @return Value of property m_iGuestHatStats.
     */
    public final int getGuestHatStats() {
        return m_iGuestHatStats;
    }

    /**
     * Setter for property m_iGuestHatStats
     */
    public void setGuestHatStats() {
        int m_iGuestHatStats = this.m_iGuestLeftDef + this.m_iGuestMidDef + this.m_iGuestRightDef;
        m_iGuestHatStats += this.m_iGuestMidfield * 3;
        m_iGuestHatStats += this.m_iGuestLeftAtt + this.m_iGuestMidAtt + this.m_iGuestRightAtt;
        this.m_iGuestHatStats = m_iGuestHatStats;
    }


    /**
     * Setter for property m_iGuestTacticSkill.
     *
     * @param m_iGuestTacticSkill New value of property m_iGuestTacticSkill.
     */
    public final void setGuestTacticSkill(int m_iGuestTacticSkill) {
        this.m_iGuestTacticSkill = m_iGuestTacticSkill;
    }

    /**
     * Getter for property m_iGuestTacticSkill.
     *
     * @return Value of property m_iGuestTacticSkill.
     */
    public final int getGuestTacticSkill() {
        return m_iGuestTacticSkill;
    }

    /**
     * Setter for property m_iGuestTacticType.
     *
     * @param m_iGuestTacticType New value of property m_iGuestTacticType.
     */
    public final void setGuestTacticType(int m_iGuestTacticType) {
        this.m_iGuestTacticType = m_iGuestTacticType;
    }

    /**
     * Getter for property m_iGuestTacticType.
     *
     * @return Value of property m_iGuestTacticType.
     */
    public final int getGuestTacticType() {
        return m_iGuestTacticType;
    }

    /**
     * Setter for property m_iHeimId.
     *
     * @param m_iHeimId New value of property m_iHeimId.
     */
    public final void setHeimId(int m_iHeimId) {
        this.m_iHeimId = m_iHeimId;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //Accessor
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Getter for property m_iHeimId.
     *
     * @return Value of property m_iHeimId.
     */
    public final int getHomeTeamId() {
        return m_iHeimId;
    }

    /**
     * Setter for property m_sHeimName.
     *
     * @param m_sHeimName New value of property m_sHeimName.
     */
    public final void setHeimName(java.lang.String m_sHeimName) {
        this.m_sHeimName = m_sHeimName;
    }

    /**
     * Getter for property m_sHeimName.
     *
     * @return Value of property m_sHeimName.
     */
    public final java.lang.String getHomeTeamName() {
        return m_sHeimName;
    }

    /**
     * Setter for property m_vHighlights.
     *
     * @param m_vHighlights New value of property m_vHighlights.
     */
    public final void setHighlights(ArrayList<MatchEvent> m_vHighlights) {
        this.m_vHighlights = m_vHighlights;
    }

    /**
     * Getter for property m_vHighlights.
     * <p>
     * checks if database object needs an update from chpp (OnlineWorker)
     * Number of implicite updates per session is limited to maxMatchdetailsReloadsPerSession
     *
     * @return Value of property m_vHighlights.
     */
    public final ArrayList<MatchEvent> getHighlights() {
        if (this.getMatchID() > -1 && (m_vHighlights == null || m_vHighlights.size() == 0)) {
            m_vHighlights = DBManager.instance().getMatchHighlights(this.getMatchType().getId(), this.getMatchID());

            if (maxMatchdetailsReloadsPerSession > 0 && this.m_MatchTyp.isOfficial()) {
                if (m_vHighlights.size() == 0 || m_vHighlights.get(0).getMatchPartId() == null) {
                    HOLogger.instance().info(Matchdetails.class,
                            "Reload Matchdetails id: " + this.getMatchID());
                    boolean silenDownloadMode = OnlineWorker.isSilentDownload();
                    try {
                        OnlineWorker.setSilentDownload(true);
                        if (OnlineWorker.downloadMatchData(this.getMatchID(), this.m_MatchTyp, true)) {
                            m_vHighlights = DBManager.instance().getMatchHighlights(this.getMatchType().getId(), this.getMatchID());
                            maxMatchdetailsReloadsPerSession--;
                        } else {
                            maxMatchdetailsReloadsPerSession = 0;
                        }
                    } catch (Exception ex) {
                        HOLogger.instance().error(Matchdetails.class, ex.getMessage());
                    } finally {
                        OnlineWorker.setSilentDownload(silenDownloadMode);
                    }
                }
            }

        }
        return m_vHighlights;
    }

    /**
     * Setter for property m_iHomeEinstellung.
     *
     * @param m_iHomeEinstellung New value of property m_iHomeEinstellung.
     */
    public final void setHomeEinstellung(int m_iHomeEinstellung) {
        this.m_iHomeEinstellung = m_iHomeEinstellung;
    }

    /**
     * Getter for property m_iHomeEinstellung.
     *
     * @return Value of property m_iHomeEinstellung.
     */
    public final int getHomeEinstellung() {
        return m_iHomeEinstellung;
    }

    /**
     * Returns the HatStats, either in absolute terms, or divide by 9
     */
    public final float getHomeGesamtstaerke(boolean absolut) {
        double res = getHomeHatStats();
        if (!absolut) {
            res /= 9d;
        }

        return (float) res;
    }

    /**
     * Setter for property m_iHomeGoals.
     *
     * @param m_iHomeGoals New value of property m_iHomeGoals.
     */
    public final void setHomeGoals(int m_iHomeGoals) {
        this.m_iHomeGoals = m_iHomeGoals;
    }

    /**
     * Getter for property m_iHomeGoals.
     *
     * @return Value of property m_iHomeGoals.
     */
    public final int getHomeGoals() {
        return m_iHomeGoals;
    }

    /**
     * Setter for property m_iHomeLeftAtt.
     *
     * @param m_iHomeLeftAtt New value of property m_iHomeLeftAtt.
     */
    public final void setHomeLeftAtt(int m_iHomeLeftAtt) {
        this.m_iHomeLeftAtt = m_iHomeLeftAtt;
    }

    /**
     * Getter for property m_iHomeLeftAtt.
     *
     * @return Value of property m_iHomeLeftAtt.
     */
    public final int getHomeLeftAtt() {
        return m_iHomeLeftAtt;
    }

    /**
     * Setter for property m_iHomeLeftDef.
     *
     * @param m_iHomeLeftDef New value of property m_iHomeLeftDef.
     */
    public final void setHomeLeftDef(int m_iHomeLeftDef) {
        this.m_iHomeLeftDef = m_iHomeLeftDef;
    }

    /**
     * Getter for property m_iHomeLeftDef.
     *
     * @return Value of property m_iHomeLeftDef.
     */
    public final int getHomeLeftDef() {
        return m_iHomeLeftDef;
    }

    /**
     * Setter for property m_iHomeMidAtt.
     *
     * @param m_iHomeMidAtt New value of property m_iHomeMidAtt.
     */
    public final void setHomeMidAtt(int m_iHomeMidAtt) {
        this.m_iHomeMidAtt = m_iHomeMidAtt;
    }

    /**
     * Getter for property m_iHomeMidAtt.
     *
     * @return Value of property m_iHomeMidAtt.
     */
    public final int getHomeMidAtt() {
        return m_iHomeMidAtt;
    }

    /**
     * Setter for property m_iHomeMidDef.
     *
     * @param m_iHomeMidDef New value of property m_iHomeMidDef.
     */
    public final void setHomeMidDef(int m_iHomeMidDef) {
        this.m_iHomeMidDef = m_iHomeMidDef;
    }

    /**
     * Getter for property m_iHomeMidDef.
     *
     * @return Value of property m_iHomeMidDef.
     */
    public final int getHomeMidDef() {
        return m_iHomeMidDef;
    }

    /**
     * Setter for property m_iHomeMidfield.
     *
     * @param m_iHomeMidfield New value of property m_iHomeMidfield.
     */
    public final void setHomeMidfield(int m_iHomeMidfield) {
        this.m_iHomeMidfield = m_iHomeMidfield;
    }

    /**
     * Getter for property m_iHomeMidfield.
     *
     * @return Value of property m_iHomeMidfield.
     */
    public final int getHomeMidfield() {
        return m_iHomeMidfield;
    }

    /**
     * Setter for property m_iHomeRightAtt.
     *
     * @param m_iHomeRightAtt New value of property m_iHomeRightAtt.
     */
    public final void setHomeRightAtt(int m_iHomeRightAtt) {
        this.m_iHomeRightAtt = m_iHomeRightAtt;
    }

    /**
     * Getter for property m_iHomeRightAtt.
     *
     * @return Value of property m_iHomeRightAtt.
     */
    public final int getHomeRightAtt() {
        return m_iHomeRightAtt;
    }

    /**
     * Setter for property m_iHomeRightDef.
     *
     * @param m_iHomeRightDef New value of property m_iHomeRightDef.
     */
    public final void setHomeRightDef(int m_iHomeRightDef) {
        this.m_iHomeRightDef = m_iHomeRightDef;
    }

    /**
     * Getter for property m_iHomeRightDef.
     *
     * @return Value of property m_iHomeRightDef.
     */
    public final int getHomeRightDef() {
        return m_iHomeRightDef;
    }

    /**
     * @return the m_iHomeHatStats
     */
    public int getM_iHomeHatStats() {
        return m_iHomeHatStats;
    }

    /**
     * Setter for property m_iHomeHatStats
     */
    public void setHomeHatStats() {
        int m_iHomeHatStats = this.m_iHomeLeftDef + this.m_iHomeMidDef + this.m_iHomeRightDef;
        m_iHomeHatStats += this.m_iHomeMidfield * 3;
        m_iHomeHatStats += this.m_iHomeLeftAtt + this.m_iHomeMidAtt + this.m_iHomeRightAtt;
        this.m_iHomeHatStats = m_iHomeHatStats;
    }

    /**
     * Setter for property m_iHomeTacticSkill.
     *
     * @param m_iHomeTacticSkill New value of property m_iHomeTacticSkill.
     */
    public final void setHomeTacticSkill(int m_iHomeTacticSkill) {
        this.m_iHomeTacticSkill = m_iHomeTacticSkill;
    }

    /**
     * Getter for property m_iHomeTacticSkill.
     *
     * @return Value of property m_iHomeTacticSkill.
     */
    public final int getHomeTacticSkill() {
        return m_iHomeTacticSkill;
    }

    /**
     * Setter for property m_iHomeTacticType.
     *
     * @param m_iHomeTacticType New value of property m_iHomeTacticType.
     */
    public final void setHomeTacticType(int m_iHomeTacticType) {
        this.m_iHomeTacticType = m_iHomeTacticType;
    }

    /**
     * Getter for property m_iHomeTacticType.
     *
     * @return Value of property m_iHomeTacticType.
     */
    public final int getHomeTacticType() {
        return m_iHomeTacticType;
    }

    /**
     * Method that extract a lineup from the full match comment
     *
     * @param home true for home lineup, false for away
     * @return The list of last names that started the match
     */
    public final List<String> getLineup(boolean home) {
        try {
            final Pattern p = Pattern.compile(".-.-.");
            final Matcher m = p.matcher(m_sMatchreport);
            int start = m.end();
            start = m_sMatchreport.indexOf(":", start);

            final int end = m_sMatchreport.indexOf(".", start);
            final List<String> lineup = new ArrayList<>();

            final String[] zones = m_sMatchreport.substring(start + 1, end).split(" - ");

            for (String zone : zones) {
                final String[] pNames = zone.split(",");

                lineup.addAll(Arrays.asList(pNames));
            }

            return lineup;
        } catch (RuntimeException e) {
            return new ArrayList<>();
        }
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

    /**
     * Setter for property m_iMatchID.
     *
     * @param m_iMatchID New value of property m_iMatchID.
     */
    public final void setMatchID(int m_iMatchID) {
        this.m_iMatchID = m_iMatchID;
    }

    public final void setMatchType(MatchType m_MatchTyp) {
        this.m_MatchTyp = m_MatchTyp;
        this.sourceSystem = m_MatchTyp.getSourceSystem();
    }

    /**
     * Getter for property m_iMatchID.
     *
     * @return Value of property m_iMatchID.
     */
    public final int getMatchID() {
        return m_iMatchID;
    }

    public final MatchType getMatchType() {
        return m_MatchTyp;
    }

    /**
     * Setter for property m_sMatchreport.
     *
     * @param m_sMatchreport New value of property m_sMatchreport.
     */
    public final void setMatchreport(java.lang.String m_sMatchreport) {
        this.m_sMatchreport = m_sMatchreport;
    }

    /**
     * Getter for property m_sMatchreport.
     *
     * @return Value of property m_sMatchreport.
     */
    public final java.lang.String getMatchreport() {
        return m_sMatchreport;
    }

    /**
     * Setter for property m_clSpielDatum.
     *
     * @param m_clSpielDatum New value of property m_clSpielDatum.
     */
    public final void setSpielDatum(java.sql.Timestamp m_clSpielDatum) {
        this.m_clSpielDatum = m_clSpielDatum;
    }

    /**
     * Getter for property m_clSpielDatum.
     *
     * @return Value of property m_clSpielDatum.
     */
    public final java.sql.Timestamp getMatchDate() {
        return m_clSpielDatum;
    }

    /**
     * Getter for property m_lDatum.
     *
     * @return Value of property m_lDatum.
     */
    public final boolean setSpielDatumFromString(String date) {
        m_clSpielDatum = Basics.parseHattrickDate(date);
        return m_clSpielDatum != null;
    }

    private MatchLineupTeam teamLineup;

    /**
     * Load the lineup of user's team
     *
     * @return MatchLineupTeam
     */
    public MatchLineupTeam getTeamLineup() {
        if (teamLineup == null) {
            teamLineup = DBManager.instance().getMatchLineupTeam(this.getMatchType().getId(), this.getMatchID(), MatchKurzInfo.user_team_id);
        }
        return teamLineup;
    }

    /**
     * Setter for property m_iWetterId.
     *
     * @param m_iWetterId New value of property m_iWetterId.
     */
    public final void setWetterId(int m_iWetterId) {
        this.m_iWetterId = m_iWetterId;
    }

    /**
     * Getter for property m_iWetterId.
     *
     * @return Value of property m_iWetterId.
     */
    public final int getWetterId() {
        return m_iWetterId;
    }

    /**
     * Setter for property m_iZuschauer.
     *
     * @param m_iZuschauer New value of property m_iZuschauer.
     */
    public final void setZuschauer(int m_iZuschauer) {
        this.m_iZuschauer = m_iZuschauer;
    }

    /**
     * Getter for property m_iZuschauer.
     *
     * @return Value of property m_iZuschauer.
     */
    public final int getZuschauer() {
        return m_iZuschauer;
    }

    /**
     * check if numeric
     */
    protected static boolean isNumeric(String aText) {
        for (int k = 0; k < aText.length(); k++) {
            if (!((aText.charAt(k) >= '0') && (aText.charAt(k) <= '9'))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Set the region ID of the match's arena
     *
     * @param regionId region Id
     */
    public void setRegionId(int regionId) {
        this.m_iRegionId = regionId;
    }

    /**
     * Get the region ID of this match's arena
     *
     * @return int region Id
     */
    public int getRegionId() {
        return m_iRegionId;
    }

    /**
     * @return the soldTerraces
     */
    public int getSoldTerraces() {
        return soldTerraces;
    }

    /**
     * @param soldTerraces the soldTerraces to set
     */
    public void setSoldTerraces(int soldTerraces) {
        this.soldTerraces = soldTerraces;
    }

    /**
     * @return the soldBasic
     */
    public int getSoldBasic() {
        return soldBasic;
    }

    /**
     * @param soldBasic the soldBasic to set
     */
    public void setSoldBasic(int soldBasic) {
        this.soldBasic = soldBasic;
    }

    /**
     * @return the soldRoof
     */
    public int getSoldRoof() {
        return soldRoof;
    }

    /**
     * @param soldRoof the soldRoof to set
     */
    public void setSoldRoof(int soldRoof) {
        this.soldRoof = soldRoof;
    }

    /**
     * @return the soldVIP
     */
    public int getSoldVIP() {
        return soldVIP;
    }

    /**
     * @param soldVIP the soldVIP to set
     */
    public void setSoldVIP(int soldVIP) {
        this.soldVIP = soldVIP;
    }


    public void setStatisics() {
        this.setHomeHatStats();
        this.setGuestHatStats();
    }

    public final int getHomeHatStats() {
        return this.m_iHomeHatStats;
    }

    public final int getAwayHatStats() {
        return this.m_iGuestHatStats;
    }

    public final double getHomeLoddarStats() {
        final double MIDFIELD_SHIFT = 0.0;
        final double COUNTERATTACK_WEIGHT = 0.25;
        final double DEFENSE_WEIGHT = 0.47;
        final double ATTACK_WEIGHT = 1 - DEFENSE_WEIGHT;
        final double CENTRAL_WEIGHT = 0.37;
        final double WINGER_WEIGTH = (1 - CENTRAL_WEIGHT) / 2d;

        double correctedCentralWeigth = CENTRAL_WEIGHT;

        switch (getHomeTacticType()) {
            case IMatchDetails.TAKTIK_MIDDLE:
                correctedCentralWeigth = CENTRAL_WEIGHT + (((0.2 * (getHomeTacticSkill() - 1)) / 19d) + 0.2);
                break;

            case IMatchDetails.TAKTIK_WINGS:
                correctedCentralWeigth = CENTRAL_WEIGHT - (((0.2 * (getHomeTacticSkill() - 1)) / 19d) + 0.2);
                break;

            default:
                break;
        }

        final double correctedWingerWeight = (1 - correctedCentralWeigth) / 2d;

        double counterCorrection = 0;

        if (getHomeTacticType() == IMatchDetails.TAKTIK_KONTER) {
            counterCorrection = (COUNTERATTACK_WEIGHT * 2 * getHomeTacticSkill()) / (getHomeTacticSkill() + 20);
        }

        // Calculate attack rating
        final double attackStrength = (ATTACK_WEIGHT + counterCorrection) * ((correctedCentralWeigth * hq(getHomeMidAtt()))
                + (correctedWingerWeight * (hq(getHomeLeftAtt()) + hq(getHomeRightAtt()))));

        // Calculate defense rating
        final double defenseStrength = DEFENSE_WEIGHT * ((CENTRAL_WEIGHT * hq(getHomeMidDef()))
                + (WINGER_WEIGTH * (hq(getHomeLeftDef()) + hq(getHomeRightDef()))));

        // Calculate midfield rating
        final double midfieldFactor = MIDFIELD_SHIFT + ((1 - MIDFIELD_SHIFT) * hq(getHomeMidfield()));

        // Calculate and return the LoddarStats rating
        return 80 * midfieldFactor * (defenseStrength + attackStrength);
    }

    public final double getAwayLoddarStats() {
        final double MIDFIELD_SHIFT = 0.0;
        final double COUNTERATTACK_WEIGHT = 0.25;
        final double DEFENSE_WEIGHT = 0.47;
        final double ATTACK_WEIGHT = 1 - DEFENSE_WEIGHT;
        final double CENTRAL_WEIGHT = 0.37;
        final double WINGER_WEIGTH = (1 - CENTRAL_WEIGHT) / 2d;

        double correctedCentralWeigth = CENTRAL_WEIGHT;

        switch (getGuestTacticType()) {
            case IMatchDetails.TAKTIK_MIDDLE:
                correctedCentralWeigth = CENTRAL_WEIGHT + (((0.2 * (getGuestTacticSkill() - 1)) / 19d) + 0.2);
                break;

            case IMatchDetails.TAKTIK_WINGS:
                correctedCentralWeigth = CENTRAL_WEIGHT - (((0.2 * (getGuestTacticSkill() - 1)) / 19d) + 0.2);
                break;

            default:
                break;
        }

        final double correctedWingerWeight = (1 - correctedCentralWeigth) / 2d;

        double counterCorrection = 0;

        if (getGuestTacticType() == IMatchDetails.TAKTIK_KONTER) {
            counterCorrection = (COUNTERATTACK_WEIGHT * 2 * getGuestTacticSkill()) / (getGuestTacticSkill() + 20);
        }

        // Calculate attack rating
        final double attackStrength = (ATTACK_WEIGHT + counterCorrection) * ((correctedCentralWeigth * hq(getGuestMidAtt()))
                + (correctedWingerWeight * (hq(getGuestLeftAtt()) + hq(getGuestRightAtt()))));

        // Calculate defense rating
        final double defenseStrength = DEFENSE_WEIGHT * ((CENTRAL_WEIGHT * hq(getGuestMidDef()))
                + (WINGER_WEIGTH * (hq(getGuestLeftDef()) + hq(getGuestRightDef()))));

        // Calculate midfield rating
        final double midfieldFactor = MIDFIELD_SHIFT + ((1 - MIDFIELD_SHIFT) * hq(getGuestMidfield()));

        // Calculate and return the LoddarStats rating
        return 80 * midfieldFactor * (defenseStrength + attackStrength);
    }

    private double hq(double value) {
        return (2 * value) / (value + 80);
    }

    private Boolean isWalkoverMatchWin;

    // return true, if the opponent team didn't appear. The match was won by 5-0
    public boolean isWalkoverMatchWin(int teamId) {
        if (isWalkoverMatchWin == null) {
            isWalkoverMatchWin = false;
            if (getLastMinute() == 0) {
                // Duration of walk over matches is 0 minutes
                for (var e : getHighlights()) {
                    if (e.getMatchEventID() == MatchEvent.MatchEventID.AWAY_TEAM_WALKOVER) {
                        if (this.m_iHeimId == teamId) {
                            isWalkoverMatchWin = true;
                        }
                        break;
                    } else if (e.getMatchEventID() == MatchEvent.MatchEventID.HOME_TEAM_WALKOVER) {
                        if (this.m_iGastId == teamId) {
                            isWalkoverMatchWin = true;
                        }
                        break;
                    }
                }
            }
        }
        return isWalkoverMatchWin;
    }
}