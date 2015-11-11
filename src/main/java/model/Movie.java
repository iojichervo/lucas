package model;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class Movie implements DataSerializable {

    @SerializedName("Title")
    private String title;

    @SerializedName("Year")
    private Year year;

    @SerializedName("Actors")
    private List<String> actors;

    @SerializedName("Director")
    private String director;

    @SerializedName("imdbVotes")
    private BigDecimal imdbVotes;

    @SerializedName("Metascore")
    private int metascore;

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
    
    public static class Builder {
        private Movie movie;

        public Builder() {
            movie = new Movie();
        }

        public Builder setTitle(String title) {
            movie.title = title;
            return this;
        }

        public Builder setYear(Year year) {
            movie.year = year;
            return this;
        }

        public Builder setActors(List<String> actors) {
            movie.actors = actors;
            return this;
        }

        public Builder setDirector(String director) {
            movie.director = director;
            return this;
        }

        public Builder setImdbVotes(BigDecimal votes) {
            movie.imdbVotes = votes;
            return this;
        }

        public Builder setMetascore(int score) {
            movie.metascore = score;
            return this;
        }

        public Movie build() {
            return movie;
        }
    }

    public String getTitle() {
        return title;
    }

    public Year getYear() {
        return year;
    }

    public List<String> getActors() {
        return actors;
    }

    public String getDirector() {
        return director;
    }

    public BigDecimal getImdbVotes() {
        return imdbVotes;
    }

    public int getMetascore() {
        return metascore;
    }

    public boolean isPosteriorTo(int otherYear) {
        return year.getValue() > otherYear;
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
        out.writeObject(year);
        out.writeObject(actors);
        out.writeUTF(director);
        out.writeObject(imdbVotes);
        out.writeInt(metascore);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        title = in.readUTF();
        year = in.readObject();
        actors = in.readObject();
        director = in.readUTF();
        imdbVotes = in.readObject();
        metascore = in.readInt();
    }

}
