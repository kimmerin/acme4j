/*
 * acme4j - Java ACME client
 *
 * Copyright (C) 2017 Richard "Shred" Körber
 *   http://acme4j.shredzone.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.shredzone.acme4j;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.shredzone.acme4j.toolbox.TestUtils.url;

import java.net.URI;
import java.net.URL;
import java.util.List;

import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.junit.jupiter.api.Test;
import org.shredzone.acme4j.toolbox.JSON;
import org.shredzone.acme4j.toolbox.JSONBuilder;
import org.shredzone.acme4j.toolbox.TestUtils;

/**
 * Unit tests for {@link Problem}.
 */
public class ProblemTest {

    @Test
    public void testProblem() {
        URL baseUrl = url("https://example.com/acme/1");
        JSON original = TestUtils.getJSON("problem");

        Problem problem = new Problem(original, baseUrl);

        assertThatJson(problem.asJSON().toString()).isEqualTo(original.toString());

        try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
            softly.assertThat(problem.getType()).isEqualTo(URI.create("urn:ietf:params:acme:error:malformed"));
            softly.assertThat(problem.getTitle()).isEqualTo("Some of the identifiers requested were rejected");
            softly.assertThat(problem.getDetail()).isEqualTo("Identifier \"abc12_\" is malformed");
            softly.assertThat(problem.getInstance()).isEqualTo(URI.create("https://example.com/documents/error.html"));
            softly.assertThat(problem.getIdentifier()).isNull();
            softly.assertThat(problem.toString()).isEqualTo(
                    "Identifier \"abc12_\" is malformed ("
                            + "Invalid underscore in DNS name \"_example.com\" ‒ "
                            + "This CA will not issue for \"example.net\")");

            List<Problem> subs = problem.getSubProblems();
            softly.assertThat(subs).isNotNull().hasSize(2);

            Problem p1 = subs.get(0);
            softly.assertThat(p1.getType()).isEqualTo(URI.create("urn:ietf:params:acme:error:malformed"));
            softly.assertThat(p1.getTitle()).isNull();
            softly.assertThat(p1.getDetail()).isEqualTo("Invalid underscore in DNS name \"_example.com\"");
            softly.assertThat(p1.getIdentifier().getDomain()).isEqualTo("_example.com");
            softly.assertThat(p1.toString()).isEqualTo("Invalid underscore in DNS name \"_example.com\"");

            Problem p2 = subs.get(1);
            softly.assertThat(p2.getType()).isEqualTo(URI.create("urn:ietf:params:acme:error:rejectedIdentifier"));
            softly.assertThat(p2.getTitle()).isNull();
            softly.assertThat(p2.getDetail()).isEqualTo("This CA will not issue for \"example.net\"");
            softly.assertThat(p2.getIdentifier().getDomain()).isEqualTo("example.net");
            softly.assertThat(p2.toString()).isEqualTo("This CA will not issue for \"example.net\"");
        }
    }

    /**
     * Test that {@link Problem#toString()} always returns the most specific message.
     */
    @Test
    public void testToString() {
        URL baseUrl = url("https://example.com/acme/1");
        URI typeUri = URI.create("urn:ietf:params:acme:error:malformed");

        JSONBuilder jb = new JSONBuilder();

        jb.put("type", typeUri);
        Problem p1 = new Problem(jb.toJSON(), baseUrl);
        assertThat(p1.toString()).isEqualTo(typeUri.toString());

        jb.put("title", "Some of the identifiers requested were rejected");
        Problem p2 = new Problem(jb.toJSON(), baseUrl);
        assertThat(p2.toString()).isEqualTo("Some of the identifiers requested were rejected");

        jb.put("detail", "Identifier \"abc12_\" is malformed");
        Problem p3 = new Problem(jb.toJSON(), baseUrl);
        assertThat(p3.toString()).isEqualTo("Identifier \"abc12_\" is malformed");
    }

}
