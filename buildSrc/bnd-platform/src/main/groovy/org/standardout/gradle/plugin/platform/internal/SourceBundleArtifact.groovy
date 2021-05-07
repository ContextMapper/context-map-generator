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

package org.standardout.gradle.plugin.platform.internal

import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedArtifact
import org.standardout.gradle.plugin.platform.internal.config.BndConfig;

class SourceBundleArtifact extends ResolvedBundleArtifact {

	BundleArtifact parentBundle

	public SourceBundleArtifact(ResolvedArtifact artifact, Project project) {
		super(artifact, null, project);
	}

	@Override
	public String getBundleName() {
		if (parentBundle) {
			parentBundle.bundleName + ' Sources'
		}
		else {
			super.getBundleName() + ' ' +  classifier
		}
	}

	@Override
	public String getSymbolicName() {
		if (parentBundle) {
			parentBundle.symbolicName + '.source'
		}
		else {
			super.getSymbolicName() + ".$classifier"
		}
	}
	
	String getModifiedVersion() {
		if (parentBundle) {
			// return the parent bundles version to prevent issues
			// with cached source bundles referencing outdated bundle
			// versions, e.g. if hash qualifiers are used
			parentBundle.modifiedVersion
		}
		else {
			super.getModifiedVersion()
		}
	}

	@Override
	public boolean isSource() {
		true
	}

	@Override
	public BndConfig getBndConfig() {
		// no bnd configuration applicable
		null
	}
	
}
