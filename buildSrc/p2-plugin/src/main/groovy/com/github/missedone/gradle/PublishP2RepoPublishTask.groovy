package com.github.missedone.gradle

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.osgi.framework.Version

class PublishP2RepoPublishTask extends AbstractPublishTask {

    static final String DEFAULT_COMPOSITE_PKG_NAME = 'composite';
    static final String DEFAULT_ZIPPED_PKG_NAME = 'zipped';
    static final String DEFAULT_UPDATESITE_PKG_NAME = 'updatesites';

    static final String MAJOR = 'MAJOR'
    static final String MINOR = 'MINOR'
    static final String MICRO = 'MICRO'
    static final String NONE = 'NONE'

    @InputDirectory
    @Optional
    File repoDir

    @InputFile
    @Optional
    File zippedRepoFile

    @Input
    @Optional
    String compositePackage

    @Input
    @Optional
    String subCompositeStrategy

    @Input
    @Optional
    String zipSitePackage

    @Input
    @Optional
    String updateSitePackage

    @Input
    @Optional
    String mainFeatureId

    @Input
    @Optional
    boolean override

    PublishP2RepoPublishTask() {
        this.description = 'Publish the P2 repo to Bintray, including zipped updatesite as well.'
        this.group = 'publishing'
    }

    @Override
    void executeAction() {
        repoDir = repoDir ?: new File(getProject().buildDir, 'updatesite')
        zippedRepoFile = zippedRepoFile ?: new File(getProject().buildDir, 'updatesite.zip')
        if (compositePackage == null) {
            // compositePackage can be empty '', while in groovy, empty string is falsy
            // the elvis operator does not work in the expected way
            // https://spin.atomicobject.com/2012/11/06/beware-the-elvis-operator-in-groovy/
            compositePackage = DEFAULT_COMPOSITE_PKG_NAME
        }
        zipSitePackage = zipSitePackage ?: DEFAULT_ZIPPED_PKG_NAME
        updateSitePackage = updateSitePackage ?: DEFAULT_UPDATESITE_PKG_NAME
        subCompositeStrategy = subCompositeStrategy ?: NONE

        assert repoDir.exists(): "repo dir '${repoDir}' does not exist"
        assert zippedRepoFile.exists(): "zipped updatesite file '${zippedRepoFile}' does not exist"

        assert compositePackage ==~ /[A-Za-z0-9]*/
        assert zipSitePackage ==~ /[A-Za-z0-9]+/
        assert updateSitePackage ==~ /[A-Za-z0-9]+/

        packageVersion = packageVersion ?: parsePackageVersion(repoDir)
        assert packageVersion != null :
                '''package version may not be null'''

        ArtifactoryClient client = new ArtifactoryClient(apiUrl, user, apiKey)
        uploadUpdateSite(client, repoDir, updateSitePackage)
        uploadZippedSite(client, zippedRepoFile)
        updateCompositeUpdateSite(client)
    }

    private void uploadUpdateSite(ArtifactoryClient client, File updateSiteDir, String updateSitePackage) {
        logger.lifecycle("Uploading updatesite ${updateSiteDir} ...")
        def tree = getProject().fileTree(dir: updateSiteDir)
        tree.visit {element ->
            logger.debug("$element.relativePath => $element.file")
            if (element.file.isFile()) {
                def packageName = updateSitePackage
                def targetPath = "${packageName}/${packageVersion}/$element.relativePath"
                client.uploadContent(element.file, repoOwner, repoName, targetPath,
                                        packageName, packageVersion, override)
            }
        }
    }

    private void uploadZippedSite(ArtifactoryClient client, File zippedSiteFile) {
        logger.lifecycle("Uploading zipped updatesite ${zippedSiteFile} ...")
        def packageName = zipSitePackage
        def targetPath = "${packageName}/${packageVersion}/" + zippedSiteFile.name
        client.uploadContent(zippedSiteFile, repoOwner, repoName, targetPath, 
                                packageName, packageVersion, override)
    }

    private void updateCompositeUpdateSite(ArtifactoryClient client) {
        def subsiteLoc = "../${updateSitePackage}/${packageVersion}"
        if (compositePackage.isEmpty()) {
            subsiteLoc = "./${updateSitePackage}/${packageVersion}"
        }
        def targetParent = compositePackage

        if (subCompositeStrategy != NONE) {
            String ver = packageVersion
            Version v = new Version(packageVersion)
            switch (subCompositeStrategy) {
                case MAJOR:
                    ver = v.getMajor()
                    break;
                case MINOR:
                    ver = v.getMajor() + "." + v.getMinor()
                    break;
                case MICRO:
                    ver = v.getMajor() + "." + v.getMinor() + "." + v.getMicro()
                    break;
            }

            subsiteLoc = "../../${updateSitePackage}/${packageVersion}"
            if (compositePackage.isEmpty()) {
                subsiteLoc = "./../${updateSitePackage}/${packageVersion}"
            }

            targetParent = "${compositePackage}/${ver}"

            updateCompositeSite(client, targetParent, subsiteLoc)

            // reset properties for parent composite
            subsiteLoc = "./${ver}"
            targetParent = compositePackage
        }

        updateCompositeSite(client, targetParent, subsiteLoc)
    }

    private void updateCompositeSite(ArtifactoryClient client, String targetParent, String subsiteLoc) {
        def targetPath = "${targetParent}/compositeContent.xml"
        def localFile = new File(repoDir, 'compositeContent.xml')
        try {
            if (localFile.exists()) {
                localFile.delete()
            }
            client.downloadContent(repoOwner, repoName, targetPath, localFile)
        } catch (IOException e) {
            // log and continue to create one in next step
            logger.error(e.getMessage())
        }

        Node rootNode = null
        if (localFile.exists() && localFile.isFile()) {
            rootNode = new XmlParser().parse(localFile)
        }
        else {
            def xmlText = """<?xml version='1.0' encoding='UTF-8'?>
<?compositeMetadataRepository version='1.0.0'?>
<repository name='${project.name} Composite P2 Repo' type='org.eclipse.equinox.internal.p2.metadata.repository.CompositeMetadataRepository' version='1.0.0'>
    <properties size='2'>
        <property name='p2.timestamp' value='1454086165279'/>
        <property name='p2.atomic.composite.loading' value='true'/>
    </properties>
    <children size='3'>
    </children>
</repository>"""
            rootNode = new XmlParser().parseText(xmlText)
        }

        def n = rootNode.'children'[0].find { it.@location == subsiteLoc }
        if (n != null) {
            logger.info("child location ${subsiteLoc} already exist, just skip")
            return
        }

        rootNode.'children'[0].appendNode("child", [location: subsiteLoc])
        new XmlNodePrinter(new PrintWriter(new FileWriter(localFile))).print(rootNode)

        logger.lifecycle("Uploading composite updatesite ...")
        targetPath = "${targetParent}/compositeContent.xml"
        client.uploadContent(localFile, repoOwner, repoName, targetPath,
                compositePackage, packageVersion, override)
        targetPath = "${targetParent}/compositeArtifacts.xml"
        client.uploadContent(localFile, repoOwner, repoName, targetPath,
                compositePackage, packageVersion, override)
    }

    private String parsePackageVersion(File updateSiteDir) {
        String ver = null
        def featuresDir = new File(updateSiteDir, "features")
        if (featuresDir.exists() && featuresDir.directory) {
            def tree = getProject().fileTree(dir: featuresDir)
            def ptn = mainFeatureId ? "${mainFeatureId}_*.jar" : '*.feature_*.jar'
            // println "::: pattern ${ptn}"
            tree.include(ptn)
            tree.visit {
                if (ver == null) {
                    String str = it.file.name
                    int sidx = str.lastIndexOf('_')
                    int didx = str.lastIndexOf('.')
                    ver = str.substring(sidx + 1, didx)
                }
            }
        }
        return ver
    }
}