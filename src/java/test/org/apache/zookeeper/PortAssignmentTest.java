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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.Test;

@RunWith(Parameterized.class)
public class PortAssignmentTest {

    private final String strProcessCount;
    private final String cmdLine;
    private final int expectedMinimumPort;
    private final int expectedMaximumPort;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.<Object[]>asList(
                new Object[] { "8", "threadid=1", 11221, 18009 },
                new Object[] { "8", "threadid=2", 18010, 24798 },
                new Object[] { "8", "threadid=3", 24799, 31587 },
                new Object[] { "8", "threadid=4", 31588, 38376 },
                new Object[] { "8", "threadid=5", 38377, 45165 },
                new Object[] { "8", "threadid=6", 45166, 51954 },
                new Object[] { "8", "threadid=7", 51955, 58743 },
                new Object[] { "8", "threadid=8", 58744, 65532 },
                new Object[] { "1", "threadid=1", 11221, 65535 },
                new Object[] { "2", "threadid=1", 11221, 38377 },
                new Object[] { "2", "threadid=2", 38378, 65534 },
                new Object[] { null, null, 11221, 65535 },
                new Object[] { null, null, 11221, 65535 },
                new Object[] { "", "", 11221, 65535 },
                new Object[] { "", "", 11221, 65535 });
    }

    public PortAssignmentTest(String strProcessCount, String cmdLine,
            int expectedMinimumPort, int expectedMaximumPort) {
        this.strProcessCount = strProcessCount;
        this.cmdLine = cmdLine;
        this.expectedMinimumPort = expectedMinimumPort;
        this.expectedMaximumPort = expectedMaximumPort;
    }

    @Test
    public void testSetupPortRange() {
        PortAssignment.PortRange portRange = PortAssignment.setupPortRange(
                strProcessCount, cmdLine);
        assertEquals(buildAssertionMessage("minimum"), expectedMinimumPort,
                portRange.getMinimum());
        assertEquals(buildAssertionMessage("maximum"), expectedMaximumPort,
                portRange.getMaximum());
    }

    private String buildAssertionMessage(String checkType) {
        return String.format("strProcessCount = %s, cmdLine = %s, checking %s",
                strProcessCount, cmdLine, checkType);
    }
}
