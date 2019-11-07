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
package guru.nidi.graphviz.attribute;

import java.util.Objects;

public class SimpleLabel {
    final String value;
    final boolean html;

    SimpleLabel(String value, boolean html) {
        this.value = value;
        this.html = html;
    }

    public static SimpleLabel of(String value) {
        return new SimpleLabel(value, false);
    }

    public static SimpleLabel of(Object value) {
        return value instanceof SimpleLabel ? (SimpleLabel) value : of(value.toString());
    }

    public String serialized() {
        return html
                ? ("<" + value + ">")
                : ("\"" + value.replace("\"", "\\\"").replace("\n", "\\n") + "\"");
    }

    public String value() {
        return value;
    }

    public boolean isHtml() {
        return html;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SimpleLabel that = (SimpleLabel) o;
        return html == that.html
                && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, html);
    }

    @Override
    public String toString() {
        return value;
    }
}
