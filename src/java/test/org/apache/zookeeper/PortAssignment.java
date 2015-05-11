/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zookeeper;

import java.io.IOException;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Assign ports to tests */
public final class PortAssignment {
    private static final Logger LOG = LoggerFactory.getLogger(PortAssignment.class);

    /**
     * Assign a new, unique port to the test.  This method works by creating a
     * server socket on port 0 so that the OS assigns an unused ephemeral port.
     * The server socket gets closed immediately and the actual port that had
     * been bound is returned.  Since the port is no longer bound after this
     * method returns, there is a brief race condition window between the test
     * binding to the port and possibly other concurrent tests calling this
     * method.  It is assumed that tests will bind to the port quickly and other
     * tests won't run so rapidly that the OS recycles a port that is assigned to
     * a test but unbound.
     *
     * @return port
     */
    public static int unique() {
        try {
            ServerSocket s = new ServerSocket(0);
            int port = s.getLocalPort();
            s.close();
            LOG.info("assigning port " + port);
            return port;
        } catch (IOException e) {
            throw new IllegalStateException("Could not assign port.");
        }
    }

    /**
     * There is no reason to instantiate this class.
     */
    private PortAssignment() {
    }
}
