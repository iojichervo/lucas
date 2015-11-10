package query;

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
    
    private static final String MAP_NAME = "movies";
    
    private int max;
    
    public AcclaimedMoviesQuery(int max) {
        this.max = max;
    }
    
    public void performQuery(HazelcastInstance instance, Movie[] movies) throws InterruptedException, ExecutionException {
     // Preparar la particion de datos y distribuirla en el cluster a trav�s del IMap
        UiUtils.showMessage("\nExecuting query 2. Acclaimed movies.");
        long beginTime = System.currentTimeMillis();
        TimeUtils.print("Initial time: ", beginTime);

        IMap<String, Movie> moviesMap = instance.getMap(MAP_NAME);
        
        for (Movie movie : movies) {
            moviesMap.set(movie.getTitle(), movie);
        }
        
        // Ahora el JobTracker y los Workers!
        JobTracker tracker = instance.getJobTracker("default");
    
        // Ahora el Job desde los pares(key, Value) que precisa MapReduce
        KeyValueSource<String, Movie> source = KeyValueSource.fromMap(moviesMap);
        Job<String, Movie> job = tracker.newJob(source);
    
        // Orquestacion de Jobs y lanzamiento
        ICompletableFuture<Map<String, Movie>> future = job
                .mapper(new MapperImplementation()) 
                .reducer(new ReducerImplementation())
                .submit();
        
        // Tomar resultado e Imprimirlo
        Map<String, Movie> rta = future.get();

        for (Entry<String, Movie> e : rta.entrySet()) 
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

    public class MapperImplementation implements Mapper<String, Movie, String, Movie> 
    {
        private static final long serialVersionUID = 1L;

        public void map(String movieName, Movie movie, Context<String, Movie> context)
        {
            if (movie.isPosteriorTo(max)) {
                context.emit(movie.getYear(), movie);
            }
          }
    }
    
    public class ReducerImplementation implements ReducerFactory<String, Movie, Movie> 
    {
        private static final long serialVersionUID = 1L;

        public Reducer<Movie, Movie> newReducer(final String year) 
        {
            return new Reducer<Movie, Movie>()
            {
                private Map<String, Movie> map;
                
                public void beginReduce()  // una sola vez en cada instancia
                {
                    map = new ConcurrentHashMap<String, Movie>();
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
/*
    public class CollatorImplementation implements
            Collator<Map.Entry<String, PopularActor>, List<Movie>> {

        @Override
        public List<Movie> collate(Iterable<Map.Entry<String, PopularActor>> values) {
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
            return list;
        }
    } */
}
