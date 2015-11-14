package model;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class FetishActors implements DataSerializable {
    
    private String director;
    private List<String> actors;
    private int appearances;

    public FetishActors() {
    }

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
    
    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(director);
        out.writeObject(actors);
        out.writeInt(appearances);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        director = in.readUTF();
        actors = in.readObject();
        appearances = in.readInt();
    }

}
