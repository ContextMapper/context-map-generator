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

import org.gradle.api.Action
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.artifacts.ResolvedDependency
import org.osgi.framework.Version;
import org.standardout.gradle.plugin.platform.PlatformPlugin;
import org.standardout.gradle.plugin.platform.internal.util.gradle.DependencyHelper;
import org.standardout.gradle.plugin.platform.internal.util.bnd.BndHelper;

import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Jar;


/**
 * Action that creates bundles.
 * 
 * @author Robert Gregor
 * @author Simon Templer
 */
class BundlesAction implements Action<Task> {

	private final Project project
	
	private final File targetDir
	
	BundlesAction(Project project, File targetDir) {
		this.project = project
		this.targetDir = targetDir
	}
	
	@Override
	public void execute(Task task) {
		Configuration config = project.getConfigurations().getByName(PlatformPlugin.CONF_PLATFORM)
		ResolvedConfiguration resolved = config.resolvedConfiguration
		
		if (project.logger.debugEnabled) {
			// output some debug information on the configuration
			configInfo(config, project.logger.&debug)
			resolvedConfigInfo(resolved.resolvedArtifacts, project.logger.&debug)
		}
		
		// collect dependency files (to later be able to determine pure file dependencies)
		def dependencyFiles = config.collect()

		// create artifact representations
		// id is mapped to artifacts
		def artifacts = project.platform.artifacts
		
		def resolvedDeps = new LinkedList(resolved.firstLevelModuleDependencies)
		// collect transitive resolved dependencies
		def allDeps = new HashSet()
		while (!resolvedDeps.empty) {
			ResolvedDependency dep = resolvedDeps.poll()
			if (!allDeps.contains(dep)) {
				allDeps << dep
				resolvedDeps.addAll(dep.children)
			}
		}
		
		allDeps.each { ResolvedDependency dep ->
			dep.moduleArtifacts.each {
				if (it.extension == 'jar') {
					// only Jars are valid artifacts (ignore poms)
					BundleArtifact artifact = new ResolvedBundleArtifact(it, dep, project)
					artifacts[artifact.id] = artifact
					
					// check if there is a source Jar that can be found via file name
					// this is added here specifically for project dependencies
					// source artifact may be overridden later 
					if (project.platform.fetchSources) {
						// try to find associated source jar by name
						String filename = artifact.file.name
						File sourceJar = ['-sources', '-source'].collect {
							String extension = (filename =~ /\.\w+$/)[0]
							String candidateName = filename[0..-(extension.length()+1)] + it + filename[-extension.length()..-1]
							new File(artifact.file.parentFile, candidateName)
						}.find {
							it.exists()
						}
						if (sourceJar) {
							FileBundleArtifact source = new FileBundleArtifact(artifact, sourceJar)
							
							// register artifact so it is included in the platform feature
							project.platform.artifacts[source.id] = source
						}
					}
				}
				
				dependencyFiles.remove(it.file)
			}
		}
		 
		// check if explicitly defined auxiliary dependencies are already present
		Configuration auxConfig = project.getConfigurations().getByName(PlatformPlugin.CONF_AUX)
		def auxAddedDeps = new HashSet<String>()
		auxConfig.dependencies.each {
			Dependency dep ->
			if (dep.name) { // only external deps supported
				String id = "${dep.group}:${dep.name}:${dep.version}"
				
				// check if artifact with given ID is already present
				if (!artifacts.containsKey(id)) {
					// if not, add the artifact
					DependencyHelper.getArtifacts(project, dep).each {
						if (it.extension == 'jar') {
							// only Jars are valid artifacts (ignore poms)
							BundleArtifact artifact = new ResolvedBundleArtifact(it, null, project, true)
							artifacts[artifact.id] = artifact
							
							auxAddedDeps << id
							
							// also add source bundle if possible
							ResolvedArtifact sourceArt = DependencyHelper.getDetachedArtifacts(project, id + ':sources').find {
								ResolvedArtifact art ->
								art.extension == 'jar' && art.classifier == 'sources'
							}
							if (sourceArt) {
								SourceBundleArtifact sourceArtifact = new SourceBundleArtifact(sourceArt, project)
								artifacts[sourceArtifact.id] = sourceArtifact
								// associate to parent artifact
								sourceArtifact.parentBundle = artifact
								artifact.sourceBundle = sourceArtifact
							}
						}
					}
				}
			}
		}
		if (auxAddedDeps) {
			project.logger.warn('Added the following explicitly defined auxiliary dependencies (platformaux) in addition to the resolved configuration (platform): '
				+ auxAddedDeps.join(', '))
		}
		
		// dependency source artifacts
		if (project.platform.fetchSources) {
			def sourceArtifacts = DependencyHelper.resolveSourceArtifacts(config, project.configurations)
			sourceArtifacts.each {
				SourceBundleArtifact artifact = new SourceBundleArtifact(it, project)
				artifacts[artifact.id] = artifact
				
				// check if associated bundle is found, associated source to 
				if (artifacts[artifact.unifiedName]) {
					BundleArtifact bundle = artifacts[artifact.unifiedName]
					if (bundle) {
						artifact.parentBundle = bundle
						bundle.sourceBundle = artifact
					}
					else {
						project.logger.warn "No parent bundle for source ${artifact.id} found"
					}
				}
			}
			
			// output info
			if (project.logger.debugEnabled) {
				resolvedConfigInfo('Source artifacts', sourceArtifacts, project.logger.&debug)
			}
		}
		
		// file artifacts
		dependencyFiles.each {
			// for all remaining dependencies assume they are local files
			FileBundleArtifact artifact = new FileBundleArtifact(it, project)
			artifacts[artifact.id] = artifact
			
			if (project.platform.fetchSources) {
				// try to find associated source jar by name
				String filename = artifact.file.name
				File sourceJar = ['-sources', '-source'].collect {
					String extension = (filename =~ /\.\w+$/)[0]
					String candidateName = filename[0..-(extension.length()+1)] + it + filename[-extension.length()..-1]
					new File(artifact.file.parentFile, candidateName)
				}.find {
					it.exists()
				}
				if (sourceJar) {
					FileBundleArtifact source = new FileBundleArtifact(artifact, sourceJar)
					
					// register artifact so it is included in the platform feature
					project.platform.artifacts[source.id] = source
				}
			}
		}
		
		targetDir.mkdirs()

		if(!artifacts) {
			project.logger.warn 'No platform artifacts could be found, no bundles created'
			return
		} else {
			project.logger.info "Processing ${artifacts.size()} dependency artifacts:"
		}
		
		project.platform.configurations.createBundles(artifacts.values(), targetDir)
	}
	
	// methods logging information for easier debugging
	
	protected void configInfo(Configuration config, def log) {
		log("Configuration: $config.name")
		
		log('  Dependencies:')
		config.allDependencies.each {
			log("    - $it.group $it.name $it.version")
//			it.properties.each {
//				k, v ->
//				log("    $k: $v")
//			}
		}
		
		log('  Files:')
		config.collect().each {
			log("    - ${it}")
		}
	}
	
	protected void resolvedConfigInfo(String title = 'Resolved configuration', Iterable<ResolvedArtifact> resolvedArtifacts, def log) {
		log(title)
		
		log('  Artifacts:')
		resolvedArtifacts.each {
			log("    ${it.name}:")
			log("      File: $it.file")
			log("      Classifier: $it.classifier")
			log("      Extension: $it.extension")
			log("      Group: $it.moduleVersion.id.group")
			log("      Name: $it.moduleVersion.id.name")
			log("      Version: $it.moduleVersion.id.version")
//			it.properties.each {
//				k, v ->
//				log("      $k: $v")
//			}
		}
	}

}
