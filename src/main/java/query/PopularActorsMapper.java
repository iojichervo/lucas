package query;

import java.math.BigDecimal;
import java.util.List;

import model.Movie;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class PopularActorsMapper implements
        Mapper<String, Movie, String, BigDecimal> {

    private static final long serialVersionUID = 1L;

    public void map(String movieName, Movie movie,
            Context<String, BigDecimal> context) {

        List<String> actors = movie.getActors();

        for (String actor : actors) {
            context.emit(actor, movie.getImdbVotes());
        }

    }

}