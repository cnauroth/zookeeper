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
 * A watch report, essentially a mapping of path to session IDs of sessions that
 * have set a watch on that path. This class is immutable.
 */
public class WatchesPathReport {

    private final Map<String, Set<Long>> dataPath2Ids;
    private final Map<String, Set<Long>> childPath2Ids;

    /**
     * Creates a new report.
     *
     * @param dataPath2Ids map of paths to session IDs of sessions that have set
     * a watch on that path
     * @param childPath2Ids map of paths to session IDs of sessions that have
     * set a watch on that path
     */
    WatchesPathReport(Map<String, Set<Long>> dataPath2Ids,
            Map<String, Set<Long>> childPath2Ids) {
        this.dataPath2Ids = Collections.unmodifiableMap(deepCopy(dataPath2Ids,
                false));
        this.childPath2Ids = Collections.unmodifiableMap(deepCopy(
                childPath2Ids, true));
    }

    private static Map<String, Set<Long>> deepCopy(Map<String, Set<Long>> m,
            boolean appendSlash) {
        Map<String, Set<Long>> m2 = new HashMap<String, Set<Long>>();
        for (Map.Entry<String, Set<Long>> e : m.entrySet()) {
            String path = appendSlash ? e.getKey() + "/" : e.getKey();
            m2.put(path, new HashSet<Long>(e.getValue()));
        }
        return m2;
    }

    /**
     * Checks if the given path has watches set.
     *
     * @param path path
     * @return true if path has watch set
     */
    public boolean hasSessions(String path) {
        if (path == null) {
            return false;
        }
        return path.endsWith("/") ?
                childPath2Ids.containsKey(path.substring(0, path.length() - 1)) :
                dataPath2Ids.containsKey(path);
    }

    /**
     * Gets the session IDs of sessions that have set watches on the given path.
     * The returned set is immutable.
     *
     * @param path session ID
     * @return session IDs of sessions that have set watches on the path, or
     * null if none
     */
    public Set<Long> getSessions(String path) {
        if (path == null) {
            return null;
        }
        Set<Long> s = path.endsWith("/") ?
                childPath2Ids.get(path.substring(0, path.length() - 1)) :
                dataPath2Ids.get(path);
        return s != null ? Collections.unmodifiableSet(s) : null;
    }

    /**
     * Converts this report to a map. The returned map is mutable, and changes
     * to it do not reflect back into this report.
     *
     * @return map representation of report
     */
    public Map<String, Set<Long>> toMap() {
        Map<String, Set<Long>> map = new LinkedHashMap<String, Set<Long>>(
                dataPath2Ids.size() + childPath2Ids.size(), 1.0f);
        map.putAll(deepCopy(dataPath2Ids, false));
        map.putAll(deepCopy(childPath2Ids, false));
        return map;
    }
}
