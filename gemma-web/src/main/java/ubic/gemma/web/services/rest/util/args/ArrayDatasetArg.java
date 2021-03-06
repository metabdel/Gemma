package ubic.gemma.web.services.rest.util.args;

import com.google.common.base.Strings;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentValueObject;
import ubic.gemma.persistence.service.expression.experiment.ExpressionExperimentService;
import ubic.gemma.persistence.util.ObjectFilter;
import ubic.gemma.web.services.rest.util.GemmaApiException;

import java.util.Arrays;
import java.util.List;

public class ArrayDatasetArg
        extends ArrayEntityArg<ExpressionExperiment, ExpressionExperimentValueObject, ExpressionExperimentService> {
    private static final String ERROR_MSG_DETAIL = "Provide a string that contains at least one ID or short name, or multiple, separated by (',') character. All identifiers must be same type, i.e. do not combine IDs and short names.";
    private static final String ERROR_MSG = ArrayArg.ERROR_MSG + " Dataset identifiers";

    private ArrayDatasetArg( List<String> values ) {
        super( values, DatasetArg.class );
    }

    private ArrayDatasetArg( String errorMessage, Exception exception ) {
        super( errorMessage, exception );
    }

    /**
     * Used by RS to parse value of request parameters.
     *
     * @param s the request arrayDataset argument
     * @return an instance of ArrayDatasetArg representing an array of Dataset identifiers from the input string,
     * or a malformed ArrayDatasetArg that will throw an {@link GemmaApiException} when accessing its value, if the
     * input String can not be converted into an array of Dataset identifiers.
     */
    @SuppressWarnings("unused")
    public static ArrayDatasetArg valueOf( final String s ) {
        if ( Strings.isNullOrEmpty( s ) ) {
            return new ArrayDatasetArg( String.format( ArrayDatasetArg.ERROR_MSG, s ),
                    new IllegalArgumentException( ArrayDatasetArg.ERROR_MSG_DETAIL ) );
        }
        return new ArrayDatasetArg( Arrays.asList( ArrayEntityArg.splitString( s ) ) );
    }

    @Override
    protected void setPropertyNameAndType( ExpressionExperimentService service ) {
        String value = this.getValue().get( 0 );
        MutableArg<?, ExpressionExperiment, ExpressionExperimentValueObject, ExpressionExperimentService> arg = DatasetArg
                .valueOf( value );
        this.argValueName = this.checkPropertyNameString( arg, value, service );
        this.argValueClass = arg.value.getClass();
    }

    @Override
    protected String getObjectDaoAlias() {
        return ObjectFilter.DAO_EE_ALIAS;
    }

}
