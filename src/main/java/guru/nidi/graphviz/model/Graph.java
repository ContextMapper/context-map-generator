/*
 * Copyright © 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.graphviz.model;

import guru.nidi.graphviz.attribute.*;

import java.util.List;

public interface Graph extends LinkSource, LinkTarget {
    Graph strict();

    Graph directed();

    Graph cluster();

    Graph named(String name);

    Graph link(LinkTarget... targets);

    Graph with(LinkSource... sources);

    Graph with(List<? extends LinkSource> sources);

    Attributed<Graph, ForNode> nodeAttr();

    Attributed<Graph, ForLink> linkAttr();

    Attributed<Graph, ForGraph> graphAttr();
}
