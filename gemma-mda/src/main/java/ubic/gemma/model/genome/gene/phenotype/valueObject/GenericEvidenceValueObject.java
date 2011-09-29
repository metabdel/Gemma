package ubic.gemma.model.genome.gene.phenotype.valueObject;

import java.util.Set;

import ubic.gemma.model.association.phenotype.GenericEvidence;

public class GenericEvidenceValueObject extends EvidenceValueObject {

    public GenericEvidenceValueObject( String description, CharacteristicValueObject associationType,
            Boolean isNegativeEvidence, String evidenceCode, Set<CharacteristicValueObject> phenotypes, String url ) {
        super( description, associationType, isNegativeEvidence, evidenceCode, phenotypes );
    }

    /** Entity to Value Object */
    public GenericEvidenceValueObject( GenericEvidence genericEvidence ) {
        super( genericEvidence );
    }

}
