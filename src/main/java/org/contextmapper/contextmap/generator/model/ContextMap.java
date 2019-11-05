/*
 * Copyright 2019 The Context Mapper Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contextmapper.contextmap.generator.model;

import org.contextmapper.contextmap.generator.model.exception.BoundedContextAlreadyPartOfContextMapException;
import org.contextmapper.contextmap.generator.model.exception.BoundedContextNotPartOfContextMapException;

import java.util.*;

/**
 * Represents a Context Map for which a graphical representation shall be created.
 *
 * @author Stefan Kapferer
 */
public class ContextMap {

    private Set<BoundedContext> boundedContexts;
    private Set<Relationship> relationships;

    public ContextMap() {
        this.boundedContexts = new HashSet<>();
        this.relationships = new HashSet<>();
    }

    /**
     * Adds a new Bounded Context to the Context Map.
     *
     * @param boundedContext the Bounded Context to be added to the Context Map
     */
    public ContextMap addBoundedContext(BoundedContext boundedContext) {
        if (this.boundedContexts.contains(boundedContext))
            throw new BoundedContextAlreadyPartOfContextMapException(boundedContext.getName());
        this.boundedContexts.add(boundedContext);
        return this;
    }

    /**
     * Adds a new relationship to the Context Map.
     *
     * @param relationship the relationship to be added to the Context Map
     */
    public ContextMap addRelationship(Relationship relationship) {
        if (!this.boundedContexts.contains(relationship.getFirstParticipant()))
            throw new BoundedContextNotPartOfContextMapException(relationship.getFirstParticipant());
        if (!this.boundedContexts.contains(relationship.getSecondParticipant()))
            throw new BoundedContextNotPartOfContextMapException(relationship.getSecondParticipant());
        this.relationships.add(relationship);
        return this;
    }

    /**
     * Gets the set of Bounded Contexts on the Context Map.
     *
     * @return the set of Bounded Contexts on the Context Map
     */
    public Set<BoundedContext> getBoundedContexts() {
        Set<BoundedContext> set = new HashSet<>();
        set.addAll(this.boundedContexts);
        return set;
    }

    /**
     * Gets the set of relationships on the Context Map.
     *
     * @return the set of relationships on the Context Map
     */
    public Set<Relationship> getRelationships() {
        Set<Relationship> set = new HashSet<>();
        set.addAll(this.relationships);
        return set;
    }
}
