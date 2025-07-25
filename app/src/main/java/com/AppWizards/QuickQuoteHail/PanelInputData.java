package com.AppWizards.QuickQuoteHail;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

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

    public PanelInputData(String panelType) {
        this.panelType = panelType;
        this.largestDentSize = "Not Set";
        this.numberOfDents = -1;
        this.isAluminum = false;
        this.estimatedCost = "N/A";
    }
}