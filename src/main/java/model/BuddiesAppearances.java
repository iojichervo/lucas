package model;

import java.util.List;

public class BuddiesAppearances {

    private String actor;
    private String buddy;
    private List<Movie> appearances;

    public BuddiesAppearances(String actor, String buddy, List<Movie> appearances) {
        this.actor = actor;
        this.buddy = buddy;
        this.appearances = appearances;
    }
    
    public int amountAppearances() {
        return appearances.size();
    }

    @Override
    public String toString() {
        return actor + " and " + buddy + " appeared in " + amountAppearances()
                + " movies. " + appearances;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((actor == null) ? 0 : actor.hashCode());
        result = prime * result + ((buddy == null) ? 0 : buddy.hashCode());
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
        BuddiesAppearances other = (BuddiesAppearances) obj;
        if (actor == null) {
            if (other.actor != null)
                return false;
        } else if (!actor.equals(other.actor))
            return false;
        if (buddy == null) {
            if (other.buddy != null)
                return false;
        } else if (!buddy.equals(other.buddy))
            return false;
        return true;
    }
    
}
