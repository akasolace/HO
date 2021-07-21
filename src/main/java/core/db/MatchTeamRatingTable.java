package core.db;

import core.model.match.MatchTeamRating;
import core.util.HOLogger;
import module.transfer.scout.ScoutEintrag;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MatchTeamRatingTable extends AbstractTable {
    public final static String TABLENAME = "MATCHTEAMRATING";

    protected MatchTeamRatingTable(JDBCAdapter  adapter){
        super(TABLENAME, adapter);
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
                new ColumnDescriptor("MatchID", Types.INTEGER, false),
                new ColumnDescriptor("MatchTyp", Types.INTEGER, false),
                new ColumnDescriptor("TeamID", Types.INTEGER, false),
                new ColumnDescriptor("FanclubSize", Types.INTEGER, false),
                new ColumnDescriptor("PowerRating", Types.INTEGER, true),
                new ColumnDescriptor("GlobalRanking", Types.INTEGER, true),
                new ColumnDescriptor("RegionRanking", Types.INTEGER, true),
                new ColumnDescriptor("LeagueRanking", Types.INTEGER, true),
                new ColumnDescriptor("NumberOfVictories", Types.INTEGER, true),
                new ColumnDescriptor("NumberOfUndefeated", Types.INTEGER, true)
        };
    }

    @Override
    protected String[] getConstraintStatements() {
        return new String[] {" PRIMARY KEY (MATCHID, MATCHTYP, TEAMID)"};
    }

    List<MatchTeamRating> load(int matchID, int matchType ) {
        var ret = new ArrayList<MatchTeamRating>();
        try {
            var sql = "SELECT * FROM " + getTableName()
                    + " WHERE MatchTyp = " + matchType
                    + " AND MatchID = " + matchID;
            var rs = adapter.executeQuery(sql);
            if (rs != null) {
                rs.beforeFirst();
                while (rs.next()) {
                    ret.add(new MatchTeamRating(rs));
                }
            }
        } catch (Exception e) {
            HOLogger.instance().log(getClass(), "DB.MatchTeamRating Error" + e);
        }
        return ret;
    }

    private String getColumnNames() {
        StringBuilder ret = new StringBuilder();
        String sep = " (";
        for (var c : columns) {
            ret.append(sep);
            ret.append(c.getColumnName());
            sep = ",";
        }
        ret.append(")");
        return ret.toString();
    }

    void store(MatchTeamRating teamRating) {
        if (teamRating != null) {
            try {
                var sql = "INSERT INTO "
                        + getTableName()
                        + getColumnNames()
                        + " VALUES("
                        + teamRating.getMatchId() + ","
                        + teamRating.getMatchTyp().getId() + ","
                        + teamRating.getTeamId() + ","
                        + teamRating.getFanclubSize() + ","
                        + teamRating.getPowerRating() + ","
                        + teamRating.getGlobalRanking() + ","
                        + teamRating.getLeagueRanking() + ","
                        + teamRating.getRegionRanking() + ","
                        + teamRating.getNumberOfVictories() + ","
                        + teamRating.getNumberOfUndefeated() + ")";
                adapter.executeUpdate(sql);
            } catch (Exception e) {
                HOLogger.instance().log(getClass(), "DB.store MatchTeamRating Error" + e);
                HOLogger.instance().log(getClass(), e);
            }
        }
    }
}
