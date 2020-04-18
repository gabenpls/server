package logic;

import model.Achievement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AchievementUtils {


    public static void sortByPercent(List<Achievement> list, boolean decrease) {

        list.sort((o1, o2) -> {
            int sigh = decrease ? -1 : 1;
            return Double.compare(o1.getPercent(), o2.getPercent()) * sigh;
        });

    }

    public static List<Achievement> rarestAchieved(List<Achievement> list, int count) {
        List<Achievement> achievedList = ListUtils.filter(list, Achievement::isAchieved);

        sortByPercent(achievedList, false);

        return achievedList.subList(0, count);
    }

    public static List<Achievement> mostCommonUnAchieved(List<Achievement> list, int count) {
        List<Achievement> unAchievedList = ListUtils.filter(list, achievement -> !achievement.isAchieved());

        sortByPercent(unAchievedList, true);

        return unAchievedList.subList(0, count);
    }
}
