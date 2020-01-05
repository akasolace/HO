package core.specialevents;

import core.constants.player.Speciality;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import module.lineup.substitution.model.MatchOrderType;

import java.util.List;
import java.util.Vector;

public class PowerfulForwardEventPredictionAnalyzer implements  ISpecialEventPredictionAnalyzer {

    @Override
    public List<SpecialEventsPrediction> analyzePosition(SpecialEventsPredictionManager.Analyse analyse, MatchRoleID position) {
        Vector<SpecialEventsPrediction> ret = new Vector<SpecialEventsPrediction>();
        int id = position.getSpielerId();
        Player p = analyse.getPlayer(id);
        if (p.hasSpeciality(Speciality.POWERFUL)) {

            switch (position.getId()) {
                case IMatchRoleID.leftForward:
                case IMatchRoleID.centralForward:
                case IMatchRoleID.rightForward:

                    if( position.getTaktik() == IMatchRoleID.NORMAL){

                        // TODO: Probability depends on defence skills of opponent defenders
                        // TODO: Overcrowding, if more than 1 PNF is in lineup
                        SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(
                                position,
                                SpecialEventType.PNF,
                                .5, 20*20, 8*8,
                                p.getSCskill() * p.getPMskill());
                        if (se != null) {
                            ret.add(se);
                        }
                    }

                    break;
            }
        }
        return ret;
    }
}