/*
 * Copyright (c) 2017 Alex Dubov <oakad@yahoo.com>
 *
 * This file is made available under the GNU General Public License
 * version 2 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
/*
 * May contain portions of MySQL Connector/J implementation
 *
 * Copyright (c) 2002, 2017, Oracle and/or its affiliates. All rights reserved.
 *
 * The MySQL Connector/J is licensed under the terms of the GPLv2
 * <http://www.gnu.org/licenses/old-licenses/gpl-2.0.html>, like most MySQL
 * Connectors. There are special exceptions to the terms and conditions of
 * the GPLv2 as it is applied to this software, see the FOSS License Exception
 * <http://www.mysql.com/about/legal/licensing/foss-exception.html>.
 */

package udentric.mysql.classic.type.binary;

import java.nio.CharBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import udentric.mysql.classic.FieldImpl;
import udentric.mysql.classic.Packet;
import udentric.mysql.classic.type.AdapterState;
import udentric.mysql.classic.type.ValueAdapter;
import udentric.mysql.classic.type.TypeId;

public class AnyString implements ValueAdapter<String> {
	public AnyString(TypeId id_) {
		id = id_;
	}

	@Override
	public TypeId typeId() {
		return id;
	}

	@Override
	public void encodeValue(
		ByteBuf dst, String value, AdapterState state,
		int bufSoftLimit, FieldImpl fld
	) {
		ByteBuf valBuf = state.get();
		if (valBuf == null) {
			valBuf = ByteBufUtil.encodeString(
				state.alloc, CharBuffer.wrap(value),
				fld.encoding.charset
			);
			Packet.writeIntLenenc(dst, valBuf.readableBytes());
		}

		if (valBuf.readableBytes() <= bufSoftLimit) {
			dst.writeBytes(valBuf);
			valBuf.release();
			state.markAsDone();
		} else {
			dst.writeBytes(valBuf, bufSoftLimit);
			state.set(valBuf);
		}
	}

	@Override
	public String decodeValue(
		String dst, ByteBuf src, AdapterState state, FieldImpl fld
	) {
		DecoderState s = state.get();
		if (s == null) {
			int sz = Packet.readIntLenencSafe(src);
			switch (sz) {
			case Packet.LENENC_INCOMPLETE:
				return null;
			case Packet.LENENC_NULL:
				state.markAsDone();
				return null;
			}

			if (src.readableBytes() >= sz) {
				String rv = src.readCharSequence(
					sz, fld.encoding.charset
				).toString();
				state.markAsDone();
				return rv;
			}

			
			s = new DecoderState(
				sz, state.alloc.compositeBuffer()
			);
			s.acc.addComponent(
				true, src.readRetainedSlice(src.readableBytes())
			);
			state.set(s, AnyString::releaseDecoderState);
		} else {
			int count = s.sz - s.acc.writerIndex();
			if (src.readableBytes() < count)
				count = src.readableBytes();

			s.acc.addComponent(true, src.readRetainedSlice(count));
			if (s.acc.writerIndex() == s.sz) {
				String rv = s.acc.readCharSequence(
					s.sz, fld.encoding.charset
				).toString();
				state.markAsDone();
				return rv;
			}
		}
		return null;
	}

	private static class DecoderState {
		DecoderState(int sz_, CompositeByteBuf acc_) {
			sz = sz_;
			acc = acc_;
		}

		final int sz;
		final CompositeByteBuf acc;
	}

	private static void releaseDecoderState(Object s_) {
		DecoderState s = (DecoderState)s_;
		s.acc.release();
	}

	private final TypeId id;
}
