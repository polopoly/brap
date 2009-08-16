package no.tornado.brap.modification;

import no.tornado.brap.common.ModificationList;

public class ChangesIgnoredModificationManager implements ModificationManager {
    public Object[] applyModificationScheme(Object[] objects) {
        return objects;
    }

    public ModificationList[] getModifications() {
        return null;
    }
}
