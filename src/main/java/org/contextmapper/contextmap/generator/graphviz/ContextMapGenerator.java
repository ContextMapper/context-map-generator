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
package org.contextmapper.contextmap.generator.graphviz;

import guru.nidi.graphviz.attribute.Attributes;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import org.contextmapper.contextmap.generator.model.ContextMap;
import org.contextmapper.contextmap.generator.model.Partnership;
import org.contextmapper.contextmap.generator.model.SharedKernel;
import org.contextmapper.contextmap.generator.model.UpstreamDownstreamRelationship;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import static guru.nidi.graphviz.model.Factory.*;

/**
 * Generating Context Map with graphviz. Experimental state!
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
            MutableNode node = mutNode(bc.getId());
            node.add(Label.lines(bc.getName()));
            bcNodesMap.put(bc.getId(), node);
        });

        // link nodes
        contextMap.getRelationships().forEach(rel -> {
            MutableNode node1 = this.bcNodesMap.get(rel.getFirstParticipant().getId());
            MutableNode node2 = this.bcNodesMap.get(rel.getSecondParticipant().getId());

            if (rel instanceof Partnership) {
                node1.addLink(to(node2).with(Label.of("Partnership")));
            } else if (rel instanceof SharedKernel) {
                node1.addLink(to(node2).with(Label.of("Shared Kernel")));
            } else {
                UpstreamDownstreamRelationship upDownRel = (UpstreamDownstreamRelationship) rel;
                node1.addLink(to(node2).with(Attributes.attr("labeldistance", "0"), Attributes.attr("headlabel", Label.html("<table cellspacing=\"0\" cellborder=\"1\" border=\"0\">\n" +
                        "<tr><td sides=\"r\">D</td><td sides=\"\" bgcolor=\"white\"><font POINT-SIZE=\"5\">" + upDownRel.getUpstreamPatterns().toString() + "</font></td></tr>\n" +
                        "</table>")), Attributes.attr("taillabel", Label.html("<table cellspacing=\"0\" cellborder=\"1\" border=\"0\">\n" +
                        "<tr><td sides=\"r\">U</td><td sides=\"\" port=\"a\" bgcolor=\"white\"><font POINT-SIZE=\"5\" >" + upDownRel.getDownstreamPatterns().toString() + "</font></td></tr>\n" +
                        "</table>"))));
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

}
