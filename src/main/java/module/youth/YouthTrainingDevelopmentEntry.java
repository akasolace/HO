package module.youth;

import core.model.HOVerwaltung;
import core.model.match.MatchLineupTeam;
import core.model.enums.MatchType;
import core.model.player.Player;
import core.model.player.Specialty;
import module.training.Skills;

import java.sql.Timestamp;
import java.util.stream.Collectors;

public class YouthTrainingDevelopmentEntry {

    /**
     * The trained player
     */
    private YouthPlayer player;

    /**
     * Training information including match details
     */
    private YouthTraining training;

    /**
     * Skills of the player after the training match
     * (calculated by calling calcSkills)
     */
    private YouthSkillsInfo skills;

    /**
     * Specialty of the player mentioned in the match highlights
     */
    private Specialty specialty;

    /**
     * Number of injured weeks
     */
    private int injuredLevel;

    /**
     * Player is suspended (red card or third yellow card)0
     */
    private boolean isSuspended;

    public YouthTrainingDevelopmentEntry(YouthPlayer player, YouthTraining training) {
        this.training = training;
        this.player = player;
        findSpecialty();
        if (specialty != null) {
            this.player.setSpecialty(specialty);
        }
    }

    /**
     * Check match highlights if special event of the player is included that indicates his specialty
     */
    private void findSpecialty() {
        var matchDetails = training.getMatchDetails();
        var highlights = matchDetails.getHighlights().stream()
                .filter(h -> h.getPlayerId() == player.getId() || h.getAssistingPlayerId() == player.getId())
                .collect(Collectors.toList());
        for (var highlight : highlights) {
            if (highlight.getPlayerId() == player.getId()) {
                switch (highlight.getMatchEventID()) {

                    case SE_NO_GOAL_CORNER_HEAD_SPECIALIST:
                    case SE_GOAL_CORNER_HEAD_SPECIALIST:
                    case SE_WINGER_TO_HEAD_SPEC_SCORES:
                        this.specialty = Specialty.Head;
                        break;

                    case SE_GOAL_UNPREDICTABLE_OWN_GOAL:
                    case SE_GOAL_UNPREDICTABLE_SCORES_ON_HIS_OWN:
                    case SE_NO_GOAL_UNPREDICTABLE_ALMOST_SCORES:
                    case SE_NO_GOAL_UNPREDICTABLE_OWN_GOAL_ALMOST:
                        this.specialty = Specialty.Unpredictable;
                        break;

                    case SE_NO_GOAL_POWERFUL_NORMAL_FORWARD_GENERATES_EXTRA_CHANCE:
                    case SE_GOAL_POWERFUL_NORMAL_FORWARD_GENERATES_EXTRA_CHANCE:
                    case SE_POWERFUL_DEFENSIVE_INNER_PRESSES_CHANCE:
                    case SE_POWERFUL_SUFFERS_FROM_SUN:
                    case SE_POWERFUL_THRIVES_IN_RAIN:
                        this.specialty = Specialty.Powerful;
                        break;

                    case SE_QUICK_LOSES_IN_RAIN:
                    case SE_QUICK_LOSES_IN_SUN:
                    case SE_QUICK_RUSHES_STOPPED_BY_QUICK_DEFENDER:
                    case SE_QUICK_SCORES_AFTER_RUSH:
                    case SE_SPEEDY_MISSES_AFTER_RUSH:
                        this.specialty = Specialty.Quick;
                        break;

                    case SE_SUPPORT_PLAYER_BOOST_FAILED:
                    case SE_SUPPORT_PLAYER_BOOST_FAILED_AND_ORGANIZATION_DROPPED:
                    case SE_SUPPORT_PLAYER_BOOST_SUCCEEDED:
                        this.specialty = Specialty.Support;
                        break;
                    case SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER:
                    case SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER_NO_GOAL:
                    case SE_TECHNICAL_SUFFERS_FROM_RAIN:
                    case SE_TECHNICAL_THRIVES_IN_SUN:
                        this.specialty = Specialty.Technical;
                        break;
                }
            } else {
                // Analyse assistant
                switch (highlight.getMatchEventID()) {
                    case SE_QUICK_RUSHES_PASSES_AND_RECEIVER_SCORES:
                    case SE_QUICK_RUSHES_STOPPED_BY_QUICK_DEFENDER:
                    case SE_QUICK_RUSHES_PASSES_BUT_RECEIVER_FAILS:
                        this.specialty = Specialty.Quick;
                        break;
                    case SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER:
                    case SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER_NO_GOAL:
                        this.specialty = Specialty.Head;
                        break;
                    case SE_GOAL_UNPREDICTABLE_SPECIAL_ACTION:
                    case SE_GOAL_UNPREDICTABLE_LONG_PASS:
                    case SE_GOAL_UNPREDICTABLE_MISTAKE:
                    case SE_NO_GOAL_UNPREDICTABLE_LONG_PASS:
                    case SE_NO_GOAL_UNPREDICTABLE_MISTAKE:
                    case SE_NO_GOAL_UNPREDICTABLE_SPECIAL_ACTION:
                        this.specialty = Specialty.Unpredictable;
                        break;
                }
            }
        }
    }

    /**
     * Check if downloaded hattrick skill information contradict calculated values
     * and adjust calculation accordingly
     *
     * @param player, player to address adjustments
     * @param skillConstraints, skill information of hattrick download
     */
    public void setSkillConstraints(YouthPlayer player, YouthSkillsInfo skillConstraints) {
        if (skillConstraints != null) {
            for (var constraint : skillConstraints.values()) {
                var skill = this.skills.get(constraint.getSkillID());
                var oldVal = skill.getCurrentValue();
                skill.setCurrentLevel(constraint.getCurrentLevel());
                skill.setMax(constraint.getMax());
                skill.setMaxReached(constraint.isMaxReached());
                var adjustment = skill.getCurrentValue() - oldVal;
                if (adjustment != 0) {
                    skill.setStartValue(player.adjustSkill(skill.getSkillID(), adjustment, this.getMatchDate()));
                }
            }
        }
    }

    /**
     * Calculation of the skills achieved by the training
     *
     * @param startSkills, skills before training
     * @param skillConstraints, skills downloaded from hattrick
     * @param lineupTeam, lineup of the team
     * @return calculated skills
     */

    public YouthSkillsInfo calcSkills(YouthSkillsInfo startSkills, YouthSkillsInfo skillConstraints, MatchLineupTeam lineupTeam) {
        if ( this.skills == null){
            this.skills = new YouthSkillsInfo();
        }
        for (var skill : startSkills.values()) {
            this.skills.put(skill.getSkillID(), training.calcSkill(skill, player, lineupTeam));
        }
        setSkillConstraints(player, skillConstraints);
        return this.skills;
    }

    /**
     * Date of the training match
     * @return timestamp
     */
    public Timestamp getMatchDate() {
        return this.training.getMatchDate();
    }

    /**
     * Id of the training match
     * @return int
     */
    public int getMatchId() {
        return this.training.getMatchId();
    }

    /**
     * Match opponents
     * @return string
     */
    public String getMatchName() {
        return this.training.getHomeTeamName() + "-" + this.training.getGuestTeamName();
    }

    /**
     * Training match type
     * @return MatchType
     */
    public MatchType getMatchType() {
        return this.training.getMatchType();
    }

    /**
     * Training
     * @return YouthTraining
     */
    public YouthTraining getTraining() {
        return this.training;
    }

    /**
     * Player age as string
     * @return String
     */
    public String getPlayerAge() {
        return Player.getAgeWithDaysAsString(this.player.getAgeYears(),this.player.getAgeDays(),this.getMatchDate().getTime());
    }

    /**
     * Skill value as string
     * @param skillID skill id
     * @return String
     */
    public String getSkillValue(Skills.HTSkillID skillID) {
        var val = this.skills.get(skillID);
        if ( val != null ) return String.format("%,.2f", val.getCurrentValue());
        return "";
    }

    /**
     * Training priority (primary, secondary) as string
     * @param prio Priority
     * @return String
     */
    public String getTrainingType(YouthTraining.Priority prio) {
        return YouthTrainingType.StringValueOf(this.training.getTraining(prio));
    }

    /**
     * Minutes in sector
     * @return String
     */
    public String getPlayerSector() {
        return this.training.getPlayerTrainedSectors(this.player.getId());
    }

    /**
     * All Skills
     * @return YouthSkillsInfo
     */
    public YouthSkillsInfo getSkills() {
        return this.skills;
    }

    /**
     * Player's age (Years only)
     * @return int
     */
    public int getPlayerAgeYears() {
        return this.player.getAgeYearsAtDate(this.training.getMatchDate());
    }

    /**
     * Player's rating in training match
     * @return double
     */
    public double getRating() {
        return this.training.getRating(this.player.getId());
    }

    /**
     * Player's specialty found in match highlights
     * @return String
     */
    public String getSpecialtyString() {
        if ( this.specialty != null ) return HOVerwaltung.instance().getLanguageString("ls.player.speciality." + this.specialty.toString().toLowerCase());
        return "";
    }

    public void setInjuryLevel(int injuryLevel) {
        this.injuredLevel =injuryLevel;
    }

    public void setIsSuspended(boolean suspended) {
        this.isSuspended = suspended;
    }

    public String getSupendedAsString() {
        if ( isSuspended ) return HOVerwaltung.instance().getLanguageString("ls.youth.isSuspended");
        return "";
    }

    public String getInjuredLevelAsString() {
        if (this.injuredLevel > 0) {
            return "" + this.injuredLevel;
        }
        return "";
    }
}
