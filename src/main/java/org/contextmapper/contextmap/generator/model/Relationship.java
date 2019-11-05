package org.contextmapper.contextmap.generator.model;

/**
 * Represents a relationship on a Context Map.
 *
 * @author Stefan Kapferer
 */
public interface Relationship {

    BoundedContext getFirstParticipant();

    BoundedContext getSecondParticipant();

}
