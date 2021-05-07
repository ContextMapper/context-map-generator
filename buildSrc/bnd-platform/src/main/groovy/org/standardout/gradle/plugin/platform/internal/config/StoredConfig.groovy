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

package org.standardout.gradle.plugin.platform.internal.config

import groovy.lang.Closure;

import java.util.List;
import java.util.Map;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import java.io.File

/**
 * Stores the configuration of a bundle concerning bnd.
 */
interface StoredConfig {
	
	List<Closure> getBndClosures()
	
	List<Closure> getImportsClosures()

	BndConfig evaluate(Project project, File file, Map<String, String> initialProperties)
	
	BndConfig evaluate(Project project, String group, String name, String version, Map<String, String> initialProperties)
	
	BndConfig evaluate(Project project, String group, String name, String version, File file, Map<String, String> initialProperties)

	/**
	 * @return the imports configuration, may be <code>null</code>
	 */
	ImportsConfig importsConfig(Project project, String group, String name, String version)
	
	/**
	 * Append the given configuration.	
	 */
	void leftShift(StoredConfig other)
	
	/**
	 * Prepend the own configuration to the given configuration object.
	 */
	void rightShift(StoredConfig other)
	
	/**
	 * @return if the configuration is empty
	 */
	boolean isEmpty()
	
}
