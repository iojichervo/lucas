package model;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.annotations.SerializedName;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class Movie implements DataSerializable {

    @SerializedName("Title")
    private String title;

    @SerializedName("Year")
    private String year;

    @SerializedName("Actors")
    private String actors;

    @SerializedName("Director")
    private String director;

    @SerializedName("imdbVotes")
    private String imdbVotes;

    @SerializedName("Metascore")
    private String metascore;

   /* Unused fields
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
    * private String imdbRating;
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

    public List<String> getActorsList() {
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

    public String getDirector() {
        return director;
    }

    public String getTitle() {
        return title;
    }

    public String getImdbVotes() {
        if (imdbVotes.contains("N")) return "0"; //TODO improve this
        return imdbVotes;
    }

    public int getMetascore() {
        try {
            int score = Integer.valueOf(metascore);
            return score;
        } catch (Exception e) {
            return 0;
        }
    }

    public String getYear() {
        return year;
    }

    public boolean isPosteriorTo(int year) {
        try {
            int yearOfThisMovie = Integer.valueOf(this.year);
            return yearOfThisMovie > year;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((year == null) ? 0 : year.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Movie other = (Movie) obj;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (year == null) {
            if (other.year != null)
                return false;
        } else if (!year.equals(other.year))
            return false;
        return true;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(title);
        out.writeUTF(year);
        out.writeUTF(actors);
        out.writeUTF(director);
        out.writeUTF(imdbVotes);
        out.writeUTF(metascore);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        title = in.readUTF();
        year = in.readUTF();
        actors = in.readUTF();
        director = in.readUTF();
        imdbVotes = in.readUTF();
        metascore = in.readUTF();
    }

}
