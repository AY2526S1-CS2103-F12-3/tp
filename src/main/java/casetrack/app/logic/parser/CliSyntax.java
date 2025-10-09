package casetrack.app.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_EMAIL = new Prefix("e/");
    public static final Prefix PREFIX_ADDRESS = new Prefix("a/");
    public static final Prefix PREFIX_TAG = new Prefix("t/");
    // Additional prefixes to support Developer Guide's Add Patient MSS
    public static final Prefix PREFIX_INCOME = new Prefix("i/");
    public static final Prefix PREFIX_MEDICAL_INFO = new Prefix("m/");

}
