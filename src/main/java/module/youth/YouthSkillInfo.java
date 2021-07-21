package module.youth;

import module.training.Skills;

public class YouthSkillInfo {

    /**
     * Skill Id
     */
    private Skills.HTSkillID skillID;

    /**
     * Value at scouting date, edited by the user (user's estimation)
     */
    private double startValue;

    /**
     * Calculated value based on trainings and edited start value
     */
    private double currentValue;

    /**
     * Skill level at the current download
     * null as long as not known
     */
    private Integer currentLevel;

    /**
     * Skill level at the scouting date
     * null as long as not known
     */
    private Integer startLevel;

    /**
     * Range of possible start values
     * if no scout info of start level is given the range will be limited by the first occurrence of a current value
     */
    private SkillRange startValueRange = new SkillRange();

    /**
     * Range of possible current values
     */
    private SkillRange currentValueRange = new SkillRange();

    /**
     * Maximum reachable skill level (potential)
     * null as long as not known
     */
    private Integer max;

    /**
     * Indicates if the skill cant be trained anymore (false if current or max is not available).
     */
    private boolean isMaxReached;

    /**
     * Reachable skill value if player will get optimal training
     * if player is older than 17 the training development status of age 17 is set.
     * Calculated value is not stored in database
     */
    private Double potential17Value;

    /**
     * Scout mentions up to 2 skill info. Both of them belong to the top 3 skills with highest maximum.
     * Information is used to restrict other skill maxima.
     *
     * True if skill is one of the skills mentioned by the scout or found maximum is greater than one of the scout infos
     * False if skill maximum is not one of the top 3 maximums
     * null otherwise (not known)
     */
    private Boolean isTop3Skill;

    /**
     * Constructor, only setting the skill id
     * @param id HTSkillId
     */
    public YouthSkillInfo(Skills.HTSkillID id) {
        this.skillID = id;
    }

    public boolean isCurrentLevelAvailable() {
        return currentLevel != null;
    }

    public boolean isStartLevelAvailable() {
        return startLevel != null;
    }

    public boolean isMaxAvailable() {
        return max != null;
    }

    public double getStartValue() {
        return this.startValue;
    }

    /**
     * Set the skill's start value.
     * AdjustValues is called to consider constraints by known skill levels downloaded from hattrick.
     * @param value New start value. This may be changed, if the value conflicts with skill constraints.
     */
    public void setStartValue(double value) {
        this.startValue = value;
        adjustValues();
    }

    /**
     * Checks and adjusts start and current value of the skill to respect constraints:
     * startlevel<=startValue<startLevel+1
     * currentLevel<=currentValue<currentLevel+1
     * 0<=startValue<=currentValue<=max+1 or 8.3
     *
     * Additionally possible ranges of start value (startValueRange) and currentValue (currentValueRange)
     * are examined.
     */
    private void adjustValues() {
        if (max != null) {
            if (currentValue > max + 1) {
                currentValue = max + 0.99;
            }
            currentValueRange.lessThan(max + 1);
            startValueRange.lessThan(max + 1);
        }

        if (currentLevel != null) {
            if (currentValue < currentLevel) {
                this.currentValue = currentLevel;
            } else if (currentValue > currentLevel + 1) {
                this.currentValue = currentLevel + 0.99;
            }

            startValueRange.lessThan(currentLevel + 1);
            currentValueRange.between(currentLevel, currentLevel + 1);
        }

        if (startLevel != null) {
            if (startValue < startLevel) {
                this.startValue = startLevel;
            } else if (startValue > startLevel + 1) {
                this.startValue = startLevel + 0.99;
            }
            if (currentValue < startValue) {
                currentValue = startValue;
            }
            startValueRange.between(startLevel, startLevel + 1);
            currentValueRange.greaterEqual(startLevel);
        } else if (currentValue < startValue) {
            if ( currentValue < 0) currentValue = 0;
            startValue = currentValue;
        } else if (startValue < 0) {
            startValue = 0;
        }
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double value) {
        this.currentValue = value;
        adjustValues();
    }

    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
        adjustValues();
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
        adjustValues();
    }

    /**
     * Skill maximum level is reached, but there might be some subskill points left for future training
     * @return true if current level is known and equal to maximum level
     */
    public boolean isMaxLevelReached() {
        return this.getCurrentLevel() != null  &&
                ( this.getCurrentLevel()==8 ||
                        this.isMaxAvailable() && this.getCurrentLevel() == this.getMax());
    }

    public boolean isMaxReached() {
        return isMaxReached;
    }

    public boolean isTrainingUsefull(){
        return !isMaxReached() && (!this.isMaxAvailable() || this.getMax()>4);
    }

    public void setMaxReached(boolean maxReached) {
        isMaxReached = maxReached;
    }

    public Integer getStartLevel() {
        return startLevel;
    }

    public void setStartLevel(Integer startLevel) {
        this.startLevel = startLevel;
        if (startLevel != null) this.startValueRange = new SkillRange(startLevel);
        adjustValues();
    }

    public SkillRange getStartValueRange() {
        return this.startValueRange;
    }

    public SkillRange getCurrentValueRange(){
        return this.currentValueRange;
    }

    public void setStartValueRange(SkillRange range) {
        this.startValueRange = range;
    }

    public Skills.HTSkillID getSkillID() {
        return skillID;
    }

    public void addStartValue(double val) {
        this.startValue += val;
    }

    public void setPotential17Value(double val) {
        this.potential17Value = val;
    }

    public Double getPotential17Value() {
        return potential17Value;
    }

    public void setIsTop3(Boolean b) {
        this.isTop3Skill = b;
    }

    public Boolean isTop3() {
        return this.isTop3Skill;
    }

    /**
     * get the minumum potential value. This is the maximum value if it is known
     * or the current level value, if maximum is unknown
     * @return int, 0 if neither current level nor maximum is known
     */
    public int getMinimumPotential() {
        if (this.max != null) return this.max;
        if (this.currentLevel != null) return this.currentLevel;
        return 0;
    }

    /**
     * Set the upper limit of skill maximum (potential).
     * The value is not for sure and could be reduced by future trainer reports
     * @param isKeeper Boolean
     *                  true, players skills are keeper skills
     *                  false, player skills are infield skills
     *                  null, unknown
     * @param minTop3Max int limit of potential
     */
    public void setMaxLimit(Boolean isKeeper, int minTop3Max) {
        if ( this.max != null && this.max <= minTop3Max) return;  // nothing to do
        if (  minTop3Max < 5) {
            setMax(minTop3Max);
        }
        else if ( isKeeper != null){
            if ( isKeeper) {
                switch (this.skillID) {
                    case Keeper, Defender, SetPieces -> setMax(minTop3Max);
                }
            }
            else {
                switch (this.skillID) {
                    case Defender, Winger, Playmaker, Passing, Scorer, SetPieces -> setMax(minTop3Max);
                }
            }
        }
        this.currentValueRange.lessThan(minTop3Max+1);
        this.startValueRange.lessThan(minTop3Max+1);
    }

    // Skill Range class
    public static class SkillRange {
        private double greaterEqual;
        private double lessThan;

        public SkillRange() {
            this(null);
        }

        public SkillRange(Integer level) {
            if (level != null) {
                greaterEqual = level;
                lessThan = level + 1;
            } else {
                greaterEqual = 0;
                lessThan = 8.3;
            }
        }

        public SkillRange(double min, double max) {
            this.lessThan = max;
            this.greaterEqual = min;
        }

        public double getGreaterEqual() {
            return greaterEqual;
        }

        public void setGreaterEqual(double greaterEqual) {
            this.greaterEqual = greaterEqual;
            if (this.lessThan < greaterEqual) this.lessThan = greaterEqual;
        }

        public double getLessThan() {
            return lessThan;
        }

        public void setLessThan(double lessThan) {
            this.lessThan = lessThan;
            if (this.greaterEqual >= lessThan) this.greaterEqual = lessThan;
        }

        public void lessThan(double limit) {
            if (this.lessThan > limit) {
                setLessThan(limit);
            }
        }

        public void between(double min, double max) {
            setGreaterEqual(min);
            setLessThan(max);
        }

        public void greaterEqual(double min) {
            if ( this.greaterEqual <min){
                setGreaterEqual(min);
            }
        }
    }
}

