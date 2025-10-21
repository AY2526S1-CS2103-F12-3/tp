---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# CaseTrack Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PatientListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Patient` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a patient).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Patient` objects (which are contained in a `UniquePatientList` object).
* stores the currently 'selected' `Patient` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Patient>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Patient` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Patient` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `casetrack.app.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th patient in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new patient. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the patient was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the patient being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* Tech-savvy social workers in hospitals (with no access to patient information from the hospital database)
* has a need to manage a significant number of contacts
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: quickly work with large amounts of patient information during sessions or home visits,
optimized for fast CLI interactions.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                    | I want to …​                 | So that I can…​                                                        |
|----------|--------------------------------------------|------------------------------|------------------------------------------------------------------------|
| `* * *`  | User                                        | add patients with essential details (name, phone, address, income, medical info) | record their data for future reference and case management |
| `* * *`  | User                                        | list all patients at once    | browse patient records easily and get an overview of my caseload      |
| `* * *`  | User                                        | delete patient records        | clean up patient records when they are no longer relevant or cases are closed |
| `* * *`  | New User                                    | see usage instructions and command help | refer to instructions when I forget how to use the app |
| `* *`     | Social Worker                               | filter contacts by attributes (medical condition, income level, name) | get information about my patients quickly during sessions |
| `* *`     | Social Worker                               | take quick notes during or right after a session, even with incomplete data | capture important context immediately and avoid forgetting key details later |
| `* *`     | Social Worker                               | enter partial patient details and still retrieve useful results | still access key patient information even when the data I have is incomplete |
| `* *`     | Social Worker                               | tag and categorize patients based on needs | prioritize cases and follow up more systematically |
| `* *`     | Social Worker                               | link related patient records  | see family connections and related cases                               |
| `* *`     | Social Worker                               | quickly copy information out of the application | work effortlessly with other systems and reports |
| `* *`     | Social Worker                               | pin frequently accessed patient records | decrease the amount of time spent searching for cases I am working on |
| `* *`     | Social Worker                               | search across all patient notes and records by keyword | quickly retrieve specific information without manually scanning through each record |
| `* *`     | Social Worker                               | set reminders or follow-up dates for patients | ensure timely check-ins and avoid missing important appointments |
| `* *`     | Social Worker                               | export selected patient records into a shareable format | easily collaborate with colleagues or submit reports without retyping data |
| `* *`     | Expert User                                 | enter command arguments in any order | focus less on the act of keying in a command and work more naturally |
| `* *`     | Expert User                                 | create custom aliases for commands | work faster with familiar shortcuts |
| `* *`     | Expert User                                 | customize where the app saves data to / loads data from | easily make backups of data and revert to older versions if necessary |
| `* *`     | New User                                    | easily discover available commands | use the product immediately, without having to consult a guide |
| `*`      | New User                                    | import data from CSV/Excel files | migrate my existing records from other systems |
| `*`      | Social Worker                               | see patients close to my proximity | plan home visits efficiently |
| `*`      | Social Worker                               | group patients by neighbourhood | cover multiple visits in one area |

*{More to be added}*

### Use cases

(For all use cases below, the **System** is the `CaseTrack` and the **Actor** is the `user`, unless specified otherwise)

**Use case (UC01): Add a patient**

**MSS**

1. User enters the `add` command in the format add n/NAME p/PHONE a/ADDRESS i/INCOME [m/MEDICAL_INFO].
2. System parses the command and validates input
3. System normalizes NAME case and spaces for internal comparison and checks for duplicates using NAME+PHONE.
4. System creates and saves the new patient record.
5. System confirms success by displaying: Patient added: <Name> (<Phone>).

   Use case ends.

**Extensions**

* 1a. Missing mandatory fields (n/ or p/ absent, or empty after prefix)

    * 1a1. System shows "Name and phone are required fields.".

    * 1a2. No patient is added.

  Use case ends.

* 2a. Invalid NAME (contains digits/symbols)
    * 2a1. System shows "Invalid name format. Names may only contain letters and spaces.".

    * 2a2. No patient is added.

  Use case ends.

* 2b. Invalid PHONE (not 8 digits after trimming):

    * 2b1. System shows "Phone number must be 8 digits.".

    * 2b2. No patient is added.

  Use case ends.

* 2c. Invalid INCOME (not numeric or < 0):

    * 2c1. System shows "Income must be a non-negative number.".

    * 2c2. No patient is added.

  Use case ends.

* 2d. Invalid input format (e.g., unrecognized/missing prefixes, duplicated prefixes or empty values):

    * 2d1. System shows an appropriate input format error message, including the correct command usage and example.

    * 2d2. No patient is added.

  Use case ends.

* 3a. Duplicate patient found (same normalized NAME and same PHONE already exist):

    * 3a1. System shows "This patient already exists.".

    * 3a2. No patient is added.

  Use case ends.

* *a. At anytime, user cancels the action.

  Use case ends.

#### Use case (UC02): View All Patients

**MSS**

1. Social Worker inputs a list command.
2. CaseTrack displays a list of all patients.

Use case ends.


#### Use case (UC03): Delete patient

**MSS**

1. User requests to list patients
2.  System <u>shows a list of patients</u> ([UC02](#use-case-uc02-view-all-patients))
3.  User requests to delete a specific patients in the list
4.  System deletes the patient
5.  System shows success message

    Use case ends.

**Extensions**

* 2a. The list is empty.

    * 2a1. System informs the user that there are no patients available

      Use case ends.

* 3a. The given index is invalid.

    * 3a1. System shows an error message.

      Use case resumes at step 2.

* 4a. Deletion fails due to system error.

    * 4a1. System shows an error message.

      Use case ends.

* *a. At anytime, user cancels the action.

  Use case ends.


#### Use case (UC04): Search/Filter Patients

**MSS**

1. User enters the `search` command with one or more filter attributes (name, condition, and/or income).
2. System parses the command and validates input parameters.
3. System filters the patient list based on the provided criteria.
4. System displays all matching patient records showing name, condition, and income.

   Use case ends.

**Extensions**

* 1a. No attributes specified (empty search command).

    * 1a1. System shows error: "Specify attribute to search."

      Use case ends.

* 2a. Invalid name format (contains non-alphabetic characters except spaces and hyphens).

    * 2a1. System shows error: "Invalid name format. Names may only contain alphabetic characters, spaces, hyphens."

      Use case ends.

* 2b. Invalid condition format (contains non-alphabetic characters except spaces and hyphens).

    * 2b1. System shows error: "Invalid condition format. Conditions can only contain letters and spaces, hyphens."

      Use case ends.

* 2c. Invalid income format (not a positive number, contains commas or currency symbols).

    * 2c1. System shows error: "Invalid income format. Enter a positive whole number."

      Use case ends.

* 2d. Unrecognized attribute prefix (e.g., age/, city/).

    * 2d1. System shows error: "Invalid attribute."

      Use case ends.

* 2e. Empty attribute value (e.g., name/, condition/, income/).

    * 2e1. System shows error for the specific empty attribute.

      Use case ends.

* 3a. No patients match the search criteria.

    * 3a1. System shows message: "No patient found."

      Use case ends.

* *a. At any time, User cancels the action.

    Use case ends.


#### Use case (UC05): Add Quick Note

**Preconditions**
* Patient record exists in the system (by index or by Name + Phone).

**Guarantees**
* A new note will be stored only if a valid patient is identified.
* Notes are linked correctly to the intended patient.
* Invalid inputs will not create notes.

**MSS**

1.	User types the command to add a quick note with patient reference and text.
2.	System validates the patient reference (index or Name + Phone).
3.	System validates the note text is not empty.
4.	System stores the note under the patient’s record.
5.	System confirms success by displaying the created note.

    Use case ends.

**Extensions**
*	2a. Missing patient reference.

    * 2a1. System shows error: “Either index or (Name and Phone) is required.”

      Use case ends.

*	2b. No matching patient found.

    * 2b1. System shows error: “No patient found with the given details.”

      Use case ends.

*	2c. Phone number invalid (not 8 digits).

    * 2c1. System shows an error: “Phone number must be 8 digits.”

      Use case ends.

*	3a. Note text is empty.

    * 3a1. System shows an error: “Note cannot be empty.”

      Use case ends.

*	*a. At any time, User cancels the action.

    Use case ends.

#### Use case: (UC06): Remove Quick Note

**Preconditions**
* Patient record exists in the system (by index or by Name + Phone).

**Guarantees**
* A note will only be deleted if a valid patient is identified and the note index exists.
* Only notes from the linked patient will be deleted.
* Invalid inputs will not delete notes.

**MSS**
1.	Actor types the command to remove a quick note with patient reference and note index.
2.	System validates the patient reference (index or Name + Phone).
3.	System validates the note index.
4.	System deletes the specified note under the patient’s record.
5.	System confirms success by displaying the deleted note.

    Use case ends.

**Extensions**
*	2a. Missing patient reference.

    * 2a1. System shows error: “Either index or (Name and Phone) is required.”

      Use case ends.

*	2b. No matching patient found.

    * 2b1. System shows error: “No patient found with the given details.”

      Use case ends.

*	2c. Phone number invalid (not 8 digits).

    * 2c1. System shows an error: “Phone number must be 8 digits.”

      Use case ends.

*	3a. Note index is empty.

    * 3a1. System shows an error: “Note index is required”

      Use case ends.

*	3b. Note index is invalid (not a number, less than 1, or more than the number of total notes the patient currently has)

    * 3b1. System shows an error: “The note index must be in the range 1 to [total number of notes the patient has].”

      Use case ends.

* *a. At any time, Actor cancels the action.

    Use case ends.


*{More to be added}*

### Non-Functional Requirements

1. Should work on any mainstream OS as long as it has Java `17` or above installed.
2. Should be able to store and retrieve up to 10,000 patient records without noticeable delay (< 3 seconds for search operations).
3. All patient data must be stored locally with no transmission over networks to ensure patient privacy compliance.
4. Healthcare helpers with basic computer literacy should be able to perform common tasks (add, search, update patient records) within 5 minutes of initial training.
5. A user with above average typing speed for regular English text should be able to accomplish most of the tasks faster using commands than using the mouse.

*{More to be added}*

### Glossary

| Term | Definition |
|------|------------|
| **Mainstream OS** | Windows, Linux, Unix, MacOS |
| **Patient Record** | A data entry with a patient's name, phone, address, income, medical info, and notes|
| **Quick Note** | A brief text annotation attached to a patient record, typically captured during or immediately after a session |
| **Session** | A meeting or consultation between a social worker and a patient |
| **Duplicate Patient** | A patient record with identical name (case-insensitive) and phone number as an existing record |
| **Medical Information** | Health-related details about a patient including conditions, medications, or treatment notes |
| **Partial Data** | Incomplete patient information, common during initial visits or when full details are not available |
| **Prefix** | Command parameter identifiers (e.g., n/ for name, p/ for phone) used in the CLI syntax |

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a patient

1. Deleting a patient while all patients are being shown

   1. Prerequisites: List all patients using the `list` command. Multiple patients in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No patient is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
