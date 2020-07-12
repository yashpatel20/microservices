package com.example.registry.service;

import com.example.registry.database.DbClient;
import com.example.registry.model.Registration;
import com.example.registry.model.ServiceNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ServiceRegistry {
    private final DbClient dbClient;

    @Autowired
    public ServiceRegistry(final DbClient dbClient) {
        this.dbClient = dbClient;
    }

    @GetMapping("/getHandlers")
    public List<ServiceNode> getHandlers(@RequestParam final String methodName) {
        return dbClient.getServiceNodes(methodName).join();
    }

    @PutMapping("/addNode")
    public void addNode(@RequestBody final ServiceNode node) {
        dbClient.addNode(node);
    }

    @PostMapping("/service/register")
    public void register(@RequestBody final Registration registration) {
        dbClient.register(registration.getServiceName(), registration.getMethodNames());
    }

    @DeleteMapping("/node")
    public void deleteNode(@RequestBody final String id) {
        dbClient.removeNode(id);
    }

}
