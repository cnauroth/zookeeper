/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zookeeper.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A watch report, essentially a mapping of session ID to paths that the session
 * has set a watch on. This class is immutable.
 */
public class WatchesReport {

    private final Map<Long, Set<String>> id2DataPaths;
    private final Map<Long, Set<String>> id2ChildPaths;

    /**
     * Creates a new report.
     *
     * @param id2DataPaths map of session IDs to paths that each session has set
     * a watch on for data watches
     * @param id2ChildPaths map of session IDs to paths that each session has
     * set a watch on for child watches
     */
    WatchesReport(Map<Long, Set<String>> id2DataPaths,
            Map<Long, Set<String>> id2ChildPaths) {
        this.id2DataPaths = Collections.unmodifiableMap(deepCopy(id2DataPaths,
                false));
        this.id2ChildPaths = Collections.unmodifiableMap(deepCopy(id2ChildPaths,
                true));
    }

    private static Map<Long, Set<String>> deepCopy(Map<Long, Set<String>> m,
            boolean appendSlash) {
        Map<Long, Set<String>> m2 = new HashMap<Long, Set<String>>();
        for (Map.Entry<Long, Set<String>> e : m.entrySet()) {
            final Set<String> paths;
            if (appendSlash) {
                paths = new HashSet<>(e.getValue().size(), 1.0f);
                for (String path : e.getValue()) {
                    paths.add(path + "/");
                }
            } else {
                paths = new HashSet<>(e.getValue());
            }
            m2.put(e.getKey(), paths);
        }
        return m2;
    }

    /**
     * Checks if the given session has watches set.
     *
     * @param sessionId session ID
     * @return true if session has paths with watches set
     */
    public boolean hasPaths(long sessionId) {
        return id2DataPaths.containsKey(sessionId) ||
                id2ChildPaths.containsKey(sessionId);
    }

    /**
     * Gets the paths that the given session has set watches on. The returned
     * set is immutable.
     *
     * @param sessionId session ID
     * @return paths that have watches set by the session, or null if none
     */
    public Set<String> getPaths(long sessionId) {
        Set<String> s = id2DataPaths.get(sessionId);
        return s != null ? Collections.unmodifiableSet(s) : null;
    }

    /**
     * Converts this report to a map. The returned map is mutable, and changes
     * to it do not reflect back into this report.
     *
     * @return map representation of report
     */
    public Map<Long, Set<String>> toMap() {
        Map<Long, Set<String>> map = new LinkedHashMap<Long, Set<String>>(
                id2DataPaths.size() + id2ChildPaths.size(), 1.0f);
        map.putAll(deepCopy(id2DataPaths, false));
        map.putAll(deepCopy(id2ChildPaths, false));
        return map;
    }
}
