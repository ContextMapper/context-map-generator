package com.github.missedone.gradle

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional

class ArtifactPublishTask extends AbstractPublishTask {

    @InputFile
    File artifactFile

    @Input
    String packageName

    @Input
    @Optional
    String targetPath

    @Input
    @Optional
    boolean override

    ArtifactPublishTask() {
        this.description = 'Publishes artifact to bintray.com.'
        this.group = 'publishing'
    }

    @Override
    void executeAction() {
        ArtifactoryClient client = new ArtifactoryClient(apiUrl, user, apiKey)
        client.uploadContent(artifactFile, repoOwner, repoName, targetPath, packageName, packageVersion, override)
    }
}