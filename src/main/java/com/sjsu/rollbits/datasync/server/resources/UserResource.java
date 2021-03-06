package com.sjsu.rollbits.datasync.server.resources;

import java.util.List;

import org.apache.log4j.Logger;

import com.sjsu.rollbits.dao.interfaces.model.User;
import com.sjsu.rollbits.dao.interfaces.service.Service;
import com.sjsu.rollbits.datasync.client.MessageClient;
import com.sjsu.rollbits.sharding.hashing.Message;
import com.sjsu.rollbits.sharding.hashing.RNode;
import com.sjsu.rollbits.sharding.hashing.ShardingService;

import io.netty.channel.Channel;
import routing.Pipe;
import routing.Pipe.Route;

public class UserResource implements RouteResource {
	protected static Logger logger = Logger.getLogger("UserResource");

	private ShardingService shardingService;

	private Service dbService = null;

	public UserResource() {
		shardingService = ShardingService.getInstance();
		dbService = new Service();
	}

	@Override
	public Pipe.Route.Path getPath() {
		return Pipe.Route.Path.USER;
	}

	@Override
	public Object process(Pipe.Route msg, Channel returnChannel) {
		boolean success = false;
		Pipe.User.ActionType option = msg.getUser().getAction();

		switch (option) {
		case ACCESS:

			break;

		case REGISTER:
			Pipe.User user = msg.getUser();

			Pipe.Header header = msg.getHeader();

			if (header != null && header.getType() != null && !header.getType().equals(Pipe.Header.Type.INTERNAL)) {

				List<RNode> nodes = shardingService.getNodes(new Message(user.getUname()));

				// save to database

				for (RNode node : nodes) {
					MessageClient mc = new MessageClient(node.getIpAddress(), (int) node.getPort());
					if (node.getType().equals(RNode.Type.REPLICA)) {
						if (mc.isConnected())

							mc.addUser(user.getUname(), user.getEmail(), RollbitsConstants.INTERNAL, true);
					} else {
						if (mc.isConnected())

							success = mc.addUser(user.getUname(), user.getEmail(), RollbitsConstants.INTERNAL, false);

					}

				}

			} else {

				logger.info("Adding to database");
				User dbuser = new User(user.getUname(), user.getEmail());

				dbService.persist(dbuser);
				success = true;

			}

			// below line can be moved to client

			// send request for sync to other intra cluster nodes

			// wait for result
			// send response back

			// Does it have to be blocking?

			break;

		}

		Route.Builder rb = ProtoUtil.createResponseRoute(msg.getId(), success, null,
				success ? RollbitsConstants.SUCCESS : RollbitsConstants.FAILED);

		return rb;
	}

}
