package main;

import java.io.File;
import java.io.FileReader;

import model.Movie;
import query.AcclaimedMoviesQuery;
import query.BuddiesQuery;
import query.FetishActorsQuery;
import query.PopularActorsQuery;
import service.Parser;
import util.TimeUtils;
import util.UiUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import exception.ParseException;

public class Lucas {
    public static void main(String[] args) {
        try {
            Parser parser = new Parser(args);

            long beginTime = System.currentTimeMillis();
            TimeUtils.print("Starting reading file: ", beginTime);

            Gson gson = new Gson();
            FileReader reader = new FileReader((File) parser.get(Parser.PATH));

            //TODO Make a deserializer to serialize online movies (check on type, exclude tv series)
            Movie[] movies = gson.fromJson(reader, Movie[].class);

            long endTime = System.currentTimeMillis();
            TimeUtils.print("Finish reading file: ", endTime);
            UiUtils.showMessage("Time difference: " + (endTime - beginTime) + "ms");

            HazelcastInstance instance = Hazelcast.newHazelcastInstance();

            switch ((int) parser.get(Parser.QUERY)) {
                case 1: {
                    int n = (int) parser.get(Parser.N);
                    PopularActorsQuery paq = new PopularActorsQuery(n);
                    paq.performQuery(instance, movies);
                }
                case 2: {
                    int max = (int) parser.get(Parser.MAX);
                    AcclaimedMoviesQuery amq = new AcclaimedMoviesQuery(max);
                    amq.performQuery(instance, movies);
                }
                case 3: {
                    BuddiesQuery bq = new BuddiesQuery();
                    bq.performQuery(instance, movies);
                }
                case 4: {
                    FetishActorsQuery faq = new FetishActorsQuery();
                    faq.performQuery(instance, movies);
                }
                default: {
                    UiUtils.showError("Invalid query number");
                }
            }

        } catch (ParseException e) {
            UiUtils.showError(e.getMessage());
        } catch (JsonSyntaxException e) {
            UiUtils.showError("The Json structure of the file isn't correct");
        } catch (Exception e) {
            UiUtils.showError("There was an error while loading the file");
            e.printStackTrace();
        }
    }
}
