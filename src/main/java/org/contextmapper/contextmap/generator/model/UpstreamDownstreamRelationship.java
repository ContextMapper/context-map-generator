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

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents an upstream-downstream relationship on a Context Map.
 *
 * @author Stefan Kapferer
 */
public class UpstreamDownstreamRelationship implements Relationship {

    private BoundedContext upstreamBoundedContext;
    private BoundedContext downstreamBoundedContext;
    private Set<UpstreamPatterns> upstreamPatterns;
    private Set<DownstreamPatterns> downstreamPatterns;
    private boolean isCustomerSupplier = false;

    public UpstreamDownstreamRelationship(BoundedContext upstreamBoundedContext, BoundedContext downstreamBoundedContext) {
        this.upstreamBoundedContext = upstreamBoundedContext;
        this.downstreamBoundedContext = downstreamBoundedContext;
        this.upstreamPatterns = new TreeSet<>();
        this.downstreamPatterns = new TreeSet<>();
    }

    /**
     * Gets the upstream Bounded Context.
     *
     * @return the upstream Bounded Context
     */
    public BoundedContext getUpstreamBoundedContext() {
        return upstreamBoundedContext;
    }

    /**
     * Gets the downstream Bounded Context
     *
     * @return the downstream Bounded Context
     */
    public BoundedContext getDownstreamBoundedContext() {
        return downstreamBoundedContext;
    }

    /**
     * Sets the upstream relationship patterns (OHS and/or PL) of the relationship.
     *
     * @param upstreamPatterns the upstream patterns (OHS and/or PL) of the relationship
     */
    public UpstreamDownstreamRelationship setUpstreamPatterns(UpstreamPatterns... upstreamPatterns) {
        this.upstreamPatterns = new TreeSet<>();
        this.upstreamPatterns.addAll(Arrays.asList(upstreamPatterns));
        return this;
    }

    /**
     * Sets the downstream relationship patterns (ACL or CF) of the relationship.
     *
     * @param downstreamPatterns the downstream patterns (ACL or CF) of the relationship
     */
    public UpstreamDownstreamRelationship setDownstreamPatterns(DownstreamPatterns... downstreamPatterns) {
        this.downstreamPatterns = new TreeSet<>();
        this.downstreamPatterns.addAll(Arrays.asList(downstreamPatterns));
        return this;
    }

    /**
     * Gets the upstream relationship patterns (OHS and/or PL) of the relationship.
     *
     * @return the upstream relationship patterns (OHS and/or PL)
     */
    public Set<UpstreamPatterns> getUpstreamPatterns() {
        return upstreamPatterns;
    }

    /**
     * Gets the downstream relationship patterns (ACL or CF) of the relationship.
     *
     * @return the downstream relationship patterns (ACL or CF)
     */
    public Set<DownstreamPatterns> getDownstreamPatterns() {
        return downstreamPatterns;
    }

    /**
     * Gets the first participant (upstream) of the relationship.
     *
     * @return the first participant (upstream) of the relationship
     */
    @Override
    public BoundedContext getFirstParticipant() {
        return upstreamBoundedContext;
    }

    /**
     * Gets the second participant (downstream) of the relationship.
     *
     * @return the second participant (downstream) of the relationship
     */
    @Override
    public BoundedContext getSecondParticipant() {
        return downstreamBoundedContext;
    }

    /**
     * Sets if upstream-downstream relationship is a customer-supplier relationship (false by default).
     *
     * @param customerSupplier boolean whether relationship is a customer-supplier relationship or not
     */
    public UpstreamDownstreamRelationship setCustomerSupplier(boolean customerSupplier) {
        isCustomerSupplier = customerSupplier;
        return this;
    }

    /**
     * Gets whether the relationship is a customer-supplier relationship or not.
     *
     * @return true, if the relationship is a customer-supplier relationship. false, otherwise.
     */
    public boolean isCustomerSupplier() {
        return isCustomerSupplier;
    }

}
