package org.contextmapper.contextmap.generator.model;

/**
 * The DDD relationship patterns allowed on the upstream side.
 *
 * @author Stefan Kapferer
 */
public enum UpstreamPatterns {

    OPEN_HOST_SERVICE("OHS"), PUBLISHED_LANGUAGE("PL");

    private String patternAbbreviation;

    UpstreamPatterns(String patternAbbreviation) {
        this.patternAbbreviation = patternAbbreviation;
    }

    @Override
    public String toString() {
        return patternAbbreviation;
    }
}
