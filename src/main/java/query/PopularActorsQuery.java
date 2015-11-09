package query;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import model.FetishActors;
import model.Movie;
import model.PopularActor;
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

public class PopularActorsQuery {
    
    private static final String MAP_NAME = "movies";
    
    private int n;
    
    public PopularActorsQuery(int n) {
        this.n = n;
    }
    
    public void performQuery(HazelcastInstance instance, Movie[] movies) throws InterruptedException, ExecutionException {
     // Preparar la particion de datos y distribuirla en el cluster a trav�s del IMap
        UiUtils.showMessage("\nExecuting query 1. Popular actors.");
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
        ICompletableFuture<Map<String, PopularActor>> future = job 
                .mapper(new MapperImplementation()) 
                .reducer(new ReducerImplementation())
                .submit();
        
        // Tomar resultado e Imprimirlo
        Map<String, PopularActor> rta = future.get();
    
        for (Entry<String, PopularActor> e : rta.entrySet()) 
        {
            System.out.println(e.getValue());
        }
        long endTime = System.currentTimeMillis();
        TimeUtils.print("Initial time: ", endTime);
        UiUtils.showMessage("Query time: " + (endTime - beginTime) + "ms");
        
        System.exit(0);
    }

    public class MapperImplementation implements Mapper<String, Movie, String, BigDecimal> 
    {
        private static final long serialVersionUID = 1L;

        public void map(String movieName, Movie movie, Context<String, BigDecimal> context)
        {
            String actors = movie.getActors();
            Scanner scanner = new Scanner(actors);
            scanner.useDelimiter(",");
            BigDecimal votes = new BigDecimal(movie.getImdbVotes().replaceAll(",", ""));
            while (scanner.hasNext()) {
                String actor = scanner.next().trim();
                context.emit(actor, votes);
            }
            scanner.close();
          }
    }
    
    public class ReducerImplementation implements ReducerFactory<String, BigDecimal, PopularActor> 
    {
        private static final long serialVersionUID = 1L;

        public Reducer<BigDecimal, PopularActor> newReducer(final String actor) 
        {
            return new Reducer<BigDecimal, PopularActor>()
            {
                private Map<String, PopularActor> map;
                
                public void beginReduce()  // una sola vez en cada instancia
                {
                    map = new ConcurrentHashMap<String, PopularActor>();
                    map.put(actor, new PopularActor(actor));
                }
        
                public void reduce(BigDecimal votes) 
                {
                    PopularActor popularActor = map.get(actor);
                    popularActor.addVotes(votes);
                    map.put(actor, popularActor);
                }
        
                public PopularActor finalizeReduce() 
                {
                    return map.get(actor);
                }
            };
        }
    }
}
