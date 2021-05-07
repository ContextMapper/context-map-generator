package com.github.missedone.gradle

import org.gradle.api.tasks.Input

class PublishArtifactRemoveTask extends AbstractPublishTask {

    @Input
    String targetPath

    PublishArtifactRemoveTask() {
        this.description = 'Remove the artifact from Bintray'
    }

    @Override
    void executeAction() {
        ArtifactoryClient client = new ArtifactoryClient(apiUrl, user, apiKey)
        client.deleteContent(repoOwner, repoName, targetPath)
    }
}
