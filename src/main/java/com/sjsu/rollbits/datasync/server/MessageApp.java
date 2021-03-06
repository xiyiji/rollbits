/**
 * Copyright 2016 Gash.
 *
 * This file and intellectual content is protected under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.sjsu.rollbits.datasync.server;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.sjsu.rollbits.discovery.UdpClient;
import com.sjsu.rollbits.discovery.UdpServer;
import com.sjsu.rollbits.raft.RaftContext;
import com.sjsu.rollbits.yml.Loadyaml;

/**
 * @author gash1
 * 
 */
public class MessageApp {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// if (args.length == 0) {
		// System.out.println("usage: server <config file>");
		// System.exit(1);
		// }

		Logger l = Logger.getLogger("MessageApp");
		l.info("Starting app");

		PropertyConfigurator.configure("./src/main/resources/log4j.properties");

		Loadyaml.loadYaml();
		Thread t = new Thread(new UdpServer());
		t.start();
		Thread.sleep(4 * 1000L);
		UdpClient.broadcast();
		// Thread.sleep(15 * 1000L);
		RaftContext.getInstance();// To Start Raft Engine

		Thread t2 = new Thread(ServerRequestQueue.getInstance());
		t2.start();

		File cf = new File("./src/main/resources/routing.conf");
		try {
			MessageServer svr = new MessageServer(cf);
			svr.startServer();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// System.out.println("server closing");
		}
	}
}
