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

package org.apache.zookeeper.test;

import static org.apache.zookeeper.client.FourLetterWordMain.send4LetterWord;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.PortAssignment;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.test.ClientBase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class WatchesAdminCommandsTest extends ClientBase {

    private static final int jettyAdminPort = PortAssignment.unique();

    private HostPort hostPortObj = null;

    @Rule
    public Timeout timeout = new Timeout(30000);

    @Before
    public void setUp() throws Exception {
        System.setProperty("zookeeper.admin.enableServer", "true");
        System.setProperty("zookeeper.admin.serverPort", "" + jettyAdminPort);
        super.setUp();
        hostPortObj = ClientBase.parseHostPortList(hostPort).get(0);

        ZooKeeper zk1 = createClient();
        createZnode(zk1, "/1");
        setDataWatch(zk1, "/1");
        createZnode(zk1, "/2");
        setDataWatch(zk1, "/2");
        createZnode(zk1, "/3");
        setChildWatch(zk1, "/3");
        createZnode(zk1, "/4");
        setChildWatch(zk1, "/4");

        ZooKeeper zk2 = createClient();
        createZnode(zk2, "/5");
        setDataWatch(zk2, "/5");
        createZnode(zk2, "/6");
        setDataWatch(zk2, "/6");
        createZnode(zk2, "/7");
        setChildWatch(zk2, "/7");
        createZnode(zk2, "/8");
        setChildWatch(zk2, "/8");
    }

    @Test
    public void testWchs() throws Exception {
        try (BufferedReader br = run4LetterWord("wchs")) {
            assertEquals("Data watches", br.readLine());
            assertEquals("2 connections watching 4 paths", br.readLine());
            assertEquals("Total watches:4", br.readLine());
            assertEquals("Children watches", br.readLine());
            assertEquals("2 connections watching 4 paths", br.readLine());
            assertEquals("Total watches:4", br.readLine());
            assertNull(br.readLine());
        }
    }

    @Test
    public void testWchc() throws Exception {
    }

    @Test
    public void testWchp() throws Exception {
    }

    private BufferedReader run4LetterWord(String cmd) throws Exception {
        String resp = send4LetterWord(hostPortObj.host, hostPortObj.port, cmd);
        return new BufferedReader(new StringReader(resp));
    }

    /**
     * Create a znode.
     *
     * @param zk ZooKeeper client to perform creation
     * @param path znode to create
     */
    private static void createZnode(ZooKeeper zk, String path)
                throws Exception {
        zk.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    /**
     * Sets a child watch on a znode.
     *
     * @param zk ZooKeeper client to set the watch
     * @param path znode to watch
     */
    private static void setChildWatch(ZooKeeper zk, String path)
                throws Exception {
        zk.getChildren(path, true);
    }

    /**
     * Sets a data watch on a znode.
     *
     * @param zk ZooKeeper client to set the watch
     * @param path znode to watch
     */
    private static void setDataWatch(ZooKeeper zk, String path)
                throws Exception {
        zk.exists(path, true);
    }
}
