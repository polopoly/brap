package no.tornado.brap.modification;

import no.tornado.brap.common.ModificationList;

public interface ModificationManager {
    public Object[] applyModificationScheme(Object[] objects);
    public ModificationList[] getModifications();
}