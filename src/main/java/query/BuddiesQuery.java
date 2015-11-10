package query;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import model.BuddiesAppearances;
import model.Buddy;
import model.Movie;
import util.TimeUtils;
import util.UiUtils;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class BuddiesQuery {
    
    private static final String MAP_NAME = "movies";
    
    public void performQuery(HazelcastInstance instance, Movie[] movies) throws InterruptedException, ExecutionException {
     // Preparar la particion de datos y distribuirla en el cluster a trav�s del IMap
        UiUtils.showMessage("\nExecuting query 3. Buddies.");
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
        ICompletableFuture<List<BuddiesAppearances>> future = job 
                .mapper(new MapperImplementation()) 
                .reducer(new ReducerImplementation())
                .submit(new CollatorImplementation()); 
        
        // Tomar resultado e Imprimirlo
        List<BuddiesAppearances> rta = future.get();
    
        for (BuddiesAppearances e : rta) 
        {
            System.out.println(e);
        }
        long endTime = System.currentTimeMillis();
        TimeUtils.print("Initial time: ", endTime);
        UiUtils.showMessage("Query time: " + (endTime - beginTime) + "ms");
        
        System.exit(0);
    }

    public class MapperImplementation implements Mapper<String, Movie, String, Buddy> 
    {
        private static final long serialVersionUID = 1L;

        public void map(String movieName, Movie movie, Context<String, Buddy> context)
        {
            List<String> actors = movie.getActorsList();
            for (String actor : actors) {
                for (String buddy : actors) {
                    if (!actor.equals(buddy)) {
                        context.emit(actor, new Buddy(buddy, movie));
                    }
                }
            }
        }
    }
    
    public class ReducerImplementation implements ReducerFactory<String, Buddy, List<BuddiesAppearances>> 
    {
        private static final long serialVersionUID = 1L;

        public Reducer<Buddy, List<BuddiesAppearances>> newReducer(final String actor) 
        {
            return new Reducer<Buddy, List<BuddiesAppearances>>()
            {
                private Map<String, List<Movie>> map;

                public void beginReduce()  // una sola vez en cada instancia
                {
                    map = new ConcurrentHashMap<String, List<Movie>>();
                }
        
                public void reduce(Buddy buddy)
                {
                    List<Movie> apps = map.get(buddy.getActor());
                    if (apps == null) {
                        apps = new LinkedList<Movie>();
                    }
                    apps.add(buddy.getMovie());
                    map.put(buddy.getActor(), apps);
                }
        
                public List<BuddiesAppearances> finalizeReduce() 
                {
                    List<BuddiesAppearances> maxApps = new LinkedList<BuddiesAppearances>();
                    for (Entry<String, List<Movie>> app : map.entrySet()) {
                        if (maxApps.isEmpty()) {
                            maxApps.add(new BuddiesAppearances(actor, app.getKey(), app.getValue()));
                        } else {
                            BuddiesAppearances maxApp = maxApps.get(0);
                            if (maxApp.amountAppearances() == app.getValue().size()) {
                                maxApps.add(new BuddiesAppearances(actor, app.getKey(), app.getValue()));
                            } else if (maxApp.amountAppearances() < app.getValue().size()) {
                                maxApps = new LinkedList<BuddiesAppearances>();
                                maxApps.add(new BuddiesAppearances(actor, app.getKey(), app.getValue()));
                            }
                        }
                    }
                    return maxApps;
                }
            };
        }
    }
    
    public class CollatorImplementation implements
            Collator<Map.Entry<String, List<BuddiesAppearances>>, List<BuddiesAppearances>> {

        @Override
        public List<BuddiesAppearances> collate(
                Iterable<Map.Entry<String, List<BuddiesAppearances>>> values) {
            List<BuddiesAppearances> finalList = new LinkedList<BuddiesAppearances>();
            for (Entry<String, List<BuddiesAppearances>> item : values) {
                if (finalList.isEmpty()) {
                    finalList.addAll(item.getValue());
                } else {
                    BuddiesAppearances existentApp = finalList.get(0);
                    BuddiesAppearances newApp = item.getValue().get(0);
                    if (existentApp.amountAppearances() == newApp.amountAppearances()) {
                        finalList.addAll(item.getValue());
                    } else if (existentApp.amountAppearances() < newApp.amountAppearances()) {
                        finalList = new LinkedList<BuddiesAppearances>();
                        finalList.addAll(item.getValue());
                    }
                }
            }
            return finalList;
        }
    }
}
