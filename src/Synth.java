import com.jsyn.Synthesizer;
import com.jsyn.JSyn;
import com.jsyn.unitgen.LineOut;
import java.util.stream.IntStream;

/**
 * Synth class for managing the synthesizer and its components
 */
public class Synth {
    // Array of voices
    private SynthVoice[] voices;
    // The synthesizer
    private Synthesizer synth;
    // The keyboard controller
    private KeyboardController keyboardController;
    // The main line out
    private LineOut mainLineOut;
    // The UI
    private SynthUI ui;

    /**
     * Creates a new Synth
     */
    public Synth() {
        synth = JSyn.createSynthesizer();
        // Create main LineOut for audio output
        mainLineOut = new LineOut(); // Create the main LineOut
        synth.add(mainLineOut); // Add the main LineOut to the synthesizer
        // Create 8 voices and add them to the synth using streams
        voices = IntStream.range(0, 8) // Range of indices from 0 to 7
                .mapToObj(i -> new SynthVoice(synth, mainLineOut)) // Create a new SynthVoice for each index
                .toArray(SynthVoice[]::new); // Convert the stream to an array
        
        // Initialize keyboard controller
        keyboardController = new KeyboardController(this);
        
        // Initialize UI
        ui = new SynthUI(this, keyboardController);
    }
    
    
    /**
     * Find an available (inactive) voice
     * 
     * @return index of available voice, or -1 if all voices are active
     */
    /**
     * Find an available (inactive) voice using Java Streams
     * 
     * @return index of available voice, or -1 if all voices are active
     */
    public int getAvailableVoice() {
        return IntStream.range(0, voices.length) // Range of indices from 0 to voices.length
                .filter(i -> !voices[i].isActive()) // Filter out active voices
                .findFirst() // Find the first available voice
                .orElse(-1); // Return -1 if no available voices
    }
    
    /**
     * Trigger a note on the specified voice
     * 
     * @param voiceIndex the index of the voice to use
     * @param note the MIDI note number
     * @param velocity the velocity (0.0 to 1.0)
     */
    public void noteOn(int voiceIndex, int note, double velocity) {
        if(voiceIndex < 0 || voiceIndex >= voices.length) throw new IllegalArgumentException("Voice index out of bounds");
        if (voiceIndex >= 0 && voiceIndex < voices.length) voices[voiceIndex].triggerNoteOn(note, velocity); // Trigger the note
    }
    
    /**
     * Release a note on the specified voice
     * 
     * @param voiceIndex the index of the voice to release
     */
    public void noteOff(int voiceIndex) {
        if(voiceIndex < 0 || voiceIndex >= voices.length) throw new IllegalArgumentException("Voice index out of bounds");
        if (voiceIndex >= 0 && voiceIndex < voices.length) voices[voiceIndex].triggerNoteOff(); // Release the note
    }

 
    /**
     * Get the array of voices
     * 
     * @return the array of SynthVoice instances
     */
    public SynthVoice[] getVoices() {return voices;}

    /**
     * Gets keyboard controller instance
     * 
     * @return the keyboard controller instance
     */
    public KeyboardController getKeyboardController() {return keyboardController;}

    /**
     * Start the synthesizer and show the keyboard window
    */

    public void start() {
        synth.start(); // Start the synthesizer
        mainLineOut.start(); // Start the main LineOut
        ui.show(); // Show the UI
    }

    /**
     * Stop the synthesizer
     */
    public void stop() {synth.stop();}
    /**
     * Main method to run the synthesizer
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Synth synth = new Synth(); // Create a new Synth
        synth.start(); // Start the synthesizer
    }
}