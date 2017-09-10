/*
 * Copyright 2017-2017 the original author or authors.
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
package org.hellojavaer.ddal.datasource;

import org.hellojavaer.ddal.core.utils.HttpUtils;
import org.hellojavaer.ddal.ddr.datasource.jdbc.DDRDataSource;
import org.hellojavaer.ddal.ddr.shard.ShardRouter;
import org.hellojavaer.ddal.sequence.Sequence;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:hellojavaer@gmail.com">Kaiming Zou</a>,created on 22/08/2017.
 */
public class DefaultDDALDataSource implements DDALDataSource {

    private static final String DDAL_PROTOCOL_PREFIX = "jdbc:ddal:";
    private DataSource          dataSource;
    private Sequence            sequence;
    private ShardRouter         shardRouter;

    public DefaultDDALDataSource(String url) {
        this(url, null, null);
    }

    public DefaultDDALDataSource(String url, String username, String password) {
        if (url != null) {
            url = url.trim();
        }
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("url can't be null");
        }
        if (!url.startsWith(DDAL_PROTOCOL_PREFIX)) {
            throw new IllegalArgumentException("url must be start with '" + DDAL_PROTOCOL_PREFIX + "'");
        }
        String subUrl = url.substring(DDAL_PROTOCOL_PREFIX.length()).trim();
        ApplicationContext context;
        if (subUrl.startsWith("classpath:") || subUrl.startsWith("classpath*:")) {
            context = new ClassPathXmlApplicationContext(subUrl);
        } else if (subUrl.startsWith("file:")) {
            context = new FileSystemXmlApplicationContext(subUrl);
        } else if (subUrl.startsWith("http:") || subUrl.startsWith("https:")) {
            Map<String, Object> param = new LinkedHashMap<>();
            param.put("username", username);
            param.put("password", password);
            String content = HttpUtils.sendPost(subUrl, param);
            Resource resource = new ByteArrayResource(content.getBytes());
            GenericXmlApplicationContext genericXmlApplicationContext = new GenericXmlApplicationContext();
            genericXmlApplicationContext.load(resource);
            genericXmlApplicationContext.refresh();
            context = genericXmlApplicationContext;
        } else {
            throw new IllegalArgumentException("Unsupported protocol:" + url);
        }
        this.dataSource = context.getBean(DDRDataSource.class, "ddrDataSource");
        this.sequence = context.getBean(Sequence.class, "sequence");
        this.shardRouter = context.getBean(ShardRouter.class, "shardRouter");
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getDataSource().getConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getDataSource().isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getDataSource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        getDataSource().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        getDataSource().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getDataSource().getLoginTimeout();
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return getDataSource().getParentLogger();
    }

    @Override
    public Sequence getSequence() {
        return sequence;
    }

    public ShardRouter getShardRouter() {
        return shardRouter;
    }

}
