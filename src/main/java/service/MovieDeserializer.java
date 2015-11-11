package service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Year;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import model.Movie;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class MovieDeserializer implements JsonDeserializer<Movie> {

    private static final String JSON_TITLE = "Title";
    private static final String JSON_YEAR = "Year";
    private static final String JSON_ACTORS = "Actors";
    private static final String JSON_DIRECTOR = "Director";
    private static final String JSON_IMDBVOTES = "imdbVotes";
    private static final String JSON_METASCORE = "Metascore";
    private static final String JSON_TYPE = "Type";
    private static final String MOVIE_TYPE = "movie";

    @Override
    public Movie deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (!isValidMovie(jsonObject)) {
            return null;
        }

        // Movie elements
        String title;
        Year year;
        List<String> actors;
        String director;
        BigDecimal imdbVotes;
        int metascore;

        title = jsonObject.get(JSON_TITLE).getAsString();
        year = yearOf(jsonObject.get(JSON_YEAR).getAsString());
        actors = getActors(jsonObject.get(JSON_ACTORS).getAsString());
        director = jsonObject.get(JSON_DIRECTOR).getAsString();
        imdbVotes = votesOf(jsonObject.get(JSON_IMDBVOTES).getAsString());
        metascore = scoreOf(jsonObject.get(JSON_METASCORE).getAsString());
        
        Movie movie = new Movie.Builder()
            .setTitle(title)
            .setYear(year)
            .setActors(actors)
            .setDirector(director)
            .setImdbVotes(imdbVotes)
            .setMetascore(metascore)
            .build();

        return movie;
    }

    private boolean isValidMovie(JsonObject jsonObject) {
        // Check if it is a movie
        JsonElement typeElement = jsonObject.get(JSON_TYPE);
        if (typeElement == null
                || !typeElement.getAsString().equals(MOVIE_TYPE)) {
            return false;
        }

        // All fields must be in the json
        if (!jsonObject.has(JSON_TITLE) || !jsonObject.has(JSON_YEAR)
                || !jsonObject.has(JSON_ACTORS)
                || !jsonObject.has(JSON_DIRECTOR)
                || !jsonObject.has(JSON_IMDBVOTES)
                || !jsonObject.has(JSON_METASCORE)) {
            return false;
        }

        return true;
    }

    private Year yearOf(String string) {
        try {
            return Year.of(Integer.valueOf(string));
        } catch (Exception e) {
            return Year.of(0);
        }
    }

    private List<String> getActors(String actors) {
        List<String> list = new LinkedList<String>();
        Scanner scanner = new Scanner(actors);
        scanner.useDelimiter(",");
        while (scanner.hasNext()) {
            String actor = scanner.next().trim();
            list.add(actor);
        }
        scanner.close();
        return list;
    }
    
    private BigDecimal votesOf(String s) {
        try {
            return new BigDecimal(s.replaceAll(",", ""));
        } catch (Exception e) {
            return new BigDecimal(0);
        }
    }

    private int scoreOf(String s) {
        try {
            return Integer.valueOf(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
