import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

/**
 * KeyboardController handles mapping computer keyboard keys to MIDI notes
 * and interfaces with the synthesizer to trigger notes.
 */
public class KeyboardController implements KeyListener {
    // Map to store key codes to MIDI note numbers
    private Map<Integer, Integer> keyToMidiMap;
    
    // Reference to the Synth object
    private Synth synth;
    
    // Currently active notes (key code -> voice index)
    private Map<Integer, Integer> activeNotes;
    
    // Default velocity for key presses
    private double defaultVelocity = 0.7;
    
    /**
     * Creates a new KeyboardController with a reference to the synth
     * 
     * @param synth The synthesizer to control
     */
    public KeyboardController(Synth synth) {
        this.synth = synth;
        this.activeNotes = new HashMap<>(); // Map to store active notes
        initializeKeyMap(); // Initialize the key map
    }
    
    /**
     * Initialize the mapping between keyboard keys and MIDI notes
     * Using a standard piano layout on the computer keyboard:
     * 
     * Row 1: Q W E R T Y U I O P [ ]
     * Row 2: A S D F G H J K L ; '
     * 
     * Where A is C3, W is C#3, S is D3, etc.
     */
    private void initializeKeyMap() {
        keyToMidiMap = new HashMap<>(); // Initialize the key map
        
        // Lower row (A-;') - white keys
        keyToMidiMap.put(KeyEvent.VK_A, 60); // C3 (middle C)
        keyToMidiMap.put(KeyEvent.VK_S, 62); // D3
        keyToMidiMap.put(KeyEvent.VK_D, 64); // E3
        keyToMidiMap.put(KeyEvent.VK_F, 65); // F3
        keyToMidiMap.put(KeyEvent.VK_G, 67); // G3
        keyToMidiMap.put(KeyEvent.VK_H, 69); // A3
        keyToMidiMap.put(KeyEvent.VK_J, 71); // B3
        keyToMidiMap.put(KeyEvent.VK_K, 72); // C4
        keyToMidiMap.put(KeyEvent.VK_L, 74); // D4
        keyToMidiMap.put(KeyEvent.VK_SEMICOLON, 76); // E4
        keyToMidiMap.put(KeyEvent.VK_QUOTE, 77); // F4
        
        // Upper row (Q-]) - black keys and additional white keys
        keyToMidiMap.put(KeyEvent.VK_W, 61); // C#3
        keyToMidiMap.put(KeyEvent.VK_E, 63); // D#3
        keyToMidiMap.put(KeyEvent.VK_R, 66); // F#3
        keyToMidiMap.put(KeyEvent.VK_T, 68); // G#3
        keyToMidiMap.put(KeyEvent.VK_Y, 70); // A#3
        keyToMidiMap.put(KeyEvent.VK_I, 73); // C#4
        keyToMidiMap.put(KeyEvent.VK_O, 75); // D#4
        keyToMidiMap.put(KeyEvent.VK_P, 78); // F#4
        keyToMidiMap.put(KeyEvent.VK_OPEN_BRACKET, 80); // G#4
        keyToMidiMap.put(KeyEvent.VK_CLOSE_BRACKET, 82); // A#4
        
        // Additional keys for more range
        keyToMidiMap.put(KeyEvent.VK_Z, 48); // C2
        keyToMidiMap.put(KeyEvent.VK_X, 50); // D2
        keyToMidiMap.put(KeyEvent.VK_C, 52); // E2
        keyToMidiMap.put(KeyEvent.VK_V, 53); // F2
        keyToMidiMap.put(KeyEvent.VK_B, 55); // G2
        keyToMidiMap.put(KeyEvent.VK_N, 57); // A2
        keyToMidiMap.put(KeyEvent.VK_M, 59); // B2
    }

    /**
     * Get the MIDI note number for a given key code
     * 
     * @param keyCode The key code from KeyEvent
     * @return The MIDI note number or -1 if the key is not mapped
     */
    public int getMidiNoteForKey(int keyCode) {
        if (keyCode < 0 || keyCode > 255) throw new IllegalArgumentException("Key code must be between 0 and 255");
        return keyToMidiMap.getOrDefault(keyCode, -1); // returns the value of the key code or -1 if it is not mapped
    } 
    
    /**
     * Check if a key is mapped to a MIDI note
     * 
     * @param keyCode The key code to check
     * @return true if the key is mapped, false otherwise
     */
    public boolean isNoteMappedKey(int keyCode) {
        if (keyCode < 0 || keyCode > 255) throw new IllegalArgumentException("Key code must be between 0 and 255");
        return keyToMidiMap.containsKey(keyCode); // returns true if the key code is mapped to a MIDI note
    }
    
    /**
     * Handles key pressed events
     * 
     * @param e the KeyEvent
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode(); // Get the key code
        // Check if this key is already active (to avoid retriggering)
        if (activeNotes.containsKey(keyCode)) return; // Return if the key is already active
        // Check if this key maps to a MIDI note
        if (isNoteMappedKey(keyCode)) {
            int midiNote = getMidiNoteForKey(keyCode); // Get the MIDI note number
            
            // Find an available voice and trigger the note
            int voiceIndex = synth.getAvailableVoice(); 
            if (voiceIndex >= 0) { // If a voice is available
                synth.noteOn(voiceIndex, midiNote, defaultVelocity); // Trigger the note
                activeNotes.put(keyCode, voiceIndex); // Add the note to the active notes
            }
        }
    }
    
    /**
     * Handles key released events
     * 
     * @param e the KeyEvent
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode(); // Get the key code
        
        // Check if this key is active
        if (activeNotes.containsKey(keyCode)) {
            int voiceIndex = activeNotes.get(keyCode); // Get the voice index
            synth.noteOff(voiceIndex); // Release the note
            activeNotes.remove(keyCode); // Remove the note from the active notes
        }
    }
    
    /**
     * Handles key typed events
     * 
     * @param e the KeyEvent
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used, but must be implemented for the key listener interface
    }
}
