package casetrack.app.ui;

import java.util.List;
import java.util.logging.Logger;

import casetrack.app.commons.core.LogsCenter;
import casetrack.app.model.patient.Note;
import casetrack.app.model.patient.Patient;
import casetrack.app.model.patient.PatientAttribute;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

/**
 * Panel containing a list of details.
 */
public class DetailListPanel extends UiPart<Region> {
    private static final String FXML = "DetailListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(DetailListPanel.class);

    @FXML
    private ListView<PatientAttribute> detailListView;

    /**
     * Creates a {@code DetailListPanel}.
     */
    public DetailListPanel() {
        super(FXML);
        detailListView.setCellFactory(listView -> new DetailListViewCell());
    }

    /**
     * Displays details for a specific patient
     * @param patient A {@code Patient} whose details are to be displayed.
     */
    public void showDetails(Patient patient) {
        // Display notes as numbered list
        String noteValue = "None";
        List<Note> patientNotes = patient.getNotes();
        if (!patientNotes.isEmpty()) {
            noteValue = "";
            for (int i = 0; i < patientNotes.size(); i++) {
                noteValue += (i + 1) + ". " + patientNotes.get(i).value + "\n";
            }
            noteValue.strip();
        }

        ObservableList<PatientAttribute> detailList = FXCollections.observableArrayList(
                new PatientAttribute(patient.getPhone().getClass().getSimpleName(), patient.getPhone().value),
                new PatientAttribute(patient.getAddress().getClass().getSimpleName(), patient.getAddress().value),
                new PatientAttribute(patient.getEmail().getClass().getSimpleName(), patient.getEmail().value),
                new PatientAttribute("Notes", noteValue)
        );
        detailListView.setItems(detailList);
    }

    /**
     * Custom {@code ListCell} that displays the details of a {@code Patient} using a
     * {@code DetailCard}.
     */
    class DetailListViewCell extends ListCell<PatientAttribute> {
        @Override
        protected void updateItem(PatientAttribute attribute, boolean empty) {
            super.updateItem(attribute, empty);

            if (empty || attribute == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new DetailCard(attribute, getIndex() + 1).getRoot());
            }
        }
    }

}
