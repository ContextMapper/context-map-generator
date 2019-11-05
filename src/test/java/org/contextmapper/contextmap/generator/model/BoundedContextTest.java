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

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BoundedContextTest {

    @Test
    public void canCreateBoundedContext() {
        // given
        String myBoundedContextName = "Test Context";

        // when
        BoundedContext bc = new BoundedContext(myBoundedContextName);

        // then
        assertEquals(myBoundedContextName, bc.getName());
    }

    @Test
    public void boundedContextsWithSameNameAreEqual() {
        // given
        BoundedContext bc1 = new BoundedContext("TestContext");
        BoundedContext bc2 = new BoundedContext("TestContext");
        Set<BoundedContext> bcSet = new HashSet<>();

        // when
        boolean equals = bc1.equals(bc2);
        bcSet.add(bc1);
        bcSet.add(bc2);

        // then
        assertTrue(equals);
        assertEquals(1, bcSet.size());
    }

    @Test
    public void otherObjectsAreNotEqual() {
        // given
        BoundedContext bc = new BoundedContext("TestContext");

        // when
        boolean equals = bc.equals(new Object());

        // then
        assertFalse(equals);
    }

}
