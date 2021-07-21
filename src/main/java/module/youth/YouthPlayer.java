package module.youth;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.player.CommentType;
import core.model.player.Specialty;
import core.util.HOLogger;
import module.training.Skills;
import module.training.Skills.ScoutCommentSkillTypeID;

import java.sql.Timestamp;
import java.util.*;

import static core.util.Helper.parseDate;
import static java.lang.Math.max;
import static module.training.Skills.HTSkillID.*;

public class YouthPlayer {
    private int hrfid;
    private int id;
    private String nickName;
    private String firstName;
    private String lastName;
    private int ageYears;
    private int ageDays;
    private Timestamp arrivalDate;
    private Timestamp promotionDate;
    private int canBePromotedIn;
    private String playerNumber;
    private String statement;
    private String ownerNotes;
    private int playerCategoryID;
    private int cards;
    private int injuryLevel;
    private Specialty specialty;
    private int careerGoals;
    private int careerHattricks;
    private int leagueGoals;
    private int friendlyGoals;
    private int scoutId;
    private int scoutingRegionID;
    private String scoutName;
    private Integer youthMatchID;
    private Integer positionCode;
    private int playedMinutes;
    private Double rating;
    private Timestamp youthMatchDate;

    private double progressLastMatch=0;

    /**
     * current skills of the player
     * mapping skill id to skill info
     */
    private YouthSkillsInfo currentSkills = new YouthSkillsInfo();

    /**
     * Comments of the scout at player's arrival
     */
    private List<ScoutComment> scoutComments;

    /**
     * Comments of the trainer after training matches (not available yet)
     */
    private List<YouthTrainerComment> trainerComments;

    /**
     * player's training development.
     * One entry for each training match the player has participated
     *
     * mapping training match date to development entry
     */
    private TreeMap<Timestamp, YouthTrainingDevelopmentEntry> trainingDevelopment;


    /**
     * Create youth player
     *
     * (used in YouthPlayerTable when player is loaded from database)
     */
    public YouthPlayer() {}

    /**
     * Get the scout comments
     * The list is loaded from database.
     *
     * @return list of Scout comments
     */
    public List<ScoutComment> getScoutComments() {
        if (scoutComments == null) {
            scoutComments = DBManager.instance().loadYouthScoutComments(this.getId());
        }
        return this.scoutComments;
    }

    /**
     * Set the scout comments
     *
     * @param scoutComments list of scout comments
     */
    public void setScoutComments(List<ScoutComment> scoutComments) {
        this.scoutComments = scoutComments;
    }

    /**
     * Look for start skill levels, potential and specialties given by the scout.
     * The mentioned skills are tagged as top3 skills.
     */
    private void evaluateScoutComments() {
        if (scoutComments != null) {
            for (var c : scoutComments) {
                if (c.type == CommentType.CURRENT_SKILL_LEVEL) {
                    var startSkill = this.getSkillInfo(c.skillType.toHTSkillId());
                    startSkill.setStartLevel(c.skillLevel);
                    startSkill.setIsTop3(true);
                } else if (c.type == CommentType.POTENTIAL_SKILL_LEVEL) {
                    var maxSkill = this.getSkillInfo(c.skillType.toHTSkillId());
                    maxSkill.setMax(c.skillLevel);
                    maxSkill.setIsTop3(true);
                } else if (c.type == CommentType.PLAYER_HAS_SPECIALTY) {
                    this.specialty = Specialty.valueOf(c.skillType.getValue());
                }
            }
        }

        // Find Top3 skills
        this.currentSkills.findTop3Skills();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAgeYears() {
        return ageYears;
    }

    public void setAgeYears(int ageYears) {
        this.ageYears = ageYears;
    }

    public int getAgeDays() {
        return ageDays;
    }

    public void setAgeDays(int ageDays) {
        this.ageDays = ageDays;
    }

    public Timestamp getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Timestamp arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public int getCanBePromotedIn() {
        return canBePromotedIn;
    }

    /**
     * Number of days until the player can be promoted at a given date.
     *
     * @param date for which the duration should be calculated, typically the current date.
     * @return not negative number of days. 0 when the player can be promoted.
     */
    public int getCanBePromotedInAtDate(long date) {
        long hrftime = HOVerwaltung.instance().getModel().getBasics().getDatum().getTime();
        long diff = (date - hrftime + 500*60*60*24) / (1000 * 60 * 60 * 24);
        return max(0, canBePromotedIn - (int) diff);
    }

    public void setCanBePromotedIn(int canBePromotedIn) {
        this.canBePromotedIn = canBePromotedIn;
    }

    public String getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(String playerNumber) {
        this.playerNumber = playerNumber;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getOwnerNotes() {
        return ownerNotes;
    }

    public void setOwnerNotes(String ownerNotes) {
        this.ownerNotes = ownerNotes;
    }

    public int getPlayerCategoryID() {
        return playerCategoryID;
    }

    public void setPlayerCategoryID(int playerCategoryID) {
        this.playerCategoryID = playerCategoryID;
    }

    public int getCards() {
        return cards;
    }

    public void setCards(int cards) {
        this.cards = cards;
    }

    public int getInjuryLevel() {
        return injuryLevel;
    }

    public void setInjuryLevel(int injuryLevel) {
        this.injuryLevel = injuryLevel;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public int getCareerGoals() {
        return careerGoals;
    }

    public void setCareerGoals(int careerGoals) {
        this.careerGoals = careerGoals;
    }

    public int getCareerHattricks() {
        return careerHattricks;
    }

    public void setCareerHattricks(int careerHattricks) {
        this.careerHattricks = careerHattricks;
    }

    public int getLeagueGoals() {
        return leagueGoals;
    }

    public void setLeagueGoals(int leagueGoals) {
        this.leagueGoals = leagueGoals;
    }

    public int getFriendlyGoals() {
        return friendlyGoals;
    }

    public void setFriendlyGoals(int friendlyGoals) {
        this.friendlyGoals = friendlyGoals;
    }

    public int getScoutId() {
        return scoutId;
    }

    public void setScoutId(int scoutId) {
        this.scoutId = scoutId;
    }

    public int getScoutingRegionID() {
        return scoutingRegionID;
    }

    public void setScoutingRegionID(int scoutingRegionID) {
        this.scoutingRegionID = scoutingRegionID;
    }

    public String getScoutName() {
        return scoutName;
    }

    public void setScoutName(String scoutName) {
        this.scoutName = scoutName;
    }

    public Integer getYouthMatchID() {
        return youthMatchID;
    }

    public void setYouthMatchID(Integer youthMatchID) {
        this.youthMatchID = youthMatchID;
    }

    public Integer getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(Integer positionCode) {
        this.positionCode = positionCode;
    }

    public int getPlayedMinutes() {
        return playedMinutes;
    }

    public void setPlayedMinutes(int playedMinutes) {
        this.playedMinutes = playedMinutes;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Timestamp getYouthMatchDate() {
        return youthMatchDate;
    }

    public void setYouthMatchDate(Timestamp youthMatchDate) {
        this.youthMatchDate = youthMatchDate;
    }

    public YouthSkillInfo getSkillInfo(Skills.HTSkillID skillID) {
        return this.currentSkills.get(skillID);
    }

    public int getHrfid() {
        return hrfid;
    }

    public void setHrfid(int hrfid) {
        this.hrfid = hrfid;
    }

    private Timestamp statusTime;
    private Timestamp getStatusTime(){
        if ( statusTime == null){
            statusTime = DBManager.instance().getHrf(this.hrfid).getDatum();
        }
        return statusTime;
    }



    public Timestamp getPromotionDate() {
        return promotionDate;
    }

    public void setPromotionDate(Timestamp promotionDate) {
        this.promotionDate = promotionDate;
    }

    public void addComment(YouthTrainerComment comment) {
        var count = this.getTrainerComments().stream()
                .filter(c -> c.getYouthPlayerId() == this.id &&
                        c.getYouthMatchId() == comment.getYouthMatchId() &&
                        c.getIndex() == comment.getIndex())
                .count();
        if (count == 0) this.trainerComments.add(comment);
    }

    private List<YouthTrainerComment> getTrainerComments() {
        if (this.trainerComments == null) {
            this.trainerComments = DBManager.instance().loadYouthTrainerComments(this.id);
        }
        return this.trainerComments;

    }

    public String getFullName() {
        var ret = this.getFirstName();
        if (ret.length() > 0 && this.getNickName().length() > 0) {
            ret += " '" + this.getNickName() + "'";
        }
        if (ret.length() > 0 && this.getLastName().length() > 0) {
            ret += " " + this.getLastName();
        }
        return ret;
    }

    public static Skills.HTSkillID[] skillIds = {Keeper, Defender, Playmaker, Winger, Passing, Scorer, SetPieces};

    public void setSkillInfo(YouthSkillInfo skillinfo) {
        this.currentSkills.put(skillinfo.getSkillID(), skillinfo);
    }

    public TreeMap<Timestamp, YouthTrainingDevelopmentEntry> getTrainingDevelopment() {
        if (trainingDevelopment == null) {
            calcTrainingDevelopment();
        }
        return trainingDevelopment;
    }

    /**
     * Calculate the player's training development and store current skills in the database.
     * Player's potential based on prior calculation is reset.
     * Player's training of last match is set.
     * @return map of match date to training development entry
     */
    public TreeMap<Timestamp, YouthTrainingDevelopmentEntry> calcTrainingDevelopment() {
        this.potential=null;    // trigger potential recalc
        // init from models match list
        trainingDevelopment = new TreeMap<>();
        var model = HOVerwaltung.instance().getModel();
        var teamId = model.getBasics().getYouthTeamId();

        // set start skill values (may be edited by the user)
        var skills = getStartSkills();
        var trainings = model.getYouthTrainingsAfter(this.getArrivalDate());
        for (var training : trainings) {
            var keeper = skills.areKeeperSkills();
            if (keeper != null) {
                skills.setPlayerMaxSkills(keeper);
                this.currentSkills.setPlayerMaxSkills(keeper);
            }
            var team = training.getTeam(teamId);
            if (team.hasPlayerPlayed(this.id)) {
                var trainingEntry = new YouthTrainingDevelopmentEntry(this, training);
                var oldSkills = skills;
                skills = trainingEntry.calcSkills(skills, getSkillsAt(training.getMatchDate()), team);
                progressLastMatch = skills.getSkillSum() - oldSkills.getSkillSum();
                trainingEntry.setInjuryLevel(getInjuryLevelAt(training.getMatchDate()));
                trainingEntry.setIsSuspended(isSuspendedAt(training.getMatchDate()));
                trainingDevelopment.put(training.getMatchDate(), trainingEntry);
            } else {
                progressLastMatch = 0;
            }
        }
        this.currentSkills = skills;
        DBManager.instance().storeYouthPlayer(this.hrfid, this);

        return trainingDevelopment;
    }

    /**
     * Skills info at player's arrival date are reconstructed.
     * @return youth skills info
     */
    private YouthSkillsInfo getStartSkills() {
        YouthSkillsInfo startSkills = new YouthSkillsInfo();
        for (var skill : this.currentSkills.values()) {
            var startSkill = new YouthSkillInfo(skill.getSkillID());
            var startValue = skill.getStartValue();
            startSkill.setCurrentValue(startValue);
            startSkill.setStartLevel(skill.getStartLevel());
            startSkill.setCurrentLevel(skill.getStartLevel());
            // check if current value was adjusted
            var adjustment = startSkill.getCurrentValue() - startValue; // Levels may raise current value
            startSkill.setStartValue(startValue + adjustment);
            skill.setStartValue(startSkill.getStartValue());
            startSkill.setMax(skill.getMax());
            startSkill.setIsTop3(skill.isTop3());
            startSkills.put(startSkill.getSkillID(), startSkill);
        }
        return startSkills;
    }

    /**
     * Cache of old skills info
     */
    private Timestamp playerDevelopmentDate;
    private YouthPlayer oldPlayerInfo;

    /**
     * Get player's state at given date
     * Skills are loaded from database, if not given in cache.
     *
     * @param date date of the requested player info
     * @return youth player state at date
     */
    private YouthPlayer getOldPlayerInfo(Timestamp date){
        if ( playerDevelopmentDate == null || !date.equals(playerDevelopmentDate)){
            playerDevelopmentDate = date;
            oldPlayerInfo = DBManager.instance().loadYouthPlayerOfMatchDate(this.id, date);
        }
        return oldPlayerInfo;
    }

    /**
     * Get player's skills after match at given date
     * @param date timestamp
     * @return youth player's skills
     */
    private YouthSkillsInfo getSkillsAt(Timestamp date) {
        if (!date.equals(this.youthMatchDate)) {
            var oldPlayerInfo = getOldPlayerInfo(date);
            if (oldPlayerInfo != null) {
                setKnownMaxValues(oldPlayerInfo.currentSkills);
                return oldPlayerInfo.currentSkills;
            } else {
                var ret = getStartSkills();
                for (var entry : this.trainingDevelopment.entrySet()) {
                    if (entry.getKey() == date) {
                        return ret;
                    }
                    ret = entry.getValue().getSkills();
                }
                setKnownMaxValues(ret);
                return ret;
            }
        }
        return this.currentSkills;
    }

    private void setKnownMaxValues(YouthSkillsInfo skills) {
        for ( var currentSkill : this.currentSkills.values()){
            if ( currentSkill.getMax() != null ){
                skills.get(currentSkill.getSkillID()).setMax(currentSkill.getMax());
            }
        }
    }

    /**
     * Get player's skills before match at given date
     * @param date timestamp
     * @return youth player's skills
     */
    private YouthSkillsInfo getSkillsBefore(Timestamp date) {
        var ret = getStartSkills();
        for (var entry : this.trainingDevelopment.entrySet()) {
            if (entry.getKey().before(date)) {
                ret = entry.getValue().getSkills();
            } else {
                break;
            }
        }
        return ret;
    }

    /**
     * Get player's injury level at given date
     * @param date timestamp
     * @return injury level
     */
    private int getInjuryLevelAt(Timestamp date){
        var oldPlayerInfo = getOldPlayerInfo(date);
        if ( oldPlayerInfo != null) return oldPlayerInfo.getInjuryLevel();
        return this.injuryLevel;
    }

    /**
     * Get player's suspension at given date
     * @param date timestamp
     * @return boolean
     */
    private boolean isSuspendedAt(Timestamp date){
        var oldPlayerInfo = getOldPlayerInfo(date);
        if ( oldPlayerInfo != null) return oldPlayerInfo.isSuspended();
        return this.isSuspended();

    }

    public boolean isSuspended() {
        return this.cards == 3;
    }

    /**
     * recalc skills since given date
     * @param since timestamp
     */
    public void recalcSkills(Timestamp since) {
        if (trainingDevelopment != null) {
            var startSkills = getSkillsBefore(since);
            for (var entry : this.trainingDevelopment.tailMap(since, true).values()) {
                var team = entry.getTraining().getTeam(HOVerwaltung.instance().getModel().getBasics().getYouthTeamId());
                startSkills = entry.calcSkills(startSkills, getSkillsAt(entry.getMatchDate()), team);
            }
        }
    }

    /**
     * Skill start value is incremented by value adjustment. Change is propagated through all existing
     * training development entries until given before date
     *  @param skillID    skill id
     * @param adjustment change of the start value
     * @param before adjust training development before given date
     */
    public double adjustSkill(Skills.HTSkillID skillID, double adjustment, Timestamp before) {
        // start skills are examined from current skill infos
        var currentSkill = getSkillInfo(skillID);
        currentSkill.addStartValue(adjustment);
        if (currentSkill.getCurrentValue() < currentSkill.getStartValue()) {
            currentSkill.setCurrentValue(currentSkill.getStartValue());
        }
        if (trainingDevelopment != null && trainingDevelopment.size() > 0) {
            var youthteamId = HOVerwaltung.instance().getModel().getBasics().getYouthTeamId();
            var skills = getStartSkills();
            for (var trainingEntry : trainingDevelopment.values()) {
                if (!trainingEntry.getMatchDate().before(before)) break; // stop if before date is reached
                var constraints = trainingEntry.getSkills(); // constraints (levels) are not changed
                // this calculates all skill Ids, not only the requested one (this is a kind of overhead, i accept)
                skills = trainingEntry.calcSkills(skills, constraints, trainingEntry.getTraining().getTeam(youthteamId));
            }
        }
        return currentSkill.getStartValue();
    }

    public String getSpecialtyString() {
        this.getTrainingDevelopment(); // may add specialties from highlights
        if (this.specialty != Specialty.NoSpecialty) {
            return HOVerwaltung.instance().getLanguageString("ls.player.speciality." + this.specialty.toString().toLowerCase());
        }
        return "";
    }

    public double getTrainedSkillSum() {
        return this.currentSkills.getTrainedSkillSum();
    }

    public int getMatchCount() {
        return this.getTrainingDevelopment().size();
    }

    public double getProgressLastMatch() {
        return progressLastMatch;
    }

    public int getAgeYearsAtDate(Timestamp matchDate) {
        var hrfdate = this.getStatusTime().getTime();
        long time = matchDate.getTime();
        long diff = (hrfdate - time) / (1000 * 60 * 60 * 24);
        long ageInDays = getAgeDays() + 112 * getAgeYears() - diff;
        return (int)(ageInDays / 112);
    }

    public static class ScoutComment {
        private int youthPlayerId;
        private int index;
        private String text;
        private CommentType type;
        private Integer variation;
        private ScoutCommentSkillTypeID skillType;
        private Integer skillLevel;

        public ScoutComment() {
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public CommentType getType() {
            return type;
        }

        public void setType(CommentType type) {
            this.type = type;
        }

        public Integer getVariation() {
            return variation;
        }

        public void setVariation(Integer variation) {
            this.variation = variation;
        }

        public ScoutCommentSkillTypeID getSkillType() {
            return skillType;
        }

        public void setSkillType(ScoutCommentSkillTypeID skillType) {
            this.skillType = skillType;
        }

        public Integer getSkillLevel() {
            return skillLevel;
        }

        public void setSkillLevel(Integer skillLevel) {
            this.skillLevel = skillLevel;
        }

        public int getYouthPlayerId() {
            return youthPlayerId;
        }

        public void setYouthPlayerId(int youthPlayerId) {
            this.youthPlayerId = youthPlayerId;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    /**
     * Create a youth player from downloaded information.
     *
     * @param properties extracted from chpp-xml file
     */
    public YouthPlayer(Properties properties) {

        id = getInt(properties, "id", 0);
        firstName = properties.getProperty("firstname", "");
        nickName = properties.getProperty("nickname", "");
        lastName = properties.getProperty("lastname", "");
        ageYears = getInt(properties, "age", 0);
        ageDays = getInt(properties, "agedays", 0);
        arrivalDate = parseDate(properties.getProperty("arrivaldate", ""));
        canBePromotedIn = getInt(properties, "canbepromotedin", 0);
        playerNumber = properties.getProperty("playernumber", "");
        statement = properties.getProperty("statement", "");
        ownerNotes = properties.getProperty("ownernotes", "");
        playerCategoryID = getInt(properties, "playercategoryid", 0);
        cards = getInt(properties, "cards", 0);
        injuryLevel = getInt(properties, "injurylevel", 0);
        specialty = Specialty.valueOf(getInt(properties, "specialty", 0));
        careerGoals = getInt(properties, "careergoals", 0);
        careerHattricks = getInt(properties, "careerhattricks", 0);
        leagueGoals = getInt(properties, "leaguegoals", 0);
        friendlyGoals = getInt(properties, "friendlygoals", 0);
        scoutId = getInt(properties, "scoutid", 0);
        scoutingRegionID = getInt(properties, "scoutingregionid", 0);
        scoutName = properties.getProperty("scoutname", "");

        youthMatchID = getInteger(properties, "youthmatchid");
        positionCode = getInteger(properties, "positioncode");
        playedMinutes = getInt(properties, "playedminutes", 0);
        rating = getDouble(properties, "rating");
        youthMatchDate = parseNullableDate(properties.getProperty("youthmatchdate"));

        var playerStatusPreviousDownload = HOVerwaltung.instance().getModel().getCurrentYouthPlayer(id);
        for (var skillId : YouthPlayer.skillIds) {
            var skillinfo = parseSkillInfo(properties, skillId);
            if (playerStatusPreviousDownload != null) {
                var prevSkills = playerStatusPreviousDownload.getSkillInfo(skillId);
                skillinfo.setCurrentValue(prevSkills.getCurrentValue());
                skillinfo.setStartValue(prevSkills.getStartValue());
            }
            this.setSkillInfo(skillinfo);
        }
        parseScoutComments(properties);
        evaluateScoutComments();
    }

    // don't want to see warnings in HO log file on empty date strings
    private Timestamp parseNullableDate(String date) {
        if (date != null && date.length() > 0) return parseDate(date);
        return null;
    }

    private void parseScoutComments(Properties properties) {
        this.scoutComments = new ArrayList<>();
        for (int i=0; true; i++ ) {
            var prefix = "scoutcomment" + i;
            var text = properties.getProperty(prefix + "text");
            if (text != null) {
                var scoutComment = new ScoutComment();
                scoutComment.text = properties.getProperty(prefix + "text", "");
                scoutComment.type = CommentType.valueOf(getInteger(properties, prefix + "type"));
                scoutComment.variation = getInteger(properties, prefix + "variation");
                if (scoutComment.type == CommentType.AVERAGE_SKILL_LEVEL) {
                    // Average comment stores skill level in skillType
                    scoutComment.skillType = ScoutCommentSkillTypeID.AVERAGE;
                    scoutComment.skillLevel = getInteger(properties, prefix + "skilltype");
                } else {
                    scoutComment.skillType = ScoutCommentSkillTypeID.valueOf(getInteger(properties, prefix + "skilltype"));
                    scoutComment.skillLevel = getInteger(properties, prefix + "skilllevel");
                }
                this.scoutComments.add(scoutComment);
            }
            else {
                // No more scout comment found
                return;
            }
        }
    }

    private YouthSkillInfo parseSkillInfo(Properties properties, Skills.HTSkillID skillID) {
        var skill = skillID.toString().toLowerCase(java.util.Locale.ENGLISH) + "skill";
        var skillInfo = new YouthSkillInfo(skillID);
        skillInfo.setCurrentLevel(getInteger(properties, skill));
        skillInfo.setMax(getInteger(properties, skill + "max"));
        skillInfo.setMaxReached(getBoolean(properties, skill + "ismaxreached", false));
        this.currentSkills.put(skillID, skillInfo);
        return skillInfo;
    }

    private Integer getInteger(Properties p, String key) {
        try {
            var s = p.getProperty(key);
            if (s != null && s.length() > 0) return Integer.parseInt(s);
        } catch (Exception ignored) {
        }
        return null;
    }

    private boolean getBoolean(Properties p, String key, boolean defaultValue) {
        try {
            var s = p.getProperty(key);
            if (s != null && s.length() > 0) return Boolean.parseBoolean(s);
        } catch (Exception e) {
            HOLogger.instance().warning(getClass(), "getBoolean: " + e);
        }
        return defaultValue;
    }

    private int getInt(Properties p, String key, int defaultValue) {
        try {
            var s = p.getProperty(key);
            if (s != null && s.length() > 0) return Integer.parseInt(s);
        } catch (Exception e) {
            HOLogger.instance().warning(getClass(), "getInt: " + e);
        }
        return defaultValue;
    }

    private Double getDouble(Properties p, String key) {
        try {
            var s = p.getProperty(key);
            if (s != null && s.length() > 0) return Double.parseDouble(s);
        } catch (Exception ignored) {
        }
        return null;
    }


    // from https://github.com/minj/foxtrick/blob/master/content/information-aggregation/htms-points.js
    // keeper, defending, playmaking, winger, passing, scoring, setPieces
    static int[][] SKILL_PTS_PER_LVL = {
            {0, 0, 0, 0, 0, 0, 0}, // 0
            {2, 4, 4, 2, 3, 4, 1}, // 1
            {12, 18, 17, 12, 14, 17, 2}, // 2
            {23, 39, 34, 25, 31, 36, 5}, // 3
            {39, 65, 57, 41, 51, 59, 9}, // 4
            {56, 98, 84, 60, 75, 88, 15}, // 5
            {76, 134, 114, 81, 104, 119, 21}, // 6
            {99, 175, 150, 105, 137, 156, 28}, // 7
            {123, 221, 190, 132, 173, 197, 37}, // 8
            {150, 271, 231, 161, 213, 240, 46}, // 9
            {183, 330, 281, 195, 259, 291, 56}, // 10
            {222, 401, 341, 238, 315, 354, 68}, // 11
            {268, 484, 412, 287, 381, 427, 81}, // 12
            {321, 580, 493, 344, 457, 511, 95}, // 13
            {380, 689, 584, 407, 540, 607, 112}, // 14
            {446, 809, 685, 478, 634, 713, 131}, // 15
            {519, 942, 798, 555, 738, 830, 153}, // 16
            {600, 1092, 924, 642, 854, 961, 179}, // 17
            {691, 1268, 1070, 741, 988, 1114, 210}, // 18
            {797, 1487, 1247, 855, 1148, 1300, 246}, // 19
            {924, 1791, 1480, 995, 1355, 1547, 287}, // 20
            {1074, 1791, 1791, 1172, 1355, 1547, 334}, // 21
            {1278, 1791, 1791, 1360, 1355, 1547, 388}, // 22
            {1278, 1791, 1791, 1360, 1355, 1547, 450}, // 23
    };

    static double[] WEEK_PTS_PER_AGE = {
            /*17:*/ 10,
            /*18:*/ 9.92,
            /*19:*/ 9.81,
            /*20:*/ 9.69,
            /*21:*/ 9.54,
            /*22:*/ 9.39,
            /*23:*/ 9.22,
            /*24:*/ 9.04,
            /*25:*/ 8.85,
            /*26:*/ 8.66,
            /*27:*/ 8.47,
            /*28:*/ 8.27,
            /*29:*/ 8.07,
            /*30:*/ 7.87,
            /*31:*/ 7.67,
            /*32:*/ 7.47,
            /*33:*/ 7.27,
            /*34:*/ 7.07,
            /*35:*/ 6.87,
            /*36:*/ 6.67,
            /*37:*/ 6.47,
            /*38:*/ 6.26,
            /*39:*/ 6.06,
            /*40:*/ 5.86,
            /*41:*/ 5.65,
            /*42:*/ 6.45,
            /*43:*/ 6.24,
            /*44:*/ 6.04,
            /*45:*/ 5.83
    };

    private Integer htms17;

    /**
     * Alternative player's potential value (not used for the moment)
     * @return int HTMS value at age of 17 years
     */
    public int getHTMS17() {
        if (htms17 == null) {
            calcMaxSkills17();
            htms17 = 0;
            int i = 0;
            for (var skillId : skillIds) {
                htms17 += SKILL_PTS_PER_LVL[this.getSkillInfo(skillId).getPotential17Value().intValue()][i++];
            }
        }
        return htms17;
    }

    private Integer averageSkillLevel = -1;

    /**
     * Get the player's overall skill level given by the scout.
     * @return String
     *          number of overall skill
     *          empty, if no overall skill was given by the scout
     */
    public String getAverageSkillLevel() {
        if ( averageSkillLevel != null && averageSkillLevel == -1){
            var sc = this.getScoutComments().stream()
                    .filter(i->i.type==CommentType.AVERAGE_SKILL_LEVEL)
                    .findFirst()
                    .orElse(null);
            if ( sc != null){
                averageSkillLevel=sc.getSkillLevel();
            }
            else {
                averageSkillLevel=null;
            }
        }
        if ( averageSkillLevel != null){
            return averageSkillLevel.toString();
        }
        return "";
    }

    private Integer potential;

    /**
     * Get a player's potential number (sum of normed maximum skill values at player's age of 17 years).
     * The maximum reachable skill values are divided by a norming factor representing the training speed of the skills.
     *
     * @return int potential number
     */
    public int getPotential()
    {
        if ( potential == null){
            //HOLogger.instance().info(this.getClass(), "getPotential " + this.getFullName() + " " + this.getAgeYears() + "," + this.getAgeDays());
            calcMaxSkills17();
            double p = 0d;
            for ( var skillId: skillIds){
                p += max(0, getSkillInfo(skillId).getPotential17Value() - 4) / YouthTraining.potentialNormingFactor.get(skillId);
                //HOLogger.instance().info(this.getClass(), skillId.toString() + "=" + getSkillInfo(skillId).getPotential17Value() + " p=" + p);
            }
            potential = (int)Math.round(p);
        }
        return potential;
    }

    /**
     * Calculate the maximum reachable skills
     */
    private void calcMaxSkills17() {
        if (this.getAgeYears() >16) {
            // the player is older than 17 years old
            // Find skill values at age of 17,0
            YouthSkillsInfo skill17 = null;
            // scan training development
            if (this.getTrainingDevelopment().size() > 0) {
                for (var entry : this.getTrainingDevelopment().values()) {
                    // find skill when player's age skipped to 17
                    skill17 = entry.getSkills();
                    if (entry.getPlayerAgeYears() > 16) {
                        break;
                    }
                }
            }
            // if no training development was found
            if (skill17 == null) {
                // use current skill value. Player was never trained
                skill17 = this.currentSkills;
            }
            // Set skill potential values of each skill
            for ( var skill: skill17.values()){
                this.getSkillInfo(skill.getSkillID()).setPotential17Value(skill.getCurrentValue());
            }
        }
        else {
            // The player is younger than 17 years old
            // The possible training is divided among the unfinished skills
            var numberOfUsefulTrainings = this.currentSkills.values().stream()
                    .filter(YouthSkillInfo::isTrainingUsefull)
                    .count();
            int age = this.getAgeYears();
            int days = this.getAgeDays();
            while (age < 17 && numberOfUsefulTrainings > 0) {
                // for each week until age of 17 is reached
                int ntrainingsMaxReached=0;
                for (var skill : this.currentSkills.values()) {
                    // find new maximum value of each skill
                    var maxVal = skill.getCurrentValue();
                    if ( skill.isTrainingUsefull()) {
                        if (skill.getPotential17Value() != null) maxVal = skill.getPotential17Value();
                        // limit of skill
                        var skillLimit = 8.3;
                        if (skill.getMax() != null && skill.getMax() < 8) {
                            skillLimit = skill.getMax() + .99;
                        }
                        if (!skill.isMaxReached() && maxVal < skillLimit) { // if maximum is not beyond the given limit
                            // maximum weekly increment of the skill
                            double increment = YouthTraining.getMaxTrainingPerWeek(skill.getSkillID(), (int) maxVal, age);
                            // increment maximum value as if the training could be distributed among the unfinished skills
                            maxVal += increment / numberOfUsefulTrainings;
                            // check if limit is reached
                            if (maxVal > skillLimit) {
                                maxVal = skillLimit;
                                // register newly finished skill
                                ntrainingsMaxReached++;
                            }
                        }
                    }
                    // set new maximum skill value
                    skill.setPotential17Value(maxVal);
                }
                // decrement number of unfinished trainings
                numberOfUsefulTrainings-=ntrainingsMaxReached;
                // player's age of next week
                days += 7;
                if (days > 111) {
                    days -= 112;
                    age++;
                }
            }
        }
    }
}