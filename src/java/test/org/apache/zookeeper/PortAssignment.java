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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Assign ports to tests */
public final class PortAssignment {
    private static final Logger LOG = LoggerFactory.getLogger(PortAssignment.class);

    private static final int GLOBAL_BASE_PORT = 11221;
    private static final int GLOBAL_MAX_PORT = 65535;

    private static final int BASE_PORT;
    private static final int MAX_PORT;

    private static int nextPort;

    static {
        // The count of JUnit processes is passed from Ant as a system property.
        Integer processCount = null;
        String strProcessCount = System.getProperty("test.junit.threads");
        if (strProcessCount != null && !strProcessCount.isEmpty()) {
            try {
                processCount = Integer.valueOf(strProcessCount);
            } catch (NumberFormatException e) {
                LOG.warn("Error parsing test.junit.threads = {}.",
                         strProcessCount, e);
            }
        }

        // Ant's JUnit runner receives the thread ID as a command line argument
        // of the form threadid=N, where N is an integer in the range
        // [1, ${test.junit.threads}].  It's not otherwise accessible, so we need
        // to parse it from the command line.
        Integer threadId = null;
        if (processCount != null) {
            String cmdLine = System.getProperty("sun.java.command");
            if (cmdLine != null && !cmdLine.isEmpty()) {
                Matcher m = Pattern.compile("threadid=(\\d+)").matcher(cmdLine);
                if (m.find()) {
                    try {
                        threadId = Integer.valueOf(m.group(1));
                    } catch (NumberFormatException e) {
                        LOG.warn("Error parsing threadid from {}.", cmdLine, e);
                    }
                }
            }
        }

        if (processCount != null && processCount > 1 && threadId != null) {
            // We know the total JUnit process count and this test process's ID.
            // Use these values to calculate the valid range for port assignments
            // within this test process.
            int portRangeSize = (GLOBAL_MAX_PORT - GLOBAL_BASE_PORT) /
                    processCount;
            BASE_PORT = GLOBAL_BASE_PORT + ((threadId - 1) * portRangeSize);
            MAX_PORT = BASE_PORT + portRangeSize - 1;
            LOG.info("Test process {}/{} using port range {} - {}.", threadId,
                    processCount, BASE_PORT, MAX_PORT);
        } else {
            // If running outside the context of ant, then use all valid ports.
            BASE_PORT = GLOBAL_BASE_PORT;
            MAX_PORT = GLOBAL_MAX_PORT;
            LOG.info("Single test process using port range {} - {}.", BASE_PORT,
                    MAX_PORT);
        }

        nextPort = BASE_PORT;
    }

    /**
     * Assign a new, unique port to the test.  This method works by assigning
     * ports from a valid port range as identified by the total number of
     * concurrent test processes and the ID of this test process.  Each
     * concurrent test process uses an isolated range, so it's not possible for
     * multiple test processes to collide on the same port.  Within the port
     * range, ports are assigned in monotonic increasing order, wrapping around
     * to the beginning of the range if needed.  As an extra precaution, the
     * method attempts to bind to the port and immediately close it before
     * returning it to the caller.  If the port cannot be bound, then it tries
     * the next one in the range.  This provides some resiliency in case the port
     * is otherwise occupied, such as a developer running other servers on the
     * machine running the tests.
     *
     * @return port
     */
    public synchronized static int unique() {
        int candidatePort = nextPort;
        for (;;) {
            ++candidatePort;
            if (candidatePort > MAX_PORT) {
                candidatePort = BASE_PORT;
            }
            if (candidatePort == nextPort) {
                throw new IllegalStateException(String.format(
                        "Could not assign port from range %d - %d.  The entire "
                        + "range has been exhausted.", BASE_PORT, MAX_PORT));
            }
            try {
                ServerSocket s = new ServerSocket(candidatePort);
                s.close();
                nextPort = candidatePort;
                LOG.info("Assigning port {} from range {} - {}.", nextPort,
                         BASE_PORT, MAX_PORT);
                return nextPort;
            } catch (IOException e) {
                LOG.debug("Could not bind to port {} from range {} - {}.  "
                        + "Attempting next port.", BASE_PORT, MAX_PORT, e);
            }
        }
    }

    /**
     * There is no reason to instantiate this class.
     */
    private PortAssignment() {
    }
}
