package acsse.csc03a3;

import java.util.UUID;

/**
 * Represents a transaction.
 * 
 * @author Manamela Machuene Albert
 * @version Mini_Project_2024 Blockchain_Based Clothing Retail Supply Chain Management.
 * 
 * @param <T> The type of data associated with the transaction.
 */
public class myTransaction<T>
{
    private T data; // Data of the transaction
    private final String sender; // Sender of the transaction
    private final String receiver; // Receiver of the transaction
    private final Integer sizeToSend; // Size to send in the transaction
    private final Long timestamp; // Timestamp of the transaction
    Transaction<T> t;

    /**
     * Constructor for creating a transaction.
     * @param data The data associated with the transaction.
     * @param sender The sender of the transaction.
     * @param receiver The receiver of the transaction.
     * @param sizeToSend The size to send in the transaction.
     * @param timestamp The timestamp of the transaction.
     */
    public myTransaction(T data, String sender, String receiver, int sizeToSend, long timestamp) 
    {
        t = new Transaction<>(sender, receiver, data);
        this.data = data;
        this.sender = sender;
        this.receiver = receiver;
        this.sizeToSend = sizeToSend;
        this.timestamp = timestamp;
    }

    /**
     * Gets the data associated with the transaction.
     * @return The data of the transaction.
     */
    public T getData() 
    {
        return data;
    }

    /**
     * Sets the data associated with the transaction.
     * @param data The data to set.
     */
    public void setData(T data)
    {
        this.data = data;
    }

    /**
     * Getters and setters
     */
    public String getSender() 
    {
        return sender;
    }

    
    public String getReceiver() 
    {
        return receiver;
    }

    
    public int getSizeToSend() 
    {
        return sizeToSend;
    }

    
    public long getTimestamp() 
    {
        return timestamp;
    }

    /**
     * Returns a string representation of the transaction.
     * @return A string representation of the transaction.
     */
    @Override
    public String toString() {
        return t.toString();
    }

    /**
     * Generates a unique transaction ID based on sender, receiver, size, and timestamp.
     * @param senderID The sender ID.
     * @param receiverID The receiver ID.
     * @param sizeToSend2 The size to send.
     * @return The unique transaction ID.
     */
    public final static String getTransactionID(String senderID, String receiverID, int sizeToSend2) 
    {
        // Generate a unique transaction ID based on sender, receiver, size, and timestamp
        String uniqueID = senderID + receiverID + sizeToSend2 + Long.toString(System.currentTimeMillis());
        return UUID.nameUUIDFromBytes(uniqueID.getBytes()).toString();
    }
}
