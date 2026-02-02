import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JLabel;

/**
 * Handles the UI for the synthesizer application
 */
public class SynthUI {
    private JFrame frame; // The main window
    private Synth synth; // The synthesizer
    private KeyboardController keyboardController; // The keyboard controller
    private JPanel ADSRPanel; // The ADSR panel
    private JPanel oscTypePanel; // The oscillator type panel
    private JPanel mainPanel; // The main panel
    private JButton sineButton; // The sine button
    private JButton squareButton; // The square button
    private JButton sawtoothButton; // The sawtooth button
    private JButton triangleButton; // The triangle button
    private JSlider attackSlider; // The attack slider
    private JSlider decaySlider; // The decay slider
    private JSlider sustainSlider;
    private JSlider releaseSlider;
    private JLabel attackLabel;
    private JLabel decayLabel;
    private JLabel sustainLabel;
    private JLabel releaseLabel;
    
    /**
     * Creates a new SynthUI
     * 
     * @param synth the Synth instance to control
     * @param keyboardController the KeyboardController to handle keyboard input
     */
    public SynthUI(Synth synth, KeyboardController keyboardController) {
        this.synth = synth;
        this.keyboardController = keyboardController;
        initializeWindow();
    }
    
    /**
     * Initialize a simple window to capture keyboard events
     */
    private void initializeWindow() {
        // Create main frame
        frame = new JFrame("Synthesizer Keyboard"); // Create the main frame
        frame.setSize(600, 400); // Set the size of the frame
        frame.addKeyListener(keyboardController); // Add the keyboard controller
        frame.setFocusable(true); // Make the frame focusable
        frame.addWindowListener(new WindowAdapter() { // Add a window listener
            @Override
            public void windowClosing(WindowEvent e) {
                synth.stop();
                System.exit(0);
            }
        });

        // Create control panel for buttons
        oscTypePanel = new JPanel(); // Create the oscillator type panel
        oscTypePanel.setLayout(new BoxLayout(oscTypePanel, BoxLayout.Y_AXIS));
        
        // Create ADSR panel
        ADSRPanel = new JPanel(); // Create the ADSR panel
        ADSRPanel.setLayout(new BoxLayout(ADSRPanel, BoxLayout.Y_AXIS)); // Set layout to vertical
        
        // Create oscillator type buttons
        sineButton = createOscillatorButton("Sine", OscType.SINE); // Create sine button
        squareButton = createOscillatorButton("Square", OscType.SQUARE); // Create square button
        sawtoothButton = createOscillatorButton("Sawtooth", OscType.SAWTOOTH); // Create sawtooth button
        triangleButton = createOscillatorButton("Triangle", OscType.TRIANGLE); // Create triangle button
       
        //create adsr sliders
        // Use the first available SynthVoice's envelope for slider defaults
        SynthVoice[] voices = (synth != null) ? synth.getVoices() : null; // Get the voices from the synth if it exists else null
        Envelope env = (voices != null && voices.length > 0 && voices[0] != null) ? voices[0].getEnvelope() : null; // Get the envelope from the first voice if it exists else null
        if (env != null) {
            attackLabel = new JLabel("Attack"); // Create attack label
            attackSlider = createSlider("Attack", env.getAttack()); // Create attack slider
            decayLabel = new JLabel("Decay"); // Create decay label
            decaySlider = createSlider("Decay", env.getDecay());
            sustainLabel = new JLabel("Sustain"); // Create sustain label
            sustainSlider = createSlider("Sustain", env.getSustain());
            releaseLabel = new JLabel("Release"); // Create release label
            releaseSlider = createSlider("Release", env.getRelease());
        } else {
            // Fallback to default ADSR values if no voices/envelope available
            attackLabel = new JLabel("Attack"); // Create attack label
            attackSlider = createSlider("Attack", 0.01); 
            decayLabel = new JLabel("Decay"); // Create decay label
            decaySlider = createSlider("Decay", 0.1);
            sustainLabel = new JLabel("Sustain"); // Create sustain label
            sustainSlider = createSlider("Sustain", 0.8);
            releaseLabel = new JLabel("Release"); // Create release label
            releaseSlider = createSlider("Release", 0.2);
            System.err.println("WARNING: No SynthVoice or Envelope available for slider defaults");
        }
        //add labels and sliders to panel
        ADSRPanel.add(attackLabel); // Add attack label to panel
        ADSRPanel.add(attackSlider); // Add attack slider to panel
        ADSRPanel.add(decayLabel); // Add decay label to panel
        ADSRPanel.add(decaySlider); // Add decay slider to panel
        ADSRPanel.add(sustainLabel); // Add sustain label to panel
        ADSRPanel.add(sustainSlider); // Add sustain slider to panel
        ADSRPanel.add(releaseLabel); // Add release label to panel
        ADSRPanel.add(releaseSlider); // Add release slider to panel


        // Add buttons to panel
        oscTypePanel.add(sineButton); // Add sine button to panel
        oscTypePanel.add(squareButton); // Add square button to panel
        oscTypePanel.add(sawtoothButton); // Add sawtooth button to panel
        oscTypePanel.add(triangleButton); // Add triangle button to panel
        
        // Create a main panel with horizontal layout
        mainPanel = new JPanel(); // Create main panel
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS)); // Set layout to horizontal
        mainPanel.add(oscTypePanel); // Add oscillator type panel to main panel
        mainPanel.add(ADSRPanel); // Add ADSR panel to main panel
        // Add main panel to frame
        frame.add(mainPanel); // Add main panel to frame
    }

    /**
     * Show the keyboard window
     * 
     * @param label the label of the button
     * @param type the type of oscillator
     * @return the button
     */
    private JButton createOscillatorButton(String label, OscType type) {
        JButton button = new JButton(label); // Create the button
        button.addActionListener(new ActionListener() { // Add an action listener to the button
            @Override
            public void actionPerformed(ActionEvent e) { // When the button is pressed
                // Update all voices to use the selected oscillator type
                SynthVoice[] voices = synth.getVoices(); // Get the voices from the synth
                for (SynthVoice voice : voices) { // For each voice
                    voice.setCurrentOscType(type); // Set the oscillator type of the voice
                }
                frame.requestFocus(); // Request focus on the frame
            }
        });
        return button; // Return the button
    }

    /**
     * Creates a slider for the ADSR parameters
     * 
     * @param label the label of the slider
     * @param value the value of the slider
     * @return the slider
     */
    private JSlider createSlider(String label, double value) {
        JSlider slider = new JSlider(); // Create the slider
        slider.setEnabled(true); // Enable the slider
        slider.setFocusable(true); // Make the slider focusable
        slider.setMajorTickSpacing(10); // Set the major tick spacing
        slider.setPaintTicks(true); // Paint the ticks
        slider.setPaintLabels(true); // Paint the labels
        slider.setValue((int) (value * 100)); // Set the value
        slider.addChangeListener(new ChangeListener() { // Add a change listener to the slider
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!slider.getValueIsAdjusting()) { // If the slider is not being adjusted
                    double value = slider.getValue() / 100.0; // Get the value of the slider and divide by 100 to get a decimal
                    for (SynthVoice voice : synth.getVoices()) { // For each voice
                        if (label.equals("Attack")) voice.getEnvelope().setAttack(value); // Set the attack value of the envelope
                        if (label.equals("Decay")) voice.getEnvelope().setDecay(value); // Set the decay value of the envelope
                        if (label.equals("Sustain")) voice.getEnvelope().setSustain(value); // Set the sustain value of the envelope
                        if (label.equals("Release")) voice.getEnvelope().setRelease(value); // Set the release value of the envelope
                        // Refresh the signal chain after updating envelope parameters
                        voice.refreshSignalChain(); // Refresh the signal chain
                        if(voice.isActive()) voice.retriggerEnvelope(); // Retrigger the envelope if the voice is active
                    }
                    frame.requestFocusInWindow(); // Request focus on the frame
                }
            }
        });
        return slider; // Return the slider
    }
    /**
     * Get the frame
     * 
     * @return the frame
     */
    public JFrame getFrame() {return frame;}

    /**
     * Show the frame
     */
    public void show() {frame.setVisible(true);}
}