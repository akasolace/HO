package core.model;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.db.DBManager;
//import core.epv.EPV;
import core.model.misc.Basics;
import core.model.misc.Finanzen;
import core.model.misc.TrainingEvent;
import core.model.misc.Verein;
import core.model.player.Player;
import core.model.series.Liga;
import core.training.SkillDrops;
import core.training.TrainingPerWeek;
import core.training.TrainingManager;
import core.training.TrainingWeekManager;
import core.util.HOLogger;
import core.util.HTCalendar;
import core.util.HTCalendarFactory;
import core.util.HelperWrapper;
import module.lineup.Lineup;
import module.series.Spielplan;
import tool.arenasizer.Stadium;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * This class bundles all models that belong to an HRF file - the data can also come from the database
 */
public class HOModel {
    //~ Instance fields ----------------------------------------------------------------------------

    private Lineup m_clAufstellung;
    private Lineup m_clLastAufstellung;
    private Basics m_clBasics;
    private Finanzen m_clFinanzen;
    private Liga m_clLiga;
    private Spielplan m_clSpielplan;
    private Stadium m_clStadium;
    private Team m_clTeam;
    private static List<Player> m_vOldPlayer;
    private List<Player> m_vPlayer;
    private Verein m_clVerein;
    private XtraData m_clXtraDaten;
    private int m_iID = -1;
    private List<StaffMember> m_clStaff;

    //~ Constructors -------------------------------------------------------------------------------

    //gibts über den Spielplan
    //private LigaTabelle         m_clLigaTabelle =   null;
    public HOModel() {
        //erst einbauen wenn db angebunden ist
        try {
            m_iID = DBManager.instance().getMaxHrfId() + 1;
        } catch (Exception e) {
        }
    }

	public HOModel(int id) {
		m_iID = id;
	}

	//~ Methods ------------------------------------------------------------------------------------


	public void setLineups(int id) {
		this.setAufstellung(DBManager.instance().getAufstellung(id, Lineup.DEFAULT_NAME));
		this.setPreviousLineup(DBManager.instance().getAufstellung(id, Lineup.DEFAULT_NAMELAST));
	}

    /**
     * Alle Player werden übergeben und die noch aktuellen Player entfernt
     */
    public final void setAllOldSpieler(Vector<Player> playerVector) {
        for (int i = 0; i < playerVector.size(); i++) {
            //Auf alt setzen, die neuen werden gleich entfernt
            ((Player) playerVector.get(i)).setOld(true);

            for (int j = 0; j < m_vPlayer.size(); j++) {
                //Schon in den aktuellen Spielern vorhanden, dann überspringen
                if (((Player) playerVector.get(i)).equals(m_vPlayer.get(j))) {
                    playerVector.remove(i);

                    //Index einen zurücksetzen, da ein wert gelöscht wurde
                    i--;
                    break;
                }
            }
        }

        m_vOldPlayer = playerVector;
    }

    /**
     * Gibt alle alten Player (nicht mehr im Team befindliche) zurück
     */
    public final List<Player> getAllOldSpieler() {
    	if ( m_vOldPlayer == null){
    		m_vOldPlayer = DBManager.instance().getAllSpieler();
		}
        return m_vOldPlayer;
    }

    //---------Player--------------------------------------

    /**
     * Returns all current players
     */
    public final List<Player> getCurrentPlayer() {
    	if ( m_vPlayer == null){
			m_vPlayer = DBManager.instance().getSpieler(this.m_iID);
		}
        return m_vPlayer;
    }

    /**
     * Setzt neue Aufstellung
     */
    public final void setAufstellung(Lineup aufstellung) {
        m_clAufstellung = aufstellung;
    }

    //---------Aufstellung ----------------------------------

    /**
     * returns the lineup
     */
    public final Lineup getLineup() {
		if (m_clAufstellung == null){
			m_clAufstellung = DBManager.instance().getAufstellung(this.m_iID, Lineup.DEFAULT_NAME);
		}
		m_clAufstellung.setRatings();
        return m_clAufstellung;
    }

	public final Lineup getCurrentLineup() {
    	if (m_clAufstellung == null){
    		m_clAufstellung = DBManager.instance().getAufstellung(this.m_iID, Lineup.DEFAULT_NAME);
		}
		return m_clAufstellung;
	}

	/**
	 * returns the lineup
	 */
	public final Lineup getLineupWithoutRatingRecalc() {
		return getCurrentLineup();
	}

    /**
     * Setzt neue Basics
     */
    public final void setBasics(Basics basics) {
        m_clBasics = basics;
    }

    //----------Basics----------------------------------------

    /**
     * Returns Basic information
     */
    public final Basics getBasics() {
    	if ( m_clBasics == null){
    		m_clBasics = DBManager.instance().getBasics(this.m_iID);
		}
        return m_clBasics;
    }

    /**
     * Setzt neue Finanzen
     */
    public final void setFinanzen(Finanzen finanzen) {
        m_clFinanzen = finanzen;
    }

    //-------Finanzen---------------------------------------

    /**
     * Returns finance information
     */
    public final Finanzen getFinanzen() {
		if ( m_clFinanzen == null){
			m_clFinanzen = DBManager.instance().getFinanzen(this.m_iID);
		}
        return m_clFinanzen;
    }

    /**
     * Setter for property m_iID.
     *
     * @param m_iID New value of property m_iID.
     */
    public final void setID(int m_iID) {
        this.m_iID = m_iID;
    }

    //------ID-------------------------

    /**
     * Getter for property m_iID.
     *
     * @return Value of property m_iID.
     */
    public final int getID() {
        return m_iID;
    }

    /**
     * Setzt neue Aufstellung
     */
    public final void setPreviousLineup(Lineup aufstellung) {
        m_clLastAufstellung = aufstellung;
    }

    /**
     * Returns previous lineup
     */
    public final Lineup getPreviousLineup() {
    	if ( m_clLastAufstellung == null){
    		m_clLastAufstellung = DBManager.instance().getAufstellung(this.m_iID, Lineup.DEFAULT_NAMELAST);
		}
        return m_clLastAufstellung;
    }

    /**
     * Setzt neue Basics
     */
    public final void setLiga(Liga liga) {
        m_clLiga = liga;
    }

    //----------Liga----------------------------------------

    /**
     * Returns league information
     */
    public final Liga getLiga() {
    	if  ( m_clLiga == null){
    		m_clLiga = DBManager.instance().getLiga(this.m_iID);
		}
        return m_clLiga;
    }

    /**
     * Set Player list of the current team
     */
    public final void setCurrentPlayer(Vector<Player> playerVector) {
        m_vPlayer = playerVector;
    }

    /**
     * Returns Player of the current team with given Id
     */
    public final Player getCurrentPlayer(int id) {
    	for ( Player p : getCurrentPlayer()){
    		if ( p.getSpielerID()==id)
    			return p;
		}
        return null;
    }

    /**
     * Setter for property m_clSpielplan.
     *
     * @param m_clSpielplan New value of property m_clSpielplan.
     */
    public final void setSpielplan(module.series.Spielplan m_clSpielplan) {
        this.m_clSpielplan = m_clSpielplan;
    }

    //-----------------------Spielplan----------------------------------------//

    /**
     * Getter for property m_clSpielplan. (Attention: This is only valid for the current model)
     *
     * @return Value of property m_clSpielplan.
     */
    public final module.series.Spielplan getSpielplan() {
    	if ( m_clSpielplan == null){
    		m_clSpielplan = DBManager.instance().getSpielplan(-1, -1); // valid only for the current Model
		}
        return m_clSpielplan;
    }

    /**
     * Setzt neues Stadium
     */
    public final void setStadium(Stadium stadium) {
        m_clStadium = stadium;
    }

    //--------Stadium----------------------------------------

    /**
     * Returns stadium information
     */
    public final Stadium getStadium() {
    	if ( m_clStadium == null){
    		m_clStadium = DBManager.instance().getStadion(this.m_iID);
		}
        return m_clStadium;
    }

    // ---------------- Staff -----------------------------
    
    /**
     * Sets the staff list
     * @param staff
     */
    public final void setStaff (List<StaffMember> staff) {
    	m_clStaff = staff;
    }
    
    /**
     * Returns the staff list
     * @return
     */
    public List<StaffMember> getStaff() {
    	if ( m_clStaff == null){
    		m_clStaff = DBManager.instance().getStaffByHrfId(this.m_iID);
		}
    	return m_clStaff;
    }
    
    /**
     * Setzt neues Team
     */
    public final void setTeam(Team team) {
        m_clTeam = team;
    }

    //----------Team----------------------------------------

    /**
     * Gibt das Team zurück
     */
    public final Team getTeam() {
    	if ( m_clTeam == null){
    		m_clTeam = DBManager.instance().getTeam(this.m_iID);
		}
        return m_clTeam;
    }

    /**
     * Gibt den Trainer zurück
     */
    public final Player getTrainer() {
		Player trainer = null;
    	for ( Player p : getCurrentPlayer()){
			if ( p.isTrainer()){
				if (trainer == null || p.getTrainer() > trainer.getTrainer()){
					trainer = p;
				}
			}
		}

        // Nt team protection, they may have no coach:
        if (trainer == null)
        {
        	trainer = new Player();
        	trainer.setTrainer(7);
        	trainer.setTrainerTyp(2); // neutral;
        }
        
        return trainer;
    }

    /**
     * Setzt neuen Verein
     */
    public final void setVerein(Verein verein) {
        m_clVerein = verein;
    }

    //----------Verein----------------------------------------

    /**
     * Returns club information
     */
    public final Verein getVerein() {
    	if  ( m_clVerein == null){
    		m_clVerein = DBManager.instance().getVerein(this.m_iID);
		}
        return m_clVerein;
    }

    /**
     * Setter for property m_clXtraDaten.
     *
     * @param m_clXtraDaten New value of property m_clXtraDaten.
     */
    public final void setXtraDaten(core.model.XtraData m_clXtraDaten) {
        this.m_clXtraDaten = m_clXtraDaten;
    }

    /**
     * Getter for property m_clXtraDaten.
     *
     * @return Value of property m_clXtraDaten.
     */
    public final core.model.XtraData getXtraDaten() {
    	if ( m_clXtraDaten == null) {
    		m_clXtraDaten = DBManager.instance().getXtraDaten(this.m_iID);
		}
        return m_clXtraDaten;
    }

    /**
     * Fügt einen Player hinzu (wofür auch immer...)
     */
    public final void addSpieler(Player player) {
        m_vPlayer.add(player);
    }

    /**
     * Caclulates the subskill of each player, based on training and the previous hrf.
     */
    public final void calcSubskills() {

    	boolean doOnce = false;
    	
    	final List<Player> vPlayer = getCurrentPlayer();
    	final java.sql.Timestamp calcDate = getBasics().getDatum();
    	
    	final int previousHrfId = DBManager.instance().getPreviousHRF(m_iID);
    	
    	Timestamp trainingDateOfPreviousHRF = null;
    	Timestamp trainingDateOfCurrentHRF = getXtraDaten().getTrainingDate();
    	if (previousHrfId > -1) {
    		trainingDateOfPreviousHRF = DBManager.instance()
    									.getXtraDaten(previousHrfId)
    									.getTrainingDate();
    	}
    	else {
    		// handle the very first hrf download
			trainingDateOfPreviousHRF = new Timestamp(0); // fetch all trainings before the first hrf was loaded
		}
    	
    	if ((trainingDateOfPreviousHRF != null) && (trainingDateOfCurrentHRF != null)) {
    		// Training Happened

    		// Find TrainingPerWeeks that should be processed (those since last training).
    		List<TrainingPerWeek> rawTrainingList = TrainingManager.instance().getTrainingWeekList();
    		List<TrainingPerWeek> trainingList = new ArrayList<TrainingPerWeek>();
    		for (TrainingPerWeek tpw : rawTrainingList) {
    			// We want to add all weeks with nextTraining after the previous date, and stop
    			// when we are after the current date.
    			
    			if (tpw.getNextTrainingDate().after(trainingDateOfCurrentHRF)) {
    				break;
    			}
    			
    			if (tpw.getNextTrainingDate().after(trainingDateOfPreviousHRF)) {
    				trainingList.add(tpw);
    			}
    		}
    		
    		// Get the trainer skill
    		int trainerLevel;
    		Player trainer = getTrainer();
    		if (trainer != null) {
    			trainerLevel = trainer.getTrainer();
    		} else {
    			// default to solid
    			trainerLevel = 7;
    		}
    		
    		// Generate a map with spielers from the last hrf.
    		final Map<Integer, Player> players = new HashMap<>();
    		for ( Player p: DBManager.instance().getSpieler(previousHrfId)){
    			players.put(p.getSpielerID(), p);
    		}

    		// Train each player
    		//for (int i = 0; i < vPlayer.size(); i++) {
			for  ( Player player : vPlayer){
    			try {

    				// The version of the player from last hrf
    				Player old = players.get(player.getSpielerID());
    				if (old == null ) {
    					if (TrainingManager.TRAININGDEBUG) {
    						HOLogger.instance().debug(HOModel.class, "Old player for id "+player.getSpielerID()+" = null");
    					}
    					// Player appears the first time
						// - was bought new
						// - promoted from youth
						// - it is the first hrf ever loaded
    					old = new Player();
    					old.setSpielerID(player.getSpielerID());
    					old.copySkills(player);
    					if (HOVerwaltung.instance().getModel().getCurrentPlayer(player.getSpielerID()) != null){
    						// PLayer is in current team (not an historical player)
							List<TrainingEvent> events = player.downloadTrainingEvents();
							if (events != null) {
								for (TrainingEvent event : events) {
									if (event.getEventDate().compareTo(player.getHrfDate()) <= 0) {
										old.setValue4Skill4(event.getPlayerSkill(), event.getOldLevel());
									}
								}
							}
						}
    				}

    				// Always copy subskills as the first thing
    				player.copySubSkills(old);
    				
    				// Always check skill drop if drop calculations are active.
    				if (SkillDrops.instance().isActive()) {
    					for (int skillType=0; skillType < PlayerSkill.EXPERIENCE; skillType++) {
    						if ((skillType == PlayerSkill.FORM) || (skillType == PlayerSkill.STAMINA)) { 
    							continue;
    						}
    						if (player.check4SkillDown(skillType, old)) {
    							player.dropSubskills(skillType);
    						}
    					}
    				}

    				if (trainingList.size() > 0 ) {
    					// Training happened

	    				// Perform training for all "untrained weeks"
    				
	    				// An "old" player we can mess with.
	    				Player tmpOld = new Player();
						tmpOld.copySkills(old);
						tmpOld.copySubSkills(old);
						tmpOld.setSpielerID(old.getSpielerID());
						tmpOld.setAlter(old.getAlter());
	
	    				Player calculationPlayer = null;
	    				TrainingPerWeek tpw;
	    				Iterator<TrainingPerWeek> iter = trainingList.iterator(); 
	    				while (iter.hasNext()) {
	    					tpw = iter.next();
	    					
	    					if (tpw == null) {
	    						continue;
	    					}
	    					
	    					// The "player" is only the relevant Player for the current Hrf. All previous
	    					// training weeks (if any), should be calculated based on "old", and the result
	    					// of the previous week.
	    					
	    					if (getXtraDaten().getTrainingDate().getTime() == tpw.getNextTrainingDate().getTime()) {
	    						// It is the same week as this model.
	    						
	    						if (calculationPlayer != null) {
	    							// We have run previous calculations because of missing training weeks. 
	    							// Subskills may have changed, but no skillup can have happened. Copy subskills.
	    							
	    							player.copySubSkills(calculationPlayer);
	    						}
	    						calculationPlayer = player;
	    	
	    					} else {
	    						// An old week
	    						calculationPlayer = new Player();
	    						calculationPlayer.copySkills(tmpOld);
	    						calculationPlayer.copySubSkills(tmpOld);
	    						calculationPlayer.setSpielerID(tmpOld.getSpielerID());
	    						calculationPlayer.setAlter(tmpOld.getAlter());
	    					}
	    		
	    					calculationPlayer.calcIncrementalSubskills(tmpOld, tpw.getAssistants(),
	    							trainerLevel,
	    							tpw.getTrainingIntensity(),
	    							tpw.getStaminaPart(),
	    							tpw,
	    							getStaff());
	    					
	    					if (iter.hasNext()) {
	    						// Use calculated skills and subskills as "old" if there is another week in line... 
	    						tmpOld = new Player();
	    						tmpOld.copySkills(calculationPlayer);
	    						tmpOld.copySubSkills(calculationPlayer);
	    						tmpOld.setSpielerID(calculationPlayer.getSpielerID());
	    						tmpOld.setAlter(calculationPlayer.getAlter());
	    					}
	    				}
    				}

    				/**
    				 * Start of debug
    				 */
    				if (TrainingManager.TRAININGDEBUG) {
    					 HelperWrapper helper = HelperWrapper.instance();
    					HTCalendar htcP;
    					String htcPs = "";
    					if (trainingDateOfPreviousHRF != null) {
    						htcP = HTCalendarFactory.createTrainingCalendar(new Date(trainingDateOfPreviousHRF.getTime()));
    						htcPs = " ("+htcP.getHTSeason()+"."+htcP.getHTWeek()+")";
    					}
    					HTCalendar htcA = HTCalendarFactory.createTrainingCalendar(new Date((trainingDateOfCurrentHRF.getTime())));
    					String htcAs = " ("+htcA.getHTSeason()+"."+htcA.getHTWeek()+")";
    					HTCalendar htcC = HTCalendarFactory.createTrainingCalendar(new Date((calcDate.getTime())));
    					String htcCs = " ("+htcC.getHTSeason()+"."+htcC.getHTWeek()+")";

    					TrainingPerWeek trWeek = TrainingWeekManager.instance().getTrainingWeek(getXtraDaten().getTrainingDate());
    					HOLogger.instance().debug(HOModel.class,
    							"WeeksCalculated="+trainingList.size()+", trArt="+(trWeek==null?"null":""+trWeek.getTrainingType())
    							+ ", numPl="+ vPlayer.size()+", calcDate="+calcDate.toString()+htcCs
    							+ ", act="+trainingDateOfCurrentHRF.toString() +htcAs
    							+ ", prev="+(trainingDateOfPreviousHRF==null?"null":trainingDateOfPreviousHRF.toString()+htcPs)
    							+ " ("+previousHrfId+")");

    					if (trainingList.size() > 0)
    						logPlayerProgress (old, player);

    				}
    				/**
    				 * End of debug
    				 */

    			} catch (Exception e) {
    				HOLogger.instance().log(getClass(),e);
    				HOLogger.instance().log(getClass(),"Model calcSubskill: " + e);
    			}
    		}

    		//Player
    		DBManager.instance().saveSpieler(m_iID, getCurrentPlayer(), getBasics().getDatum());
    	}
    }

    private void logTraining() {
    	
    }
    
    
    private void logPlayerProgress (Player before, Player after) {
    	
    	if ((after == null) || (before == null)) {	
    		// crash due to non paranoid logging is too silly
    		return;
    	}
    	
    	int playerID = after.getSpielerID();
    	String playerName = after.getFullName();
    	TrainingPerWeek train = TrainingWeekManager.instance().getTrainingWeek(getXtraDaten().getTrainingDate());
    	if (train == null) { 
    		// Just say no to logging crashes.
    		return;
    	}
    	
    	int trLevel = train.getTrainingIntensity();
    	int trArt = train.getTrainingType();
    	String trArtString = TrainingType.toString(trArt);
    	int trStPart = train.getStaminaPart();
    	int age = after.getAlter();
    	int skill = -1;
		int beforeSkill = 0;
		int afterSkill = 0;
    	switch (trArt) {
    	case TrainingType.WING_ATTACKS:
    	case TrainingType.CROSSING_WINGER:
    		skill = PlayerSkill.WINGER;
    		break;
    	case TrainingType.SET_PIECES:
    		skill = PlayerSkill.SET_PIECES;
    		break;
    	case TrainingType.DEF_POSITIONS:
    	case TrainingType.DEFENDING:
    		skill = PlayerSkill.DEFENDING;
    		break;
    	case TrainingType.SHOOTING:
    	case TrainingType.SCORING:
    		skill = PlayerSkill.SCORING;
    		break;
    	case TrainingType.SHORT_PASSES:
    	case TrainingType.THROUGH_PASSES:
    		skill = PlayerSkill.PASSING;
    		break;
    	case TrainingType.PLAYMAKING:
    		skill = PlayerSkill.PLAYMAKING;
    		break;
    	case TrainingType.GOALKEEPING:
    		skill = PlayerSkill.KEEPER;
    		break;
    	}
    	if (skill >= 0) {
    		beforeSkill = before.getValue4Skill4(skill);
    		afterSkill = after.getValue4Skill4(skill);
    		int beforeStamina = before.getKondition();
    		int afterStamina = after.getKondition();
    		double beforeSub = before.getSubskill4Pos(skill);
    		double afterSub = after.getSubskill4Pos(skill);


    		HOLogger.instance().debug(getClass(), "TrLog:" + m_iID + "|"
    				+ m_clBasics.getSeason() + "|" + m_clBasics.getSpieltag() + "|"
    				+ playerID + "|" + playerName + "|" + age + "|"
    				+ trArt + "|" + trArtString + "|" + trLevel + "|" + trStPart + "|"
    				+ beforeStamina + "|" + afterStamina + "|"
    				+ beforeSkill + "|" + core.util.Helper.round(beforeSub, 2) + "|"
    				+ afterSkill + "|" + core.util.Helper.round(afterSub, 2)
    				);
    	}
    }
    public final void loadStdAufstellung() {
        m_clAufstellung = DBManager.instance().getAufstellung(-1,
                                                                                            Lineup.DEFAULT_NAME);

        //prüfen ob alle aufgstellen Player noch existieren
        m_clAufstellung.checkAufgestellteSpieler();
    }

    public final void loadStdLastAufstellung() {
        m_clLastAufstellung = DBManager.instance().getAufstellung(-1,
                                                                                                Lineup.DEFAULT_NAMELAST);

        //prüfen ob alle aufgstellen Player noch existieren
        m_clLastAufstellung.checkAufgestellteSpieler();
    }

    /**
     * Entfernt einen Player
     */
    public final void removeSpieler(Player player) {
        m_vPlayer.remove(player);
    }
   
    /**
     * save the model in the database
     */
    public final synchronized void saveHRF() {
        DBManager.instance().saveHRF(m_iID,
        		java.text.DateFormat.getDateTimeInstance().format(new java.util.Date(
        				System.currentTimeMillis())), m_clBasics.getDatum());

        //basics
        DBManager.instance().saveBasics(m_iID, m_clBasics);
        //Verein
        DBManager.instance().saveVerein(m_iID, m_clVerein);
        //Team
        DBManager.instance().saveTeam(m_iID, m_clTeam);
        //Finanzen
        DBManager.instance().saveFinanzen(m_iID, m_clFinanzen, m_clBasics.getDatum());
        //Stadion
        DBManager.instance().saveStadion(m_iID, m_clStadium);
        //Liga
        DBManager.instance().saveLiga(m_iID, m_clLiga);
        //Aufstellung + aktu Sys als Standard saven
        DBManager.instance().saveAufstellung(m_iID, m_clAufstellung, Lineup.DEFAULT_NAME);
        //Aufstellung + aktu Sys als Standard saven
        DBManager.instance().saveAufstellung(m_iID, m_clLastAufstellung, Lineup.DEFAULT_NAMELAST);
        //Xtra Daten
        DBManager.instance().saveXtraDaten(m_iID, m_clXtraDaten);
        //Player
        DBManager.instance().saveSpieler(m_iID, m_vPlayer, m_clBasics.getDatum());
        //Staff
        DBManager.instance().saveStaff(m_iID, m_clStaff);
    }

    /**
     * Speichert den Spielplan in der DB
     */
    public final synchronized void saveSpielplan2DB() {
        if (m_clSpielplan != null) 
            DBManager.instance().storeSpielplan(m_clSpielplan);
    }
}
