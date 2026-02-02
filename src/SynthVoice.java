import com.jsyn.Synthesizer;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitOscillator;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.Add;

import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.SquareOscillator;
import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.unitgen.TriangleOscillator;

/**
 * SynthVoice class for generating a single voice of the synthesizer
 * extends UnitGenerator
 */
public class SynthVoice extends UnitGenerator {
    //The oscillator for the voice
    private UnitOscillator oscillator;
    //The synthesizer that the voice belongs to
    private Synthesizer synth;
    // The current oscillator type
    private OscType currentOscType = OscType.SINE;
    // Whether the voice is active
    private boolean isActive = false;
    // The envelope for the voice
    private Envelope envelope;
    //The line out for the voice
    private LineOut lineOut;
    // The mixer for the voice
    private Add mixer;
     //The filter for the voice
    private Filter filter;
    //The current note being played
    private int currentNote = -1;
    
    /**
     * Constructor for SynthVoice
     * 
     * @param synth the Synthesizer to add components to
     * @param mainLineOut the LineOut to connect to
     */
    public SynthVoice(Synthesizer synth, LineOut mainLineOut){
        this.synth = synth; // The synthesizer
        this.lineOut = mainLineOut; // The main line out
        initComponents(synth); // Initialize components
        connectComponents(); // Connect components
    }

    /**
     * Generates audio for the voice
     * 
     * @param start the start index
     * @param limit the limit index
     */
    @Override
    public void generate(int start, int limit) {
        // the generate method is required because SynthVoice extends UnitGenerator 
        //from the JSyn library, and UnitGenerator is an abstract class that 
        //requires the implementation of the generate method. 
        // The actual audio generation is handled by the oscillator and envelope
    }

    private void initComponents(Synthesizer synth){
        if(synth == null) throw new IllegalArgumentException("Synthesizer cannot be null");
        //create oscillator with zero initial amplitude to prevent automatic sound
        oscillator = createOsc(currentOscType);
        oscillator.amplitude.set(0.0); // Start silent until a note is triggered
        
        //Create envelope
        envelope = new Envelope(synth);
        // Set default envelope parameters for better sound
        envelope.setAttack(0.05); // Attack time in seconds
        envelope.setDecay(0.1); // Decay time in seconds
        envelope.setSustain(0.7); // Sustain level (0.0 to 1.0)
        envelope.setRelease(0.2); // Release time in seconds
        
        //create filter
        filter = new Filter(synth); // Create filter
        // Set default filter parameters
        filter.setFilterCutoff(2000.0); // 2000 Hz cutoff frequency
        filter.setFilterResonance(0.5); // Moderate resonance
        
        //create mixer for this voice
        mixer = new Add();
        
        // LineOut is already set in the constructor
        
        //add components to synth
        synth.add(oscillator); // Add oscillator to synthesizer
        synth.add(envelope.get()); // Add envelope to synthesizer
        synth.add(filter.get()); // Add filter to synthesizer
        synth.add(mixer); // Add mixer to synthesizer
    }
    /**
     * Connects all components in the audio signal chain:
     * Oscillator → Envelope → Filter → Mixer → LineOut → Speakers
     */
    private void connectComponents(){
        // Connect oscillator to filter input
        oscillator.output.connect(filter.get().input);
        // Connect envelope output to oscillator amplitude for volume control
        envelope.get().output.connect(oscillator.amplitude);
        // Set initial oscillator amplitude to 1.0 (full volume)
        oscillator.amplitude.set(1.0);
        // Connect filter output to mixer
        filter.get().output.connect(mixer.inputA);
        // Connect mixer to mainLineOut (both left and right channels)
        mixer.output.connect(0, this.lineOut.input, 0); // Left channel
        mixer.output.connect(0, this.lineOut.input, 1); // Right channel
    }
    

    private UnitOscillator createOsc(OscType type) {
        if(type == null) throw new IllegalArgumentException("Oscillator type cannot be null");
        //check if there is a current oscillator in use
        //if so remove it from the synth
        if (oscillator != null) synth.remove(oscillator); // Remove old oscillator from synth
        
        UnitOscillator newOsc = null;
        switch (type) {
            case SINE:
                newOsc = new SineOscillator(); // Create sine oscillator
                break;
            case SQUARE:
                newOsc = new SquareOscillator(); // Create square oscillator
                break;
            case SAWTOOTH:
                newOsc = new SawtoothOscillator(); // Create sawtooth oscillator
                break;
            case TRIANGLE:
                newOsc = new TriangleOscillator(); // Create triangle oscillator
                break;
        }
        return newOsc;
    }

    /**
     * Sets the current oscillator type
     * 
     * @param type the new oscillator type
     */
    public void setCurrentOscType(OscType type) {
        if(type == null) throw new IllegalArgumentException("Oscillator type cannot be null");
        
        // If we already have this type, do nothing
        if (type == currentOscType) {return;}
        
        // Update current oscillator type first
        currentOscType = type;
        
        // Remove old oscillator if it exists
        if (oscillator != null) {
            System.out.println("[Voice " + this + "] Disconnecting old oscillator");
            synth.remove(oscillator); // Remove old oscillator from synth
        }
        
        // Create new oscillator with type
        UnitOscillator newOsc = createOsc(type);
        if (newOsc != null) { // If we successfully created the oscillator
            System.out.println("[Voice " + this + "] Creating new oscillator");
            
            // Disconnect all relevant connections
            try {
                filter.get().output.disconnect(mixer.inputA); // Disconnect filter output from mixer input A
                System.out.println("[Voice " + this + "] Disconnected filter.output -> mixer.inputA");
            } catch (IllegalArgumentException e) {
                System.out.println("[Voice " + this + "] filter.output not connected to mixer.inputA" + e.getMessage());
            }
            try {
                mixer.output.disconnect(0, lineOut.input, 0);
                System.out.println("[Voice " + this + "] Disconnected mixer.output[0] -> lineOut.input[0]");
            } catch (IllegalArgumentException e) {
                System.out.println("[Voice " + this + "] mixer.output[0] not connected to lineOut.input[0]" + e.getMessage());
            }
            try {
                mixer.output.disconnect(0, lineOut.input, 1); // Disconnect mixer output from lineOut input
                System.out.println("[Voice " + this + "] Disconnected mixer.output[0] -> lineOut.input[1]");
            } catch (IllegalArgumentException e) {
                System.out.println("[Voice " + this + "] mixer.output[0] not connected to lineOut.input[1]" + e.getMessage());
            }
            try {
                envelope.get().output.disconnect(filter.get().amplitude);
                envelope.get().output.disconnect(oscillator.amplitude);
                System.out.println("[Voice " + this + "] Disconnected envelope.output -> filter.amplitude");
            } catch (IllegalArgumentException e) {
                System.out.println("[Voice " + this + "] envelope.output not connected to filter.amplitude" + e.getMessage());
            }
            try {
                if (oscillator != null) {
                    oscillator.output.disconnect(0, filter.get().input, 0); // Disconnect old oscillator output from filter input
                    System.out.println("[Voice " + this + "] Disconnected old oscillator.output -> filter.input");
                }
            } catch (   IllegalArgumentException e) {
                System.out.println("[Voice " + this + "] old oscillator.output not connected to filter.input" + e.getMessage());
            }

            // Reconnect the signal chain in order
            newOsc.output.connect(0, filter.get().input, 0); // Connect new oscillator output to filter input
            System.out.println("[Voice " + this + "] Connected newOsc.output -> filter.input");

            envelope.get().output.connect(newOsc.amplitude); // Connect envelope output to new oscillator amplitude
            System.out.println("[Voice " + this + "] Connected envelope.output -> filter.amplitude");

            filter.get().output.connect(mixer.inputA); // Connect filter output to mixer input A
            System.out.println("[Voice " + this + "] Connected filter.output -> mixer.inputA");

            mixer.output.connect(0, lineOut.input, 0); // Left channel
            System.out.println("[Voice " + this + "] Connected mixer.output[0] -> lineOut.input[0]");
            mixer.output.connect(0, lineOut.input, 1); // Right channel
            System.out.println("[Voice " + this + "] Connected mixer.output[0] -> lineOut.input[1]");

            // Set default values
            newOsc.frequency.set(440.0); // A4 note
            newOsc.amplitude.set(1.0); // Full volume

            // Add to synth after connections are set up
            synth.add(newOsc); // Add new oscillator to synth
            System.out.println("[Voice " + this + "] Added new oscillator to synth");
            newOsc.start(); // Start the new oscillator
            System.out.println("[Voice " + this + "] Started new oscillator");

            // Update oscillator reference at the end
            oscillator = newOsc;
        }
    }

    /**
     * Triggers specified MIDI note converts MIDI note to frequency and starts envelope
     *
     * @param note the MIDI note to trigger
     * @param velocity the velocity of the note
     */
    public void triggerNoteOn(int note, double velocity) {
        if(note < 0 || note > 127)throw new IllegalArgumentException("Note must be between 0 and 127");
        if(velocity < 0 || velocity > 1) throw new IllegalArgumentException("Velocity must be between 0 and 1");
    
        currentNote = note; // Set the current note
        isActive = true; // Set the voice to active
        
        // Convert MIDI note to frequency
        double freq = 440.0 * Math.pow(2, (note - 69) / 12.0); // Convert MIDI note to frequency
        
        // Set frequency and trigger envelope
        if (oscillator != null) { // If the oscillator exists
            oscillator.frequency.set(freq); // Set oscillator frequency
            envelope.trigger(true); // Trigger envelope
            envelope.setVelocity(velocity); // Set envelope velocity
        }
    }

    /**
     * Releases the specified MIDI note and stops the envelope
     * 
     */
    public void triggerNoteOff(){
        envelope.trigger(false); // Release envelope
        // Reset oscillator amplitude after release time to ensure no sound leakage
        // We'll use a thread to delay this reset to allow the envelope release to complete
        Thread resetThread = new Thread(new Runnable() {
            @Override
            public void run() { // Run the thread
                try {
                    // Wait for envelope release to complete (in milliseconds) 
                    Thread.sleep((long)(envelope.getRelease() * 1000)); // Wait for envelope release to complete
                    oscillator.amplitude.set(0.0); // Reset oscillator amplitude
                } catch (InterruptedException e) {
                    System.err.println("Thread interrupted: " + e.getMessage());
                }
            }
        });
        System.out.println("Voice envelope on noteOn: " + this.envelope);
        resetThread.start(); // Start the reset thread
        isActive = false; // Set the voice to inactive
    }

    /**
     * Checks if the voice is active
     * 
     * @return true if the voice is active, false otherwise
     */
    public boolean isActive(){return isActive;}

    /**
     * Gets the current note being played
     * 
     * @return the current note
     */
    public int getCurrentNote() {return currentNote;}

    /**
     * Gets the current oscillator type
     * 
     * @return the current oscillator type
     */
    public OscType getCurrentOscType(){return currentOscType;} // Get current oscillator type
     /**
     * Get the envelope for this voice
     * 
     * @return the envelope
     */
    public Envelope getEnvelope() {return envelope;}

    /**
     * Retriggers the envelope 
     * required to get the envelope to work after a note off
     */
    public void retriggerEnvelope() {
        System.out.println("Retriggering envelope for voice " + this + " (force retrigger)");
        envelope.trigger(false); // Note off
        envelope.trigger(true);  // Note on
    }

    /**
     * Fully disconnects and reconnects the signal chain for this voice.
     * mimicking the setCurrentOscType method but without creating a new oscillator
     */
    public void refreshSignalChain() {
        System.out.println("[Voice " + this + "] refreshSignalChain() called");
        // Disconnect all relevant connections
        try {
            oscillator.output.disconnect(filter.get().input); // Disconnect oscillator output from filter input
            System.out.println("[Voice " + this + "] Disconnected oscillator.output -> filter.input");
        } catch (IllegalArgumentException e) {
            System.out.println("[Voice " + this + "] oscillator.output not connected to filter.input" + e.getMessage());
        }
        try {
            envelope.get().output.disconnect(oscillator.amplitude); // Disconnect envelope output from oscillator amplitude
            System.out.println("[Voice " + this + "] Disconnected envelope.output -> oscillator.amplitude");
        } catch (IllegalArgumentException e) {
            System.out.println("[Voice " + this + "] envelope.output not connected to oscillator.amplitude" + e.getMessage());
        }
        try {
            envelope.get().output.disconnect(filter.get().amplitude); // Disconnect envelope output from filter amplitude
            System.out.println("[Voice " + this + "] Disconnected envelope.output -> filter.amplitude");
        } catch (IllegalArgumentException e) {
            System.out.println("[Voice " + this + "] envelope.output not connected to filter.amplitude" + e.getMessage());
        }
        try {
            filter.get().output.disconnect(mixer.inputA); // Disconnect filter output from mixer input A
            System.out.println("[Voice " + this + "] Disconnected filter.output -> mixer.inputA");
        } catch (IllegalArgumentException e) {
            System.out.println("[Voice " + this + "] filter.output not connected to mixer.inputA" + e.getMessage());
        }
        try {
            mixer.output.disconnect(0, this.lineOut.input, 0); // Disconnect mixer output from lineOut input A
            System.out.println("[Voice " + this + "] Disconnected mixer.output[0] -> lineOut.input[0]");
        } catch (IllegalArgumentException e) {
            System.out.println("[Voice " + this + "] mixer.output[0] not connected to lineOut.input[0]" + e.getMessage());
        }
        try {
            mixer.output.disconnect(0, this.lineOut.input, 1); // Disconnect mixer output from lineOut input B
            System.out.println("[Voice " + this + "] Disconnected mixer.output[0] -> lineOut.input[1]" );
        } catch (IllegalArgumentException e) {
            System.out.println("[Voice " + this + "] mixer.output[0] not connected to lineOut.input[1]" + e.getMessage());
        }

        // 2. Reconnect in correct order
        oscillator.output.connect(filter.get().input); // Connect oscillator output to filter input
        System.out.println("[Voice " + this + "] Connected oscillator.output -> filter.input");
        envelope.get().output.connect(oscillator.amplitude); // Connect envelope output to oscillator amplitude
        System.out.println("[Voice " + this + "] Connected envelope.output -> oscillator.amplitude");
        envelope.get().output.connect(filter.get().amplitude); // Connect envelope output to filter amplitude
        System.out.println("[Voice " + this + "] Connected envelope.output -> filter.amplitude");
        filter.get().output.connect(mixer.inputA); // Connect filter output to mixer input A
        System.out.println("[Voice " + this + "] Connected filter.output -> mixer.inputA");
        mixer.output.connect(0, this.lineOut.input, 0); // Connect mixer output to lineOut input A
        System.out.println("[Voice " + this + "] Connected mixer.output[0] -> lineOut.input[0]");
        mixer.output.connect(0, this.lineOut.input, 1); // Connect mixer output to lineOut input B
        System.out.println("[Voice " + this + "] Connected mixer.output[0] -> lineOut.input[1]");
    }
}