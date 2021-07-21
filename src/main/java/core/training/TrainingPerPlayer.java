package core.training;

import core.model.enums.MatchType;
import core.model.match.IMatchType;
import core.model.player.Player;
import core.util.HOLogger;

import java.sql.Timestamp;


/**
 * Holds and calculates how much skill training a player received
 *
 * @author
 */
public class TrainingPerPlayer  {
    private Player _Player;
    private TrainingPoints _TrainingPair = null;
    private TrainingPerWeek _TrainingWeek;
	private double experienceSub=0;

	//~ Constructors -------------------------------------------------------------------------------

	/**
     * Creates a new TrainingPerPlayer object initialized with a specific player
     */
    public TrainingPerPlayer(Player oPlayer) {
    	_Player = oPlayer; 
    }

    //~ Methods ------------------------------------------------------------------------------------

    
    /**
     * Setter for property player.
     *
     * @param player New value of property player.
     */
    public final void setPlayer(Player player) {
        this._Player = player;
    }

    /**
     * Getter for property spieler.
     *
     * @return Value of property spieler.
     */
    public final Player getPlayer() {
        return this._Player;
    }

    public void setTrainingPair(TrainingPoints trp) {
		_TrainingPair = trp;
	}

    public TrainingPoints getTrainingPair() {
    	return _TrainingPair;
    }

    /**
     * add sub values of another ITrainingPerPlayer instance to this instance
     * @param values	the instance we take the values from
     */
    public void addValues(TrainingPerPlayer values) {
    	if (_TrainingPair == null) {
    		if (values != null && values.getTrainingPair() != null) {
    			_TrainingPair = values.getTrainingPair();
    		}
    		else
    		{
	    		HOLogger.instance().error(getClass(), "_TrainingPair is null. Aborting addValues.");
    		}
    	} else { 
	    	_TrainingPair.addPrimary(values.getTrainingPair().getPrimary());
	    	_TrainingPair.addSecondary(values.getTrainingPair().getSecondary());
    	}
    }
	/**
	 * Checks if trainingDate is after the last skill up in skillType
	 * 
	 * @param trainingDate
	 * @param skillType
	 * @return

	private boolean isAfterSkillup (Calendar trainingDate, int skillType) {
		if (getTimestamp() == null) {
			if (TrainingManager.TRAINING_DEBUG) {
				HOLogger.instance().debug(getClass(), 
						"isAfterSkillup: traindate NULL (" + skillType + ") is always after skillup");
			}
			return true;			
		}
		Date skillupTime = getLastSkillupDate(skillType, getTimestamp());
		if (trainingDate.getTimeInMillis() > skillupTime.getTime()) {
			if (TrainingManager.TRAINING_DEBUG) {
				HOLogger.instance().debug(getClass(), 
						"isAfterSkillup: traindate "+trainingDate.getTime().toString() 
						+ " (" + skillType + ") is after skillup");
			}
			return true;	
		} else {
			if (TrainingManager.TRAINING_DEBUG) {
				HOLogger.instance().debug(getClass(), 
						"isAfterSkillup: traindate "+trainingDate.getTime().toString() 
						+ " (" + skillType + ") is NOT after skillup");
			}
			return false;
		}
	}
	 */

   /**
     * Calculates the last skillup for the player in the correct training
     *
     * @param trainskill Skill we are looking for a skillup
     * @param trainTime Trainingtime
     *
     * @return Last skillup Date, or Date(0) if no skillup was found

    private Date getLastSkillupDate(int trainskill, Date trainTime) {
        //get relevant skillups for calculation period
        final Vector<Object[]> skillUps = getPlayer().getAllLevelUp(trainskill);
        Date skilluptime = new Date(0);
        for (Iterator<Object[]> it = skillUps.iterator(); it.hasNext();) {
            final Object[] aobj = it.next();
            final Boolean bLevel = (Boolean) aobj[1];

            if (bLevel.booleanValue() == true) {
                final Date tmpTime = new Date(((Timestamp) aobj[0]).getTime());
                if ((tmpTime.before(trainTime)) && (tmpTime.after(skilluptime))) {
                    skilluptime = HelperWrapper.instance().getLastTrainingDate(tmpTime, 
                    		HOVerwaltung.instance().getModel().getXtraDaten().getNextTrainingDate()).getTime();
                }
            }
        }
        return skilluptime;
    }
   */
    /**
     * get the training point for this instance
     * @return	training point
     */
	public TrainingPerWeek getTrainingWeek() {
		return _TrainingWeek;
	}

	/**
	 * set the training point for this instance and
	 * calculate the sub skills for the player using 
	 * the training week from this training point
	 *  
	 * @param trainingWeek training week info
	 */
	public void setTrainingWeek(TrainingPerWeek trainingWeek) {
		this._TrainingWeek = trainingWeek;
	}

    public void addExperience(double inc) {
		this.experienceSub += inc;
    }

	public double getExperienceSub() {
		return experienceSub;
	}

    public float calcSubskillIncrement(int skill, float skillValueBeforeTraining) {

		int skillValue = (int)skillValueBeforeTraining;
		float ret = 0;

		/* Time to perform skill drop */
		if (SkillDrops.instance().isActive()) {
			ret -= SkillDrops.instance().getSkillDrop(skillValue, this._Player.getAlter(), skill) / 100;
		}

		var wt = WeeklyTrainingType.instance(this._TrainingWeek.getTrainingType());
		if ( wt != null ) {
			if (skill == wt.getPrimaryTrainingSkill()) {
				ret += wt.getTrainingAlternativeFormula(skillValue, this, true);
			} else if (skill == wt.getSecondaryTrainingSkill()) {
				ret += wt.getTrainingAlternativeFormula(skillValue, this, false);
			}
		}

		if (ret > 1) ret = 1; // limit 1

//		HOLogger.instance().info(this.getClass(),
//				_Player.getLastName() +
//						"; Age=" + _Player.getAlter() +
//						"; Minutes=" + this.logTrainingMinutes() +
//						"; Training: " + (wt!=null?wt._Name:"unknown") +
//						"; " + PlayerSkill.toString(skill) + " before Training: " + skillValueBeforeTraining +
//						" Increment=" + ret
//		);

		return ret;
	}

	private String logTrainingMinutes() {
		if ( this.getTrainingPair() != null){
			var duration = this.getTrainingPair().getTrainingDuration();
			if ( duration != null){
				return duration.getFullTrainingMinutes() +
						";" + duration.getPartlyTrainingMinutes() +
						";" + duration.getOsmosisTrainingMinutes();
			}
		}
		return "0;0;0";
	}

	/**
	 * Calculate player's age in years at the given training date
	 * @return int age of the player in hattrick-years
	 */
	public int getPlayerAgeAtTrainingDate() {
		Timestamp t = Timestamp.from(this._TrainingWeek.getTrainingDate());
		return (int) this.getPlayer().getDoubleAgeFromDate(t);
	}
}
