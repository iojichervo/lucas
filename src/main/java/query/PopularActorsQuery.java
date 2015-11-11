package query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import model.Movie;
import model.PopularActor;
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

public class PopularActorsQuery {
    
    private int n;
    
    public PopularActorsQuery(int n) {
        this.n = n;
    }
    
    public void performQuery(HazelcastInstance instance, IMap<String, Movie> moviesMap) throws InterruptedException, ExecutionException {
     // Preparar la particion de datos y distribuirla en el cluster a trav�s del IMap
        UiUtils.showMessage("\nExecuting query 1. Popular actors.");
        long beginTime = System.currentTimeMillis();
        TimeUtils.print("Initial time: ", beginTime);

        // Ahora el JobTracker y los Workers!
        JobTracker tracker = instance.getJobTracker("default");
    
        // Ahora el Job desde los pares(key, Value) que precisa MapReduce
        KeyValueSource<String, Movie> source = KeyValueSource.fromMap(moviesMap);
        Job<String, Movie> job = tracker.newJob(source);
    
        // Orquestacion de Jobs y lanzamiento
        ICompletableFuture<List<PopularActor>> future = job
                .mapper(new MapperImplementation()) 
                .reducer(new ReducerImplementation())
                .submit(new CollatorImplementation());
        
        // Tomar resultado e Imprimirlo
        List<PopularActor> rta = future.get();

        int min = Math.min(rta.size(), n);
        for (int i = 0 ; i < min ; i++ )
        {
            System.out.println(rta.get(i));
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
            List<String> actors = movie.getActors();
            for (String actor : actors) {
                context.emit(actor, movie.getImdbVotes());
            }
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

    public class CollatorImplementation implements
            Collator<Map.Entry<String, PopularActor>, List<PopularActor>> {

        @Override
        public List<PopularActor> collate(Iterable<Map.Entry<String, PopularActor>> values) {
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
    }
}
