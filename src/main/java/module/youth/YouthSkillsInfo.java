package module.youth;

import module.training.Skills;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class YouthSkillsInfo extends HashMap<Skills.HTSkillID, YouthSkillInfo> {

    /**
     * Determine if skills are keeper skills
     *
     * @return true, if keeper max > 4
     * false, if max of pm,ps,wi or sc > 4
     * null, if decision is not possible
     */
    public Boolean areKeeperSkills() {
        if (size() == 0) return null;
        for (var skill : this.values()) {
            if (skill.getMax() != null && skill.getMax() >= 5 ||
                    skill.getCurrentLevel() != null && skill.getCurrentLevel() >= 5) {
                var skillId = skill.getSkillID();
                if (skillId == Skills.HTSkillID.Keeper) return true;
                else if (skillId == Skills.HTSkillID.Winger ||
                        skillId == Skills.HTSkillID.Playmaker ||
                        skillId == Skills.HTSkillID.Passing ||
                        skillId == Skills.HTSkillID.Scorer) return false;
            }
        }
        return null;
    }

    /**
     * If player is a GoalKeeper, maximum skills of winger, playmaking, passing and scoring are set (4)
     * otherwise the maximum of the keeper skill is set to 4 (field player)
     *
     * @param isKeeper true, keeper maximums are set
     *                 false, field player maximum is set
     */
    public void setPlayerMaxSkills(boolean isKeeper) {
        if (size() == 0) return;
        for (var skill : this.values()) {
            if (skill.getMax() == null || skill.getMax() > 4) {
                switch (skill.getSkillID()) {
                    case Winger:
                    case Playmaker:
                    case Passing:
                    case Scorer:
                        if (isKeeper) {
                            skill.setMax(4);
                        }
                        break;
                    case Keeper:
                        if (!isKeeper) {
                            skill.setMax(4);
                        }
                }
            }
        }
    }

    /**
     * Find skills that have one of the top 3 highest potentials
     * From scout report up to 2 skills are known to be in top3
     * Trainer reporting skill maxima greater than those reported by the scout are marked as further top3 skill
     * This also applies to current skill reports of the trainer that exceeds the scout reported value.
     * Maxima reported by the trainer that could not reach top3 ranking gets a negative (false) top3 mark.
     * When all top3 skills are known, the other skills' maximum values are reduced accordingly.
     */
    public void findTop3Skills() {
        // Number of skills marked as top3 skill
        var nTop3 = this.values().stream().filter(i -> i.isTop3() != null && i.isTop3()).count();
        if (nTop3 == 3) return; // nothing to do

        int minTop3Max = 8;
        if (nTop3 > 0) { // ntop3 == 0 should only happen with new youth teams with players that were not scouted
            // There are top3 skills available
            // Lowest Top3 maximum
            var minTop3MaxSkill = this.values().stream()
                    .filter(i -> i.isTop3() != null && i.isTop3() && i.isMaxAvailable())
                    .min(Comparator.comparingInt(YouthSkillInfo::getMax)).get();
            minTop3Max = minTop3MaxSkill.getMax();
        }

        // Find skills with higher maximum of current level than lowest known top3 maximum
        // Those skills are marked as top3 skills
        var notTop3Yet = new ArrayList<YouthSkillInfo>();   // list skill that are not surely in top3
        for (var skill : this.values()) {
            if (skill.isMaxAvailable() && skill.getMax() > minTop3Max ||
                    skill.isCurrentLevelAvailable() && skill.getCurrentLevel() > minTop3Max) {
                if (skill.isTop3() == null || !skill.isTop3()) {
                    skill.setIsTop3(true);
                    nTop3++;
                    if (nTop3 == 3) break; // the rest is NOT in top 3
                }
            } else if (skill.isTop3() == null || !skill.isTop3()) {
                notTop3Yet.add(skill);
            }
        }

        if (nTop3 < 3 && notTop3Yet.size() > 3 - nTop3) {
            // Order candidates by minimum potential value
            notTop3Yet.sort(Comparator.comparingInt(YouthSkillInfo::getMinimumPotential).reversed());

            // Candidates for latest top3 skills
            var it = notTop3Yet.iterator();
            int max = 0;
            while (nTop3++ < 3) {
                var skill = it.next();
                if (skill == null) break;               // no more candidate available (should not happen, i think)
                max = skill.getMinimumPotential();      // remember lowest max value of candidate
            }

            // Find skills that never will reach top 3
            while (it.hasNext()) {
                var skill = it.next();
                if (skill.isMaxAvailable() && skill.getMax() < max) {
                    skill.setIsTop3(false);             // is definitely not a top3 candidate
                }
            }
            // count skills not in top3
            var nNotTop3 = this.values().stream().filter(i -> i.isTop3() != null && !i.isTop3()).count();
            if (nNotTop3 == 4) {                        // there are 7 skills
                // the others are top3
                for (var skill : notTop3Yet) {
                    if (skill.isTop3() == null) skill.setIsTop3(true);
                }
            }
        } else {
            var isKeeper = this.areKeeperSkills();
            // three skills are marked as top3 skill
            // mark 4 other skills and set maximum to lowest top3 max
            for (var skill : this.values()) {
                if (skill.isTop3() == null || !skill.isTop3()) {
                    skill.setIsTop3(false);
                    if (!skill.isMaxAvailable()) {
                        skill.setMaxLimit(isKeeper, minTop3Max);
                    }
                }
            }
        }
    }

    /**
     * Get Sum of all skills
     * @return double sum
     */
    public double getSkillSum() {
        return this.values().stream().mapToDouble(YouthSkillInfo::getCurrentValue).sum();
    }

    /**
     * Get trained skill sum of all skills
     * @return double sum of current minus start skill values
     */
    public double getTrainedSkillSum() {
        double ret = 0;
        for (var skill : this.values()) {
            ret += skill.getCurrentValue() - skill.getStartValue();
        }
        return ret;
    }
}