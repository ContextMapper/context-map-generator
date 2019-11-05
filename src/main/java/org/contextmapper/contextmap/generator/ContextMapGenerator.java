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

    private Map<String, MutableNode> bcNodesMap;

    public void generateContextMapGraphic(ContextMap contextMap, String fileName) throws IOException {
        this.bcNodesMap = new TreeMap<>();
        MutableGraph graph = mutGraph("ContextMapGraph");

        // create nodes
        contextMap.getBoundedContexts().forEach(bc -> {
            MutableNode node = mutNode(bc.getName());
            node.add(Label.lines(bc.getName()));
            node.add(Shape.EGG);
            node.add(attr("margin", "0.3"));
            node.add(attr("orientation", orientationDegree()));
            bcNodesMap.put(bc.getName(), node);
        });

        // link nodes
        contextMap.getRelationships().forEach(rel -> {
            MutableNode node1 = this.bcNodesMap.get(rel.getFirstParticipant().getName());
            MutableNode node2 = this.bcNodesMap.get(rel.getSecondParticipant().getName());

            if (rel instanceof Partnership) {
                node1.addLink(to(node2).with(Label.of("Partnership")));
            } else if (rel instanceof SharedKernel) {
                node1.addLink(to(node2).with(Label.of("Shared Kernel")));
            } else {
                UpstreamDownstreamRelationship upDownRel = (UpstreamDownstreamRelationship) rel;
                node1.addLink(to(node2).with(
                        (Label.of("              ")),
                        attr("labeldistance", "0"),
                        attr("headlabel", getEdgeHTMLLabel("D", downstreamPatternsToStrings(upDownRel.getDownstreamPatterns()))),
                        attr("taillabel", getEdgeHTMLLabel("U", upstreamPatternsToStrings(upDownRel.getUpstreamPatterns())))
                ));
            }
        });

        // add nodes to graph
        for (MutableNode node : this.bcNodesMap.values()) {
            graph.add(node);
        }

        // store file
        Graphviz.fromGraph(graph).width(2000).render(Format.PNG).toFile(new File(fileName));
        Graphviz.fromGraph(graph).width(2000).render(Format.DOT).toFile(new File(fileName + ".dot"));
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
            patternCell = "<td sides=\"trbl\" bgcolor=\"white\"><font POINT-SIZE=\"5\">" + String.join(", ", patterns) + "</font></td>";
            border = "1";
        }
        return Label.html("<table cellspacing=\"0\" cellborder=\"" + border + "\" border=\"0\">\n" +
                "<tr>" + upstreamDownstreamCell + patternCell + "</tr>\n" +
                "</table>");
    }

}
