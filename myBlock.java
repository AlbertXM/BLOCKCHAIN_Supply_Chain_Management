package acsse.csc03a3;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a block in a blockchain.
 *
 * @author Manamela Machuene Albert
 * @version Mini_Project_2024 Blockchain_Based Clothing Retail Supply Chain Management.
 * 
 * @param <T> The type of transactions stored in the block.
 */
public class myBlock<T> 
{
    private Long blockNumber; // Block number
    private String hash; // Hash value of the block
    private Long nonce; // Nonce value for proof of work
    private String previousHash; // Hash of the previous block
    private List<myTransaction<T>> transactions; // List of transactions in the block
    private List<Transaction<T>> transaction; // List of transactions in the block

    Block<T> b ;

    /**
     * Constructor for creating a new block.
     *
     * @param blockNumber   The block number.
     * @param previousHash  The hash of the previous block.
     * @param transactions  The list of transactions to be included in this block.
     */
    public myBlock(long blockNumber, String previousHash, List<myTransaction<T>> transactions) 
    {
        this.blockNumber = blockNumber;
        this.previousHash = previousHash;
        this.transactions = transactions != null ? transactions : new ArrayList<>(); // Initialize transactions list

        this.transaction = new ArrayList<Transaction<T>>(); // Initialize transactions list
        this.nonce = (long) 0; // Initialize nonce
        this.b = new Block<>(previousHash, transaction); // Initialize the Block object
        this.hash = b.calculateHash(); // Calculate hash for the current block
    }

    /**
     * Calculates the hash value of the block.
     *
     * @return The hash value of the block.
     */
    public String calculateHash() 
    {
        try 
        {
            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Concatenate the block's data including nonce
            String data = previousHash + Long.toString(nonce) + transactions.toString();

            // Perform the hashing
            byte[] hashBytes = digest.digest(data.getBytes());

            // Convert byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) 
                {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) 
        {
            // Handle if SHA-256 algorithm is not available
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Mines the block by performing proof of work.
     *
     * @param difficulty The difficulty level for mining.
     */
    public void mineBlock(int difficulty) 
    {
        // Create a string with leading zeroes based on difficulty
        String target = new String(new char[difficulty]).replace('\0', '0');

        // Increment nonce and recalculate hash until a hash with required difficulty is found
        while (!hash.substring(0, difficulty).equals(target)) 
        {
            nonce++;
            hash = b.calculateHash();
        }

        System.out.println("Block mined: " + hash);
    }

    // Getters and Setters

    /**
     * Gets the block number.
     *
     * @return The block number.
     */
    public long getBlockNumber() 
    {
       return blockNumber;
    }

    /**
     * Sets the block number.
     *
     * @param blockNumber The block number to be set.
     */
    public void setBlockNumber(long blockNumber) 
    {
        this.blockNumber = blockNumber;
    }

    /**
     * Gets the hash value of the block.
     *
     * @return The hash value of the block.
     */
    public String getHash() 
    {
        return hash;
    }

    /**
     * Sets the hash value of the block.
     *
     * @param hash The hash value to be set.
     */
    public void setHash(String hash) 
    {
        this.hash = hash;
    }

    /**
     * Gets the nonce value of the block.
     *
     * @return The nonce value of the block.
     */
    public long getNonce() 
    {
        return nonce;
    }

    /**
     * Sets the nonce value of the block.
     *
     * @param nonce The nonce value to be set.
     */
    public void setNonce(long nonce) 
    {
        this.nonce = nonce;
    }

    /**
     * Gets the hash of the previous block.
     *
     * @return The hash of the previous block.
     */
    public String getPreviousHash() 
    {
        return previousHash;
    }

    /**
     * Sets the hash of the previous block.
     *
     * @param previousHash The hash of the previous block to be set.
     */
    public void setPreviousHash(String previousHash) 
    {
        this.previousHash = previousHash;
    }

    /**
     * Gets the list of transactions in the block.
     *
     * @return The list of transactions in the block.
     */
    public List<myTransaction<T>> getTransactions() 
    {
        return transactions;
    }

    /**
     * Gets the list of transactions in the block.
     *
     * @return The list of transactions in the block.
     */
    public List<Transaction<T>> getTransaction() 
    {
        return transaction;
    }

    /**
     * Sets the list of transactions in the block.
     *
     * @param transactions The list of transactions to be set.
     */
    public void setTransactions(List<myTransaction<T>> transactions) 
    {
        this.transactions = transactions;
    }

    /**
     * Sets the list of transactions in the block.
     *
     * @param transaction The list of transactions to be set.
     */
    public void setTransaction(List<Transaction<T>> transaction) 
    {
        this.transaction = transaction;
    }

    /**
     * Provides a string representation of the block.
     *
     * @return A string representation of the block.
     */
    @Override
    public String toString() 
    {
        return b.toString();
    }
}
