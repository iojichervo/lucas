package model;

import java.io.IOException;
import java.math.BigDecimal;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class PopularActor implements DataSerializable {

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
    
    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(actor);
        out.writeObject(votes);
    }
    
    @Override
    public void readData(ObjectDataInput in) throws IOException {
        actor = in.readUTF();
        votes = in.readObject();
    }

}
