package org.contextmapper.contextmap.generator.model;

public interface Relationship {

    BoundedContext getFirstParticipant();
    BoundedContext getSecondParticipant();

}
