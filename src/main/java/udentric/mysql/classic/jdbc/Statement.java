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

package udentric.mysql.classic.jdbc;

import io.netty.util.concurrent.Future;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.Phaser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import udentric.mysql.classic.ColumnDefinition;
import udentric.mysql.classic.Commands;
import udentric.mysql.classic.ResponseConsumer;
import udentric.mysql.classic.command.Any;

public class Statement implements java.sql.Statement {
	Statement(Connection conn_) {
		conn = conn_;
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		int phase = responseWaiter.register();

		ResultSetResponseConsumer rc = new ResultSetResponseConsumer();
		Any cmd = Commands.query(sql).withResponseConsumer(rc);

		conn.submitCommand(cmd).addListener(
			rc::commandSend
		);

		responseWaiter.awaitAdvance(phase);

		if (rc.error != null) {
			
		}

		return rc.value();
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		int phase = responseWaiter.register();

		SimpleResponseConsumer rc = new SimpleResponseConsumer();
		Any cmd = Commands.query(sql).withResponseConsumer(rc);

		conn.submitCommand(cmd).addListener(
			rc::commandSend
		);

		responseWaiter.awaitAdvance(phase);

		if (rc.error != null) {
			
		}

		return rc.value();
	}

	@Override
	public void close() throws SQLException {
		conn.releaseStatement(this);
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		return 0;
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
	}

	@Override
	public int getMaxRows() throws SQLException {
		return 0;
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return 0;
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
	}

	@Override
	public void cancel() throws SQLException {
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	@Override
	public void clearWarnings() throws SQLException {
	}

	@Override
	public void setCursorName(String name) throws SQLException {
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		return false;
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		return null;
	}

	@Override
	public int getUpdateCount() throws SQLException {
		return 0;
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		return false;
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return 0;
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
	}

	@Override
	public int getFetchSize() throws SQLException {
		return 0;
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		return 0;
	}

	@Override
	public int getResultSetType()  throws SQLException {
		return 0;
	}

	@Override
	public void addBatch(String sql) throws SQLException {
	}

	@Override
	public void clearBatch() throws SQLException {
	}

	@Override
	public int[] executeBatch() throws SQLException {
		return null;
	}

	@Override
	public java.sql.Connection getConnection() throws SQLException {
		return conn;
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		return false;
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		return null;
	}

	@Override
	public int executeUpdate(
		String sql, int autoGeneratedKeys
	) throws SQLException {
		return 0;
	}

	@Override
	public int executeUpdate(
		String sql, int columnIndexes[]
	) throws SQLException {
		return 0;
	}

	@Override
	public int executeUpdate(
		String sql, String columnNames[]
	) throws SQLException {
		return 0;
	}

	@Override
	public boolean execute(
		String sql, int autoGeneratedKeys
	) throws SQLException {
		return false;
	}

	@Override
	public boolean execute(
		String sql, int columnIndexes[]
	) throws SQLException {
		return false;
	}

	@Override
	public boolean execute(
		String sql, String columnNames[]
	) throws SQLException {
		return false;
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return 0;
	}

	@Override
	public boolean isClosed() throws SQLException {
		return false;
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return false;
	}

	@Override
	public void closeOnCompletion() throws SQLException {
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return false;
	}

	@Override
	public long getLargeUpdateCount() throws SQLException {
		throw new UnsupportedOperationException(
			"getLargeUpdateCount not implemented"
		);
	}

	@Override
	public void setLargeMaxRows(long max) throws SQLException {
		throw new UnsupportedOperationException(
			"setLargeMaxRows not implemented"
		);
	}

	@Override
	public long getLargeMaxRows() throws SQLException {
		return 0;
	}

	@Override
	public long[] executeLargeBatch() throws SQLException {
		throw new UnsupportedOperationException(
			"executeLargeBatch not implemented"
		);
	}

	@Override
	public long executeLargeUpdate(String sql) throws SQLException {
		throw new UnsupportedOperationException(
			"executeLargeUpdate not implemented"
		);
	}

	@Override
	public long executeLargeUpdate(
		String sql, int autoGeneratedKeys
	) throws SQLException {
		throw new SQLFeatureNotSupportedException(
			"executeLargeUpdate not implemented"
		);
	}

	@Override
	public long executeLargeUpdate(
		String sql, int columnIndexes[]
	) throws SQLException {
		throw new SQLFeatureNotSupportedException(
			"executeLargeUpdate not implemented"
		);
	}

	@Override
	public long executeLargeUpdate(
		String sql, String columnNames[]
	) throws SQLException {
		throw new SQLFeatureNotSupportedException(
			"executeLargeUpdate not implemented"
		);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	private class SimpleResponseConsumer implements ResponseConsumer {


		@Override
		public void onMetadata(List<ColumnDefinition> colDef) {

		}

		@Override
		public void onData() {

		}

		@Override
		public void onFailure(Throwable cause) {

		}

		@Override
		public void onSuccess() {

		}

		
		int value() {
			return 0;
		}

		void commandSend(Future f) {
			if (f.isSuccess()) {
				
			} else {
				error = f.cause();
				responseWaiter.arriveAndDeregister();
			}
		}

		Throwable error;
	}

	private class ResultSetResponseConsumer implements ResponseConsumer {


		@Override
		public void onMetadata(List<ColumnDefinition> colDef) {

		}

		@Override
		public void onData() {

		}

		@Override
		public void onFailure(Throwable cause) {

		}

		@Override
		public void onSuccess() {

		}

		
		ResultSet value() {
			return null;
		}

		void commandSend(Future f) {
			if (f.isSuccess()) {
				
			} else {
				error = f.cause();
				responseWaiter.arriveAndDeregister();
			}
		}

		Throwable error;
	}

	synchronized void releaseResult(ResultSet rs) {
		results.remove(rs);
	}

	private static final Logger LOGGER = LogManager.getLogger(
		Statement.class
	);

	private final Connection conn;
	private final Phaser responseWaiter = new Phaser();
	private final IdentityHashMap<
		ResultSet, Boolean
	> results = new IdentityHashMap<>();
}
