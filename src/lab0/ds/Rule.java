package lab0.ds;

public class Rule {
    private String action;
    private String source;
    private String destination;
    private String kind;
    private int sequenceNumber;
    
    /* If any property doesn't need to be set, for string, please pass "", for int, please pass -1 */
    public Rule(String action, String source, String destination, String kind, int sequenceNumber) {
        this.action = action;
        this.source = source;
        this.destination = destination;
        this.kind = kind;
        this.sequenceNumber = sequenceNumber;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
