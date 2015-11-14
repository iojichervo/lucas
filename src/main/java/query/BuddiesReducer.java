package query;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import model.BuddiesAppearances;
import model.Buddy;
import model.Movie;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class BuddiesReducer implements
        ReducerFactory<String, Buddy, List<BuddiesAppearances>> {
    private static final long serialVersionUID = 1L;

    public Reducer<Buddy, List<BuddiesAppearances>> newReducer(
            final String actor) {
        return new Reducer<Buddy, List<BuddiesAppearances>>() {
            private Map<String, List<Movie>> map;

            public void beginReduce() {
                map = new ConcurrentHashMap<String, List<Movie>>();
            }

            public void reduce(Buddy buddy) {
                List<Movie> apps = map.get(buddy.getActor());
                if (apps == null) {
                    apps = new LinkedList<Movie>();
                }
                apps.add(buddy.getMovie());
                map.put(buddy.getActor(), apps);
            }

            public List<BuddiesAppearances> finalizeReduce() {
                List<BuddiesAppearances> maxApps = new LinkedList<BuddiesAppearances>();
                for (Entry<String, List<Movie>> app : map.entrySet()) {
                    if (maxApps.isEmpty()) {
                        maxApps.add(new BuddiesAppearances(actor, app.getKey(),
                                app.getValue()));
                    } else {
                        BuddiesAppearances maxApp = maxApps.get(0);
                        if (maxApp.amountAppearances() == app.getValue().size()) {
                            maxApps.add(new BuddiesAppearances(actor, app
                                    .getKey(), app.getValue()));
                        } else if (maxApp.amountAppearances() < app.getValue()
                                .size()) {
                            maxApps = new LinkedList<BuddiesAppearances>();
                            maxApps.add(new BuddiesAppearances(actor, app
                                    .getKey(), app.getValue()));
                        }
                    }
                }
                return maxApps;
            }
        };
    }
}