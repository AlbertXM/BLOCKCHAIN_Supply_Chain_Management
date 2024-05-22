package acsse.csc03a3;

/**
 * Represents a stakeholder with a public key and stake.
 * 
 * @author Manamela Machuene Albert
 * @version Mini_Project_2024 Blockchain_Based Clothing Retail Supply Chain Management..
 */
public class StakeHolder
{

    private String publicKey; // The public key of the stakeholder
    private int stake; // The amount of stake held by the stakeholder

    /**
     * Constructs a StakeHolder object with the specified public key and stake.
     *
     * @param publicKey the public key of the stakeholder
     * @param stake the amount of stake held by the stakeholder
     */
    public StakeHolder(String publicKey, int stake) 
    {
        this.publicKey = publicKey;
        this.stake = stake;
    }

    /**
     * Gets the public key of the stakeholder.
     *
     * @return the public key of the stakeholder
     */
    public String getPublicKey() 
    {
        return publicKey;
    }

    /**
     * Gets the stake held by the stakeholder.
     *
     * @return the stake held by the stakeholder
     */
    public int getStake() 
    {
        return stake;
    }
}