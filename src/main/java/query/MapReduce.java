package query;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import model.Movie;
import util.TimeUtils;
import util.UiUtils;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.ReducerFactory;

public class MapReduce {

    private HazelcastInstance instance;
    private IMap<String, Movie> moviesMap;

    public MapReduce(HazelcastInstance instance, IMap<String, Movie> moviesMap) {
        this.instance = instance;
        this.moviesMap = moviesMap;
    }

    public <T, S, U, R> void performQuery(Mapper<String, Movie, T, S> mapper,
            ReducerFactory<T, S, U> reducer,
            Collator<Map.Entry<T, U>, List<R>> collator)
            throws InterruptedException, ExecutionException {
        long beginTime = System.currentTimeMillis();
        TimeUtils.print("Initial time: ", beginTime);

        JobTracker tracker = instance.getJobTracker("default");
        KeyValueSource<String, Movie> source = KeyValueSource
                .fromMap(moviesMap);
        Job<String, Movie> job = tracker.newJob(source);

        if (collator == null) {
            ICompletableFuture<Map<T, U>> future = job.mapper(mapper)
                    .reducer(reducer).submit();

            Map<T, U> ans = future.get();
            for (Entry<T, U> e : ans.entrySet()) {
                UiUtils.showMessage(e.getKey() + " - " + e.getValue());
            }
        } else {
            ICompletableFuture<List<R>> future = job.mapper(mapper)
                    .reducer(reducer).submit(collator);

            List<R> ans = future.get();
            for (R elem : ans) {
                UiUtils.showMessage(elem.toString());
            }
        }

        long endTime = System.currentTimeMillis();
        TimeUtils.print("End time: ", endTime);
        UiUtils.showMessage("Query time: " + (endTime - beginTime) + "ms");

        System.exit(0);
    }
}
