package casetrack.app.ui;

import java.util.function.Consumer;
import java.util.logging.Logger;

import casetrack.app.commons.core.LogsCenter;
import casetrack.app.model.patient.Patient;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

/**
 * Panel containing the list of patients.
 */
public class PatientListPanel extends UiPart<Region> {
    private static final String FXML = "PatientListPanel.fxml";

    protected Consumer<Patient> selectPatientCallback;
    private final Logger logger = LogsCenter.getLogger(PatientListPanel.class);

    @FXML
    private ListView<Patient> patientListView;

    /**
     * Creates a {@code PatientListPanel} with the given {@code ObservableList}.
     */
    public PatientListPanel(ObservableList<Patient> patientList) {
        super(FXML);
        patientListView.setItems(patientList);
        patientListView.setCellFactory(listView -> new PatientListViewCell());
    }

    public void setPatientSelectCallback(Consumer<Patient> selectPatientCallback) {
        this.selectPatientCallback = selectPatientCallback;
        patientListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                selectPatientCallback.accept(patientListView.getSelectionModel().getSelectedItem());
            }
        });
    }

    public void setSelectedPatient(Patient patient) {
        patientListView.getSelectionModel().select(patient);
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Patient} using
     * a {@code PatientCard}.
     */
    class PatientListViewCell extends ListCell<Patient> {
        @Override
        protected void updateItem(Patient patient, boolean empty) {
            super.updateItem(patient, empty);

            if (empty || patient == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PatientCard(patient, getIndex() + 1).getRoot());
            }
        }
    }

}
