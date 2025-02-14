/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.net;

import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.MDC;
import org.apache.log4j.helpers.LogLog;

/**
 * This SocketServer exits after certain number of connections from a client.
 * This number is determined the totalsTest parameter, that is the first
 * argument on the commmand line. The second argument, prefix, determines the
 * prefix of the configuration file to use. Each run of the server will use a
 * different properties file. For the i-th run, the path to the file is
 * (prefix+i+".properties").
 *
 * @author Ceki Gulcu
 */

public class ShortSocketServer implements Runnable {

	static Logger cat = Logger.getLogger(ShortSocketServer.class);

	public static void main(String args[]) throws Exception {
		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		int argTotalTests = 0;
		String argPrefix = null;

		if (args.length == 2) {
			argTotalTests = Integer.parseInt(args[0]);
			argPrefix = args[1];
		} else {
			usage("Wrong number of arguments.");
		}

		new ShortSocketServer(argTotalTests, argPrefix).runServer();
	}

	final int totalTests;
	final String prefix;
	
	ShortSocketServer(int totalTests, String prefix) {
		this.totalTests = totalTests;
		this.prefix = prefix;
	}
	
	void runServer() throws IOException, InterruptedException {
		LogLog.debug("Listening on port " + SocketServerTestCase.PORT);
		ServerSocket serverSocket = new ServerSocket(SocketServerTestCase.PORT);

		MDC.put("hostID", "shortSocketServer");

		for (int i = 1; i <= totalTests; i++) {
			PropertyConfigurator.configure(prefix + i + ".properties");
			LogLog.debug("Waiting to accept a new client.");
			Socket socket = serverSocket.accept();
			LogLog.debug("Connected to client at " + socket.getInetAddress());
			LogLog.debug("Starting new socket node.");
			SocketNode sn = new SocketNode(socket, LogManager.getLoggerRepository());
			Thread t = new Thread(sn);
			t.start();
			t.join();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	static void usage(String msg) {
		System.err.println(msg);
		System.err.println("Usage: java " + ShortSocketServer.class.getName() + " totalTests configFilePrefix");
		System.exit(1);
	}


}
