/*
 * Copyright 2016-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hellojavaer.ddr.core.sharding;

import org.hellojavaer.ddr.core.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

import java.util.List;

/**
 *
 * @author <a href="mailto:hellojavaer@gmail.com">zoukaiming[邹凯明]</a>,created on 23/11/2016.
 */
public class ShardingRouteHelper {

    private static Map<String, List> map = new HashMap<String, List>();

    public static void setConfigedShardingInfos(String scName, String tbName, List<ShardingInfo> shardingInfos) {
        map.put(buildQueryKey(scName, tbName), shardingInfos);
    }

    public static List<ShardingInfo> getConfigedShardingInfos(String scName, String tbName) {
        return map.get(buildQueryKey(scName, tbName));
    }

    private static String buildQueryKey(String scName, String tbName) {
        StringBuilder sb = new StringBuilder();
        scName = StringUtils.trim(scName);
        if (scName != null) {
            sb.append(scName.toLowerCase());
        }
        sb.append('.');
        sb.append(StringUtils.trim(tbName).toLowerCase());
        return sb.toString();
    }
}