/*
 * acme4j - Java ACME client
 *
 * Copyright (C) 2018 Richard "Shred" Körber
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.shredzone.acme4j.exception.AcmeProtocolException;
import org.shredzone.acme4j.toolbox.JSONBuilder;

/**
 * Unit tests for {@link Identifier}.
 */
public class IdentifierTest {

    @Test
    public void testConstants() {
        assertThat(Identifier.TYPE_DNS).isEqualTo("dns");
        assertThat(Identifier.TYPE_IP).isEqualTo("ip");
    }

    @Test
    public void testGetters() {
        Identifier id1 = new Identifier("foo", "123.456");
        assertThat(id1.getType()).isEqualTo("foo");
        assertThat(id1.getValue()).isEqualTo("123.456");
        assertThat(id1.toString()).isEqualTo("foo=123.456");
        Map<String, Object> map1 = id1.toMap();
        assertThat(map1).hasSize(2);
        assertThat(map1.get("type")).isEqualTo("foo");
        assertThat(map1.get("value")).isEqualTo("123.456");

        JSONBuilder jb = new JSONBuilder();
        jb.put("type", "bar");
        jb.put("value", "654.321");
        Identifier id2 = new Identifier(jb.toJSON());
        assertThat(id2.getType()).isEqualTo("bar");
        assertThat(id2.getValue()).isEqualTo("654.321");
        assertThat(id2.toString()).isEqualTo("bar=654.321");
        Map<String, Object> map2 = id2.toMap();
        assertThat(map2).hasSize(2);
        assertThat(map2.get("type")).isEqualTo("bar");
        assertThat(map2.get("value")).isEqualTo("654.321");
    }

    @Test
    public void testDns() {
        Identifier id1 = Identifier.dns("example.com");
        assertThat(id1.getType()).isEqualTo(Identifier.TYPE_DNS);
        assertThat(id1.getValue()).isEqualTo("example.com");
        assertThat(id1.getDomain()).isEqualTo("example.com");

        Identifier id2 = Identifier.dns("ëxämþlë.com");
        assertThat(id2.getType()).isEqualTo(Identifier.TYPE_DNS);
        assertThat(id2.getValue()).isEqualTo("xn--xml-qla7ae5k.com");
        assertThat(id2.getDomain()).isEqualTo("xn--xml-qla7ae5k.com");
    }

    @Test
    public void testNoDns() {
        assertThrows(AcmeProtocolException.class, () ->
            new Identifier("foo", "example.com").getDomain()
        );
    }

    @Test
    public void testIp() throws UnknownHostException {
        Identifier id1 = Identifier.ip(InetAddress.getByName("192.168.1.2"));
        assertThat(id1.getType()).isEqualTo(Identifier.TYPE_IP);
        assertThat(id1.getValue()).isEqualTo("192.168.1.2");
        assertThat(id1.getIP().getHostAddress()).isEqualTo("192.168.1.2");

        Identifier id2 = Identifier.ip(InetAddress.getByName("2001:db8:85a3::8a2e:370:7334"));
        assertThat(id2.getType()).isEqualTo(Identifier.TYPE_IP);
        assertThat(id2.getValue()).isEqualTo("2001:db8:85a3:0:0:8a2e:370:7334");
        assertThat(id2.getIP().getHostAddress()).isEqualTo("2001:db8:85a3:0:0:8a2e:370:7334");

        Identifier id3 = Identifier.ip("192.168.2.99");
        assertThat(id3.getType()).isEqualTo(Identifier.TYPE_IP);
        assertThat(id3.getValue()).isEqualTo("192.168.2.99");
        assertThat(id3.getIP().getHostAddress()).isEqualTo("192.168.2.99");
    }

    @Test
    public void testNoIp() {
        assertThrows(AcmeProtocolException.class, () ->
            new Identifier("foo", "example.com").getIP()
        );
    }

    @Test
    public void testEquals() {
        Identifier idRef = new Identifier("foo", "123.456");

        Identifier id1 = new Identifier("foo", "123.456");
        assertThat(idRef.equals(id1)).isTrue();

        Identifier id2 = new Identifier("bar", "654.321");
        assertThat(idRef.equals(id2)).isFalse();

        Identifier id3 = new Identifier("foo", "555.666");
        assertThat(idRef.equals(id3)).isFalse();

        Identifier id4 = new Identifier("sna", "123.456");
        assertThat(idRef.equals(id4)).isFalse();

        assertThat(idRef.equals(new Object())).isFalse();
        assertThat(idRef.equals(null)).isFalse();
    }

    @Test
    public void testNull() {
        assertThrows(NullPointerException.class, () -> new Identifier(null, "123.456"));
        assertThrows(NullPointerException.class, () -> new Identifier("foo", null));
    }

}
