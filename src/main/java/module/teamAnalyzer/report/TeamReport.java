// %3625019770:hoplugins.teamAnalyzer.report%
package module.teamAnalyzer.report;

import core.model.HOVerwaltung;
import core.module.config.ModuleConfig;
import core.specialevents.SpecialEventsPredictionManager;
import module.lineup.Lineup;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.manager.PlayerDataManager;
import module.teamAnalyzer.manager.TeamLineupBuilder;
import module.teamAnalyzer.vo.MatchDetail;
import module.teamAnalyzer.vo.MatchRating;
import module.teamAnalyzer.vo.PlayerPerformance;
import module.teamAnalyzer.vo.TeamLineup;

import java.util.*;


/**
 * The main report containing all the data
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class TeamReport {

    private TeamLineup adjustedRatingsLineup;
    private TeamLineup averageRatingslineup;
    private List<MatchDetail> matchDetails = new ArrayList<>();
    private SpecialEventsPredictionManager specialEventsPredictionManager;

    private int selection=0;


    //~ Instance fields ----------------------------------------------------------------------------

    /** Map of SpotReport */
    private Map<Integer,SpotReport> spotReports = new LinkedHashMap<>();

    /** Match Ratings */
    private MatchRating rating  = new MatchRating();

    /** Average stars */
    private double averageStars = 0d;

    /** Number of matches considered */
    private int matchNumber = 0;


    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TeamReport object.
     */
    public TeamReport(List<MatchDetail> matchDetails) {
        for (MatchDetail m:matchDetails ) {
            addMatch(m, ModuleConfig.instance().getBoolean(SystemManager.ISSHOWUNAVAILABLE));
        }
        this.averageRatingslineup = new TeamLineupBuilder(this)
                .setName(HOVerwaltung.instance().getLanguageString("Durchschnitt")).build();
    }

    private TeamReport(MatchDetail matchDetail) {
        addMatch(matchDetail,ModuleConfig.instance().getBoolean(SystemManager.ISSHOWUNAVAILABLE));
        this.averageRatingslineup = new TeamLineupBuilder(this).setMatchDetail(matchDetail).build();
    }

    public int size() {
        int ret = this.matchDetails.size()+1;
        if ( this.adjustedRatingsLineup != null) ret++;
        return ret;
    }

    public TeamLineup getLineup(int selection)
    {
        if (this.matchDetails == null || this.matchDetails.size()==0)return null;
        this.selection=selection;
        if ( selection == 0 ){
            return this.averageRatingslineup;
        }
        else {
            int offset = 1;
            if (this.adjustedRatingsLineup != null) {
                if (selection == 1) {
                    return this.adjustedRatingsLineup;
                }
                offset = 2;
            }
            int matchNumber = selection - offset;
            // create a team report of one single match
            TeamReport report = new TeamReport(matchDetails.get(matchNumber));
            return report.getLineup(1);
        }
    }

    public void adjustRatingsLineup(MatchRating newRatings) {
        // copy of selected lineup
        adjustedRatingsLineup =  new TeamLineupBuilder(new TeamReport(getLineup(selection).getMatchDetail()))
                .setMatchRating(newRatings)
                .setName(HOVerwaltung.instance().getLanguageString("ls.teamanalyzer.Adjusted")).build();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public MatchRating getRating() {
        return rating;
    }

    public SpecialEventsPredictionManager getSpecialEventsPredictionManager() {
        return specialEventsPredictionManager;
    }

    /**
     * Returns the spot report for the specified spot field
     *
     * @param spot the spot number we want
     *
     * @return SpotReport
     */
    public SpotReport getSpotReport(int spot) {
        return spotReports.get(spot);
    }

    public double getStars() {
        return averageStars;
    }

    /**
     * Add a match to the report
     *
     * @param matchDetail Match to be analyzed
     * @param showUnavailable consider also unavailable or not
     */
    public void addMatch(MatchDetail matchDetail, boolean showUnavailable) {
        this.matchDetails.add(matchDetail);

        for (Iterator<PlayerPerformance> iter = matchDetail.getPerformances().iterator(); iter.hasNext();) {
            addPerformance( iter.next(), showUnavailable);
        }

        addRating(matchDetail.getRating());
        addStars(matchDetail.getStars());
        addSpecialEvents(matchDetail);
        matchNumber++;
    }

    private void addSpecialEvents(MatchDetail matchDetail)
    {
        Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();

        if ( this.specialEventsPredictionManager == null){
            this.specialEventsPredictionManager = new SpecialEventsPredictionManager();
        }
        this.specialEventsPredictionManager.analyzeLineup(lineup, matchDetail);
    }

    /**
     * Add a performance to the correct SpotReport
     *
     * @param pp
     * @param showUnavailable
     */
    private void addPerformance(PlayerPerformance pp, boolean showUnavailable) {
        if ((!showUnavailable) && (pp.getStatus() != PlayerDataManager.AVAILABLE)) {
            return;
        }

        SpotReport spotReport = getSpotReport(pp.getId());

        if (spotReport == null) {
            spotReport = new SpotReport(pp);
            spotReports.put(pp.getId(), spotReport);
        }

        spotReport.addPerformance(pp);
    }

    /**
     * Updated the ratings
     *
     * @param aRating new match ratings
     */
    private void addRating(MatchRating aRating) {
        rating.setMidfield(updateAverage(rating.getMidfield(), aRating.getMidfield()));
        rating.setLeftDefense(updateAverage(rating.getLeftDefense(), aRating.getLeftDefense()));
        rating.setCentralDefense(updateAverage(rating.getCentralDefense(),
                                               aRating.getCentralDefense()));
        rating.setRightDefense(updateAverage(rating.getRightDefense(), aRating.getRightDefense()));
        rating.setLeftAttack(updateAverage(rating.getLeftAttack(), aRating.getLeftAttack()));
        rating.setCentralAttack(updateAverage(rating.getCentralAttack(), aRating.getCentralAttack()));
        rating.setRightAttack(updateAverage(rating.getRightAttack(), aRating.getRightAttack()));
        rating.setHatStats(updateAverage(rating.getHatStats(), aRating.getHatStats()));
        rating.setLoddarStats(updateAverage(rating.getLoddarStats(), aRating.getLoddarStats()));
    }

    /**
     * Updates the average stars
     *
     * @param stars new game stars
     */
    private void addStars(double stars) {
        averageStars = updateAverage(averageStars, stars);
    }

    /**
     * Generic calculate average method
     *
     * @param oldValue
     * @param newValue
     *
     * @return the new average number
     */
    private double updateAverage(double oldValue, double newValue) {
        double rat = ((oldValue * matchNumber) + newValue) / (matchNumber + 1);

        return rat;
    }

    public void selectLineup(int i) {
        this.selection=i;
    }
}
