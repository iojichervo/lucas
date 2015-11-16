package query;

import java.util.ArrayList;
import java.util.Collections;
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
        actors = new ArrayList<String>(actors);
        Collections.sort(actors);
        for (int i = 0; i < actors.size() - 1; i++) {
            for (int j = i + 1; j < actors.size(); j++) {
                context.emit(actors.get(i), new Buddy(actors.get(j), movie));
            }
        }
    }
}