package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.ExecutionException;

import model.Movie;
import query.AcclaimedMoviesMapper;
import query.AcclaimedMoviesReducer;
import query.BuddiesCollator;
import query.BuddiesMapper;
import query.BuddiesReducer;
import query.FetishActorsMapper;
import query.FetishActorsReducer;
import query.MapReduce;
import query.PopularActorsCollator;
import query.PopularActorsMapper;
import query.PopularActorsReducer;
import service.MovieDeserializer;
import service.Parser;
import util.TimeUtils;
import util.UiUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import exception.ParseException;

public class Lucas {

    private static final String MAP_NAME = "movies";
    private static final String DEFAULT_CLIENT_NAME = "lucas";
    private static final String DEFAULT_CLIENT_PASS = "dev-pass";

    public static void main(String[] args) {
        try {
            Parser parser = new Parser(args);

            Movie[] movies = readJson((File) parser.get(Parser.PATH));

            HazelcastInstance client = configureClient();

            IMap<String, Movie> moviesMap = client.getMap(MAP_NAME);
            moviesMap.clear();
            for (Movie movie : movies) {
                if (movie != null) {
                    moviesMap.putIfAbsent(movie.getTitle(), movie);
                }
            }

            MapReduce mr = new MapReduce(client, moviesMap);
            performQuery(mr, parser);

        } catch (ParseException e) {
            UiUtils.showError(e.getMessage());
        } catch (JsonSyntaxException e) {
            UiUtils.showError("The Json structure of the file isn't correct");
        } catch (Exception e) {
            UiUtils.showError("There was an error while loading the file");
            e.printStackTrace();
        }
    }

    private static Movie[] readJson(File file) throws FileNotFoundException {
        long beginTime = System.currentTimeMillis();
        TimeUtils.print("Starting reading file: ", beginTime);

        Gson gson = new GsonBuilder().registerTypeAdapter(Movie.class,
                new MovieDeserializer()).create();
        FileReader reader = new FileReader(file);

        Movie[] movies = gson.fromJson(reader, Movie[].class);

        long endTime = System.currentTimeMillis();
        TimeUtils.print("Finish reading file: ", endTime);
        UiUtils.showMessage("Time difference: " + (endTime - beginTime) + "ms");

        return movies;
    }

    private static HazelcastInstance configureClient() {
        String name = System.getProperty("name");
        if (name == null) {
            name = DEFAULT_CLIENT_NAME;
        }

        String pass = System.getProperty("password");
        if (pass == null) {
            pass = DEFAULT_CLIENT_PASS;
        }

        ClientConfig ccfg = new ClientConfig();
        ccfg.getGroupConfig().setName(name).setPassword(pass);

        String addresses = System.getProperty("addresses");
        if (addresses != null) {
            String[] arrayAddresses = addresses.split("[,;]");
            ClientNetworkConfig net = new ClientNetworkConfig();
            net.addAddress(arrayAddresses);
            ccfg.setNetworkConfig(net);
        }

        return HazelcastClient.newHazelcastClient(ccfg);
    }

    private static void performQuery(MapReduce mr, Parser parser)
            throws InterruptedException, ExecutionException {
        switch ((int) parser.get(Parser.QUERY)) {
        case 1: {
            int n = (int) parser.get(Parser.N);
            mr.performQuery(new PopularActorsMapper(),
                    new PopularActorsReducer(), new PopularActorsCollator(n));
        }
        case 2: {
            int max = (int) parser.get(Parser.MAX);
            mr.performQuery(new AcclaimedMoviesMapper(max),
                    new AcclaimedMoviesReducer(), null);
        }
        case 3: {
            mr.performQuery(new BuddiesMapper(), new BuddiesReducer(),
                    new BuddiesCollator());
        }
        case 4: {
            mr.performQuery(new FetishActorsMapper(),
                    new FetishActorsReducer(), null);
        }
        default: {
            UiUtils.showError("Invalid query number");
        }
        }
    }
}
