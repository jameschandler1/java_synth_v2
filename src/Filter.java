

import com.jsyn.Synthesizer;
import com.jsyn.unitgen.FilterStateVariable;
import com.jsyn.unitgen.LinearRamp;


/**
 * Filter class for filtering the audio signal using a state variable filter
 * (low pass)
 * 
 */
public class Filter {
    //The filter
    private FilterStateVariable filter;
    //Ramps for smooth transitions
    private LinearRamp filterCutoffRamp; // Ramp for smooth transitions
    private LinearRamp filterResonanceRamp; // Ramp for smooth transitions
   
    /**
     * Creates a new Filter object.
     * 
     * @param synth the synthesizer to add the filter to
     */
    public Filter(Synthesizer synth) {
        if(synth == null) throw new IllegalArgumentException("Synthesizer cannot be null");
        // Initialize components
        filter = new FilterStateVariable(); // The filter
        filterCutoffRamp = new LinearRamp(); // Ramp for smooth transitions
        filterResonanceRamp = new LinearRamp(); // Ramp for smooth transitions
      
        
        // Add components to synth
        synth.add(filter); // Add the filter to the synth
        synth.add(filterCutoffRamp); // Add the filter cutoff ramp to the synth
        synth.add(filterResonanceRamp); // Add the filter resonance ramp to the synth
       
        
        // Set up initial connections
        filterCutoffRamp.output.connect(filter.frequency); // Connect the filter cutoff ramp to the filter frequency
        filterResonanceRamp.output.connect(filter.resonance); // Connect the filter resonance ramp to the filter resonance
        
        // Set default filter type
        setFilterType("Low Pass"); // Set the default filter type
    }

    /**
     * Sets the frequency of the filter
     * 
     * @param frequency the frequency
     */
    public void setFrequency(double frequency) {
        if (frequency < 20 || frequency > 60000) throw new IllegalArgumentException("Frequency must be between 20 and 60000");
        double safeFrequency = Math.min(Math.max(frequency, 20.0), 60000.0); // Ensure the frequency is within the valid range
        filter.frequency.set(safeFrequency); // Set the frequency of the filter
    }

    /**
     * Sets the filter type
     * 
     * @param type the filter type
     */
    public void setFilterType(String type) {
        if (type == null) throw new IllegalArgumentException("Filter type cannot be null");
        // Disconnect all filter outputs
        filter.lowPass.disconnectAll();
        filter.highPass.disconnectAll();
        filter.bandPass.disconnectAll();
        
       
        // No need to connect outputs to filter.output since we'll use the specific outputs directly
        // Each output (lowPass, highPass, bandPass) is already available as a port
    }

    /**
     * Sets the filter cutoff frequency
     * 
     * @param freq the cutoff frequency
     */
    public void setFilterCutoff(double freq) {
        if (freq < 20 || freq > 60000) throw new IllegalArgumentException("Frequency must be between 20 and 60000");
        double safeFreq = Math.min(60000.0, Math.max(20.0, freq)); // Ensure the frequency is within the valid range
        this.filterCutoffRamp.input.set(safeFreq); // Set the cutoff frequency of the filter 
    }

    /**
     * Sets the filter resonance
     * 
     * @param resonance the resonance
     */
    public void setFilterResonance(double resonance){
        if (resonance < 0 || resonance > 1) throw new IllegalArgumentException("Resonance must be between 0 and 1");
        double safeResonance = Math.min(0.9, resonance * 0.3);
        this.filterResonanceRamp.input.set(safeResonance);
    }

    /**
     * Returns the filter
     * 
     * @return the filter
     */
    public FilterStateVariable get() {return filter;}
 
}
