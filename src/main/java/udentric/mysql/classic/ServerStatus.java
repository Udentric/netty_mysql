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

package udentric.mysql.classic;

import udentric.mysql.util.BitsetEnum;

public enum ServerStatus implements BitsetEnum<Short> {
	IN_TRANS(0, ""),
	AUTOCOMMIT(1, "server in auto_commit mode"),
	MORE_RESULTS_EXISTS(3, "multi query - next query exists"),
	QUERY_NO_GOOD_INDEX_USED(4, ""),
	QUERY_NO_INDEX_USED(5, ""),
	CURSOR_EXISTS(6, ""),
	LAST_ROW_SENT(7, ""),
	DB_DROPPED(8, "a database was dropped"),
	NO_BACKSLASH_ESCAPES(9, ""),
	METADATA_CHANGED(10, ""),
	QUERY_WAS_SLOW(11, ""),
	PS_OUT_PARAMS(12, ""),
	IN_TRANS_READONLY(13, ""),
	SESSION_STATE_CHANGED (14, ""),
	ANSI_QUOTES(15, "");

	private ServerStatus(int bitPos_, String description_) {
		bitPos = bitPos_;
		description = description_;
	}

	@Override
	public boolean get(Short bits) {
		return ((bits >>> bitPos) & 1) == 1;
	}

	@Override
	public Short mask() {
		return (short)(1 << bitPos);
	}

	@Override
	public int bitPos() {
		return bitPos;
	}

	@Override
	public String description() {
		return description;
	}

	private final int bitPos;
	private final String description;
}
