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

import org.contextmapper.contextmap.generator.model.exception.BoundedContextIsNotATeamException;
import org.contextmapper.contextmap.generator.model.exception.TeamCannotImplementTeamException;
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
    public void BoundedContextIsGenericByDefault() {
        // given
        BoundedContext testContext;

        // when
        testContext = new BoundedContext("TestContext");

        // then
        assertEquals(BoundedContextType.GENERIC, testContext.getType());
    }

    @Test
    public void canChangeBoundedContextType() {
        // given
        BoundedContext testContext = new BoundedContext("TestContext");

        // when
        testContext.withType(BoundedContextType.TEAM);

        // then
        assertEquals(BoundedContextType.TEAM, testContext.getType());
    }

    @Test
    public void genericContextCannotRealizeAnything() {
        // given
        BoundedContext testContext = new BoundedContext("TestContext");

        // when, then
        assertThrows(BoundedContextIsNotATeamException.class, () -> {
            testContext.realizing(new BoundedContext("AnotherContext"));
        });
    }

    @Test
    public void teamCannotImplementOtherTeam() {
        // given
        BoundedContext team = new BoundedContext("TestTeam", BoundedContextType.TEAM);

        // when, then
        assertThrows(TeamCannotImplementTeamException.class, () -> {
            team.realizing(new BoundedContext("AnotherTeam", BoundedContextType.TEAM));
        });
    }

    @Test
    public void teamCanRealizeSystem() {
        // given
        BoundedContext team = new BoundedContext("TestTeam", BoundedContextType.TEAM);
        BoundedContext implementedSystem = new BoundedContext("TestSystem");

        // when
        team.realizing(implementedSystem);

        // then
        assertEquals(1, team.getRealizedBoundedContexts().size());
        assertEquals("TestSystem", team.getRealizedBoundedContexts().get(0).getName());
    }

    @Test
    public void genericSystemDoesNotRealizeAnything() {
        // given
        BoundedContext testContext1 = new BoundedContext("TestContext1", BoundedContextType.TEAM);
        BoundedContext testContext2 = new BoundedContext("TestContext2");

        // when
        testContext1.realizing(testContext2);
        testContext1.withType(BoundedContextType.GENERIC); // converting team to generic context

        // then
        assertTrue(testContext1.getRealizedBoundedContexts().isEmpty());
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
