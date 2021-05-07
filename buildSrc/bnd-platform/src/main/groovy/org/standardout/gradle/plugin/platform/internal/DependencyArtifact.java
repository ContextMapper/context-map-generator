/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.standardout.gradle.plugin.platform.internal;

import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedDependency;

public interface DependencyArtifact {
	
	/**
	 * @return the direct dependencies of this artifact
	 */
	Set<ResolvedArtifact> getDirectDependencies(Project project);
	
	/**
	 * @return the resolved dependencies the artifact represents
	 *   (no transitive dependencies)
	 */
	Iterable<ResolvedDependency> getRepresentedDependencies();

}
