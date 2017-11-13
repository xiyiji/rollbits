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
package com.sjsu.rollbits.datasync.client;

import java.util.concurrent.ConcurrentHashMap;

import routing.Pipe;
import routing.Pipe.Route;

/**
 * front-end (proxy) to our service - functional-based
 * 
 * @author gash
 * 
 */
public class MessageClient {
	// track requests
	private long curID = 0;

	ConcurrentHashMap<Long, Route> requestResponseMap = new ConcurrentHashMap<>();

	public MessageClient(String host, int port) {
		init(host, port);
	}

	private void init(String host, int port) {
		CommConnection.initConnection(host, port);
	}

	public void addListener(CommListener listener) {
		CommConnection.getInstance().addListener(listener);
	}

	public void ping() {
		// construct the message to send
		Route.Builder rb = Route.newBuilder();
		rb.setId(nextId());
		rb.setPath(Route.Path.PING);
		rb.setPayload("ping");

		try {
			// direct no queue
			 CommConnection.getInstance().write(rb.build());

			// using queue
			//CommConnection.getInstance().enqueue(rb.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void postMessage(String msg) {
		// construct the message to send
		Route.Builder rb = Route.newBuilder();
		rb.setId(nextId());
		rb.setPath(Route.Path.MSG);
		rb.setPayload(msg);

		try {
			CommConnection.getInstance().enqueue(rb.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	//
	public Route sendSyncronousMessage(Route.Builder msg) {
		// construct the message to send

		Route response = null;
		long id = nextId();
		msg.setId(id);
		
		
		try {
			
			this.addListener(new CommListener() {

				@Override
				public void onMessage(Route msg) {
					System.out.println("Recieved Message");
					requestResponseMap.put(msg.getId(), msg);
				}

				@Override
				public String getListenerID() {
					// TODO Auto-generated method stub
					return "userresource";
				}
			});
			
			CommConnection.getInstance().write(msg.build());

			

			while (true) {

				Thread.sleep(20000);
				System.out.println("Checking..... whether we recieved response!!!! for requestId" + id);
				if (requestResponseMap.get(id) != null) {
					response = requestResponseMap.get(id);
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	// Send already formed message
	public void postOnQueue(Route.Builder msg) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean addUser(String name, String email, boolean internal, boolean async) {
		// construct the message to send
		boolean added = false;
		Route.Builder rb = Route.newBuilder();
		rb.setId(nextId());
		rb.setPath(Route.Path.USER);
		// rb.setAction(routing.Pipe.actionType.PUT);
		Pipe.User.Builder ub = Pipe.User.newBuilder();
		ub.setEmail(email);
		ub.setUname(name);
		ub.setAction(routing.Pipe.actionType.PUT);
		rb.setUser(ub);

		Pipe.Header.Builder header = Pipe.Header.newBuilder();

		if (internal) {
			header.setType("INTERNAL");

		} else {
			header.setType("EXTERNAL");

		}
		rb.setHeader(header);
		CommConnection conn = CommConnection.getInstance();

		try {
			if (async)
				conn.enqueue(rb.build());
			else
				added = conn.write(rb.build());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// conn.release();

		}
		return added;
	}

	public boolean addGroup(String gname, int gid, boolean internal, boolean async) {
		// TODO Auto-generated method stub
		boolean added = false;
		Route.Builder rb = Route.newBuilder();
		rb.setId(nextId());
		rb.setPath(Route.Path.GROUP);
		// rb.setAction(routing.Pipe.actionType.PUT);
		Pipe.Group.Builder gb = Pipe.Group.newBuilder();
		gb.setGname(gname);
		gb.setGid(gid);
		gb.setAction(routing.Pipe.actionType.PUT);
		rb.setGroup(gb);

		Pipe.Header.Builder header = Pipe.Header.newBuilder();

		if (internal) {
			header.setType("INTERNAL");

		} else {
			header.setType("EXTERNAL");

		}
		rb.setHeader(header);
		CommConnection conn = CommConnection.getInstance();

		try {
			if (async)
				conn.enqueue(rb.build());
			else
				added = conn.write(rb.build());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// conn.release();

		}
		return added;

	}

	
	public boolean sendMessage(String fromUserId, String toUserId, int type, String message, boolean internal,
			boolean async) {
		// construct the message to send
		boolean added = false;

		Route.Builder rb = Route.newBuilder();
		rb.setId(nextId());
		rb.setPath(Route.Path.MESSAGE);
		Pipe.Message.Builder msg = Pipe.Message.newBuilder();
		msg.setMessage(message);
		msg.setFromuname(fromUserId);
		msg.setTouname(toUserId);

		msg.setAction(routing.Pipe.actionType.PUT);
		rb.setMessage(msg);
		Pipe.Header.Builder header = Pipe.Header.newBuilder();

		if (internal) {
			header.setType("INTERNAL");

		} else {
			header.setType("EXTERNAL");

		}
		rb.setHeader(header);
		CommConnection conn = CommConnection.getInstance();
		try {
			if (async)
				conn.enqueue(rb.build());
			else
				added = conn.write(rb.build());
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			// conn.release();
		}
		return added;

	}

	public void release() {
		CommConnection.getInstance().release();
	}

	/**
	 * Since the service/server is asychronous we need a unique ID to associate our
	 * requests with the server's reply
	 * 
	 * @return
	 */
	private synchronized long nextId() {
		return ++curID;
	}

	public boolean sendProto(Route.Builder routeBuilder) {
		CommConnection conn = CommConnection.getInstance();
		routeBuilder.setId(nextId());
		return conn.write(routeBuilder.build());
	}
}
