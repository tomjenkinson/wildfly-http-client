/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2023 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.httpclient.naming;

import io.undertow.server.HttpServerExchange;
import org.jboss.ejb.client.Affinity;
import org.jboss.ejb.client.URIAffinity;
import org.jboss.marshalling.ObjectResolver;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public final class HttpNamingServerObjectResolver implements ObjectResolver {
    private URIAffinity selfNodeAffinity;

    public HttpNamingServerObjectResolver(HttpServerExchange exchange) {
        try {
            selfNodeAffinity = createLocalURIAffinity(exchange);
        } catch (URISyntaxException ignored) {
        }
    }

    private URIAffinity createLocalURIAffinity(HttpServerExchange exchange) throws URISyntaxException {
        InetSocketAddress localAddress = (InetSocketAddress) exchange.getConnection().getLocalAddress();
        StringBuilder uriStringBuilder = new StringBuilder();
        uriStringBuilder.append(exchange.getRequestScheme()).append("://")
                .append(localAddress.getHostName()).append(":").append(localAddress.getPort())
                .append("/wildfly-services");

        System.out.println("HELLOTOM: " + uriStringBuilder.toString());
        return new URIAffinity(new URI(uriStringBuilder.toString()));
    }

    public Object readResolve(final Object replacement) {
        return replacement;
    }

    public Object writeReplace(final Object original) {
        if (original == Affinity.LOCAL && selfNodeAffinity != null) {
            return selfNodeAffinity;
        }
        return original;
    }
}