package model;

import java.util.LinkedList;
import java.util.List;

public class FetishActors {
    
    private String director;
    private List<String> actors;
    private int appearances;

    public FetishActors(String director, int appearances) {
        this.director = director;
        this.actors = new LinkedList<String>();
        this.appearances = appearances;
    }
    
    public void addActor(String actor) {
        actors.add(actor);
    }

    @Override
    public String toString() {
        return "For director " + director + ", the actors are " + actors
                + " with " + appearances + " appearances";
    }
    
    

}
