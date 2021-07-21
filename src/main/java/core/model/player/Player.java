package core.model.player;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.constants.player.PlayerSpeciality;
import core.constants.player.Speciality;
import core.db.DBManager;
import core.model.FactorObject;
import core.model.FormulaFactors;
import core.model.HOVerwaltung;
import core.model.match.MatchLineupTeam;
import core.model.enums.MatchType;
import core.model.match.Weather;
import core.model.misc.TrainingEvent;
import core.net.OnlineWorker;
import core.rating.RatingPredictionManager;
import core.training.*;
import core.util.HOLogger;
import core.util.Helper;
import core.util.HelperWrapper;
import module.training.Skills;
import org.jetbrains.annotations.Nullable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static java.lang.Integer.min;
import static core.constants.player.PlayerSkill.*;

public class Player {


    /**
     * Cache for player contribution (Hashtable<String, Float>)
     */
    private static Hashtable<String, Object> PlayerAbsoluteContributionCache = new Hashtable<>();
    private static Hashtable<String, Object> PlayerRelativeContributionCache = new Hashtable<>();
    private byte idealPos = IMatchRoleID.UNKNOWN;
    private static final String BREAK = "[br]";
    private static final String O_BRACKET = "[";
    private static final String C_BRACKET = "]";
    private static final String EMPTY = "";



    /**
     * canPlay
     */
    private Boolean m_bCanBeSelectedByAssistant;

    /**
     * Manual Smilie Filename
     */
    private String m_sManuellerSmilie;

    /**
     * Name
     */
    private String m_sFirstName = "";
    private String m_sNickName = "";
    private String m_sLastName = "";

    /**
     * TeamInfo Smilie Filename
     */
    private String m_sTeamInfoSmilie;
    private java.sql.Timestamp m_clhrfDate;

    /**
     * Date of the first HRF referencing that player
     */
    private Timestamp m_tsTime4FirstHRF;

    /**
     * The player is no longer available in the current HRF
     */
    private boolean m_bOld;
    private byte m_bUserPosFlag = -2;

    /**
     * Wing skill
     */
    private double m_dSubFluegelspiel;

    /**
     * Pass skill
     */
    private double m_dSubPasspiel;

    /**
     * Playmaking skill
     */
    private double m_dSubSpielaufbau;

    /**
     * Standards
     */
    private double m_dSubStandards;

    /**
     * Goal
     */
    private double m_dSubTorschuss;

    //Subskills
    private double m_dSubTorwart;

    /**
     * Verteidigung
     */
    private double m_dSubVerteidigung;

    /**
     * Agressivität
     */
    private int m_iAgressivitaet;

    /**
     * Alter
     */
    private int m_iAlter;

    /**
     * Age Days
     */
    private int m_iAgeDays;

    /**
     * Ansehen (ekel usw. )
     */
    private int m_iAnsehen = 1;

    /**
     * Bewertung
     */
    private int m_iBewertung;

    /**
     * charakter ( ehrlich)
     */
    private int m_iCharakter = 1;

    /**
     * Erfahrung
     */
    private int m_iErfahrung = 1;

    /**
     * Fluegelspiel
     */
    private int m_iFluegelspiel = 1;

    /**
     * Form
     */
    private int m_iForm = 1;

    /**
     * Führungsqualität
     */
    private int m_iFuehrung = 1;

    /**
     * Gehalt
     */
    private int m_iGehalt = 1;

    /**
     * Gelbe Karten
     */
    private int m_iCards;

    /**
     * Hattricks
     */
    private int m_iHattrick;

    /**
     * Home Grown
     */
    private boolean m_bHomeGrown = false;

    /**
     * Kondition
     */
    private int m_iKondition = 1;

    /**
     * Länderspiele
     */
    private int m_iLaenderspiele;

    //Cache

    /**
     * Letzte Bewertung
     */
    private int m_iLastBewertung = -1;

    /**
     * Loyalty
     */
    private int m_iLoyalty = 0;

    /**
     * Markwert
     */
    private int m_iTSI;

    /* bonus in Prozent */

    //protected int       m_iBonus            =   0;

    /**
     * Aus welchem Land kommt der Player
     */
    private int m_iNationalitaet = 49;

    /**
     * Passpiel
     */
    private int m_iPasspiel = 1;

    /**
     * SpezialitätID
     */
    private int iPlayerSpecialty;

    /**
     * Spielaufbau
     */
    private int m_iSpielaufbau = 1;

    ////////////////////////////////////////////////////////////////////////////////
    //Member
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * SpielerID
     */
    private int m_iSpielerID;

    /**
     * Standards
     */
    private int m_iStandards = 1;

    /**
     * Tore Freundschaftspiel
     */
    private int m_iToreFreund;

    /**
     * Tore Gesamt
     */
    private int m_iToreGesamt;

    /**
     * Tore Liga
     */
    private int m_iToreLiga;

    /**
     * Tore Pokalspiel
     */
    private int m_iTorePokal;

    /**
     * Torschuss
     */
    private int m_iTorschuss = 1;

    /**
     * Torwart
     */
    private int m_iTorwart = 1;

    /**
     * Trainerfähigkeit
     */
    private int m_iTrainer;

    /**
     * Trainertyp
     */
    private int m_iTrainerTyp = -1;

    /**
     * Transferlisted
     */
    private int m_iTransferlisted;

    //TODO Noch in DB adden

    /**
     * Fetchdate
     */

    //    protected Timestamp m_clFetchDate       =   new Timestamp( System.currentTimeMillis () );

    /*TrikotNummer*/
    private int m_iTrikotnummer = -1;

    /**
     * Länderspiele
     */
    private int m_iU20Laenderspiele;

    /**
     * Verletzt Wochen
     */
    private int m_iInjuryWeeks = -1;

    /**
     * Verteidigung
     */
    private int m_iVerteidigung = 1;

    /**
     * Training block
     */
    private boolean m_bTrainingBlock = false;

    // LastMAtch
    private String m_lastMatchDate;
    private double m_lastMatchRating=0;
    private int m_lastMatchId=0;
    private MatchType lastMatchType;


    /**
     * specifying at what time –in minutes- that player entered the field
     * This parameter is only used by RatingPredictionManager to calculate the stamina effect
     * along the course of the game
     */
    private int GameStartingTime = 0;
    private int nationalTeamId=0;
    private double subExperience;

    /**
     * future training priorities planed by the user
     */
    private List<FuturePlayerTraining> futurePlayerTrainings;

    public int getGameStartingTime() {
        return GameStartingTime;
    }

    public void setGameStartingTime(int gameStartingTime) {
        GameStartingTime = gameStartingTime;
    }

    //~ Constructors -------------------------------------------------------------------------------


    /**
     * Creates a new instance of Player
     */
    public Player() {
    }

    /**
     * Erstellt einen Player aus den Properties einer HRF Datei
     */
    public Player(java.util.Properties properties, Timestamp hrfdate) {
        // Separate first, nick and last names are available. Utilize them?

        m_iSpielerID = Integer.parseInt(properties.getProperty("id", "0"));
        m_sFirstName = properties.getProperty("firstname", "");
        m_sNickName = properties.getProperty("nickname", "");
        m_sLastName = properties.getProperty("lastname", "");
        m_iAlter = Integer.parseInt(properties.getProperty("ald", "0"));
        m_iAgeDays = Integer.parseInt(properties.getProperty("agedays", "0"));
        m_iKondition = Integer.parseInt(properties.getProperty("uth", "0"));
        m_iForm = Integer.parseInt(properties.getProperty("for", "0"));
        m_iTorwart = Integer.parseInt(properties.getProperty("mlv", "0"));
        m_iVerteidigung = Integer.parseInt(properties.getProperty("bac", "0"));
        m_iSpielaufbau = Integer.parseInt(properties.getProperty("spe", "0"));
        m_iPasspiel = Integer.parseInt(properties.getProperty("fra", "0"));
        m_iFluegelspiel = Integer.parseInt(properties.getProperty("ytt", "0"));
        m_iTorschuss = Integer.parseInt(properties.getProperty("mal", "0"));
        m_iStandards = Integer.parseInt(properties.getProperty("fas", "0"));
        iPlayerSpecialty = Integer.parseInt(properties.getProperty("speciality", "0"));
        m_iCharakter = Integer.parseInt(properties.getProperty("gentleness", "0"));
        m_iAnsehen = Integer.parseInt(properties.getProperty("honesty", "0"));
        m_iAgressivitaet = Integer.parseInt(properties.getProperty("aggressiveness", "0"));
        m_iErfahrung = Integer.parseInt(properties.getProperty("rut", "0"));
        m_bHomeGrown = Boolean.parseBoolean(properties.getProperty("homegr", "FALSE"));
        m_iLoyalty = Integer.parseInt(properties.getProperty("loy", "0"));
        m_iFuehrung = Integer.parseInt(properties.getProperty("led", "0"));
        m_iGehalt = Integer.parseInt(properties.getProperty("sal", "0"));
        m_iNationalitaet = Integer.parseInt(properties.getProperty("countryid", "0"));
        m_iTSI = Integer.parseInt(properties.getProperty("mkt", "0"));

        // also read subskills when importing hrf from hattrickportal.pro/ useful for U20/NT
        m_dSubFluegelspiel = Double.parseDouble(properties.getProperty("yttsub", "0"));
        m_dSubPasspiel = Double.parseDouble(properties.getProperty("frasub", "0"));
        m_dSubSpielaufbau = Double.parseDouble(properties.getProperty("spesub", "0"));
        m_dSubStandards = Double.parseDouble(properties.getProperty("fassub", "0"));
        m_dSubTorschuss = Double.parseDouble(properties.getProperty("malsub", "0"));
        m_dSubTorwart = Double.parseDouble(properties.getProperty("mlvsub", "0"));
        m_dSubVerteidigung = Double.parseDouble(properties.getProperty("bacsub", "0"));
        subExperience = Double.parseDouble(properties.getProperty("experiencesub", "0"));

        //TSI, alles vorher durch 1000 teilen
        m_clhrfDate = hrfdate;

        if (hrfdate.before(DBManager.TSIDATE)) {
            m_iTSI /= 1000d;
        }

        m_iCards = Integer.parseInt(properties.getProperty("warnings", "0"));
        m_iInjuryWeeks = Integer.parseInt(properties.getProperty("ska", "0"));
        m_iToreFreund = Integer.parseInt(properties.getProperty("gtt", "0"));
        m_iToreLiga = Integer.parseInt(properties.getProperty("gtl", "0"));
        m_iTorePokal = Integer.parseInt(properties.getProperty("gtc", "0"));
        m_iToreGesamt = Integer.parseInt(properties.getProperty("gev", "0"));
        m_iHattrick = Integer.parseInt(properties.getProperty("hat", "0"));

        if (properties.get("rating") != null) {
            m_iBewertung = Integer.parseInt(properties.getProperty("rating", "0"));
        }

        String temp = properties.getProperty("trainertype", "-1");

        if ((temp != null) && !temp.equals("")) {
            m_iTrainerTyp = Integer.parseInt(temp);
        }

        temp = properties.getProperty("trainerskill", "0");

        if ((temp != null) && !temp.equals("")) {
            m_iTrainer = Integer.parseInt(temp);
        }

        temp = properties.getProperty("playernumber", "");

        if ((temp != null) && !temp.equals("") && !temp.equals("null")) {
            m_iTrikotnummer = Integer.parseInt(temp);
        }

        m_iTransferlisted = Boolean.parseBoolean(properties.getProperty("transferlisted", "False"))?1:0;
        m_iLaenderspiele = Integer.parseInt(properties.getProperty("caps", "0"));
        m_iU20Laenderspiele = Integer.parseInt(properties.getProperty("capsU20", "0"));
        nationalTeamId = Integer.parseInt(properties.getProperty("nationalTeamID","0"));

        // #461-lastmatch
        m_lastMatchDate =  properties.getProperty("lastmatch_date");
        if(m_lastMatchDate!=null) {
            m_lastMatchRating = 2*Double.parseDouble(properties.getProperty("lastmatch_rating", "0"));
            m_lastMatchId = Integer.parseInt(properties.getProperty("lastmatch_id","0"));
        }

        setLastMatchType(MatchType.getById(
                Integer.parseInt(properties.getProperty("lastmatch_type", "0"))
        ));

        //Subskills calculation
        //Called when saving the HRF because the necessary data is not available here
        final core.model.HOModel oldmodel = core.model.HOVerwaltung.instance().getModel();
        final Player oldPlayer = oldmodel.getCurrentPlayer(m_iSpielerID);
        if (oldPlayer != null) {
            // Training block
            m_bTrainingBlock = oldPlayer.hasTrainingBlock();
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_iAgressivitaet.
     *
     * @param m_iAgressivitaet New value of property m_iAgressivitaet.
     */
    public void setAgressivitaet(int m_iAgressivitaet) {
        this.m_iAgressivitaet = m_iAgressivitaet;
    }

    /**
     * Getter for property m_iAgressivitaet.
     *
     * @return Value of property m_iAgressivitaet.
     */
    public int getAgressivitaet() {
        return m_iAgressivitaet;
    }

    /**
     * gives information of skill ups
     * returns vector of
     * object[]
     *      [0] = date of skill up
     *      [1] = Boolean: false=no skill up found
     *      [2] = skill value
     */
    public Vector<Object[]> getAllLevelUp(int skill) {
        return DBManager.instance().getAllLevelUp(skill, m_iSpielerID);
    }

    /**
     * Setter for property m_iAlter.
     *
     * @param m_iAlter New value of property m_iAlter.
     */
    public void setAlter(int m_iAlter) {
        this.m_iAlter = m_iAlter;
    }

    /**
     * Getter for property m_iAlter.
     *
     * @return Value of property m_iAlter.
     */
    public int getAlter() {
        return m_iAlter;
    }

    /**
     * Setter for property m_iAgeDays.
     *
     * @param m_iAgeDays New value of property m_iAgeDays.
     */
    public void setAgeDays(int m_iAgeDays) {
        this.m_iAgeDays = m_iAgeDays;
    }

    /**
     * Getter for property m_iAgeDays.
     *
     * @return Value of property m_iAgeDays.
     */
    public int getAgeDays() {
        return m_iAgeDays;
    }

    /**
     * Calculates full age with days and offset
     *
     * @return Double value of age & agedays & offset combined,
     * i.e. age + (agedays+offset)/112
     */
    public double getAlterWithAgeDays() {
        long hrftime = HOVerwaltung.instance().getModel().getBasics().getDatum().getTime();
        long now = new Date().getTime();
        long diff = (now - hrftime) / (1000 * 60 * 60 * 24);
        int years = getAlter();
        int days = getAgeDays();
        return years + (double) (days + diff) / 112;
    }


    /**
     * Calculates full age with days and offset for a given timestamp
     * used to sort columns
     * pay attention that it takes the hour and minute of the matchtime into account
     * if you only want the days between two days use method calendarDaysBetween(Calendar start, Calendar end)
     *
     * @return Double value of age & agedays & offset combined,
     * i.e. age + (agedays+offset)/112
     */
    public double getDoubleAgeFromDate(Timestamp t) {
        long hrftime = HOVerwaltung.instance().getModel().getBasics().getDatum().getTime();
        long time = t.getTime();
        long diff = Math.abs((hrftime - time)) / (1000 * 60 * 60 * 24);
        int years = getAlter();
        int days = getAgeDays();
        return years + (double) (days - diff) / 112;
    }

    /**
     * Calculates String for full age and days correcting for the difference between (now and last HRF file)
     *
     * @return String of age & agedays format is "YY (DDD)"
     */
    public String getAlterWithAgeDaysAsString() {
        return getAgeWithDaysAsString(new Date());
    }

    public String getAgeWithDaysAsString(Date date) {
        return getAgeWithDaysAsString(date.getTime());
    }

    private String getAgeWithDaysAsString(long now)
    {
        return getAgeWithDaysAsString(getAlter(), getAgeDays(), now);
    }

    public static String getAgeWithDaysAsString(int years, int days, long now){
        long hrftime = HOVerwaltung.instance().getModel().getBasics().getDatum().getTime();
        long diff = (now - hrftime) / (1000 * 60 * 60 * 24);
        days += diff;
        while (days > 111) {
            days -= 112;
            years++;
        }
        while (days < 0) {
            days += 112;
            years--;
        }
        return years + " (" + days + ")";
    }

    /**
     * Calculates String for full age with days and offset for a given timestamp
     * only takes days between dates into account
     * used for the age column in player analysis tab
     *
     * @return String of age & agedays & offset combined,
     * format is "YY (DDD)"
     */
    public String getAdjustedAgeFromDate(Timestamp t) {
        return getAgeWithDaysAsString(t.getTime());
    }

    /**
     * Get the full i18n'd string representing the player's age. Includes
     * the birthday indicator as well.
     *
     * @return the full i18n'd string representing the player's age
     */
    public String getAgeStringFull() {
        long hrftime = HOVerwaltung.instance().getModel().getBasics().getDatum().getTime();
        long now = new Date().getTime();
        long diff = (now - hrftime) / (1000 * 60 * 60 * 24);
        int years = getAlter();
        int days = getAgeDays();
        days += diff;
        boolean birthday = false;
        while (days > 111) {
            days -= 112;
            years++;
            birthday = true;
        }
        StringBuilder ret = new StringBuilder();
        ret.append(years);
        ret.append(" ");
        ret.append(HOVerwaltung.instance().getLanguageString("ls.player.age.years"));
        ret.append(" ");
        ret.append(days);
        ret.append(" ");
        ret.append(HOVerwaltung.instance().getLanguageString("ls.player.age.days"));
        if (birthday) {
            ret.append(" (");
            ret.append(HOVerwaltung.instance().getLanguageString("ls.player.age.birthday"));
            ret.append(")");
        }
        return ret.toString();
    }

    /**
     * Setter for property m_iAnsehen.
     *
     * @param m_iAnsehen New value of property m_iAnsehen.
     */
    public void setAnsehen(int m_iAnsehen) {
        this.m_iAnsehen = m_iAnsehen;
    }

    /**
     * Getter for property m_iAnsehen.
     *
     * @return Value of property m_iAnsehen.
     */
    public int getAnsehen() {
        return m_iAnsehen;
    }

    /**
     * Setter for property m_iBewertung.
     *
     * @param m_iBewertung New value of property m_iBewertung.
     */
    public void setBewertung(int m_iBewertung) {
        this.m_iBewertung = m_iBewertung;
    }

    /**
     * Getter for property m_iBewertung.
     *
     * @return Value of property m_iBewertung.
     */
    public int getBewertung() {
        return m_iBewertung;
    }

    /**
     * Getter for property m_iBonus.
     *
     * @return Value of property m_iBonus.
     */
    public int getBonus() {
        int bonus = 0;

        if (m_iNationalitaet != HOVerwaltung.instance().getModel().getBasics().getLand()) {
            bonus = 20;
        }

        return bonus;
    }

    /**
     * Setter for property m_iCharakter.
     *
     * @param m_iCharakter New value of property m_iCharakter.
     */
    public void setCharakter(int m_iCharakter) {
        this.m_iCharakter = m_iCharakter;
    }

    /**
     * Getter for property m_iCharackter.
     *
     * @return Value of property m_iCharackter.
     */
    public int getCharakter() {
        return m_iCharakter;
    }

    /**
     * Setter for property m_iErfahrung.
     *
     * @param m_iErfahrung New value of property m_iErfahrung.
     */
    public void setErfahrung(int m_iErfahrung) {
        this.m_iErfahrung = m_iErfahrung;
    }

    /**
     * Getter for property m_iErfahrung.
     *
     * @return Value of property m_iErfahrung.
     */
    public int getErfahrung() {
        return m_iErfahrung;
    }

    /**
     * get the experience bonus
     *
     * @param experience effective experience to calculate the bonus, use the xp from the player if set to 0
     * @return experience bonus in percent
     */
    public float getErfahrungsBonus(float experience) {
        if (experience <= 0)
            // take xp from player (use medium xp sub, i.e. add 0.5)
            experience = m_iErfahrung + 0.5f;

        // normalize xp [1,20] -> [0,19]
        experience -= 1;

        if (experience <= 0)
            return 0; /*If experience is non-existent, the bonus is zero!*/

        // Use hardcorded values here,
        // make sure to apply the same values as in prediction/*/playerStrength.dat
        //
        // We return the experience bonus in percent (0% = no bonus, 100% = doubled player strength...)

        return (float) (0.0716 * Math.sqrt(experience));
    }

    /**
     * Setter for property m_iFluegelspiel.
     *
     * @param m_iFluegelspiel New value of property m_iFluegelspiel.
     */
    public void setFluegelspiel(int m_iFluegelspiel) {
        this.m_iFluegelspiel = m_iFluegelspiel;
    }

    /**
     * Getter for property m_iFluegelspiel.
     *
     * @return Value of property m_iFluegelspiel.
     */
    public int getWIskill() {
        return m_iFluegelspiel;
    }

    /**
     * Setter for property m_iForm.
     *
     * @param m_iForm New value of property m_iForm.
     */
    public void setForm(int m_iForm) {
        this.m_iForm = m_iForm;
    }

    /**
     * Getter for property m_iForm.
     *
     * @return Value of property m_iForm.
     */
    public int getForm() {
        return m_iForm;
    }

    /**
     * Setter for property m_iFuehrung.
     *
     * @param m_iFuehrung New value of property m_iFuehrung.
     */
    public void setFuehrung(int m_iFuehrung) {
        this.m_iFuehrung = m_iFuehrung;
    }

    /**
     * Getter for property m_iFuehrung.
     *
     * @return Value of property m_iFuehrung.
     */
    public int getFuehrung() {
        return m_iFuehrung;
    }

    /**
     * Setter for property m_iGehalt.
     *
     * @param m_iGehalt New value of property m_iGehalt.
     */
    public void setGehalt(int m_iGehalt) {
        this.m_iGehalt = m_iGehalt;
    }

    /**
     * Getter for property m_iGehalt.
     *
     * @return Value of property m_iGehalt.
     */
    public int getGehalt() {
        return m_iGehalt;
    }

    /**
     * Setter for property m_iGelbeKarten.
     *
     * @param m_iGelbeKarten New value of property m_iGelbeKarten.
     */
    public void setGelbeKarten(int m_iGelbeKarten) {
        this.m_iCards = m_iGelbeKarten;
    }

    /**
     * Getter for property m_iGelbeKarten.
     *
     * @return Value of property m_iGelbeKarten.
     */
    public int getCards() {
        return m_iCards;
    }

    /**
     * gibt an ob der spieler gesperrt ist
     */
    public boolean isRedCarded() {
        return (m_iCards > 2);
    }

    /**
     * Setter for property m_iHattrick.
     *
     * @param m_iHattrick New value of property m_iHattrick.
     */
    public void setHattrick(int m_iHattrick) {
        this.m_iHattrick = m_iHattrick;
    }

    /**
     * Getter for property m_iHattrick.
     *
     * @return Value of property m_iHattrick.
     */
    public int getHattrick() {
        return m_iHattrick;
    }

    /**
     * Setter for m_bHomeGrown
     *
     */
    public void setHomeGrown(boolean hg) {
        m_bHomeGrown = hg;
    }

    /**
     * Getter for m_bHomeGrown
     *
     * @return Value of property m_bHomeGrown
     */
    public boolean isHomeGrown() {
        return m_bHomeGrown;
    }

    public Timestamp getHrfDate() {
        if ( m_clhrfDate == null){
            m_clhrfDate = HOVerwaltung.instance().getModel().getBasics().getDatum();
        }
        return m_clhrfDate;
    }

    public void setHrfDate(Timestamp timestamp) {
        m_clhrfDate = timestamp;
    }

    public void setHrfDate() {
        Date now = new Date();
        setHrfDate(new Timestamp(now.getTime()));
    }

    /**
     * calculate the contribution for the ideal position
     */
    public float getIdealPositionStrength(boolean mitForm, @Nullable Weather weather, boolean useWeatherImpact) {
        return getIdealPositionStrength(mitForm, false, weather, useWeatherImpact);
    }


    /**
     * calculate the contribution for the ideal position
     */
    public float getIdealPositionStrength(boolean mitForm, boolean normalized, @Nullable Weather weather, boolean useWeatherImpact) {
        return getIdealPositionStrength(mitForm, normalized, 2, weather, useWeatherImpact);
    }

    /**
     * calculate the contribution for the ideal position
     */
    public float getIdealPositionStrength(boolean mitForm, boolean normalized, int nb_decimal, @Nullable Weather weather, boolean useWeatherImpact) {
        return calcPosValue(getIdealPosition(), mitForm, normalized, nb_decimal, weather, useWeatherImpact);
    }



    /**
     * Calculate Player Ideal Position (weather impact not relevant here)
     */
    public byte getIdealPosition() {
        //in case player best position is forced by user
        final byte flag = getUserPosFlag();

        if (flag == IMatchRoleID.UNKNOWN) {
            if (idealPos == IMatchRoleID.UNKNOWN) {
                final FactorObject[] allPos = FormulaFactors.instance().getAllObj();
                float maxStk = -1.0f;
                byte currPosition;
                float contrib;

                for (int i = 0; (allPos != null) && (i < allPos.length); i++) {
                    if (allPos[i].getPosition() == IMatchRoleID.FORWARD_DEF_TECH) continue;
                    currPosition = allPos[i].getPosition();
                    contrib = calcPosValue(currPosition, true, true, null, false);
                    if (contrib > maxStk) {
                        maxStk = contrib;
                        idealPos = currPosition;
                    }
                }
            }
            return idealPos;
        }

        return flag;
    }

    /**
     * Calculate Player Alternative Best Positions (weather impact not relevant here)
     */
    public byte[] getAlternativeBestPositions() {

        List<PositionContribute> positions = new ArrayList<>();
        final FactorObject[] allPos = FormulaFactors.instance().getAllObj();
        byte currPosition;
        PositionContribute currPositionContribute;

        for (int i = 0; (allPos != null) && (i < allPos.length); i++) {
            if (allPos[i].getPosition() == IMatchRoleID.FORWARD_DEF_TECH) continue;
            currPosition = allPos[i].getPosition();
            currPositionContribute = new PositionContribute(calcPosValue(currPosition, true, true, null, false), currPosition);
            positions.add(currPositionContribute);
        }

        positions.sort((PositionContribute player1, PositionContribute player2) -> Float.compare(player2.getRating(), player1.getRating()));

        byte[] alternativePositions = new byte[positions.size()];
        float tolerance = 1f - core.model.UserParameter.instance().alternativePositionsTolerance;

        int i;
        final float threshold = positions.get(0).getRating() * tolerance;

        for (i = 0; i < positions.size(); i++) {
            if (positions.get(i).getRating() >= threshold) {
                alternativePositions[i] = positions.get(i).getClPostionID();
            } else {
                break;
            }
        }

        alternativePositions = Arrays.copyOf(alternativePositions, i);

        return alternativePositions;
    }

    /**
     * return whether or not the position is one of the best position for the player
     */
   public boolean isAnAlternativeBestPosition(byte position){
       return Arrays.asList(getAlternativeBestPositions()).contains(position);
   }


    /**
     * Setter for property m_iKondition.
     *
     * @param m_iKondition New value of property m_iKondition.
     */
    public void setKondition(int m_iKondition) {
        this.m_iKondition = m_iKondition;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //Accessor
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Getter for property m_iKondition.
     *
     * @return Value of property m_iKondition.
     */
    public int getKondition() {
        return m_iKondition;
    }

    /**
     * Setter for property m_iLaenderspiele.
     *
     * @param m_iLaenderspiele New value of property m_iLaenderspiele.
     */
    public void setLaenderspiele(int m_iLaenderspiele) {
        this.m_iLaenderspiele = m_iLaenderspiele;
    }

    /**
     * Getter for property m_iLaenderspiele.
     *
     * @return Value of property m_iLaenderspiele.
     */
    public int getLaenderspiele() {
        return m_iLaenderspiele;
    }

    /**
     * liefert das Datum des letzen LevelAufstiegs für den angeforderten Skill [0] = Time der
     * Änderung [1] = Boolean: false=Keine Änderung gefunden
     */
    public Object[] getLastLevelUp(int skill) {
        return DBManager.instance().getLastLevelUp(skill, m_iSpielerID);
    }

    /**
     * liefert die vergangenen Tage seit dem letzem LevelAufstieg für den angeforderten Skill
     *
     * @return anzahl Tage seit dem letzen Aufstieg
     */
    public int getLastLevelUpInTage(int skill) {
        int tage = 0;
        final Timestamp datum = (Timestamp) getLastLevelUp(skill)[0];
        final Timestamp heute = new Timestamp(System.currentTimeMillis());
        long diff;

        if (datum != null) {
            diff = heute.getTime() - datum.getTime();

            //In Tage umrechnen
            tage = (int) (diff / 86400000);
        }

        return tage;
    }

    /**
     * Gibt die Letzte Bewertung zurück, die der Player bekommen hat
     */
    public int getLetzteBewertung() {
        if (m_iLastBewertung < 0) {
            m_iLastBewertung = DBManager.instance().getLetzteBewertung4Spieler(m_iSpielerID);
        }

        return m_iLastBewertung;
    }


    /**
     * Returns the loyalty stat
     */
    public int getLoyalty() {
        return m_iLoyalty;
    }

    /**
     * Sets the loyalty stat
     */
    public void setLoyalty(int loy) {
        m_iLoyalty = loy;
    }


    /**
     * Setter for property m_sManuellerSmilie.
     *
     * @param manuellerSmilie New value of property m_sManuellerSmilie.
     */
    public void setManuellerSmilie(java.lang.String manuellerSmilie) {
        if (manuellerSmilie == null) {
            manuellerSmilie = "";
        }

        m_sManuellerSmilie = manuellerSmilie;
        DBManager.instance().saveManuellerSmilie(m_iSpielerID, manuellerSmilie);
    }

    /**
     * Getter for property m_sManuellerSmilie.
     *
     * @return Value of property m_sManuellerSmilie.
     */
    public java.lang.String getManuellerSmilie() {
        if (m_sManuellerSmilie == null) {
            m_sManuellerSmilie = DBManager.instance().getManuellerSmilie(m_iSpielerID);

            //Steht null in der DB?
            if (m_sManuellerSmilie == null) {
                m_sManuellerSmilie = "";
            }
        }

        //database.DBZugriff.instance ().getManuellerSmilie( m_iSpielerID );
        return m_sManuellerSmilie;
    }

    /**
     * Sets the TSI
     *
     * @param m_iTSI New value of property m_iMarkwert.
     */
    public void setTSI(int m_iTSI) {
        this.m_iTSI = m_iTSI;
    }

    /**
     * Returns the TSI
     *
     * @return Value of property m_iMarkwert.
     */
    public int getTSI() {
        return m_iTSI;
    }

    public void setFirstName(java.lang.String m_sName) {
        this.m_sFirstName = m_sName;
    }

    public java.lang.String getFirstName() {
        return DBManager.deleteEscapeSequences(m_sFirstName);
    }

    public void setNickName(java.lang.String m_sName) {
        this.m_sNickName = m_sName;
    }

    public java.lang.String getNickName() {
        return DBManager.deleteEscapeSequences(m_sNickName);
    }

    public void setLastName(java.lang.String m_sName) {
        this.m_sLastName = m_sName;
    }

    public java.lang.String getLastName() {
        return DBManager.deleteEscapeSequences(m_sLastName);
    }



    /**
     * Getter for shortName
     * eg: James Bond = J. Bond
     * Nickname are ignored
     */
    public String getShortName() {

        if (getFirstName().isEmpty())
        {
            return getLastName();
        }
        return getFirstName().charAt(0) + ". " + getLastName();

        }


    public java.lang.String getFullName() {

        if (getNickName().isEmpty())
        {
            return getFirstName() + " " +getLastName();
        }

        return getFirstName() + " '" + getNickName() + "' " +getLastName();
    }

    /**
     * Setter for property m_iNationalitaet.
     *
     * @param m_iNationalitaet New value of property m_iNationalitaet.
     */
    public void setNationalitaet(int m_iNationalitaet) {
        this.m_iNationalitaet = m_iNationalitaet;
    }

    /**
     * Getter for property m_iNationalitaet.
     *
     * @return Value of property m_iNationalitaet.
     */
    public int getNationalitaet() {
        return m_iNationalitaet;
    }

    /**
     * Setter for property m_bOld.
     *
     * @param m_bOld New value of property m_bOld.
     */
    public void setOld(boolean m_bOld) {
        this.m_bOld = m_bOld;
    }

    /**
     * Getter for property m_bOld.
     *
     * @return Value of property m_bOld.
     */
    public boolean isOld() {
        return m_bOld;
    }

    /**
     * Setter for property m_iPasspiel.
     *
     * @param m_iPasspiel New value of property m_iPasspiel.
     */
    public void setPasspiel(int m_iPasspiel) {
        this.m_iPasspiel = m_iPasspiel;
    }

    /**
     * Getter for property m_iPasspiel.
     *
     * @return Value of property m_iPasspiel.
     */
    public int getPSskill() {
        return m_iPasspiel;
    }

    /**
     * Zum speichern! Die Reduzierung des Marktwerts auf TSI wird rückgängig gemacht
     */
    public int getSaveMarktwert() {
        if (m_clhrfDate == null || m_clhrfDate.before(DBManager.TSIDATE)) {
            //Echter Marktwert
            return m_iTSI * 1000;
        }

        //TSI
        return m_iTSI;
    }

    /**
     * Setter for property iPlayerSpecialty.
     *
     * @param iPlayerSpecialty New value of property iPlayerSpecialty.
     */
    public void setPlayerSpecialty(int iPlayerSpecialty) {
        this.iPlayerSpecialty = iPlayerSpecialty;
    }

    /**
     * Getter for property iPlayerSpecialty.
     *
     * @return Value of property iPlayerSpecialty.
     */
    public int getPlayerSpecialty() {
        return iPlayerSpecialty;
    }

    public boolean hasSpeciality(Speciality speciality)
    {
        Speciality s = Speciality.values()[iPlayerSpecialty];
        return s.equals(speciality);
    }

    // returns the name of the speciality in the used language
    public String getSpecialityName() {
        Speciality s = Speciality.values()[iPlayerSpecialty];
        if (s.equals(Speciality.NO_SPECIALITY)) {
            return EMPTY;
        } else {
            return HOVerwaltung.instance().getLanguageString("ls.player.speciality." + s.toString().toLowerCase(Locale.ROOT));
        }
    }

    // return the name of the speciality with a break before and in brackets
    // e.g. [br][quick], used for HT-ML export
    public String getSpecialityExportName() {
        Speciality s = Speciality.values()[iPlayerSpecialty];
        if (s.equals(Speciality.NO_SPECIALITY)) {
            return EMPTY;
        } else {
            return BREAK + O_BRACKET + getSpecialityName() + C_BRACKET;
        }
    }

    // no break so that the export looks better
    public String getSpecialityExportNameForKeeper() {
        Speciality s = Speciality.values()[iPlayerSpecialty];
        if (s.equals(Speciality.NO_SPECIALITY)) {
            return EMPTY;
        } else {
            return O_BRACKET + getSpecialityName() + C_BRACKET;
        }
    }


    /**
     * Setter for property m_iSpielaufbau.
     *
     * @param m_iSpielaufbau New value of property m_iSpielaufbau.
     */
    public void setSpielaufbau(int m_iSpielaufbau) {
        this.m_iSpielaufbau = m_iSpielaufbau;
    }

    /**
     * Getter for property m_iSpielaufbau.
     *
     * @return Value of property m_iSpielaufbau.
     */
    public int getPMskill() {
        return m_iSpielaufbau;
    }

    /**
     * set whether or not that player can be selected by the assistant
     */
    public void setCanBeSelectedByAssistant(boolean flag) {
        m_bCanBeSelectedByAssistant = flag;
        DBManager.instance().saveSpielerSpielberechtigt(m_iSpielerID,  flag);
    }

    /**
     * get whether or not that player can be selected by the assistant
     */
    public boolean getCanBeSelectedByAssistant() {
        //Only check if not authorized to play: Reduced access!
        if (m_bCanBeSelectedByAssistant == null) {
            m_bCanBeSelectedByAssistant = DBManager.instance().getSpielerSpielberechtigt(m_iSpielerID);
        }

        return m_bCanBeSelectedByAssistant;

    }

    /**
     * Setter for property m_iSpielerID.
     *
     * @param m_iSpielerID New value of property m_iSpielerID.
     */
    public void setSpielerID(int m_iSpielerID) {
        this.m_iSpielerID = m_iSpielerID;
    }

    /**
     * Getter for property m_iSpielerID.
     *
     * @return Value of property m_iSpielerID.
     */
    public int getPlayerID() {
        return m_iSpielerID;
    }

    /**
     * Setter for property m_iStandards.
     *
     * @param m_iStandards New value of property m_iStandards.
     */
    public void setStandards(int m_iStandards) {
        this.m_iStandards = m_iStandards;
    }

    /**
     * Getter for property m_iStandards.
     *
     * @return Value of property m_iStandards.
     */
    public int getSPskill() {
        return m_iStandards;
    }

    /**
     * berechnet den Subskill pro position
     */
    public float getSub4Skill(int skill) {
        return Math.min(0.99f, Helper.round(getSub4SkillAccurate(skill), 2));
    }

    /**
     * Returns accurate subskill number. If you need subskill for UI
     * purpose it is better to use getSubskill4Pos()
     *
     * @param skill skill number
     * @return subskill between 0.0-0.999
     */
    public float getSub4SkillAccurate(int skill) {
        double value = switch (skill) {
            case KEEPER -> m_dSubTorwart;
            case PLAYMAKING -> m_dSubSpielaufbau;
            case DEFENDING -> m_dSubVerteidigung;
            case PASSING -> m_dSubPasspiel;
            case WINGER -> m_dSubFluegelspiel;
            case SCORING -> m_dSubTorschuss;
            case SET_PIECES -> m_dSubStandards;
            case EXPERIENCE -> subExperience;
            default -> 0;
        };

        return (float) Math.min(0.999, value);
    }

    public void setSubskill4PlayerSkill(int skill, float value) {
        switch (skill) {
            case KEEPER -> m_dSubTorwart = value;
            case PLAYMAKING -> m_dSubSpielaufbau = value;
            case DEFENDING -> m_dSubVerteidigung = value;
            case PASSING -> m_dSubPasspiel = value;
            case WINGER -> m_dSubFluegelspiel = value;
            case SCORING -> m_dSubTorschuss = value;
            case SET_PIECES -> m_dSubStandards = value;
            case EXPERIENCE -> subExperience = value;
        }
    }

    /**
     * Setter for property m_sTeamInfoSmilie.
     *
     * @param teamInfoSmilie New value of property m_sTeamInfoSmilie.
     */
    public void setTeamInfoSmilie(String teamInfoSmilie) {
        if (teamInfoSmilie == null) {
            teamInfoSmilie = "";
        }

        m_sTeamInfoSmilie = teamInfoSmilie;
        DBManager.instance().saveTeamInfoSmilie(m_iSpielerID, teamInfoSmilie);
    }

    /**
     * Getter for property m_sTeamInfoSmilie.
     *
     * @return Value of property m_sTeamInfoSmilie.
     */
    public String getTeamInfoSmilie() {
        if (m_sTeamInfoSmilie == null) {
            m_sTeamInfoSmilie = DBManager.instance().getTeamInfoSmilie(m_iSpielerID);

            //Steht null in der DB?
            if (m_sTeamInfoSmilie == null) {
                m_sTeamInfoSmilie = "";
            }
        }

        return m_sTeamInfoSmilie.replaceAll("\\.png$", "");
    }

    /**
     * Gibt das Datum des ersten HRFs mit dem Player zurück
     */
    public Timestamp getTimestamp4FirstPlayerHRF() {
        if (m_tsTime4FirstHRF == null) {
            m_tsTime4FirstHRF = DBManager.instance().getTimestamp4FirstPlayerHRF(m_iSpielerID);
        }

        return m_tsTime4FirstHRF;
    }

    /**
     * Setter for property m_iToreFreund.
     *
     * @param m_iToreFreund New value of property m_iToreFreund.
     */
    public void setToreFreund(int m_iToreFreund) {
        this.m_iToreFreund = m_iToreFreund;
    }

    /**
     * Getter for property m_iToreFreund.
     *
     * @return Value of property m_iToreFreund.
     */
    public int getToreFreund() {
        return m_iToreFreund;
    }

    /**
     * Setter for property m_iToreGesamt.
     *
     * @param m_iToreGesamt New value of property m_iToreGesamt.
     */
    public void setToreGesamt(int m_iToreGesamt) {
        this.m_iToreGesamt = m_iToreGesamt;
    }

    /**
     * Getter for property m_iToreGesamt.
     *
     * @return Value of property m_iToreGesamt.
     */
    public int getToreGesamt() {
        return m_iToreGesamt;
    }

    /**
     * Setter for property m_iToreLiga.
     *
     * @param m_iToreLiga New value of property m_iToreLiga.
     */
    public void setToreLiga(int m_iToreLiga) {
        this.m_iToreLiga = m_iToreLiga;
    }

    /**
     * Getter for property m_iToreLiga.
     *
     * @return Value of property m_iToreLiga.
     */
    public int getToreLiga() {
        return m_iToreLiga;
    }

    /**
     * Setter for property m_iTorePokal.
     *
     * @param m_iTorePokal New value of property m_iTorePokal.
     */
    public void setTorePokal(int m_iTorePokal) {
        this.m_iTorePokal = m_iTorePokal;
    }

    /**
     * Getter for property m_iTorePokal.
     *
     * @return Value of property m_iTorePokal.
     */
    public int getTorePokal() {
        return m_iTorePokal;
    }

    /**
     * Setter for property m_iTorschuss.
     *
     * @param m_iTorschuss New value of property m_iTorschuss.
     */
    public void setTorschuss(int m_iTorschuss) {
        this.m_iTorschuss = m_iTorschuss;
    }

    /**
     * Getter for property m_iTorschuss.
     *
     * @return Value of property m_iTorschuss.
     */
    public int getSCskill() {
        return m_iTorschuss;
    }

    /**
     * Setter for property m_iTorwart.
     *
     * @param m_iTorwart New value of property m_iTorwart.
     */
    public void setTorwart(int m_iTorwart) {
        this.m_iTorwart = m_iTorwart;
    }

    /**
     * Getter for property m_iTorwart.
     *
     * @return Value of property m_iTorwart.
     */
    public int getGKskill() {
        return m_iTorwart;
    }

    /**
     * Setter for property m_iTrainer.
     *
     * @param m_iTrainer New value of property m_iTrainer.
     */
    public void setTrainerSkill(int m_iTrainer) {
        this.m_iTrainer = m_iTrainer;
    }

    /**
     * Getter for property m_iTrainer.
     *
     * @return Value of property m_iTrainer.
     */
    public int getTrainerSkill() {
        return m_iTrainer;
    }

    /**
     * gibt an ob der Player Trainer ist
     */
    public boolean isTrainer() {
        return ((m_iTrainer > 0) && (m_iTrainerTyp >= 0));
    }

    /**
     * Setter for property m_iTrainerTyp.
     *
     * @param m_iTrainerTyp New value of property m_iTrainerTyp.
     */
    public void setTrainerTyp(int m_iTrainerTyp) {
        this.m_iTrainerTyp = m_iTrainerTyp;
    }

    /**
     * Getter for property m_iTrainerTyp.
     *
     * @return Value of property m_iTrainerTyp.
     */
    public int getTrainerTyp() {
        return m_iTrainerTyp;
    }

    /**
     * Last match
     * @return date
     */
    public String getLastMatchDate(){
        return m_lastMatchDate;
    }

    /**
     * Last match
     * @return rating
     */
    public double getLastMatchRating(){
        return m_lastMatchRating;
    }

    /**
     * Last match id
     * @return id
     */
    public int getLastMatchId(){
        return m_lastMatchId;
    }

    /**
     * Returns the {@link MatchType} of the last match.
     */
    public MatchType getLastMatchType() {
        return lastMatchType;
    }

    /**
     * Sets the value of <code>lastMatchType</code> to <code>matchType</code>.
     */
    public void setLastMatchType(MatchType matchType) {
        this.lastMatchType = matchType;
    }

    /**
     * Set last match £461
     * @param date
     * @param rating
     * @param id
     */
    public void setLastMatchDetails(String date, int rating, int id){
        m_lastMatchDate = date;
        m_lastMatchRating = rating;
        m_lastMatchId = id;
    }

    /**
     * Setter for property m_iTransferlisted.
     *
     * @param m_iTransferlisted New value of property m_iTransferlisted.
     */
    public void setTransferlisted(int m_iTransferlisted) {
        this.m_iTransferlisted = m_iTransferlisted;
    }

    /**
     * Getter for property m_iTransferlisted.
     *
     * @return Value of property m_iTransferlisted.
     */
    public int getTransferlisted() {
        return m_iTransferlisted;
    }

    /**
     * Setter for property m_iTrikotnummer.
     *
     * @param m_iTrikotnummer New value of property m_iTrikotnummer.
     */
    public void setTrikotnummer(int m_iTrikotnummer) {
        this.m_iTrikotnummer = m_iTrikotnummer;
    }

    /**
     * Getter for property m_iTrikotnummer.
     *
     * @return Value of property m_iTrikotnummer.
     */
    public int getTrikotnummer() {
        return m_iTrikotnummer;
    }

    /**
     * Setter for property m_iU20Laenderspiele.
     *
     * @param m_iU20Laenderspiele New value of property m_iU20Laenderspiele.
     */
    public void setU20Laenderspiele(int m_iU20Laenderspiele) {
        this.m_iU20Laenderspiele = m_iU20Laenderspiele;
    }

    /**
     * Getter for property m_iU20Laenderspiele.
     *
     * @return Value of property m_iU20Laenderspiele.
     */
    public int getU20Laenderspiele() {
        return m_iU20Laenderspiele;
    }

    public void setUserPosFlag(byte flag) {
        m_bUserPosFlag = flag;
        DBManager.instance().saveSpielerUserPosFlag(m_iSpielerID, m_bUserPosFlag);
        this.setCanBeSelectedByAssistant(flag != IMatchRoleID.UNSELECTABLE);
    }

    /**
     * liefert User Notiz zum Player
     */
    public byte getUserPosFlag() {
        if (m_bUserPosFlag < MatchRoleID.UNKNOWN) {
            m_bUserPosFlag = DBManager.instance().getSpielerUserPosFlag(m_iSpielerID);
        }

        //database.DBZugriff.instance ().getSpielerNotiz ( m_iSpielerID );
        return m_bUserPosFlag;
    }

    /**
     * get Skillvalue 4 skill
     */
    public int getValue4Skill(int skill) {
        return switch (skill) {
            case KEEPER -> m_iTorwart;
            case PLAYMAKING -> m_iSpielaufbau;
            case DEFENDING -> m_iVerteidigung;
            case PASSING -> m_iPasspiel;
            case WINGER -> m_iFluegelspiel;
            case SCORING -> m_iTorschuss;
            case SET_PIECES -> m_iStandards;
            case STAMINA -> m_iKondition;
            case EXPERIENCE -> m_iErfahrung;
            case FORM -> m_iForm;
            case LEADERSHIP -> m_iFuehrung;
            case LOYALTY -> m_iLoyalty;
            default -> 0;
        };
    }

    public float getSkillValue(int skill){
        return getSub4Skill(skill) + getValue4Skill(skill);
    }

    /**
     * set Skillvalue 4 skill
     *
     * @param skill the skill to change
     * @param value the new skill value
     */
    public void setValue4Skill(int skill, int value) {
        switch (skill) {
            case KEEPER -> setTorwart(value);
            case PLAYMAKING -> setSpielaufbau(value);
            case PASSING -> setPasspiel(value);
            case WINGER -> setFluegelspiel(value);
            case DEFENDING -> setVerteidigung(value);
            case SCORING -> setTorschuss(value);
            case SET_PIECES -> setStandards(value);
            case STAMINA -> setKondition(value);
            case EXPERIENCE -> setErfahrung(value);
            case FORM -> setForm(value);
            case LEADERSHIP -> setFuehrung(value);
            case LOYALTY -> setLoyalty(value);
        }
    }


    /**
     * Setter for property m_iVerletzt.
     *
     * @param m_iVerletzt New value of property m_iVerletzt.
     */
    public void setVerletzt(int m_iVerletzt) {
        this.m_iInjuryWeeks = m_iVerletzt;
    }

    /**
     * Getter for property m_iVerletzt.
     *
     * @return Value of property m_iVerletzt.
     */
    public int getInjuryWeeks() {
        return m_iInjuryWeeks;
    }

    /**
     * Setter for property m_iVerteidigung.
     *
     * @param m_iVerteidigung New value of property m_iVerteidigung.
     */
    public void setVerteidigung(int m_iVerteidigung) {
        this.m_iVerteidigung = m_iVerteidigung;
    }

    /**
     * Getter for property m_iVerteidigung.
     *
     * @return Value of property m_iVerteidigung.
     */
    public int getDEFskill() {
        return m_iVerteidigung;
    }


    public int getWeatherEffect(Weather weather) {
        return PlayerSpeciality.getWeatherEffect(weather, iPlayerSpecialty);
    }

    public float getImpactWeatherEffect(Weather weather) {
        return PlayerSpeciality.getImpactWeatherEffect(weather, iPlayerSpecialty);
    }

    @Deprecated
    private void incrementSubskills(Player originalPlayer, int trainerlevel, int skill, double points, WeeklyTrainingType wt, TrainingPerPlayer trForPlayer) {
        if (skill < KEEPER || points <= 0)
            return;

        var trainingLength = wt.getTrainingLength(this, trainerlevel, trForPlayer.getTrainingWeek().getTrainingIntensity(), trForPlayer.getTrainingWeek().getStaminaShare(), trForPlayer.getTrainingWeek().getTrainingAssistantsLevel());

        var trainingAlternativeFormula = wt.getTrainingAlternativeFormula( getValue4Skill(skill), trForPlayer, skill==wt.getPrimaryTrainingSkill());

        HOLogger.instance().info(this.getClass(),
                this.getLastName()+ "; " + PlayerSkill.toString(skill) +
                "; Age=" + this.getAlter() +
                "; Skill=" + this.getValue4Skill(skill) +
                "; Minutes=" + trForPlayer.getTrainingPair().getTrainingDuration().getFullTrainingMinutes() +
                        ";" + trForPlayer.getTrainingPair().getTrainingDuration().getPartlyTrainingMinutes() +
                        ";" + trForPlayer.getTrainingPair().getTrainingDuration().getOsmosisTrainingMinutes() +
                "; training=" + points/trainingLength + "; " + trainingAlternativeFormula);

        float gain = (float) Helper.round(points / trainingLength, 3);

        if (gain <= 0)
            return;

        /* Limit training to one full level max */
        gain = Math.min(gain, 1.0f);

        if (check4SkillUp(skill, originalPlayer)) {
            /* Carry subskill over skillup */
            gain = 0.0f;
        }
        setSubskill4PlayerSkill(skill, Math.min(0.99f, getSub4SkillAccurate(skill) + gain));
    }

    /*
     * Calculates training benefit, and updates subskill for the player.
     * Used when there is only 1 week of training to be calculated.
     *
     * @param originalPlayer - The player to calculate subskills on
     * @param trainerlevel   - The trainer level
     * @param trainingWeek

    @Deprecated
    public void calcIncrementalSubskills(Player originalPlayer, int trainerlevel, TrainingPerWeek trainingWeek) {

        if (this.hasTrainingBlock()) {
            return;
        }

        if (trainingWeek == null)
            return;

        TrainingPerPlayer trainingForPlayer = calculateWeeklyTraining(trainingWeek);

        if (trainingForPlayer == null)
            return;

        TrainingPoints tp = trainingForPlayer.getTrainingPair();

        if (tp == null)
            return;

        // Time to perform skill drop
        if (SkillDrops.instance().isActive()) {
            performSkilldrop(originalPlayer, 1);
        }

        WeeklyTrainingType wt = WeeklyTrainingType.instance(trainingWeek.getTrainingType());

        incrementSubskills(originalPlayer, trainerlevel, wt.getPrimaryTrainingSkill(), tp.getPrimary(), wt, trainingForPlayer);

        incrementSubskills(originalPlayer, trainerlevel, wt.getSecondaryTrainingSkill(), tp.getSecondary(), wt, trainingForPlayer);

        addExperienceSub(trainingForPlayer.getExperienceSub());

    }
 */
    /**
     * Training for given player for each skill
     *
     * @param train preset Trainingweeks
     *
     * @return TrainingPerPlayer
     */
    public TrainingPerPlayer calculateWeeklyTraining(TrainingPerWeek train) {
        final int playerID = this.getPlayerID();
        TrainingPerPlayer ret = new TrainingPerPlayer(this);
        ret.setTrainingWeek(train);
        if (train == null || train.getTrainingType() < 0) {
            return ret;
        }

        WeeklyTrainingType wt = WeeklyTrainingType.instance(train.getTrainingType());
        if (wt != null) {
            try {
                var matches = train.getMatches();
                int myID = HOVerwaltung.instance().getModel().getBasics().getTeamId();
                TrainingWeekPlayer tp = new TrainingWeekPlayer(this);
                int minutes=0;
                for (var match : matches) {
                    //Get the MatchLineup by id
                    MatchLineupTeam mlt = DBManager.instance().getMatchLineupTeam(match.getMatchType().getId(), match.getMatchID(), myID);
                    //MatchStatistics ms = new MatchStatistics(match, mlt);
                    MatchType type = mlt.getMatchType();
                    boolean walkoverWin = match.getMatchdetails().isWalkoverMatchWin(HOVerwaltung.instance().getModel().getBasics().getYouthTeamId());
                    if ( type != MatchType.MASTERS) { // MASTERS counts only for experience
                        tp.addFullTrainingMinutes(mlt.getTrainingMinutesPlayedInSectors(playerID, wt.getFullTrainingSectors(), walkoverWin));
                        tp.addBonusTrainingMinutes(mlt.getTrainingMinutesPlayedInSectors(playerID, wt.getBonusTrainingSectors(), walkoverWin));
                        tp.addPartlyTrainingMinutes(mlt.getTrainingMinutesPlayedInSectors(playerID, wt.getPartlyTrainingSectors(), walkoverWin));
                        tp.addOsmosisTrainingMinutes(mlt.getTrainingMinutesPlayedInSectors(playerID, wt.getOsmosisTrainingSectors(), walkoverWin));
                    }
                    tp.addPlayedMinutes(mlt.getTrainingMinutesPlayedInSectors(playerID, null, walkoverWin));
                    ret.addExperience(match.getExperienceIncrease(min(90,tp.getPlayedMinutes())));
                    minutes = tp.getPlayedMinutes();
                }
                TrainingPoints trp = new TrainingPoints(wt, tp);

                // get experience increase of national matches
                if  ( this.getNationalTeamID() != 0 && this.getNationalTeamID() != myID){
                    // TODO check if national matches are stored in database
                    var nationalMatches = train.loadMatchesOfNTPlayers(this.getNationalTeamID());
                    for (var match : nationalMatches){
                        MatchLineupTeam mlt = DBManager.instance().getMatchLineupTeam(match.getMatchType().getId(), match.getMatchID(), this.getNationalTeamID());
                        minutes = mlt.getTrainingMinutesPlayedInSectors(playerID, null, false);
                        if ( minutes > 0 ) {
                            ret.addExperience(match.getExperienceIncrease(min(90,tp.getPlayedMinutes())));
                        }
                    }
                }
                ret.setTrainingPair(trp);
            } catch (Exception e) {
                HOLogger.instance().log(getClass(),e);
            }
        }
        return ret;
    }

    private void addExperienceSub(double experienceSub) {
        this.subExperience += experienceSub;
        if ( this.subExperience > .99) this.subExperience = .99;
    }

    /**
     * Performs skill drops on the player based on age and skills
     *
     * @param originalPlayer The player as he was before this week. Used to find a subskill to drop from.
     * @param weeks          The number of weeks to drop in case of missing info.
     */

    public void performSkilldrop(Player originalPlayer, int weeks) {

        if (originalPlayer == null) {
            return;
        }

        for (int skillType = 0; skillType < EXPERIENCE; skillType++) {

            if ((skillType == FORM) || (skillType == STAMINA)) {
                continue;
            }

            if (getValue4Skill(skillType) >= 1) {
                float drop = weeks * SkillDrops.instance().getSkillDrop(getValue4Skill(skillType), originalPlayer.getAlter(), skillType);

                // Only bother if there is drop, there is something to drop from,
                //and check that the player has not popped
                if ((drop > 0) && (originalPlayer.getSub4SkillAccurate(skillType) > 0)
                        && (getValue4Skill(skillType) == originalPlayer.getValue4Skill(skillType))) {
                    setSubskill4PlayerSkill(skillType, Math.max(0, getSub4SkillAccurate(skillType) - drop / 100));
                }
            }
        }
    }


    /**
     * Calculate the player strength on a specific lineup position
     * with or without form
     *
     * @param fo         FactorObject with the skill weights for this position
     * @param useForm    consider form?
     * @param normalized absolute or normalized contribution?
     * @return the player strength on this position
     */
    float calcPosValue(FactorObject fo, boolean useForm, boolean normalized, @Nullable Weather weather, boolean useWeatherImpact) {
        if ((fo == null) || (fo.getSum() == 0.0f)) {
            return -1.0f;
        }

        // The stars formulas are changed by the user -> clear the cache
        if (!PlayerAbsoluteContributionCache.containsKey("lastChange") || ((Date) PlayerAbsoluteContributionCache.get("lastChange")).before(FormulaFactors.getLastChange())) {
//    		System.out.println ("Clearing stars cache");
            PlayerAbsoluteContributionCache.clear();
            PlayerRelativeContributionCache.clear();
            PlayerAbsoluteContributionCache.put("lastChange", new Date());
        }
        /*
         * Create a key for the Hashtable cache
         * We cache every star rating to speed up calculation
         * (calling RPM.calcPlayerStrength() is quite expensive and this method is used very often)
         */

        float loy = RatingPredictionManager.getLoyaltyHomegrownBonus(this);

        String key = fo.getPosition() + ":"
                + Helper.round(getGKskill() + getSub4Skill(KEEPER) + loy, 2) + "|"
                + Helper.round(getPMskill() + getSub4Skill(PLAYMAKING) + loy, 2) + "|"
                + Helper.round(getDEFskill() + getSub4Skill(DEFENDING) + loy, 2) + "|"
                + Helper.round(getWIskill() + getSub4Skill(WINGER) + loy, 2) + "|"
                + Helper.round(getPSskill() + getSub4Skill(PASSING) + loy, 2) + "|"
                + Helper.round(getSPskill() + getSub4Skill(SET_PIECES) + loy, 2) + "|"
                + Helper.round(getSCskill() + getSub4Skill(SCORING) + loy, 2) + "|"
                + getForm() + "|"
                + getKondition() + "|"
                + getErfahrung() + "|"
                + getPlayerSpecialty(); // used for Technical DefFW

        // Check if the key already exists in cache
        if (PlayerAbsoluteContributionCache.containsKey(key)) {
            // System.out.println ("Using star rating from cache, key="+key+", tablesize="+starRatingCache.size());

            float rating = normalized ? (float) PlayerRelativeContributionCache.get(key) : (Float) PlayerAbsoluteContributionCache.get(key);
            if(useWeatherImpact){
                rating *= getImpactWeatherEffect(weather);
            }

            return rating;
        }

        // Compute contribution
        float gkValue = fo.getGKfactor() * RatingPredictionManager.calcPlayerStrength(-2, this, KEEPER, useForm, false);
        float pmValue = fo.getPMfactor() * RatingPredictionManager.calcPlayerStrength(-2, this, PLAYMAKING, useForm, false);
        float deValue = fo.getDEfactor() * RatingPredictionManager.calcPlayerStrength(-2, this, DEFENDING, useForm, false);
        float wiValue = fo.getWIfactor() * RatingPredictionManager.calcPlayerStrength(-2, this, WINGER, useForm, false);
        float psValue = fo.getPSfactor() * RatingPredictionManager.calcPlayerStrength(-2, this, PASSING, useForm, false);
        float spValue = fo.getSPfactor() * RatingPredictionManager.calcPlayerStrength(-2, this, SET_PIECES, useForm, false);
        float scValue = fo.getSCfactor() * RatingPredictionManager.calcPlayerStrength(-2, this, SCORING, useForm, false);
        float val = gkValue + pmValue + deValue + wiValue + psValue + spValue + scValue;

        float absVal = val * 10; // multiplied by 10 for improved visibility
        float normVal = val / fo.getNormalizationFactor() * 100;  // scaled between 0 and 100%

        // Put to cache
        PlayerAbsoluteContributionCache.put(key, absVal);
        PlayerRelativeContributionCache.put(key, normVal);

//    	System.out.println ("Star rating put to cache, key="+key+", val="+val+", tablesize="+starRatingCache.size());
        if (normalized) {
            return normVal;
        } else {
            return absVal;
        }
    }


    public float calcPosValue(byte pos, boolean useForm, boolean normalized, @Nullable Weather weather, boolean useWeatherImpact) {
        return calcPosValue(pos, useForm, normalized, core.model.UserParameter.instance().nbDecimals, weather, useWeatherImpact);
    }

    public float calcPosValue(byte pos, boolean useForm, @Nullable Weather weather, boolean useWeatherImpact) {
        return calcPosValue(pos, useForm, false, weather, useWeatherImpact);
    }

    /**
     * Calculate the player strength on a specific lineup position
     * with or without form
     *
     * @param pos     position from IMatchRoleID (TORWART.. POS_ZUS_INNENV)
     * @param useForm consider form?
     * @return the player strength on this position
     */
    public float calcPosValue(byte pos, boolean useForm, boolean normalized, int nb_decimals,  @Nullable Weather weather, boolean useWeatherImpact) {
        float es;
        FactorObject factor = FormulaFactors.instance().getPositionFactor(pos);

        // Fix for TDF
        if (pos == IMatchRoleID.FORWARD_DEF && this.getPlayerSpecialty() == PlayerSpeciality.TECHNICAL) {
            factor = FormulaFactors.instance().getPositionFactor(IMatchRoleID.FORWARD_DEF_TECH);
        }

        if (factor != null) {
            es = calcPosValue(factor, useForm, normalized, weather, useWeatherImpact);
        } else {
            //	For Coach or factor not found return 0
            return 0.0f;
        }

        return core.util.Helper.round(es, nb_decimals);
    }


    /**
     * Copy the skills of old player.
     * Used by training
     *
     * @param old player to copy from
     */
    public void copySkills(Player old) {

        for (int skillType = 0; skillType <= LOYALTY; skillType++) {
            setValue4Skill(skillType, old.getValue4Skill(skillType));
        }
    }

    /**
     * Performs the subskill reset needed at skill drop.
     *
     * @param skillType The ID of the skill to perform drop on.
     */
    public void dropSubskills(int skillType) {
        if (getValue4Skill(skillType) > 0) {
            // non-existent has no subskill.
            setSubskill4PlayerSkill(skillType, 0.999f);

        } else {
            setSubskill4PlayerSkill(skillType, 0);
        }
    }


    //////////////////////////////////////////////////////////////////////////////////
    //equals
    /////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;

        if (obj instanceof Player) {
            equals = ((Player) obj).getPlayerID() == m_iSpielerID;
        }

        return equals;
    }

    /**
     * prüft ob Skillup vorliegt
     */
    protected boolean check4SkillUp(int skill, Player oldPlayer) {
        if ((oldPlayer != null) && (oldPlayer.getPlayerID() > 0))
            return oldPlayer.getValue4Skill(skill) < getValue4Skill(skill);
        return false;
    }

    /**
     * Test for whether skilldown has occurred
     */
    public boolean check4SkillDown(int skill, Player oldPlayer) {
        if (skill < EXPERIENCE)
            if ((oldPlayer != null) && (oldPlayer.getPlayerID() > 0))
                return oldPlayer.getValue4Skill(skill) > getValue4Skill(skill);
        return false;
    }

    /**
     * Does this player have a training block?
     *
     * @return training block
     */
    public boolean hasTrainingBlock() {
        return m_bTrainingBlock;
    }

    /**
     * Set the training block of this player (true/false)
     *
     * @param isBlocked new value
     */
    public void setTrainingBlock(boolean isBlocked) {
        this.m_bTrainingBlock = isBlocked;
    }

    public int getNationalTeamID() {
        return nationalTeamId;
    }

    public void setNationalTeamId( int id){
        this.nationalTeamId=id;
    }

    public double getSubExperience() {
        return this.subExperience;
    }

    public void setSubExperience( double experience){
        this.subExperience = experience;
    }

    public List<TrainingEvent> downloadTrainingEvents() {
        return OnlineWorker.getTrainingEvents(this.m_iSpielerID);
    }


    public List<FuturePlayerTraining> getFuturePlayerTrainings(){
        if ( futurePlayerTrainings == null){
            futurePlayerTrainings = DBManager.instance().getFuturePlayerTrainings(this.getPlayerID());
            if (futurePlayerTrainings.size()>0) {
                var start = HOVerwaltung.instance().getModel().getBasics().getHattrickWeek();
                var remove = new ArrayList<FuturePlayerTraining>();
                for (var t : futurePlayerTrainings) {
                    if (start.isAfter(t.getTo())){
                        remove.add(t);
                    }
                }
                futurePlayerTrainings.removeAll(remove);
            }
        }
        return futurePlayerTrainings;
    }

    /**
     * Get the training priority of a hattrick week. If user training plan is given for the week this user selection is
     * returned. If no user plan is available, the training priority is determined by the player's best position.
     *
     * @param wt
     *  used to get priority depending from the player's best position.
     * @param trainingDate
     *  the training week
     * @return
     *  the training priority
     */
    public FuturePlayerTraining.Priority getTrainingPriority(WeeklyTrainingType wt, Instant trainingDate) {
        for ( var t : getFuturePlayerTrainings()) {
            if (t.contains(trainingDate)) {
                return t.getPriority();
            }
        }

        // get Prio from best position
        int position = HelperWrapper.instance().getPosition(this.getIdealPosition());

        for ( var p: wt.getTrainingSkillBonusPositions()){
            if ( p == position) return FuturePlayerTraining.Priority.FULL_TRAINING;
        }
        for ( var p: wt.getTrainingSkillPositions()){
            if ( p == position) {
                if ( wt.getTrainingType() == TrainingType.SET_PIECES) return FuturePlayerTraining.Priority.PARTIAL_TRAINING;
                return FuturePlayerTraining.Priority.FULL_TRAINING;
            }
        }
        for ( var p: wt.getTrainingSkillPartlyTrainingPositions()){
            if ( p == position) return FuturePlayerTraining.Priority.PARTIAL_TRAINING;
        }
        for ( var p: wt.getTrainingSkillOsmosisTrainingPositions()){
            if ( p == position) return FuturePlayerTraining.Priority.OSMOSIS_TRAINING;
        }

        return null; // No training
    }

    /**
     * Set training priority for a time interval.
     * Previously saved trainings of this interval are overwritten or deleted.
     *  @param prio new training priority for the given time interval
     * @param fromWeek first week with new training priority
     * @param toWeek last week with new training priority, null means open end
     */
    public void setFutureTraining(FuturePlayerTraining.Priority prio, Instant fromWeek, Instant toWeek) {
        var removeIntervals = new ArrayList<FuturePlayerTraining>();
        var from = HattrickDate.fromInstant(fromWeek);
        var to = toWeek != null ? HattrickDate.fromInstant(toWeek) : null;
        for (var t : getFuturePlayerTrainings()) {
            if (t.cut(from, to) ||
                    t.cut(new HattrickDate(0, 0), HOVerwaltung.instance().getModel().getBasics().getHattrickWeek())) {
                removeIntervals.add(t);
            }
        }
        futurePlayerTrainings.removeAll(removeIntervals);
        if (prio != null) {
            futurePlayerTrainings.add(new FuturePlayerTraining(this.getPlayerID(), prio, from, to));
        }
        DBManager.instance().storeFuturePlayerTrainings(this.getPlayerID(), futurePlayerTrainings);
    }

    public String getBestPositionInfo(@Nullable Weather weather, boolean useWeatherImpact) {
        return MatchRoleID.getNameForPosition(getIdealPosition())
                + " ("
                +  getIdealPositionStrength(true, true, 1, weather, useWeatherImpact)
                + "%)";
    }

    /**
     * training priority information of the training panel
     *
     * @param nextWeek training priorities after this week will be considered
     * @return if there is one user selected priority, the name of the priority is returned
     *  if there are more than one selected priorities, "individual priorities" is returned
     *  if is no user selected priority, the best position information is returned
     */
    public String getTrainingPriorityInformation(HattrickDate nextWeek) {
        String ret=null;
        for ( var t : getFuturePlayerTrainings()) {
            if (!nextWeek.isAfter(t.getTo())){
                if ( ret != null ){
                    ret = HOVerwaltung.instance().getLanguageString("trainpre.individual.prios");
                    break;
                }
                ret = t.getPriority().toString();
            }
        }
        if ( ret != null ) return ret;
        return getBestPositionInfo(null, false);

    }

    private static int[] trainingSkills= { KEEPER, SET_PIECES, DEFENDING, SCORING, WINGER, PASSING, PLAYMAKING };

    /**
     * Calculates skill status of the player
     *
     * @param previousID Id of the previous download. Previous player status is loaded by this id.
     * @param trainingWeeks List of training week information
     */
    public void calcSubskills(int previousID, List<TrainingPerWeek> trainingWeeks) {
        var before = DBManager.instance().getSpieler(previousID).stream()
                .filter(i -> i.getPlayerID() == this.getPlayerID()).findFirst()
                .orElse(this.CloneWithoutSubskills());

        // since we don't want to work with temp player objects we calculate skill by skill
        // whereas experience is calculated within the first skill
        boolean experienceSubDone = this.getErfahrung() > before.getErfahrung(); // Do not calculate sub on experience skill up
        var experienceSub = experienceSubDone?0:before.getSubExperience(); // set sub to 0 on skill up
        for (var skill : trainingSkills) {
            var sub = before.getSub4Skill(skill);
            var valueBeforeTraining = before.getValue4Skill(skill);
            var valueAfterTraining = this.getValue4Skill(skill);

            if (trainingWeeks.size() > 0) {
                for (var training : trainingWeeks) {

                    var trainingPerPlayer = calculateWeeklyTraining(training);
                    if ( trainingPerPlayer != null) {

                        if ( !this.hasTrainingBlock()) {// player training is not blocked (blocking is no longer possible)

                            sub += trainingPerPlayer.calcSubskillIncrement(skill, valueBeforeTraining + sub);

                            if (sub > 1) { // Skill up expected
                                if (valueAfterTraining > valueBeforeTraining) { // OK
                                    valueBeforeTraining++;
                                    sub -= 1.;
                                } else {                                        // No skill up
                                    sub = 0.99f;
                                }
                            } else if (sub < 0) {
                                if (valueAfterTraining < valueBeforeTraining) { // OK
                                    valueBeforeTraining--;
                                    sub += 1.;
                                } else {                                        // No skill down
                                    sub = 0;
                                }
                            }
                        }

                        if ( !experienceSubDone){
                            experienceSub+=trainingPerPlayer.getExperienceSub();
                            if ( experienceSub > 0.99) experienceSub = 0.99;
                        }
                    }
                }
                experienceSubDone=true;
                if (valueAfterTraining > valueBeforeTraining) { // Skill up (not yet expected)
                    sub = 0;
                }
            }

            // Handle skill drops that happens the monday after training date
            var nextWeekTraining = TrainingManager.instance().getNextWeekTraining();
            if ( SkillDrops.instance().isActive() && nextWeekTraining != null &&
                    TrainingManager.instance().getNextWeekTraining().skillDropDayIsBetween(before.getHrfDate().toInstant(), this.getHrfDate().toInstant()))
            {
                // calc another skill down
                sub -= SkillDrops.instance().getSkillDrop(valueBeforeTraining, this.getAlter(), skill) / 100;
                if (sub < 0) {
                    if (valueAfterTraining < valueBeforeTraining) { // OK
                        valueBeforeTraining--;
                        sub += 1.;
                    } else {                                        // No skill down from Hattrick
                        sub = 0;
                    }
                }
            }
            else if (valueAfterTraining < valueBeforeTraining) {
                sub = .99f;
            }

            this.setSubskill4PlayerSkill(skill, sub);
            this.setSubExperience(experienceSub);
        }
    }

    private int getValue4Skill(Skills.HTSkillID skill) {
        return getValue4Skill(skill.convertToPlayerSkill());
    }

    private double getSub4Skill(Skills.HTSkillID skill) {
        return getSub4Skill(skill.convertToPlayerSkill());
    }

    private Player CloneWithoutSubskills() {
        var ret = new Player();
        ret.copySkills(this);
        ret.setSpielerID(getPlayerID());
        ret.setAlter(getAlter());
        ret.setLastName(getLastName());
        return ret;
    }


    static class PositionContribute {
    private final float m_rating;
    private final byte clPositionID;

    public PositionContribute(float rating, byte clPostionID) {
        m_rating = rating;
        clPositionID = clPostionID;
    }

    public float getRating() {
        return m_rating;
    }

    public byte getClPostionID() {
        return clPositionID;
    }

}

}
