package model;

public class Buddy {
    
    private String actor;
    private Movie movie;

    public Buddy(String actor, Movie movie) {
        this.actor = actor;
        this.movie = movie;
    }

    public String getActor() {
        return actor;
    }

    public Movie getMovie() {
        return movie;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((actor == null) ? 0 : actor.hashCode());
        result = prime * result + ((movie == null) ? 0 : movie.hashCode());
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
        Buddy other = (Buddy) obj;
        if (actor == null) {
            if (other.actor != null)
                return false;
        } else if (!actor.equals(other.actor))
            return false;
        if (movie == null) {
            if (other.movie != null)
                return false;
        } else if (!movie.equals(other.movie))
            return false;
        return true;
    }

    
}
