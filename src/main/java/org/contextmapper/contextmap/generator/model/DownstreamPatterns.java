package org.contextmapper.contextmap.generator.model;

/**
 * The DDD relationship patterns allowed on the downstream side.
 *
 * @author Stefan Kapferer
 */
public enum DownstreamPatterns {

    ANTICORRUPTION_LAYER("ACL"), CONFORMIST("CF");

    private String patternAbbreviation;

    DownstreamPatterns(String patternAbbreviation) {
        this.patternAbbreviation = patternAbbreviation;
    }

    @Override
    public String toString() {
        return patternAbbreviation;
    }
}
