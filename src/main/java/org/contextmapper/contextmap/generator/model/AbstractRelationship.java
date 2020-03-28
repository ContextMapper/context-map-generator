/*
 * Copyright 2020 The Context Mapper Project Team
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

/**
 * Represents a DDD relationship (that can have a name and an implementation technology).
 *
 * @author Stefan Kapferer
 */
public abstract class AbstractRelationship implements Relationship {

    private String name = "";
    private String implementationTechnology = "";

    public AbstractRelationship setName(String name) {
        this.name = name;
        return this;
    }

    public AbstractRelationship setImplementationTechnology(String implementationTechnology) {
        this.implementationTechnology = implementationTechnology;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getImplementationTechnology() {
        return implementationTechnology;
    }
}
