package query;

import java.time.Year;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import model.Movie;
import util.TimeUtils;
import util.UiUtils;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class AcclaimedMoviesQuery {
    
    private int max;
    
    public AcclaimedMoviesQuery(int max) {
        this.max = max;
    }
    
    public void performQuery(HazelcastInstance instance, IMap<String, Movie> moviesMap) throws InterruptedException, ExecutionException {
     // Preparar la particion de datos y distribuirla en el cluster a trav�s del IMap
        UiUtils.showMessage("\nExecuting query 2. Acclaimed movies.");
        long beginTime = System.currentTimeMillis();
        TimeUtils.print("Initial time: ", beginTime);

        // Ahora el JobTracker y los Workers!
        JobTracker tracker = instance.getJobTracker("default");
    
        // Ahora el Job desde los pares(key, Value) que precisa MapReduce
        KeyValueSource<String, Movie> source = KeyValueSource.fromMap(moviesMap);
        Job<String, Movie> job = tracker.newJob(source);
    
        // Orquestacion de Jobs y lanzamiento
        ICompletableFuture<Map<Year, Movie>> future = job
                .mapper(new MapperImplementation()) 
                .reducer(new ReducerImplementation())
                .submit();
        
        // Tomar resultado e Imprimirlo
        Map<Year, Movie> rta = future.get();

        for (Entry<Year, Movie> e : rta.entrySet())
        {
            UiUtils.showMessage("For year: " + e.getKey()
                    + ", " + e.getValue().getTitle()
                    + " with Metascore " + e.getValue().getMetascore());
        }
        long endTime = System.currentTimeMillis();
        TimeUtils.print("Initial time: ", endTime);
        UiUtils.showMessage("Query time: " + (endTime - beginTime) + "ms");
        
        System.exit(0);
    }

    public class MapperImplementation implements Mapper<String, Movie, Year, Movie> 
    {
        private static final long serialVersionUID = 1L;

        public void map(String movieName, Movie movie, Context<Year, Movie> context)
        {
            if (movie.isPosteriorTo(max)) {
                context.emit(movie.getYear(), movie);
            }
          }
    }
    
    public class ReducerImplementation implements ReducerFactory<Year, Movie, Movie> 
    {
        private static final long serialVersionUID = 1L;

        public Reducer<Movie, Movie> newReducer(final Year year) 
        {
            return new Reducer<Movie, Movie>()
            {
                private Map<Year, Movie> map;
                
                public void beginReduce()  // una sola vez en cada instancia
                {
                    map = new ConcurrentHashMap<Year, Movie>();
                }
        
                public void reduce(Movie movie) 
                {
                    Movie bestOfYear = map.get(year);
                    if (bestOfYear == null
                            || movie.getMetascore() > bestOfYear.getMetascore()) {
                        map.put(year, movie);
                    }
                }
        
                public Movie finalizeReduce() 
                {
                    return map.get(year);
                }
            };
        }
    }
}
