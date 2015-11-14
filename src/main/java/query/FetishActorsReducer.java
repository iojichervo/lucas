package query;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import model.FetishActors;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class FetishActorsReducer implements
        ReducerFactory<String, String, FetishActors> {
    private static final long serialVersionUID = 1L;

    public Reducer<String, FetishActors> newReducer(final String director) {
        return new Reducer<String, FetishActors>() {
            private Map<String, Integer> appearances;
            private int maxApp;

            public void beginReduce() {
                appearances = new ConcurrentHashMap<String, Integer>();
                maxApp = -1;
            }

            public void reduce(String actor) {
                Integer app = appearances.getOrDefault(actor, 0);
                appearances.put(actor, ++app);
                if (app > maxApp)
                    maxApp = app;
            }

            public FetishActors finalizeReduce() {
                FetishActors fa = new FetishActors(director, maxApp);
                for (Entry<String, Integer> entry : appearances.entrySet()) {
                    if (entry.getValue() == maxApp)
                        fa.addActor(entry.getKey());
                }
                return fa;
            }
        };
    }
}