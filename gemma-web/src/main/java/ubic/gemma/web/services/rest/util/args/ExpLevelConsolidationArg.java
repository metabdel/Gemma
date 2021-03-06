package ubic.gemma.web.services.rest.util.args;

import com.google.common.base.Strings;
import ubic.gemma.model.expression.bioAssayData.ExperimentExpressionLevelsValueObject;
import ubic.gemma.web.services.rest.util.GemmaApiException;

/**
 * Class representing an API argument that should be one of the expression level consolidation options.
 *
 * @author tesarst
 */
public class ExpLevelConsolidationArg extends MalformableArg {
    private static final String ERROR_MSG = "Value '%s' can not converted to a boolean";
    private String value;

    private ExpLevelConsolidationArg( String value ) {
        this.value = value;
    }

    private ExpLevelConsolidationArg( String errorMessage, Exception exception ) {
        super( errorMessage, exception );
    }

    /**
     * Used by RS to parse value of request parameters.
     *
     * @param s the request boolean argument
     * @return an instance of BoolArg representing boolean value of the input string, or a malformed BoolArg that will throw an
     * {@link GemmaApiException} when accessing its value, if the input String can not be converted into a boolean.
     */
    @SuppressWarnings("unused")
    public static ExpLevelConsolidationArg valueOf( final String s ) {
        if ( Strings.isNullOrEmpty( s ) )
            return null;
        if ( !( s.equals( ExperimentExpressionLevelsValueObject.OPT_PICK_MAX ) || s
                .equals( ExperimentExpressionLevelsValueObject.OPT_PICK_VAR ) || s
                .equals( ExperimentExpressionLevelsValueObject.OPT_AVG ) ) ) {
            return new ExpLevelConsolidationArg( String.format( ExpLevelConsolidationArg.ERROR_MSG, s ),
                    new IllegalArgumentException(
                            "The consolidate option has to be one of: " + ExperimentExpressionLevelsValueObject.OPT_AVG
                                    + ", " + ExperimentExpressionLevelsValueObject.OPT_PICK_MAX + ", "
                                    + ExperimentExpressionLevelsValueObject.OPT_PICK_VAR ) );
        }
        return new ExpLevelConsolidationArg( s );
    }

    @Override
    public String toString() {
        if ( this.value == null )
            return "";
        return String.valueOf( this.value );
    }

    /**
     * @return the boolean value of the original String argument. If the original argument could not be converted into
     * a boolean, will produce a {@link GemmaApiException} instead.
     */
    public String getValue() {
        this.checkMalformed();
        return value;
    }

}
