package query;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import model.PopularActor;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class PopularActorsReducer implements
        ReducerFactory<String, BigDecimal, PopularActor> {

    private static final long serialVersionUID = 1L;

    public Reducer<BigDecimal, PopularActor> newReducer(final String actor) {

        return new Reducer<BigDecimal, PopularActor>() {

            private Map<String, PopularActor> map;

            public void beginReduce() {
                map = new ConcurrentHashMap<String, PopularActor>();
                map.put(actor, new PopularActor(actor));
            }

            public void reduce(BigDecimal votes) {
                PopularActor popularActor = map.get(actor);
                popularActor.addVotes(votes);
                map.put(actor, popularActor);
            }

            public PopularActor finalizeReduce() {
                return map.get(actor);
            }

        };

    }

}