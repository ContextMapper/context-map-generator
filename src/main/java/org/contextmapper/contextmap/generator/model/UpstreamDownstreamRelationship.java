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

public class UpstreamDownstreamRelationship implements Relationship {

    private BoundedContext upstreamBoundedContext;
    private BoundedContext downstreamBoundedContext;
    private Set<UpstreamPatterns> upstreamPatterns;
    private Set<DownstreamPatterns> downstreamPatterns;

    public UpstreamDownstreamRelationship(BoundedContext upstreamBoundedContext, BoundedContext downstreamBoundedContext) {
        this.upstreamBoundedContext = upstreamBoundedContext;
        this.downstreamBoundedContext = downstreamBoundedContext;
        this.upstreamPatterns = new TreeSet<>();
        this.downstreamPatterns = new TreeSet<>();
    }

    public BoundedContext getUpstreamBoundedContext() {
        return upstreamBoundedContext;
    }

    public BoundedContext getDownstreamBoundedContext() {
        return downstreamBoundedContext;
    }

    public UpstreamDownstreamRelationship setUpstreamPatterns(UpstreamPatterns... upstreamPatterns) {
        this.upstreamPatterns = new TreeSet<>();
        this.upstreamPatterns.addAll(Arrays.asList(upstreamPatterns));
        return this;
    }

    public UpstreamDownstreamRelationship setDownstreamPatterns(DownstreamPatterns... downstreamPatterns) {
        this.downstreamPatterns = new TreeSet<>();
        this.downstreamPatterns.addAll(Arrays.asList(downstreamPatterns));
        return this;
    }

    public Set<UpstreamPatterns> getUpstreamPatterns() {
        return upstreamPatterns;
    }

    public Set<DownstreamPatterns> getDownstreamPatterns() {
        return downstreamPatterns;
    }

    @Override
    public BoundedContext getFirstParticipant() {
        return upstreamBoundedContext;
    }

    @Override
    public BoundedContext getSecondParticipant() {
        return downstreamBoundedContext;
    }
}
