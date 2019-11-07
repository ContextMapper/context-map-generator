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
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import org.contextmapper.contextmap.generator.model.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
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

    protected int labelSpacingFactor = 1;
    protected int height = 1500;
    protected int width = 3600;
    protected boolean useHeight = false;
    protected boolean useWidth = true;

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
     * Generates the graphical Context Map.
     *
     * @param contextMap the {@link ContextMap} for which the graphical representation shall be generated
     * @param format     the file format to be generated
     * @param fileName   the target filename
     * @throws IOException
     */
    public void generateContextMapGraphic(ContextMap contextMap, Format format, String fileName) throws IOException {
        MutableGraph graph = createGraph(contextMap);

        // store file
        if (useWidth)
            Graphviz.fromGraph(graph).width(width).render(format).toFile(new File(fileName));
        else
            Graphviz.fromGraph(graph).height(height).render(format).toFile(new File(fileName));
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
        MutableGraph graph = createGraph(contextMap);

        // store file
        if (useWidth)
            Graphviz.fromGraph(graph).width(width).render(format).toOutputStream(outputStream);
        else
            Graphviz.fromGraph(graph).height(height).render(format).toOutputStream(outputStream);
    }

    private MutableGraph createGraph(ContextMap contextMap) {
        this.bcNodesMap = new TreeMap<>();
        MutableGraph graph = mutGraph("ContextMapGraph");

        // create nodes
        contextMap.getBoundedContexts().forEach(bc -> {
            MutableNode node = mutNode(bc.getName());
            node.add(Label.lines(bc.getName()));
            node.add(Shape.EGG);
            node.add(attr("margin", "0.3"));
            node.add(attr("orientation", orientationDegree()));
            node.add(attr("fontname", "sans-serif"));
            node.add(attr("fontsize", "16"));
            node.add(attr("style", "bold"));
            bcNodesMap.put(bc.getName(), node);
        });

        // link nodes
        contextMap.getRelationships().forEach(rel -> {
            MutableNode node1 = this.bcNodesMap.get(rel.getFirstParticipant().getName());
            MutableNode node2 = this.bcNodesMap.get(rel.getSecondParticipant().getName());

            if (rel instanceof Partnership) {
                node1.addLink(to(node2).with(createLabel("Partnership"))
                        .add(attr("fontname", "sans-serif"))
                        .add(attr("style", "bold"))
                        .add(attr("fontsize", "12")));
            } else if (rel instanceof SharedKernel) {
                node1.addLink(to(node2).with(createLabel("Shared Kernel"))
                        .add(attr("fontname", "sans-serif"))
                        .add(attr("style", "bold"))
                        .add(attr("fontsize", "12")));
            } else {
                UpstreamDownstreamRelationship upDownRel = (UpstreamDownstreamRelationship) rel;
                node1.addLink(to(node2).with(
                        createLabel(upDownRel.isCustomerSupplier() ? "Customer/Supplier" : ""),
                        attr("labeldistance", "0"),
                        attr("fontname", "sans-serif"),
                        attr("fontsize", "12"),
                        attr("style", "bold"),
                        attr("headlabel", getEdgeHTMLLabel("D", downstreamPatternsToStrings(upDownRel.getDownstreamPatterns()))),
                        attr("taillabel", getEdgeHTMLLabel("U", upstreamPatternsToStrings(upDownRel.getUpstreamPatterns())))
                ));
            }
        });

        // add nodes to graph
        for (MutableNode node : this.bcNodesMap.values()) {
            graph.add(node);
        }
        return graph;
    }

    private Label createLabel(String label) {
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

}
