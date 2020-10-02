/*
 * Copyright 2019 The Context Mapper Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contextmapper.contextmap.generator;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.Renderer;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import org.contextmapper.contextmap.generator.model.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.model.Factory.*;

/**
 * Generating graphical Context Map with Graphviz.
 *
 * @author Stefan Kapferer
 */
public class ContextMapGenerator {

    private static final String EDGE_SPACING_UNIT = "        ";

    private Map<String, MutableNode> bcNodesMap;
    private Set<MutableNode> genericNodes;
    private Set<MutableNode> teamNodes;
    private File baseDir; // used for Graphviz images

    protected int labelSpacingFactor = 1;
    protected int height = 1000;
    protected int width = 2000;
    protected boolean useHeight = false;
    protected boolean useWidth = true;
    protected boolean clusterTeams = true;

    public ContextMapGenerator() {
        this.baseDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "GraphvizJava");
    }

    /**
     * Sets the base directory for included images (team maps).
     * In case you work with SVG or DOT files it is recommended to set the directory into which you generate the images.
     *
     * @param baseDir the baseDir into which we copy the team map image.
     */
    public ContextMapGenerator setBaseDir(File baseDir) {
        this.baseDir = baseDir;
        return this;
    }

    /**
     * Defines how much spacing we add to push the edges apart from each other.
     * Can be increased if the labels at the edges are overlapping.
     *
     * @param spacingFactor a factor from 1 to 20
     */
    public ContextMapGenerator setLabelSpacingFactor(int spacingFactor) {
        if (spacingFactor < 1)
            labelSpacingFactor = 1;
        else if (spacingFactor > 20)
            labelSpacingFactor = 20;
        else
            labelSpacingFactor = spacingFactor;
        return this;
    }

    /**
     * Fixes the height of the produced image.
     * When you call this method, the width of the image will be determined flexibly.
     *
     * @param height the height the produced image must have
     */
    public ContextMapGenerator setHeight(int height) {
        this.useHeight = true;
        this.useWidth = false;
        this.height = height;
        return this;
    }

    /**
     * Fixes the width of the produced image.
     * When you call this method, the height of the image will be determined flexibly.
     *
     * @param width the width the produced image must have
     */
    public ContextMapGenerator setWidth(int width) {
        this.useWidth = true;
        this.useHeight = false;
        this.width = width;
        return this;
    }

    /**
     * Defines whether teams (also generic contexts) are clustered together; is only relevant for mixed team maps
     * containing both types of BCs. If true, the resulting layout clusters BCs of the same types.
     *
     * @param clusterTeams whether BCs of the same type shall be clustered or not
     */
    public ContextMapGenerator clusterTeams(boolean clusterTeams) {
        this.clusterTeams = clusterTeams;
        return this;
    }

    /**
     * Generates the graphical Context Map.
     *
     * @param contextMap the {@link ContextMap} for which the graphical representation shall be generated
     * @param format     the file format to be generated
     * @param fileName   the target filename
     * @throws IOException
     */
    public void generateContextMapGraphic(ContextMap contextMap, Format format, String fileName) throws IOException {
        generateContextMapGraphic(contextMap, format).toFile(new File(fileName));
    }

    /**
     * Generates the graphical Context Map.
     *
     * @param contextMap   the {@link ContextMap} for which the graphical representation shall be generated
     * @param format       the file format to be generated
     * @param outputStream the outputstream to which the image is written
     * @throws IOException
     */
    public void generateContextMapGraphic(ContextMap contextMap, Format format, OutputStream outputStream) throws IOException {
        generateContextMapGraphic(contextMap, format).toOutputStream(outputStream);
    }

    private Renderer generateContextMapGraphic(ContextMap contextMap, Format format) throws IOException {
        exportImages();
        MutableGraph graph = createGraph(contextMap);

        // store file
        if (useWidth)
            return Graphviz.fromGraph(graph).basedir(baseDir).width(width).render(format);
        else
            return Graphviz.fromGraph(graph).basedir(baseDir).height(height).render(format);
    }

    private MutableGraph createGraph(ContextMap contextMap) {
        this.bcNodesMap = new TreeMap<>();
        this.genericNodes = new HashSet<>();
        this.teamNodes = new HashSet<>();
        MutableGraph rootGraph = createGraph("ContextMapGraph");

        createNodes(contextMap.getBoundedContexts());

        if (!needsSubGraphs(contextMap)) {
            addNodesToGraph(rootGraph, bcNodesMap.values());
            createRelationshipLinks4ExistingNodes(contextMap.getRelationships());
        } else {
            MutableGraph genericGraph = createGraph(getSubgraphName("GenericSubgraph"))
                    .graphAttrs().add("color", "white");
            addNodesToGraph(genericGraph, genericNodes);
            MutableGraph teamGraph = createGraph(getSubgraphName("Teams_Subgraph"))
                    .graphAttrs().add("color", "white");
            addNodesToGraph(teamGraph, teamNodes);
            genericGraph.addTo(rootGraph);
            teamGraph.addTo(rootGraph);

            createRelationshipLinks4ExistingNodes(contextMap.getRelationships().stream().filter(rel -> rel.getFirstParticipant().getType() == rel.getSecondParticipant().getType())
                    .collect(Collectors.toSet()));
            createRelationshipLinks(rootGraph, contextMap.getRelationships().stream().filter(rel -> rel.getFirstParticipant().getType() != rel.getSecondParticipant().getType())
                    .collect(Collectors.toSet()));
            createTeamImplementationLinks(rootGraph, contextMap.getBoundedContexts().stream().filter(bc -> bc.getType() == BoundedContextType.TEAM
                    && !bc.getRealizedBoundedContexts().isEmpty()).collect(Collectors.toList()));
        }
        return rootGraph;
    }

    private String getSubgraphName(String baseName) {
        return clusterTeams ? "cluster_" + baseName : baseName;
    }

    private boolean needsSubGraphs(ContextMap contextMap) {
        boolean hasTeams = contextMap.getBoundedContexts().stream().anyMatch(bc -> bc.getType() == BoundedContextType.TEAM);
        boolean hasGenericContexts = contextMap.getBoundedContexts().stream().anyMatch(bc -> bc.getType() == BoundedContextType.GENERIC);
        return hasGenericContexts && hasTeams;
    }

    private MutableGraph createGraph(String name) {
        MutableGraph rootGraph = mutGraph(name);
        rootGraph.setDirected(true);
        rootGraph.graphAttrs().add(attr("imagepath", baseDir.getAbsolutePath()));
        return rootGraph;
    }

    private void addNodesToGraph(MutableGraph graph, Collection<MutableNode> nodes) {
        for (MutableNode node : nodes) {
            graph.add(node);
        }
    }

    private void createNodes(Set<BoundedContext> boundedContexts) {
        boundedContexts.forEach(bc -> {
            MutableNode node = createNode(bc);
            bcNodesMap.put(bc.getName(), node);
            if (bc.getType() == BoundedContextType.TEAM)
                teamNodes.add(node);
            else
                genericNodes.add(node);
        });
    }

    private MutableNode createNode(BoundedContext bc) {
        MutableNode node = mutNode(bc.getName());
        node.add(createNodeLabel(bc));
        node.add(Shape.EGG);
        node.add(attr("margin", "0.3"));
        node.add(attr("orientation", orientationDegree()));
        node.add(attr("fontname", "sans-serif"));
        node.add(attr("fontsize", "16"));
        node.add(attr("style", "bold"));
        return node;
    }

    private void createRelationshipLinks4ExistingNodes(Set<Relationship> relationships) {
        relationships.forEach(rel -> {
            createRelationshipLink(this.bcNodesMap.get(rel.getFirstParticipant().getName()),
                    this.bcNodesMap.get(rel.getSecondParticipant().getName()), rel);
        });
    }

    private void createRelationshipLinks(MutableGraph graph, Set<Relationship> relationships) {
        relationships.forEach(rel -> {
            MutableNode node1 = createNode(rel.getFirstParticipant());
            MutableNode node2 = createNode(rel.getSecondParticipant());
            createRelationshipLink(node1, node2, rel);
            graph.add(node1);
            graph.add(node2);
        });
    }

    private void createRelationshipLink(MutableNode node1, MutableNode node2, Relationship rel) {
        if (rel instanceof Partnership) {
            node1.addLink(to(node2).with(createRelationshipLabel("Partnership", rel.getName(), rel.getImplementationTechnology()))
                    .add(attr("dir", "none"))
                    .add(attr("fontname", "sans-serif"))
                    .add(attr("style", "bold"))
                    .add(attr("fontsize", "12")));
        } else if (rel instanceof SharedKernel) {
            node1.addLink(to(node2).with(createRelationshipLabel("Shared Kernel", rel.getName(), rel.getImplementationTechnology()))
                    .add(attr("dir", "none"))
                    .add(attr("fontname", "sans-serif"))
                    .add(attr("style", "bold"))
                    .add(attr("fontsize", "12")));
        } else {
            UpstreamDownstreamRelationship upDownRel = (UpstreamDownstreamRelationship) rel;
            node1.addLink(to(node2).with(
                    createRelationshipLabel(upDownRel.isCustomerSupplier() ? "Customer/Supplier" : "", rel.getName(), rel.getImplementationTechnology()),
                    attr("dir", "none"),
                    attr("labeldistance", "0"),
                    attr("fontname", "sans-serif"),
                    attr("fontsize", "12"),
                    attr("style", "bold"),
                    attr("headlabel", getEdgeHTMLLabel("D", downstreamPatternsToStrings(upDownRel.getDownstreamPatterns()))),
                    attr("taillabel", getEdgeHTMLLabel("U", upstreamPatternsToStrings(upDownRel.getUpstreamPatterns())))
            ));
        }
    }

    private void createTeamImplementationLinks(MutableGraph graph, List<BoundedContext> teams) {
        for (BoundedContext team : teams) {
            team.getRealizedBoundedContexts().forEach(system -> {
                if (bcNodesMap.containsKey(team.getName()) && bcNodesMap.containsKey(system.getName())) {
                    MutableNode node1 = createNode(team);
                    MutableNode node2 = createNode(system);
                    node1.addLink(to(node2).with(
                            Label.lines("  «realizes»"),
                            attr("color", "#686868"),
                            attr("fontname", "sans-serif"),
                            attr("fontsize", "12"),
                            attr("fontcolor", "#686868"),
                            attr("style", "dashed")));
                    graph.add(node1);
                    graph.add(node2);
                }
            });
        }
    }

    private Label createNodeLabel(BoundedContext boundedContext) {
        if (boundedContext.getType() == BoundedContextType.TEAM)
            return Label.html("<table cellspacing=\"0\" cellborder=\"0\" border=\"0\"><tr><td rowspan=\"2\"><img src='team-icon.png' /></td><td width=\"10px\">" +
                    "</td><td><b>Team</b></td></tr><tr><td width=\"10px\"></td><td>" + boundedContext.getName() + "</td></tr></table>");
        return Label.lines(boundedContext.getName());
    }

    private Label createRelationshipLabel(String relationshipType, String relationshipName, String implementationTechnology) {
        boolean relationshipTypeDefined = relationshipType != null && !"".equals(relationshipType);
        boolean nameDefined = relationshipName != null && !"".equals(relationshipName);
        boolean implementationTechnologyDefined = implementationTechnology != null && !"".equals(implementationTechnology);

        String label = relationshipType;

        if (relationshipTypeDefined && nameDefined && implementationTechnologyDefined)
            label = relationshipName + " (" + relationshipType + " implemented with " + implementationTechnology + ")";
        else if (nameDefined && implementationTechnologyDefined)
            label = relationshipName + " (" + implementationTechnology + ")";
        else if (relationshipTypeDefined && implementationTechnologyDefined)
            label = relationshipType + " (" + implementationTechnology + ")";
        else if (relationshipTypeDefined && nameDefined)
            label = relationshipName + " (" + relationshipType + ")";
        else if (nameDefined)
            label = relationshipName;
        else if (implementationTechnologyDefined)
            label = implementationTechnology;

        if (!"".equals(label))
            return Label.of(label);

        // create spacing for edges without label
        String spacing = "";
        for (int i = 1; i <= labelSpacingFactor; i++) {
            spacing = spacing + EDGE_SPACING_UNIT;
        }
        return Label.of(spacing);
    }

    /*
     * Generate random orientation degree
     */
    private int orientationDegree() {
        return new Random().nextInt(350);
    }

    private Set<String> downstreamPatternsToStrings(Set<DownstreamPatterns> patterns) {
        return patterns.stream().map(p -> p.toString()).collect(Collectors.toSet());
    }

    private Set<String> upstreamPatternsToStrings(Set<UpstreamPatterns> patterns) {
        return patterns.stream().map(p -> p.toString()).collect(Collectors.toSet());
    }

    private Label getEdgeHTMLLabel(String upstreamDownstreamLabel, Set<String> patterns) {
        String upstreamDownstreamCell = "<td bgcolor=\"white\">" + upstreamDownstreamLabel + "</td>";
        String patternCell = "";
        String border = "0";
        if (patterns.size() > 0) {
            upstreamDownstreamCell = "<td bgcolor=\"white\" sides=\"r\">" + upstreamDownstreamLabel + "</td>";
            patternCell = "<td sides=\"trbl\" bgcolor=\"white\"><font>" + String.join(", ", patterns) + "</font></td>";
            border = "1";
        }
        return Label.html("<table cellspacing=\"0\" cellborder=\"" + border + "\" border=\"0\">\n" +
                "<tr>" + upstreamDownstreamCell + patternCell + "</tr>\n" +
                "</table>");
    }

    private void exportImages() throws IOException {
        InputStream teamIconInputStream = ContextMapGenerator.class.getClassLoader().getResourceAsStream("team-icon.png");
        byte[] buffer = new byte[teamIconInputStream.available()];
        teamIconInputStream.read(buffer);
        File targetFile = new File(baseDir, "team-icon.png");
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        outStream.flush();
        outStream.close();
    }

}
