package com.github.missedone.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class AbstractPublishTask extends DefaultTask {
    static final String DEFAULT_API_URL = 'https://api.bintray.com'

    @Input
    @Optional
    String apiUrl = DEFAULT_API_URL

    @Input
    String user

    @Input
    String apiKey

    @Input
    String repoOwner

    @Input
    String repoName

    @Input
    @Optional
    String packageVersion

    @TaskAction
    void start() {
        withExceptionHandling {
            executeAction()
        }
    }

    private void withExceptionHandling(Closure c) {
        try {
            c()
        }
        catch (Exception e) {
            logger.error "Failed to execute Bintray P2 task", e
            throw new GradleException(e.message)
        }
    }

    abstract void executeAction()
}
