package logic;

import model.Achievement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AchievementUtils {

    public static List<Achievement> sortByPercent(List<Achievement> achievementList) {
        List<Achievement> sortedList = new ArrayList<>();
        Achievement tmp;
        int index = 0;
        for (int i = 0; i < achievementList.size(); i++) {
            tmp = achievementList.get(i);
            for (Achievement elem : achievementList) {
                if (elem.getPercent() > tmp.getPercent()) {
                    tmp = elem;
                    index = achievementList.indexOf(elem);
                } else {
                    index = 0;
                }
            }
            sortedList.add(tmp);
            achievementList.remove(index);
        }
        return sortedList;
    }

    public static void sort(List<Achievement> list) {
        list.sort(new Comparator<Achievement>() {
            @Override
            public int compare(Achievement o1, Achievement o2) {
                if (o1.getPercent() == o2.getPercent()) {
                    return 0;
                } else if (o1.getPercent() > o2.getPercent()) {
                    return 1;
                } else return 2;
            }
        });
    }


}
