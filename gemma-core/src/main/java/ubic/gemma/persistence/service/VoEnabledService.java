package ubic.gemma.persistence.service;

import org.springframework.transaction.annotation.Transactional;
import ubic.gemma.model.IdentifiableValueObject;
import ubic.gemma.model.common.Identifiable;

import java.util.Collection;

/**
 * Created by tesarst on 01/06/17.
 * A special case of Service that also provides value object functionality.
 */
public abstract class VoEnabledService<O extends Identifiable, VO extends IdentifiableValueObject<O>>
        extends AbstractService<O> implements BaseVoEnabledService<O, VO> {

    private BaseVoEnabledDao<O, VO> voDao;

    public VoEnabledService( BaseVoEnabledDao<O, VO> voDao ) {
        super( voDao );
        this.voDao = voDao;
    }

    @Override
    @Transactional(readOnly = true)
    public VO loadValueObject( O entity ) {
        return entity == null ? null : voDao.loadValueObject( entity );
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<VO> loadValueObjects( Collection<O> entities ) {
        return entities == null ? null : voDao.loadValueObjects( entities );
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<VO> loadAllValueObjects() {
        return voDao.loadAllValueObjects();
    }

}
