package query;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import model.FetishActors;
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

public class FetishActorsQuery {
    
    private static final String MAP_NAME = "movies";
    
    public void performQuery(HazelcastInstance instance, Movie[] movies) throws InterruptedException, ExecutionException {
     // Preparar la particion de datos y distribuirla en el cluster a trav�s del IMap
        UiUtils.showMessage("\nExecuting query 4. Fetish actors.");
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
        ICompletableFuture<Map<String, FetishActors>> future = job 
                .mapper(new MapperImplementation()) 
                .reducer(new ReducerImplementation())
                .submit(); 
        
        // Tomar resultado e Imprimirlo
        Map<String, FetishActors> rta = future.get();
    
        for (Entry<String, FetishActors> e : rta.entrySet()) 
        {
            System.out.println(e.getValue());
        }
        long endTime = System.currentTimeMillis();
        TimeUtils.print("Initial time: ", endTime);
        UiUtils.showMessage("Query time: " + (endTime - beginTime) + "ms");
        
        System.exit(0);
    }

    public class MapperImplementation implements Mapper<String, Movie, String, String> 
    {
        private static final long serialVersionUID = 1L;

        public void map(String movieName, Movie movie, Context<String, String> context)
        {
            String actors = movie.getActors();
            Scanner scanner = new Scanner(actors);
            scanner.useDelimiter(",");
            while (scanner.hasNext()) {
                String actor = scanner.next().trim();
                context.emit(movie.getDirector(), actor);
            }
            scanner.close();
          }
    }
    
    public class ReducerImplementation implements ReducerFactory<String, String, FetishActors> 
    {
        private static final long serialVersionUID = 1L;

        public Reducer<String, FetishActors> newReducer(final String director) 
        {
            return new Reducer<String, FetishActors>()
            {
                private Map<String, Integer> appearances;
                private int maxApp;
                
                public void beginReduce()  // una sola vez en cada instancia
                {
                    appearances = new ConcurrentHashMap<String, Integer>();
                    maxApp = -1;
                }
        
                public void reduce(String actor) 
                {
                    Integer app = appearances.getOrDefault(actor, 0);
                    appearances.put(actor, ++app);
                    if (app > maxApp) maxApp = app;
                }
        
                public FetishActors finalizeReduce() 
                {
                    FetishActors fa = new FetishActors(director, maxApp);
                    for (Entry<String, Integer> entry : appearances.entrySet()) {
                        if (entry.getValue() == maxApp) fa.addActor(entry.getKey());
                    }
                    return fa;
                }
            };
        }
    }
}
