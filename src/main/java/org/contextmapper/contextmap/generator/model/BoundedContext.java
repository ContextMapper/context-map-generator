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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.contextmapper.contextmap.generator.model.exception.BoundedContextIsNotATeamException;
import org.contextmapper.contextmap.generator.model.exception.TeamCannotImplementTeamException;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a Bounded Context for the graphical Context Map to be generated.
 *
 * @author Stefan Kapferer
 */
public class BoundedContext {

    private String name;
    private BoundedContextType type;
    private List<BoundedContext> realizedBoundedContexts; // for teams only!

    public BoundedContext(String name) {
        this.name = name;
        this.type = BoundedContextType.GENERIC;
        this.realizedBoundedContexts = new LinkedList<>();
    }

    public BoundedContext(String name, BoundedContextType type) {
        this(name);
        this.type = type;
    }

    /**
     * Sets the type of the Bounded Context.
     *
     * @param type the type that shall be set on the represented Bounded Context
     * @return returns the Bounded Context
     */
    public BoundedContext withType(BoundedContextType type) {
        this.type = type;
        return this;
    }

    /**
     * Adds a generic context to the realized systems; only allowed to use if the represented Bounded Context is a team.
     *
     * @param genericContext the generic Bounded Context that shall be realized by the team.
     * @return the team
     */
    public BoundedContext realizing(BoundedContext genericContext) {
        if (this.type != BoundedContextType.TEAM)
            throw new BoundedContextIsNotATeamException(this.name);
        if (genericContext.getType() == BoundedContextType.TEAM)
            throw new TeamCannotImplementTeamException(genericContext.getName());
        this.realizedBoundedContexts.add(genericContext);
        return this;
    }

    /**
     * Gets the Bounded Context name.
     *
     * @return the name of the Bounded Context
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type of the Bounded Context.
     *
     * @return the type of the Bounded Context
     */
    public BoundedContextType getType() {
        return type;
    }

    /**
     * Returns the Bounded Contexts that are realized by the represented team. If the represented Bounded Context
     * is not a team, this method returns an empty list.
     *
     * @return the list of realized Bounded Contexts in case the represented context is a team, an empty list otherwise
     */
    public List<BoundedContext> getRealizedBoundedContexts() {
        if (this.type != BoundedContextType.TEAM)
            return new LinkedList<>();
        return realizedBoundedContexts.stream().filter(bc -> bc.getType() != BoundedContextType.TEAM).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BoundedContext))
            return false;

        BoundedContext bc = (BoundedContext) object;

        return new EqualsBuilder()
                .append(name, bc.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .hashCode();
    }
}
