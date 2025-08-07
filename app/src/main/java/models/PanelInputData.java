package models;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

/**
 * A data class that holds all the information and UI elements for a single,
 * dynamically added vehicle panel in the `ActivityCalculator`. This class is used to
 * manage the state of each panel, including its type, dent details, and the
 * corresponding views in the user interface
 */
public class PanelInputData {
    public String panelType;
    public String largestDentSize;
    public int numberOfDents;
    public boolean isAluminum;
    public String estimatedCost;

    // UI elements specific to this panel
    public LinearLayout panelLayout;
    public TextView selectedPanelDisplay;
    public TextView dentSizeDisplay;
    public TextView numDentsDisplay;
    public Button selectDentSizeButton;
    public Button enterNumDentsButton;
    public Switch aluminumSwitch;
    public TextView panelEstimatedCostDisplay; // To show individual panel cost

    /**
     * Constructs a new PanelInputData object with default initial values.
     * The dent size and number of dents are set to indicate that they haven't been
     * configured yet.
     *
     * @param panelType The type of panel, such as "HOOD" or "LFF".
     */
    public PanelInputData(String panelType) {
        this.panelType = panelType;
        this.largestDentSize = "Not Set";
        this.numberOfDents = -1;
        this.isAluminum = false;
        this.estimatedCost = "N/A";
    }
}