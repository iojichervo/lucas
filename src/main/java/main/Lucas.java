package main;

import java.io.File;
import java.io.FileReader;

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

    public static void main(String[] args) {
        try {
            Parser parser = new Parser(args);

            long beginTime = System.currentTimeMillis();
            TimeUtils.print("Starting reading file: ", beginTime);

            Gson gson = new GsonBuilder().registerTypeAdapter(Movie.class,
                    new MovieDeserializer()).create();
            FileReader reader = new FileReader((File) parser.get(Parser.PATH));

            Movie[] movies = gson.fromJson(reader, Movie[].class);

            long endTime = System.currentTimeMillis();
            TimeUtils.print("Finish reading file: ", endTime);
            UiUtils.showMessage("Time difference: " + (endTime - beginTime)
                    + "ms");

            String name = System.getProperty("name");
            if (name == null) {
            	name = "lucas";
            }

            String pass = System.getProperty("password");
            if (pass == null) {
            	pass = "dev-pass";
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

            HazelcastInstance client = HazelcastClient.newHazelcastClient(ccfg);
            IMap<String, Movie> moviesMap = client.getMap(MAP_NAME);

            moviesMap.clear();

            for (Movie movie : movies) {
                if (movie != null) {
                    moviesMap.set(movie.getTitle(), movie);
                }
            }
            MapReduce mr = new MapReduce(client, moviesMap);

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
