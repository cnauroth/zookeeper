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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A summary of watch information. This class is immutable.
 */
public class WatchesSummary {

    /**
     * The key in the map returned by {@link #toMap()} for the number of
     * connections.
     */
    public static final String KEY_NUM_CONNECTIONS = "num_connections";
    /**
     * The key in the map returned by {@link #toMap()} for the number of paths.
     */
    public static final String KEY_NUM_PATHS = "num_paths";
    /**
     * The key in the map returned by {@link #toMap()} for the number of child
     * watches.
     */
    public static final String KEY_NUM_CHILD_WATCHES = "num_child_watches";
    /**
     * The key in the map returned by {@link #toMap()} for the number of data
     * watches.
     */
    public static final String KEY_NUM_DATA_WATCHES = "num_data_watches";
    /**
     * The key in the map returned by {@link #toMap()} for the total number of
     * watches.
     */
    public static final String KEY_NUM_TOTAL_WATCHES = "num_total_watches";

    private final int childWatches;
    private final int dataWatches;
    private final int numConnections;
    private final int numPaths;
    private final int totalWatches;

    /**
     * Creates a new summary.
     *
     * @param numConnections the number of sessions that have set watches
     * @param numPaths the number of paths that have watches set on them
     * @param totalWatches the total number of watches set
     */
    WatchesSummary(int numConnections, int numPaths, int totalWatches) {
        this.childWatches = 0;
        this.dataWatches = 0;
        this.numConnections = numConnections;
        this.numPaths = numPaths;
        this.totalWatches = totalWatches;
    }

    /**
     * Creates a new summary by combining two previously created summaries for
     * data watches and child watches.
     *
     * @param dataSummary summary for data watches
     * @param childSummary summary for child watches
     */
    WatchesSummary(WatchesSummary dataSummary, WatchesSummary childSummary) {
        this.childWatches = childSummary.totalWatches;
        this.dataWatches = dataSummary.totalWatches;
        this.numConnections = dataSummary.numConnections +
                childSummary.numConnections;
        this.numPaths = dataSummary.numPaths + childSummary.numPaths;
        this.totalWatches = dataSummary.totalWatches +
                childSummary.totalWatches;
    }

    /**
     * Gets the number of child watches set.
     *
     * @return child watches
     */
    public int getChildWatches() {
        return childWatches;
    }
    /**
     * Gets the number of data watches set.
     *
     * @return data watches
     */
    public int getDataWatches() {
        return dataWatches;
    }
    /**
     * Gets the number of connections (sessions) that have set watches.
     *
     * @return number of connections
     */
    public int getNumConnections() {
        return numConnections;
    }
    /**
     * Gets the number of paths that have watches set on them.
     *
     * @return number of paths
     */
    public int getNumPaths() {
        return numPaths;
    }
    /**
     * Gets the total number of watches set.
     *
     * @return total watches
     */
    public int getTotalWatches() {
        return totalWatches;
    }

    /**
     * Converts this summary to a map. The returned map is mutable, and changes
     * to it do not reflect back into this summary.
     *
     * @return map representation of summary
     */
    public Map<String, Object> toMap() {
        Map<String, Object> summary = new LinkedHashMap<String, Object>();
        summary.put(KEY_NUM_CHILD_WATCHES, childWatches);
        summary.put(KEY_NUM_DATA_WATCHES, dataWatches);
        summary.put(KEY_NUM_CONNECTIONS, numConnections);
        summary.put(KEY_NUM_PATHS, numPaths);
        summary.put(KEY_NUM_TOTAL_WATCHES, totalWatches);
        return summary;
    }
}
