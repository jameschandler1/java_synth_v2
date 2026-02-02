

import com.jsyn.Synthesizer;
import com.jsyn.unitgen.EnvelopeDAHDSR;


/**
 * Envelope class for generating an envelope for a voice
 * the envelope controls the amplitude of the voice over time
 */
public class Envelope {
    /*
     * sustain is the level of the envelope when it is held
     * attack is the time it takes for the envelope to reach the sustain level
     * decay is the time it takes for the envelope to reach the sustain level from the attack level
     * release is the time it takes for the envelope to reach 0 from the sustain level
     */

    private EnvelopeDAHDSR envelope; // The envelope

    /**
     * Creates a new Envelope object.
     * 
     * @param synth the synthesizer to add the envelope to
     */
    public Envelope(Synthesizer synth) {
        this.envelope = new EnvelopeDAHDSR(); // Create the envelope
        synth.add(envelope);
        
        // Set default envelope parameters to reduce clicking
        setAttack(0.01);  
        setDecay(0.1); 
        setSustain(0.8);  
        setRelease(0.2);
    }

    /**
     * Sets the attack time of the envelope.
     * 
     * @param sec the attack time in seconds
     */
    public void setAttack(double sec) {
        if (sec < 0 || sec > 10.0)  throw new IllegalArgumentException("Attack time must be between 0 and 127");
        double safeAttack = Math.min(Math.max(sec, 0.0), 10.0); // Ensure the attack time is within the valid range
        envelope.attack.set(safeAttack); // Set the attack time of the envelope
    }
    /**
     * Gets the attack time of the envelope.
     * 
     * @return the attack time in seconds
     */
    public double getAttack(){return this.envelope.attack.get();}
    /**
     * Sets the decay time of the envelope.
     * 
     * @param sec the decay time in seconds
     */
    public void setDecay(double sec) {
        if (sec < 0 || sec > 10.0)  throw new IllegalArgumentException("Decay time must be between 0 and 127");
        double safeDecay = Math.min(Math.max(sec, 0.0), 10.0); // Ensure the decay time is within the valid range
        envelope.decay.set(safeDecay); // Set the decay time of the envelope
    }
    /**
     * Gets the decay time of the envelope.
     * 
     * @return the decay time in seconds
     */
    public double getDecay(){return this.envelope.decay.get();}
    /**
     * Sets the sustain level of the envelope.
     * 
     * @param level the sustain level
     */
    public void setSustain(double level) {
        if (level < 0 || level > 10.0)  throw new IllegalArgumentException("Sustain level must be between 0 and 10");
        double safeSustain = Math.min(Math.max(level, 0.0), 10.0); // Ensure the sustain level is within the valid range
        envelope.sustain.set(safeSustain); // Set the sustain level of the envelope 
    }
    /**
     * Gets the sustain level of the envelope.
     * 
     * @return the sustain level
     */
    public double getSustain(){return this.envelope.sustain.get();}
    /**
     * Sets the release time of the envelope.
     * 
     * @param sec the release time in seconds
     */
    public void setRelease(double sec) {
        if (sec < 0 || sec > 10.0) throw new IllegalArgumentException("Release time must be between 0 and 10");
        double safeRelease = Math.min(Math.max(sec, 0.0), 10.0); // Ensure the release time is within the valid range
        envelope.release.set(safeRelease); // Set the release time of the envelope
    }
    /**
     * Gets the release time of the envelope.
     * 
     * @return the release time in seconds
     */
    public double getRelease(){return this.envelope.release.get();}

   /**
    * Sets the amplitude of the envelope.
    * 
    * @param amp the amplitude
    */
    public void setAmplitude(double amp) {
        if (amp < 0 || amp > 1)  throw new IllegalArgumentException("Amplitude must be between 0 and 1");
        double safeAmplitude = Math.min(Math.max(amp, 0.0), 1.0); // Ensure the amplitude is within the valid range
        envelope.amplitude.set(safeAmplitude); // Set the amplitude of the envelope
    }
    /**
     * Gets the amplitude of the envelope.
     * 
     * @return the amplitude
     */
    public double getAmplitude(){return this.envelope.amplitude.get();}

    /**
     * Sets the velocity of the envelope.
     * 
     * @param velocity the velocity
     */
    public void setVelocity(double velocity) {
        if (velocity < 0 || velocity > 1) throw new IllegalArgumentException("Velocity must be between 0 and 1");
        double safeVelocity = Math.min(Math.max(velocity, 0.0), 1.0); // Ensure the velocity is within the valid range
        envelope.input.set(safeVelocity); // Set the velocity of the envelope
    }

    //Dont need a getter for velocity because the triggerNoteOn method inside the SynthVoice class sets the velocity
    //through its call by synth.noteOn or Off inside the KeyboardController class which "plays" the synth for us by 
    //creating events that send midi notes to the synth

    /**
     * Triggers the envelope
     * 
     * @param on true to trigger the envelope, false to release it
     */
    public void trigger(boolean on) {
        // Use a value well above the default threshold (0.01) for reliable triggering
        envelope.input.set(on ? 1.0 : 0.0); // Set the input of the envelope
    }

    /**
     * returns the envelope.amplitude
     * 
     * @return the envelope.amplitude
     */
    public EnvelopeDAHDSR get() {return envelope;}
}