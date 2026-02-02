import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

/**
 * Tests the functionallity of the program
 * focusing testing on the Synth, SynthVoice, and Envelope classes
 * as they are the ones the UI interacts with
 */

public class SynthTest {
    private Synth synth;
    private SynthVoice voice;
    private Envelope envelope;

    public static void main(String[] args) {
        
    }

    @BeforeEach
    public void setUp() {
        synth = new Synth(); // Create a new synth
        voice = synth.getVoices()[0]; // Get the first voice
        envelope = voice.getEnvelope(); // Get the envelope
    }

    // 1. Creating a synth
    @Test
    public void testSynthCreation() {
        assertNotNull(synth); // Check that the synth is not null
        assertNotNull(synth.getVoices()); // Check that the voices are not null
        assertEquals(8, synth.getVoices().length); // Check that the number of voices is 8
    }

    // 2. Switching oscillator types
    @Test
    public void testOscillatorTypeSine() {
        voice.setCurrentOscType(OscType.SINE); // Set the oscillator type to sine
        assertEquals(OscType.SINE, voice.getCurrentOscType()); // Check that the oscillator type is sine
    }
    @Test
    public void testOscillatorTypeSquare() {
        voice.setCurrentOscType(OscType.SQUARE); // Set the oscillator type to square
        assertEquals(OscType.SQUARE, voice.getCurrentOscType()); // Check that the oscillator type is square
    }
    @Test
    public void testOscillatorTypeSawtooth() {
        voice.setCurrentOscType(OscType.SAWTOOTH); // Set the oscillator type to sawtooth
        assertEquals(OscType.SAWTOOTH, voice.getCurrentOscType()); // Check that the oscillator type is sawtooth
    }
    @Test
    public void testOscillatorTypeTriangle() {
        voice.setCurrentOscType(OscType.TRIANGLE); // Set the oscillator type to triangle
        assertEquals(OscType.TRIANGLE, voice.getCurrentOscType()); // Check that the oscillator type is triangle
    }

    // 3. Adjusting sliders (ADSR)
    @Test
    public void testSetAttack() {
        envelope.setAttack(0.5); // Set the attack to 0.5
        assertEquals(0.5, envelope.getAttack(), 0.0001); // Check that the attack is set to 0.5
    }
    @Test
    public void testSetDecay() {
        envelope.setDecay(0.5); // Set the decay to 0.5
        assertEquals(0.5, envelope.getDecay(), 0.0001); // Check that the decay is set to 0.5
    }
    @Test
    public void testSetSustain() {
        envelope.setSustain(0.5); // Set the sustain to 0.5
        assertEquals(0.5, envelope.getSustain(), 0.0001); // Check that the sustain is set to 0.5
    }
    @Test
    public void testSetRelease() {
        envelope.setRelease(0.5); // Set the release to 0.5
        assertEquals(0.5, envelope.getRelease(), 0.0001); // Check that the release is set to 0.5
    }

    // 4. Out-of-range values for ADSR
    @Test
    public void testAttackOutOfRange() {
        // Test that an IllegalArgumentException is thrown when the attack is out of range
        assertThrows(IllegalArgumentException.class, () -> envelope.setAttack(-1.0));
    }
    @Test
    public void testDecayOutOfRange() {
        // Test that an IllegalArgumentException is thrown when the decay is out of range
        assertThrows(IllegalArgumentException.class, () -> envelope.setDecay(20.0));
    }
    @Test
    public void testSustainOutOfRange() {
        // Test that an IllegalArgumentException is thrown when the sustain is out of range
        assertThrows(IllegalArgumentException.class, () -> envelope.setSustain(-0.5));
    }
    @Test
    public void testReleaseOutOfRange() {
        // Test that an IllegalArgumentException is thrown when the release is out of range
        assertThrows(IllegalArgumentException.class, () -> envelope.setRelease(100.0));
    }
}