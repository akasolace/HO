package core.training;

import core.constants.TrainingType;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.enums.DBDataSource;
import core.model.match.MatchKurzInfo;
import core.model.enums.MatchType;
import core.util.DateTimeUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

/**
 * Class that holds all information required to calculate training effect of a given week
 * (e.g. training intensity, stamina part, assistant level, played games ...)
 */
public class TrainingPerWeek  {

    private final static int myClubID = HOVerwaltung.instance().getModel().getBasics().getTeamId();

    private int o_TrainingIntensity;
    private int o_StaminaShare;
    private int o_TrainingType;
    private int o_CoachLevel;
    private int o_TrainingAssistantsLevel;
    private Instant o_TrainingDate;
    private MatchKurzInfo[] o_Matches;
    private MatchKurzInfo[] o_NTmatches;
    private DBDataSource o_Source;
    private boolean o_includeMatches;


    /**
     *
     * Constructor, matches are not passsed as parameters but are loaded at object creation
     */
    public TrainingPerWeek(Instant trainingDate, int trainingType, int trainingIntensity, int staminaShare, int trainingAssistantsLevel, int coachLevel, DBDataSource source, boolean o_includeMatches) {
        o_TrainingDate = trainingDate;
        o_TrainingType = trainingType;
        o_TrainingIntensity = trainingIntensity;
        o_StaminaShare = staminaShare;
        o_CoachLevel = coachLevel;
        o_TrainingAssistantsLevel = trainingAssistantsLevel;
        o_Source = source;

        if (o_includeMatches) {
            loadMatches();
        }
    }

    public TrainingPerWeek(Instant trainingDate, int trainingType, int trainingIntensity, int staminaShare, int trainingAssistantsLevel, int coachLevel, DBDataSource source) {
        this(trainingDate, trainingType, trainingIntensity, staminaShare, trainingAssistantsLevel, coachLevel, source, false);
    }


    public TrainingPerWeek(Instant trainingDate, int training_type, int training_intensity, int staminaShare, int trainingAssistantsLevel, int coachLevel) {
        this(trainingDate,training_type,training_intensity,staminaShare,trainingAssistantsLevel,coachLevel, DBDataSource.GUESS);
    }

    public void addMatchesInfo(){
        var _startDate = o_TrainingDate.minus(7, ChronoUnit.DAYS);
        String _firstMatchDate = DateTimeUtils.InstantToSQLtimeStamp(_startDate);
        String _lastMatchDate = DateTimeUtils.InstantToSQLtimeStamp(o_TrainingDate.plus(23, ChronoUnit.HOURS));
        o_Matches = fetchMatches(_firstMatchDate, _lastMatchDate);
        o_NTmatches = fetchNTMatches(_firstMatchDate, _lastMatchDate);
    }

    /**
     * function that fetch info of NT match played related to the TrainingPerWeek instance
     * @return MatchKurzInfo[] related to this TrainingPerWeek instance
     */
    private MatchKurzInfo[] fetchNTMatches(String firstMatchDate, String lastMatchDate) {

        var matchTypes= MatchType.getNTMatchType();

        String sOfficialMatchType = matchTypes.stream().map(m -> m.getId()+"").collect(Collectors.joining(","));

        final String where = String.format("WHERE MATCHDATE BETWEEN '%s' AND '%s' AND MATCHTYP in (%s) AND STATUS=%s ORDER BY MatchDate DESC",
                firstMatchDate, lastMatchDate, sOfficialMatchType, MatchKurzInfo.FINISHED);

        return DBManager.instance().getMatchesKurzInfo(where);
    }


    /**
     * function that fetch info of match played related to the TrainingPerWeek instance
     * @return MatchKurzInfo[] related to this TrainingPerWeek instance
     */
    private MatchKurzInfo[] fetchMatches(String firstMatchDate, String lastMatchDate) {


        var matchTypes= MatchType.getOfficialMatchType();
        String sOfficialMatchType = matchTypes.stream().map(m -> m.getId()+"").collect(Collectors.joining(","));

        final String where = String.format("WHERE (HEIMID = %s OR GASTID = %s) AND MATCHDATE BETWEEN '%s' AND '%s' AND MATCHTYP in (%s) AND STATUS in (%s, %s) ORDER BY MatchDate DESC",
                    myClubID, myClubID, firstMatchDate, lastMatchDate, sOfficialMatchType, MatchKurzInfo.FINISHED, MatchKurzInfo.UPCOMING);


        return DBManager.instance().getMatchesKurzInfo(where);
    }

    public MatchKurzInfo[] getMatches() {
        return o_Matches;
    }

    public MatchKurzInfo[] getNTmatches() {
        return o_NTmatches;
    }

    public Instant getTrainingDate() {
        return o_TrainingDate;
    }

    public void setTrainingDate(Instant trainingDate) {
        o_TrainingDate = trainingDate;
    }

    public final int getStaminaShare() {
        return o_StaminaShare;
    }

    public final void setStaminaPart(int staminaPart) {
        o_StaminaShare = staminaPart;
    }


    public final int getTrainingIntensity() {
        return o_TrainingIntensity;
    }

    public final void setTrainingIntensity(int trainingIntensity) {
        o_TrainingIntensity = trainingIntensity;
    }

    public final int getTrainingType() {
        return o_TrainingType;
    }

    public final void setTrainingType(int trainingType) {
        o_TrainingType = trainingType;
    }

	public int getTrainingAssistantsLevel() {
		return o_TrainingAssistantsLevel;
	}

	public int getCoachLevel(){return o_CoachLevel;}

    public DBDataSource getSource() {
        return o_Source;
    }

    public Integer getSourceAsInt() {
        return o_Source.getValue();
    }

    public void setSource(DBDataSource source) {
        this.o_Source = source;
    }

    @Override
    public final String toString() {
        return "TrainingPerWeek[" +
                "Training date: " + DateTimeUtils.InstantToSQLtimeStamp(o_TrainingDate) +
                ", Training Type: " + TrainingType.toString(o_TrainingType)  +
                "%, Intensity: " + o_TrainingIntensity +
                "%, StaminaPart: " + o_StaminaShare +
                "]";
    }


    public void setStaminaShare(int staminaShare) {
        this.o_StaminaShare=staminaShare;
    }

    public void setCoachLevel(int coachLevel) {
        this.o_CoachLevel=coachLevel;
    }

    public void setTrainingAssistantLevel(int trainingAssistantsLevel) {
        this.o_TrainingAssistantsLevel=trainingAssistantsLevel;
    }

    public void loadMatches() {
        // Loading matches played the week preceding the training date --------------------------
        var _startDate = o_TrainingDate.minus(7, ChronoUnit.DAYS);
        String _firstMatchDate = DateTimeUtils.InstantToSQLtimeStamp(_startDate);
        String _lastMatchDate = DateTimeUtils.InstantToSQLtimeStamp(o_TrainingDate.plus(23, ChronoUnit.HOURS));
        o_Matches = fetchMatches(_firstMatchDate, _lastMatchDate);
        o_NTmatches = fetchNTMatches(_firstMatchDate, _lastMatchDate);
    }

    public boolean skillDropDayIsBetween(Instant from, Instant to) {
        var skillDropDay = o_TrainingDate.minus(Duration.ofHours(7*12)); // half week. TODO: check exact time difference
        return from.isBefore(skillDropDay) && !to.isBefore(skillDropDay);
    }

    private String getSqlBetween()
    {
        var _startDate = o_TrainingDate.minus(7, ChronoUnit.DAYS);
        String _firstMatchDate = DateTimeUtils.InstantToSQLtimeStamp(_startDate);
        String _lastMatchDate = DateTimeUtils.InstantToSQLtimeStamp(o_TrainingDate.plus(23, ChronoUnit.HOURS));
        return "MATCHDATE BETWEEN " + _firstMatchDate + " AND " + _lastMatchDate;
    }

    /**
     * Load matches of natial team players in own team, during training week
     * (used for experience calculation)
     *
     * @param nationalTeamID id of player's national team (it is NOT own team id)
     * @return MatchKurzInfo array
     */
    public MatchKurzInfo[] loadMatchesOfNTPlayers(int nationalTeamID) {
        final String where = "WHERE (HEIMID=" + nationalTeamID + " OR GASTID=" + nationalTeamID + ") AND " +
                getSqlBetween() + " ORDER BY MatchDate DESC";
        return DBManager.instance().getMatchesKurzInfo(where);
    }
}
