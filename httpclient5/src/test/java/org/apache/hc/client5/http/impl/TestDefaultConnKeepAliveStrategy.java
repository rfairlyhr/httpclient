/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.hc.client5.http.impl;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.apache.hc.core5.http.message.BasicStatusLine;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.Assert;
import org.junit.Test;

/**
 *  Simple tests for {@link DefaultConnectionKeepAliveStrategy}.
 */
public class TestDefaultConnKeepAliveStrategy {

    @Test(expected=IllegalArgumentException.class)
    public void testIllegalResponseArg() throws Exception {
        final HttpContext context = new BasicHttpContext(null);
        final ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy();
        keepAliveStrat.getKeepAliveDuration(null, context);
    }

    @Test
    public void testNoKeepAliveHeader() throws Exception {
        final HttpContext context = new BasicHttpContext(null);
        final HttpResponse response = new BasicHttpResponse(
                new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        final ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy();
        final long d = keepAliveStrat.getKeepAliveDuration(response, context);
        Assert.assertEquals(-1, d);
    }

    @Test
    public void testEmptyKeepAliveHeader() throws Exception {
        final HttpContext context = new BasicHttpContext(null);
        final HttpResponse response = new BasicHttpResponse(
                new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        response.addHeader("Keep-Alive", "timeout, max=20");
        final ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy();
        final long d = keepAliveStrat.getKeepAliveDuration(response, context);
        Assert.assertEquals(-1, d);
    }

    @Test
    public void testInvalidKeepAliveHeader() throws Exception {
        final HttpContext context = new BasicHttpContext(null);
        final HttpResponse response = new BasicHttpResponse(
                new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        response.addHeader("Keep-Alive", "timeout=whatever, max=20");
        final ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy();
        final long d = keepAliveStrat.getKeepAliveDuration(response, context);
        Assert.assertEquals(-1, d);
    }

    @Test
    public void testKeepAliveHeader() throws Exception {
        final HttpContext context = new BasicHttpContext(null);
        final HttpResponse response = new BasicHttpResponse(
                new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        response.addHeader("Keep-Alive", "timeout=300, max=20");
        final ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy();
        final long d = keepAliveStrat.getKeepAliveDuration(response, context);
        Assert.assertEquals(300000, d);
    }

}
