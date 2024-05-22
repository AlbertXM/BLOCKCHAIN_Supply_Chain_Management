package acsse.csc03a3;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class represents a generic blockchain implementation.
 * @author Manamela Machuene Albert
 *@version Mini_Project_2024 Blockchain_Based Clothing Retail Supply Chain Management.
 *
 * @param <T> Type parameter for transactions in the blockchain.
 */

public class myBlockchain<T> 
{
	private static myBlockchain<String> instance;
    private List<myBlock<T>> chain;
    private List<String> stakes;
    private List<StakeHolder> stakeholders = new ArrayList<>();
    private int difficulty;
    private Blockchain<T> bc;
    

    // Replace null with your actual list of transactions for the genesis block
    List<myTransaction<T>> genesisData = new ArrayList<>();
    String previousHash = "0000000000000000000000000000000000000000000000000000000000000000";

    /**
     * Constructor for the blockchain.
     * @param dif Difficulty level for mining blocks.
     */
    public myBlockchain(int dif) 
    {
        this.chain = new ArrayList<>();
        this.stakes = new ArrayList<>();
        this.difficulty = dif;
        bc = new Blockchain<>();
        
        // Assuming genesis block creation with some default data
        myBlock<T> genesisBlock = new myBlock<>(0, previousHash, genesisData);
        chain.add(genesisBlock);
    }

        

    /**
     * Method to add a stakeholder.
     * @param stakeholder Stakeholder to be added.
     */
    public void addStakeholder(StakeHolder stakeholder) 
    {
        stakeholders.add(stakeholder);
    }
    
    
    
 
    // Method to calculate proof of work nonce for a block
    private long calculatePoWNonce(myBlock<T> newBlock) 
    {
    	Random random = new Random();
    	long nonce = random.nextLong() & Long.MAX_VALUE;;
        // Keep generating nonce until the hash of the block meets the difficulty criteria
        while (!isValidNonce(newBlock, nonce)) 
        {
        	nonce = random.nextLong() & Long.MAX_VALUE;
        }
        return nonce;
    }

    // Method to mine a block (proof of work)
    private void mineBlock(myBlock<T> newBlock) 
    {
        int difficulty = getDifficulty();
        String targetHashPrefix = new String(new char[difficulty]).replace('\0', '0');

        long nonce = 0;
        while (true) 
        {
            String hash = calculateHashWithNonce(newBlock, nonce);
            if (hash.startsWith(targetHashPrefix)) 
            {
                newBlock.setNonce(nonce);
                return;
            }
            nonce++;
        }
    }
    
    /**
     * Method to add a block to the blockchain.
     * @param transactions List of transactions to be included in the block.
     */
    public void addBlock(List<myTransaction<T>> transactions) 
    {
    	int blockNumber = chain.size(); // Get the block number for the new block
        myBlock<T> newBlock;
        if (getLastBlock() == null) 
        {
            newBlock = new myBlock<>(0, "GENESIS BLOCK", transactions);
        } else 
        {
            newBlock = new myBlock<>(blockNumber, getLastBlock().getHash(), transactions);
        }
        // Initialize nonce for proof of work
        newBlock.setNonce(calculatePoWNonce(newBlock));
        // Mine the block
        mineBlock(newBlock);
        chain.add(newBlock);
    	
    	
    }
    
    /**
     * Method to get the last block in the blockchain.
     * @return The last block in the blockchain.
     */
    public myBlock<T> getLastBlock() 
    {
        if (chain.isEmpty()) 
        {
            return null; // Return null if the chain is empty
        } else {
            return chain.get(chain.size() - 1); // Return the last block in the chain
        }
    }

    // Method to check if a nonce is valid for a block (proof of work)
    private boolean isValidNonce(myBlock<T> newBlock, long nonce) 
    {
        String hash = calculateHashWithNonce(newBlock, nonce);
        return hash.startsWith(new String(new char[difficulty]).replace('\0', '0'));
    }
    
    private int getDifficulty() 
    {
    	return difficulty; 
	}
 
	// Method to calculate the hash of a block with a given nonce
    private String calculateHashWithNonce(myBlock<T> block, long nonce) 
    {
        // Save the current nonce and set the new nonce
        long originalNonce = block.getNonce();
        block.setNonce(nonce);
        
        // Calculate the hash of the block
        String hash = block.calculateHash();
        
        // Restore the original nonce
        block.setNonce(originalNonce);
        
        return hash;
    }

    /**
     * Method to register a stakeholder.
     * @param stakeholder Name of the stakeholder.
     * @param amount Amount of stake.
     */
    public void registerStake(String stakeholder, int amount) 
    {
        bc.registerStake(stakeholder, amount);
    }




 // Method to check if the chain is valid
    public boolean isChainValid() 
    {
        return bc.isChainValid();
    }

    // Getters and Setters

    public List<myBlock<T>> getChain() 
    {
        return chain;
    }

    public void setChain(List<myBlock<T>> chain) 
    {
        this.chain = chain;
    }

    public List<String> getStakes() 
    {
        return stakes;
    }

    public void setStakes(List<String> stakes)
    {
        this.stakes = stakes;
    }

    /**
     * Method to get the blockchain instance.
     * @return The blockchain instance.
     */
    public static myBlockchain<String> getBlockchainInstance() 
    {
        return getInstance();
    }

    /**
     * Method to add a transaction to the blockchain.
     * @param transaction Transaction to be added.
     */
    public static void addTransaction(myTransaction<String> transaction)
    {
        myBlockchain<String> blockchain = getBlockchainInstance();
        if (blockchain != null) 
        {
            List<myTransaction<String>> transactions = new ArrayList<>();
            transactions.add(transaction);
            blockchain.addBlock(transactions);
        } else {
            System.out.println("Blockchain instance is null. Cannot add transaction.");
        }
    }



    /**
     * Method to get the blockchain instance.
     * @return The blockchain instance.
     */
    public static myBlockchain<String> getInstance() 
    {
        if (instance == null) 
        {
            instance = new myBlockchain<>(4);
        }
        return instance;
    }
    
    
    
    /**
     * Method to select a validator.
     * @return The selected validator.
     */
    public String selectValidator() 
    {
        if (!stakes.isEmpty()) 
        {
            Random random = new Random();
            int index = random.nextInt(stakes.size());
            return stakes.get(index);
        } else 
        {
            return ""; // No stakeholder to select as validator
        }
    }


    /**
     * Method to get the previous block's hash.
     * @return The hash of the previous block.
     */
    public String getPreviousBlockHash() 
    {
        if (chain.isEmpty()) 
        {
            return null;
        }
        return chain.get(chain.size() - 1).getPreviousHash();
    }
}
