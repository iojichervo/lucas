package query;

import java.time.Year;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import model.Movie;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class AcclaimedMoviesReducer implements
        ReducerFactory<Year, Movie, Movie> {

    private static final long serialVersionUID = 1L;

    public Reducer<Movie, Movie> newReducer(final Year year) {
        return new Reducer<Movie, Movie>() {
            private Map<Year, Movie> map;

            public void beginReduce() {
                map = new ConcurrentHashMap<Year, Movie>();
            }

            public void reduce(Movie movie) {
                Movie bestOfYear = map.get(year);
                if (bestOfYear == null
                        || movie.getMetascore() > bestOfYear.getMetascore()) {
                    map.put(year, movie);
                }
            }

            public Movie finalizeReduce() {
                return map.get(year);
            }
        };
    }
}
