package query;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import model.PopularActor;

import com.hazelcast.mapreduce.Collator;

public class PopularActorsCollator implements
        Collator<Map.Entry<String, PopularActor>, List<PopularActor>> {

	int n;

    public PopularActorsCollator(int n) {
    	this.n = n;
	}

	@Override
    public List<PopularActor> collate(
            Iterable<Map.Entry<String, PopularActor>> values) {

        List<PopularActor> list = new ArrayList<PopularActor>();

        for (Map.Entry<String, PopularActor> item : values) {
            list.add(item.getValue());
        }

        list.sort(new Comparator<PopularActor>() {
            @Override
            public int compare(PopularActor o1, PopularActor o2) {
                int i = o1.getVotes().compareTo(o2.getVotes());
                if (i == 0) {
                    return o1.getActor().compareTo(o2.getActor());
                }
                return -i;
            }
        });

        return n > list.size() ? list : list.subList(0, n);

    }

}