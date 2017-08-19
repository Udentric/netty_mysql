/*
 * Copyright (c) 2017 Alex Dubov <oakad@yahoo.com>
 *
 * This file is made available under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package udentric.mysql.classic;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import udentric.mysql.classic.auth.CredentialsProvider;

public class Client extends ChannelInitializer<SocketChannel> {
	public static class Builder {
		public Builder withCredentials(CredentialsProvider creds_) {
			creds = creds_;
			return this;
		}

		public Client build() {
			return new Client(this);
		}

		private CredentialsProvider creds;
	}

	public static Builder builder() {
		return new Builder();
	}

	private Client(Builder bld) {
		creds = bld.creds;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(
			"mysql.packet.in", new InboundPacketFramer()
		).addLast(
			"mysql.protocol", new ProtocolHandler(this)
		);
	}

	final CredentialsProvider creds;
}