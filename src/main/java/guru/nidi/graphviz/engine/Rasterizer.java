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
package guru.nidi.graphviz.engine;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import static guru.nidi.graphviz.engine.IoUtils.isOnClasspath;

public interface Rasterizer {

    @Nullable
    Rasterizer SALAMANDER = isOnClasspath("com.kitfox.svg.SVGDiagram") ? new SalamanderRasterizer() : null;
    @Nullable
    Rasterizer DEFAULT = SALAMANDER;

    static Rasterizer builtIn(String format) {
        return new BuiltInRasterizer(format, null, null);
    }

    static Rasterizer builtIn(String format, String renderer) {
        return new BuiltInRasterizer(format, renderer, null);
    }

    static Rasterizer builtIn(String format, String renderer, String formatter) {
        return new BuiltInRasterizer(format, renderer, formatter);
    }

    Format format();

    BufferedImage rasterize(Graphviz graphviz, @Nullable Consumer<Graphics2D> graphicsConfigurer, String input);
}
