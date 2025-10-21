package casetrack.app.ui;

import casetrack.app.model.patient.PatientAttribute;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * An UI component that displays information of an {@code Attribute}.
 */
public class DetailCard extends UiPart<Region> {

    private static final String FXML = "DetailListCard.fxml";

    public final PatientAttribute attribute;

    @FXML
    private Label title;
    @FXML
    private Label value;

    /**
     * Creates a {@code DetailCard} with the given {@code Attribute} and index to
     * display.
     */
    public DetailCard(PatientAttribute attribute, int displayedIndex) {
        super(FXML);
        this.attribute = attribute;
        title.setText(attribute.name);
        value.setText(attribute.value);
    }
}
