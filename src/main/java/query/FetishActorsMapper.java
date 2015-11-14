package query;

import java.util.List;

import model.Movie;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class FetishActorsMapper implements
        Mapper<String, Movie, String, String> {
    private static final long serialVersionUID = 1L;

    public void map(String movieName, Movie movie,
            Context<String, String> context) {
        List<String> actors = movie.getActors();
        for (String actor : actors) {
            context.emit(movie.getDirector(), actor);
        }
    }
}