package model;

import java.io.IOException;

import com.google.gson.annotations.SerializedName;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class Movie implements DataSerializable {

    @SerializedName("Title")
    private String title;

    @SerializedName("Actors")
    private String actors;

    @SerializedName("Director")
    private String director;

   /* Unused fields
    * private String year;
    * private String rated;
    * private String released;
    * private String runtime;
    * private String genre;
    * private String writer;
    * private String plot;
    * private String language;
    * private String country;
    * private String awards;
    * private String poster;
    * private String metascore;
    * private String imdbRating;
    * private String imdbVotes;
    * private String imdbID;
    * private String type;
    * private String tomatoMeter;
    * private String tomatoImage;
    * private String tomatoRating;
    * private String tomatoReviews;
    * private String tomatoFresh;
    * private String tomatoRotten;
    * private String tomatoConsensus;
    * private String tomatoUserMeter;
    * private String tomatoUserRating;
    * private String tomatoUserReviews;
    * private String DVD;
    * private String boxOffice;
    * private String production;
    * private String website;
    * private String response;
    */
    
    public String getActors() {
        return actors;
    }

    public String getDirector() {
        return director;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(title);
        out.writeUTF(actors);
        out.writeUTF(director);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        title = in.readUTF();
        actors = in.readUTF();
        director = in.readUTF();
    }
}
