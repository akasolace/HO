package core.specialevents;

import core.model.player.MatchRoleID;

import java.util.List;

public interface ISpecialEventPredictionAnalyzer {

    enum SpecialEventType {
        EXPERIENCE,
        PNF,
        PDIM,
        UNPREDICTABLE,
        WINGER_SCORER,
        WINGER_HEAD,
        QUICK_PASS,
        QUICK_SCORES,
        TECHNICAL_HEAD
    }

    List<SpecialEventsPrediction> analyzePosition(SpecialEventsPredictionManager.Analyse analyse,  MatchRoleID position);
}