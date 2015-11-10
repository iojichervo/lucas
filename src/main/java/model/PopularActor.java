package model;

import java.math.BigDecimal;

public class PopularActor {

    private String actor;
    private BigDecimal votes;

    public PopularActor(String actor) {
        this.actor = actor;
        this.votes = new BigDecimal(0);
    }

    public void addVotes(BigDecimal votes) {
        this.votes = this.votes.add(votes);
    }

    @Override
    public String toString() {
        return "The actor " + actor + " has " + votes + " votes";
    }

    public String getActor() {
        return actor;
    }

    public BigDecimal getVotes() {
        return votes;
    }

}
