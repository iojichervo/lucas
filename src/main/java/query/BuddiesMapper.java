package query;

import java.util.List;

import model.Buddy;
import model.Movie;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class BuddiesMapper implements Mapper<String, Movie, String, Buddy> {

    private static final long serialVersionUID = 1L;

    public void map(String movieName, Movie movie,
            Context<String, Buddy> context) {
        List<String> actors = movie.getActors();
        for (String actor : actors) {
            for (String buddy : actors) {
                if (!actor.equals(buddy)) {
                    context.emit(actor, new Buddy(buddy, movie));
                }
            }
        }
    }
}