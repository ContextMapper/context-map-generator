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

import java.util.*;

public class ContextMap {

    private List<BoundedContext> boundedContexts;
    private List<Relationship> relationships;

    public ContextMap() {
        this.boundedContexts = new ArrayList<>();
        this.relationships = new ArrayList<>();
    }

    public void addBoundedContext(BoundedContext boundedContext) {
        this.boundedContexts.add(boundedContext);
    }

    public void addRelationship(Relationship relationship) {
        if (!this.boundedContexts.contains(relationship.getFirstParticipant()))
            throw new BoundedContextNotPartOfContextMapException(relationship.getFirstParticipant());
        if (!this.boundedContexts.contains(relationship.getSecondParticipant()))
            throw new BoundedContextNotPartOfContextMapException(relationship.getSecondParticipant());
        this.relationships.add(relationship);
    }

    public List<BoundedContext> getBoundedContexts() {
        List<BoundedContext> set = new ArrayList<>();
        set.addAll(this.boundedContexts);
        return set;
    }

    public List<Relationship> getRelationships() {
        List<Relationship> set = new ArrayList<>();
        set.addAll(this.relationships);
        return set;
    }
}
