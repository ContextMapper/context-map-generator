![Context Mapper](https://raw.githubusercontent.com/wiki/ContextMapper/context-mapper-dsl/logo/cm-logo-github-small.png) 
# Graphical DDD Context Map Generator
[![Build Status](https://travis-ci.com/ContextMapper/context-map-generator.svg?branch=master)](https://travis-ci.com/ContextMapper/context-map-generator) [![codecov](https://codecov.io/gh/ContextMapper/context-map-generator/branch/master/graph/badge.svg)](https://codecov.io/gh/ContextMapper/context-map-generator) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Maven Central](https://img.shields.io/maven-central/v/org.contextmapper/context-map-generator.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.contextmapper%22%20AND%20a:%22context-map-generator%22)

This repository contains a Java library to generate graphical Domain-driven Design (DDD) Context Maps inspired by [Brandolini](https://www.infoq.com/articles/ddd-contextmapping/) and [Vernon](https://www.amazon.de/Implementing-Domain-Driven-Design-Vaughn-Vernon/dp/0321834577). The generation of the Context Maps is based on [Graphviz](https://www.graphviz.org/), [graphviz-java](https://github.com/nidi3/graphviz-java), and used within the [Context Mapper](https://contextmapper.org) tool.

## Usage
The library is published to [Maven central](https://search.maven.org/search?q=g:%22org.contextmapper%22%20AND%20a:%22context-map-generator%22) and as an Eclipse feature to a [P2 repository](https://dl.bintray.com/contextmapper/context-map-generator-releases/).

Therefore, you can easily include the library to your Maven or Gradle build:

**Maven:**
```xml
<dependency>
  <groupId>org.contextmapper</groupId>
  <artifactId>context-map-generator</artifactId>
  <version>1.2.1</version>
</dependency>
```

**Gradle:**
```gradle
implementation 'org.contextmapper:context-map-generator:1.2.1'
```

### Preconditions
**Important note:** The generator requires [Graphviz](https://www.graphviz.org/) to be installed on the machine on which you run it. Ensure that the [Graphviz](https://www.graphviz.org/) binaries are part of your **PATH** environment variable (**especially on Windows since the installer does not add it automatically**; the path to be added to the PATH variable is typically `C:\Program Files (x86)\GraphvizX.XX\bin`).

## Examples
The following Java programs illustrates how you can create a Context Map (png file in these cases). They generate Context Maps for our [insurance example](https://github.com/ContextMapper/context-mapper-examples/tree/master/src/main/cml/insurance-example) and the [DDD "Cargo" sample application](https://github.com/ContextMapper/context-mapper-examples/tree/master/src/main/cml/ddd-sample) (CML examples can be found in our [examples repository](https://github.com/ContextMapper/context-mapper-examples)).

### Insurance Example
```java
BoundedContext customerManagement = new BoundedContext("Customer Management Context");
BoundedContext customerSelfService = new BoundedContext("Customer Self-Service Context");
BoundedContext printing = new BoundedContext("Printing Context");
BoundedContext debtCollection = new BoundedContext("Debt Collection Context");
BoundedContext policyManagement = new BoundedContext("Policy Management Context");
BoundedContext riskManagement = new BoundedContext("Risk Management Context");

ContextMap contextMap = new ContextMap()
  .addBoundedContext(customerManagement)
  .addBoundedContext(customerSelfService)
  .addBoundedContext(printing)
  .addBoundedContext(debtCollection)
  .addBoundedContext(policyManagement)
  .addBoundedContext(riskManagement)

  // Customer/Supplier relationship example
  .addRelationship(new UpstreamDownstreamRelationship(customerManagement, customerSelfService)
    .setCustomerSupplier(true))

  // Upstream/Downstream relationship with OHS, PL, ACL, and CF examples
  .addRelationship(new UpstreamDownstreamRelationship(printing, customerManagement)
    .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE)
    .setDownstreamPatterns(ANTICORRUPTION_LAYER))
  .addRelationship(new UpstreamDownstreamRelationship(printing, policyManagement)
    .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE)
    .setDownstreamPatterns(ANTICORRUPTION_LAYER))
  .addRelationship(new UpstreamDownstreamRelationship(printing, debtCollection)
    .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE)
    .setDownstreamPatterns(ANTICORRUPTION_LAYER))
  .addRelationship(new UpstreamDownstreamRelationship(customerManagement, policyManagement)
    .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE)
    .setDownstreamPatterns(CONFORMIST))

  // Shared Kernel relationship example
  .addRelationship(new SharedKernel(debtCollection, policyManagement))

  // Partnership relationship example
  .addRelationship(new Partnership(riskManagement, policyManagement));

// generate the Context Map
new ContextMapGenerator().setLabelSpacingFactor(10)
  .setWidth(3600)
  .generateContextMapGraphic(contextMap, Format.PNG, "/home/user/myContextMap.png");
```

The program above generates the following Context Map:

<a href="https://raw.githubusercontent.com/ContextMapper/context-map-generator/master/examples/context-map-example-1.png" target="_blank"><img src="https://raw.githubusercontent.com/ContextMapper/context-map-generator/master/examples/context-map-example-1.png" alt="Example Context Map" /></a>

### Labels
Optionally it is possible to define a `name` or `implementation technology` for each relationship by using the corresponding setters. The following example calls show how we set them on relationships from the example above:

```java
.addRelationship(new UpstreamDownstreamRelationship(printing, debtCollection)
    .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE)
    .setDownstreamPatterns(ANTICORRUPTION_LAYER)
    .setName("PrintingDebts"))
    
.addRelationship(new SharedKernel(debtCollection, policyManagement)
    .setImplementationTechnology("Java Library"))
```

The generator adds those information as additional labels to the generated relationships, as this example shows:

<a href="https://raw.githubusercontent.com/ContextMapper/context-map-generator/master/examples/context-map-example-3.png" target="_blank"><img src="https://raw.githubusercontent.com/ContextMapper/context-map-generator/master/examples/context-map-example-3.png" alt="Example Context Map" /></a>

### Team Maps
The Context Map generator also allows users to illustrate which development teams work on which subsystems, components, or Bounded Contexts. By default, a Bounded Context is of
the type "generic", but there is another constructor that allows you to create Bounded Contexts of the type "team". If a Bounded Context is of the type "team", it can
"realize" a Bounded Context of the type "generic". The following example illustrates how you can use this concept to visualize which teams work on which Bounded Contexts:

```java
BoundedContext customerManagement = new BoundedContext("Customer Management Context");
BoundedContext customerSelfService = new BoundedContext("Customer Self-Service Context");
BoundedContext policyManagementContext = new BoundedContext("Policy Management Context");
BoundedContext riskManagementContext = new BoundedContext("Risk Management Context");

BoundedContext customersBackendTeam = new BoundedContext("Customers Backend Team", BoundedContextType.TEAM)
  .realizing(customerManagement);
BoundedContext customersFrontendTeam = new BoundedContext("Customers Frontend Team", BoundedContextType.TEAM)
  .realizing(customerSelfService);
BoundedContext contractsTeam = new BoundedContext("Contracts", BoundedContextType.TEAM)
  .realizing(policyManagementContext)
  .realizing(riskManagementContext);

ContextMap contextMap = new ContextMap()
  .addBoundedContext(customerManagement)
  .addBoundedContext(customerSelfService)
  .addBoundedContext(policyManagementContext)
  .addBoundedContext(riskManagementContext)
  .addBoundedContext(customersBackendTeam)
  .addBoundedContext(customersFrontendTeam)
  .addBoundedContext(contractsTeam)
  .addRelationship(new UpstreamDownstreamRelationship(customerManagement, customerSelfService)
    .setCustomerSupplier(true))
  .addRelationship(new UpstreamDownstreamRelationship(customerManagement, policyManagementContext)
    .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE)
    .setDownstreamPatterns(CONFORMIST))
  .addRelationship(new Partnership(policyManagementContext, riskManagementContext))
  .addRelationship(new UpstreamDownstreamRelationship(customersBackendTeam, customersFrontendTeam)
    .setCustomerSupplier(true))
  .addRelationship(new UpstreamDownstreamRelationship(customersBackendTeam, contractsTeam));

new ContextMapGenerator()
  .generateContextMapGraphic(contextMap, Format.PNG, "/home/user/myContextMap.png");
``` 

By default, the generator produces the following _team map_:

<a href="https://raw.githubusercontent.com/ContextMapper/context-map-generator/master/examples/team-map-example-1.png" target="_blank"><img src="https://raw.githubusercontent.com/ContextMapper/context-map-generator/master/examples/team-map-example-1.png" alt="Example Team Map (Clustered)" /></a>

As you can see, the generator clusters the Bounded Contexts of the two types (teams and generic BCs) together. Alternatively, you can disable the clustering as follows:

```java
new ContextMapGenerator()
  .clusterTeams(false) // disable clustering
  .generateContextMapGraphic(contextMap, Format.PNG, "/home/user/myContextMap.png");
``` 

In this case the produced graphic looks as follows:

<a href="https://raw.githubusercontent.com/ContextMapper/context-map-generator/master/examples/team-map-example-2.png" target="_blank"><img src="https://raw.githubusercontent.com/ContextMapper/context-map-generator/master/examples/team-map-example-2.png" alt="Example Team Map (Unclustered)" /></a>

### DDD "Cargo" Sample Application
```java
BoundedContext cargoBookingContext = new BoundedContext("Cargo Booking Context");
BoundedContext voyagePlanningContext = new BoundedContext("Voyage Planning Context");
BoundedContext locationContext = new BoundedContext("Location Context");

ContextMap contextMap = new ContextMap()
  .addBoundedContext(cargoBookingContext)
  .addBoundedContext(voyagePlanningContext)
  .addBoundedContext(locationContext)

  .addRelationship(new SharedKernel(cargoBookingContext, voyagePlanningContext))
  .addRelationship(new UpstreamDownstreamRelationship(locationContext, cargoBookingContext)
    .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE))
  .addRelationship(new UpstreamDownstreamRelationship(locationContext, voyagePlanningContext)
    .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE));

// generate the Context Map
new ContextMapGenerator().setLabelSpacingFactor(10)
  .generateContextMapGraphic(contextMap, Format.PNG, "/home/user/myContextMap.png");
```

The result:

<a href="https://raw.githubusercontent.com/ContextMapper/context-map-generator/master/examples/context-map-example-2.png" target="_blank"><img src="https://raw.githubusercontent.com/ContextMapper/context-map-generator/master/examples/context-map-example-2.png" alt="Example Context Map" /></a>

## Parameters
With the following methods you can parameterize the `ContextMapGenerator`:

| Method / Parameter                       | Description                                                                                                                                                                                                                                                                                                                                              | Default value |
|------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| setHeight(int height)                    | By using this parameter you can fix the height of the produced image. Note that if you use fix the height, the width will be adjusted dynamically.                                                                                                                                                                                                       | 1000          |
| setWidth(int width)                      | By using this parameter you can fix the width of the produced image. Note that if you use fix the width, the height will be adjusted dynamically.                                                                                                                                                                                                        | 2000          |
| setLabelSpacingFactor(int spacingFactor) | The Graphviz layouting algorithm doesn't ensure that the labels of the edges do not overlap. Especially the boxes with the relationship patterns (OHS, PL, ACL, CF) may often overlap in our case. By introducing spacing between the edges we can often bypass this issue. This parameter (a factor between 1 and 20) controls how much spacing we add. | 1             |
| clusterTeams(boolean clusterTeams)       | This parameter allows you to control whether Bounded Contexts of the different types (teams vs. generic) are clustered together or not. It is relevant for team maps only (see example team maps above).                                                                                                                                                 | true          |

## Supported Output Formats
As illustrated in the example code above, the `generateContextMapGraphic` method takes a parameter to define the output format. The following formats are supported:

 * PNG
 * SVG
 * DOT ([Graphviz dot format](https://www.graphviz.org/doc/info/lang.html); *.gv file)

## Development / Build
If you want to contribute to this project you can create a fork and a pull request. The project is built with Gradle, so you can import it as Gradle project within Eclipse or IntelliJ IDEA (or any other IDE supporting Gradle).

## Contributing
Contribution is always welcome! Here are some ways how you can contribute:
 * Create Github issues if you find bugs or just want to give suggestions for improvements.
 * This is an open source project: if you want to code, [create pull requests](https://help.github.com/articles/creating-a-pull-request/) from [forks of this repository](https://help.github.com/articles/fork-a-repo/). Please refer to a Github issue if you contribute this way. 
 * If you want to contribute to our documentation and user guides on our website [https://contextmapper.org/](https://contextmapper.org/), create pull requests from forks of the corresponding page repo [https://github.com/ContextMapper/contextmapper.github.io](https://github.com/ContextMapper/contextmapper.github.io) or create issues [there](https://github.com/ContextMapper/contextmapper.github.io/issues).

## Licence
ContextMapper is released under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
