/*
 * acme4j - Java ACME client
 *
 * Copyright (C) 2016 Richard "Shred" Körber
 *   http://acme4j.shredzone.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.shredzone.acme4j.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.shredzone.acme4j.toolbox.TestUtils.createProblem;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.shredzone.acme4j.Problem;

/**
 * Unit tests for {@link AcmeUserActionRequiredException}.
 */
public class AcmeUserActionRequiredExceptionTest {

    /**
     * Test that parameters are correctly returned.
     */
    @Test
    public void testAcmeUserActionRequiredException() throws MalformedURLException {
        URI type = URI.create("urn:ietf:params:acme:error:userActionRequired");
        String detail = "Accept new TOS";
        URI tosUri = URI.create("http://example.com/agreement.pdf");
        URL instanceUrl = new URL("http://example.com/howToAgree.html");

        Problem problem = createProblem(type, detail, instanceUrl);

        AcmeUserActionRequiredException ex
            = new AcmeUserActionRequiredException(problem, tosUri);

        assertThat(ex.getType()).isEqualTo(type);
        assertThat(ex.getMessage()).isEqualTo(detail);
        assertThat(ex.getTermsOfServiceUri()).isEqualTo(tosUri);
        assertThat(ex.getInstance()).isEqualTo(instanceUrl);
    }

    /**
     * Test that optional parameters are null-safe.
     */
    @Test
    public void testNullAcmeUserActionRequiredException() throws MalformedURLException {
        URI type = URI.create("urn:ietf:params:acme:error:userActionRequired");
        String detail = "Call our service";
        URL instanceUrl = new URL("http://example.com/howToContactUs.html");

        Problem problem = createProblem(type, detail, instanceUrl);

        AcmeUserActionRequiredException ex
            = new AcmeUserActionRequiredException(problem, null);

        assertThat(ex.getType()).isEqualTo(type);
        assertThat(ex.getMessage()).isEqualTo(detail);
        assertThat(ex.getTermsOfServiceUri()).isNull();
        assertThat(ex.getInstance()).isEqualTo(instanceUrl);
    }

}
