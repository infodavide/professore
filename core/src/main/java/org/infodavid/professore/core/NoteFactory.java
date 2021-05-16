package org.infodavid.professore.core;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * The Class NoteFactory.
 */
public class NoteFactory extends BasePooledObjectFactory<Note> {

    /*
     * (non-javadoc)
     * @see org.apache.commons.pool2.BasePooledObjectFactory#create()
     */
    @Override
    public Note create() {
        return new Note();
    }

    /*
     * (non-javadoc)
     * @see org.apache.commons.pool2.BasePooledObjectFactory#wrap(java.lang.Object)
     */
    @Override
    public PooledObject<Note> wrap(final Note obj) {
        return new DefaultPooledObject<>(obj);
    }

    /*
     * (non-javadoc)
     * @see org.apache.commons.pool2.BasePooledObjectFactory#passivateObject(org.apache.commons.pool2.PooledObject)
     */
    @Override
    public void passivateObject(final PooledObject<Note> pooledObject) {
        final Note obj = pooledObject.getObject();

        obj.setChannel((byte)0);
        obj.setKey((byte)0);
        obj.setOctave((byte)0);
        obj.setBaseNote(null);
        obj.setTrack((byte)0);
    }
}
