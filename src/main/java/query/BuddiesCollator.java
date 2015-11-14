package query;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import model.BuddiesAppearances;

import com.hazelcast.mapreduce.Collator;

public class BuddiesCollator
        implements
        Collator<Map.Entry<String, List<BuddiesAppearances>>, List<BuddiesAppearances>> {

    @Override
    public List<BuddiesAppearances> collate(
            Iterable<Map.Entry<String, List<BuddiesAppearances>>> values) {
        List<BuddiesAppearances> finalList = new LinkedList<BuddiesAppearances>();
        for (Entry<String, List<BuddiesAppearances>> item : values) {
            if (finalList.isEmpty()) {
                finalList.addAll(item.getValue());
            } else {
                BuddiesAppearances existentApp = finalList.get(0);
                BuddiesAppearances newApp = item.getValue().get(0);
                if (existentApp.amountAppearances() == newApp
                        .amountAppearances()) {
                    finalList.addAll(item.getValue());
                } else if (existentApp.amountAppearances() < newApp
                        .amountAppearances()) {
                    finalList = new LinkedList<BuddiesAppearances>();
                    finalList.addAll(item.getValue());
                }
            }
        }
        return finalList;
    }
}