package com.example.registry.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;
import java.util.List;

import com.example.registry.model.ServiceNode;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.x.protobuf.MysqlxPrepare.Prepare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DbClient {
    private final Connection connection;
    private final Logger logger;

    @Autowired
    public DbClient() throws SQLException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost/registry");
        dataSource.setUser("root");
        dataSource.setPassword("yhp12345");
        connection = dataSource.getConnection();
        logger = LoggerFactory.getLogger(DbClient.class.getCanonicalName());
    }

    public CompletableFuture<Void> addNode(final ServiceNode node) {
        try {
            final PreparedStatement insertNodeQuery = connection
                    .prepareStatement("insert ignore into node(id,ipAddress,serviceName,port) values (?,?,?,?)");
            insertNodeQuery.setString(1, node.getId());
            insertNodeQuery.setString(2, node.getIpAddress());
            insertNodeQuery.setString(3, node.getServiceName());
            insertNodeQuery.setInt(4, node.getPort());
            return CompletableFuture.runAsync(() -> {
                try {
                    insertNodeQuery.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).whenComplete((data, e) -> {
                if (e != null) {
                    logger.error("exception inserting node record ", e);
                }
            });
        } catch (SQLException e) {
            logger.error("exception building insert statement ", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<Void> removeNode(final String id) {
        try {
            final PreparedStatement deleteNodeQuery = connection.prepareStatement("delete from node where id=?");
            deleteNodeQuery.setString(1, id);
            return CompletableFuture.runAsync(() -> {
                try {
                    deleteNodeQuery.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).whenComplete((data, e) -> {
                if (e != null) {
                    logger.error("exception inserting node record ", e);
                }
            });
        } catch (SQLException e) {
            logger.error("exception building insert statement ", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<List<ServiceNode>> getServiceNodes(final String methodName) {
        try {
            final PreparedStatement getServiceNodesQuery = connection.prepareStatement(
                    "select * from node where serviceName = (select serviceName from registration where methodName = ?)");
            getServiceNodesQuery.setString(1, methodName);
            return CompletableFuture.supplyAsync(() -> {
                final ResultSet rs;
                try {
                    rs = getServiceNodesQuery.executeQuery();
                    final List<ServiceNode> serviceNodes = new ArrayList<>();
                    while (rs.next()) {
                        serviceNodes
                                .add(new ServiceNode(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4)));
                    }
                    return serviceNodes;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            logger.error("exception building insert statement ", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<Void> register(final String serviceName, final String[] methodNames){
        try{
            final PreparedStatement registerQuery = connection.prepareStatement("insert ignore into registration(methodName, serviceName) values(?,?)");
            return CompletableFuture.runAsync(()->{
                try{
                    for(String methodName : methodNames){
                        registerQuery.setString(1, methodName);
                        registerQuery.setString(2, serviceName);
                        registerQuery.execute();
                    }
                }catch(SQLException e){
                    throw new RuntimeException(e);
                }
            }).whenComplete((data, e)->{
                if(e!=null){
                    logger.error("exception inserting node record ", e);
                }
            })
        }catch(SQLException e){
            logger.error("exception building insert statement ", e);
            return CompletableFuture.failedFuture(e);
        }
    }

}