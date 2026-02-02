/**
 * Enum for oscillator types
 * 
 */

public enum OscType {
    /**
     * Sine oscillator
     */
    SINE("sine"), 
    /**
     * Square oscillator
     */
    SQUARE("square"), 
    /**
     * Sawtooth oscillator
     */
    SAWTOOTH("sawtooth"), 
    /**
     * Triangle oscillator
     */
    TRIANGLE("triangle"); 

    private final String name; // The name of the oscillator
    private OscType(String name){this.name = name;}// Constructor
    @Override
    public String toString(){return name;}// Returns the name of the oscillator

}
