package query;

import java.time.Year;

import model.Movie;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class AcclaimedMoviesMapper implements
        Mapper<String, Movie, Year, Movie> {

    private static final long serialVersionUID = 1L;
    private int max;

    public AcclaimedMoviesMapper(int max) {
        this.max = max;
    }

    public void map(String movieName, Movie movie, Context<Year, Movie> context) {
        if (movie.isPosteriorTo(max)) {
            context.emit(movie.getYear(), movie);
        }
    }
}