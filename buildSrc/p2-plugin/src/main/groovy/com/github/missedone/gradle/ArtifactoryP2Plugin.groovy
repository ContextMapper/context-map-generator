package com.github.missedone.gradle

import org.gradle.api.*

class ArtifactoryP2Plugin implements Plugin<Project> {
    static final String EXTENSION_NAME = 'p2-plugin'

    @Override
    void apply(Project project) {
        ArtifactoryP2Extension extension = project.extensions.create(EXTENSION_NAME, ArtifactoryP2Extension)
        addTasks(project, extension)
    }

    private void addTasks(Project project, ArtifactoryP2Extension extension) {
        project.tasks.withType(AbstractPublishTask) {
            conventionMapping.apiUrl = { getApiUrl(project, extension) }
            conventionMapping.user = { getBintrayUser(project, extension) }
            conventionMapping.apiKey = { getApiKey(project, extension) }
            conventionMapping.repoOwner = { extension.repoOwner }
            conventionMapping.repoName = { extension.repoName }
            conventionMapping.packageVersion = { extension.packageVersion }
        }

        project.task('publishArtifact', type: ArtifactPublishTask) {
            conventionMapping.packageName = { extension.packageName }
            conventionMapping.artifactFile = { extension.artifactFile }
            conventionMapping.targetPath = { extension.targetPath }
            conventionMapping.override = { extension.override }
        }

        project.task('removeArtifact', type: PublishArtifactRemoveTask) {
            conventionMapping.targetPath = { extension.targetPath }
        }

        project.task('publishP2Repo', type: PublishP2RepoPublishTask) {
            conventionMapping.repoDir = { extension.repoDir }
            conventionMapping.zippedRepoFile = { extension.zippedRepoFile }
            conventionMapping.override = { extension.override }
            conventionMapping.compositePackage = { extension.compositePackage }
            conventionMapping.zipSitePackage = { extension.zipSitePackage }
            conventionMapping.updateSitePackage = { extension.updateSitePackage }
            conventionMapping.subCompositeStrategy = { extension.subCompositeStrategy }
            conventionMapping.mainFeatureId = { extension.mainFeatureId }
        }
    }

    private String getApiUrl(Project project, ArtifactoryP2Extension extension) {
        def apiUrl = extension.apiUrl ?: AbstractPublishTask.DEFAULT_API_URL
        return apiUrl
    }

    private String getBintrayUser(Project project, ArtifactoryP2Extension extension) {
        def user = extension.user ?: System.getProperty('BINTRAY_USER')
        user = user ?: System.getenv('BINTRAY_USER')
        return user
    }

    private String getApiKey(Project project, ArtifactoryP2Extension extension) {
        def apiKey = extension.apiKey ?: System.getProperty('BINTRAY_API_KEY')
        apiKey = apiKey ?: System.getenv('BINTRAY_API_KEY')
        return apiKey
    }
}
