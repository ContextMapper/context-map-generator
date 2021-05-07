package com.github.missedone.gradle

class ArtifactoryP2Extension {

    // 
    // common properties for the tasks
    //

    String apiUrl

    String user

    String apiKey

    String repoOwner

    String repoName
    
    String packageVersion


    //
    // for artifact publish
    //

    File artifactFile

    String packageName

    String targetPath

    boolean override

    //
    // for p2 repo publish
    //

    File repoDir
    File zippedRepoFile
    String compositePackage
    String zipSitePackage
    String updateSitePackage
    String subCompositeStrategy
    String mainFeatureId
}
