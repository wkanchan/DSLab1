package lab0.ds;
import java.io.Serializable;

public class Message implements Serializable {	
	
    private static final long serialVersionUID = 1L;
    
    protected String source;
    protected String destination;
	protected int sequenceNumber;
	protected boolean duplicate;
    protected String kind;
    protected Object data;
	
	public Message(String destination, String kind, Object data) {
		this.destination = destination;
		this.kind = kind;
		this.data = data;
	}
	
	public Message(Message message) {
		this.source = message.getSource();
		this.destination = message.getDestination();
		this.sequenceNumber = message.getSequenceNumber();
		this.duplicate = message.getDuplicate();
		this.kind = message.getKind();
		this.data = message.getData();
	}
	
	public Message(String source, String destination, int sequenceNumber, boolean duplicate, 
		String kind, Object data) {
		this.source = source;
		this.destination = destination;
		this.sequenceNumber = sequenceNumber;
		this.duplicate = duplicate;
	    this.kind = kind;
		this.data = data;
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

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public boolean getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}	
