package org.server.server.server.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.server.server.model.Server;
import org.server.server.repo.ServerRepo;
import org.server.server.server.ServerService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;

import static java.lang.Boolean.TRUE;
import static org.server.server.enumeration.Status.SERVER_DOWN;
import static org.server.server.enumeration.Status.SERVER_UP;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ServerServiceImpl implements ServerService {
    private final ServerRepo serverRepo;
    @Override
    public Server create(Server server) {
        log.info("Creating a new server {}", server.getName());
        server.setImageUrl(setImageUrl());
        return serverRepo.save(server);
    }

    @Override
    public Server ping(String ipAddress) throws IOException {
        log.info("Pinging server IP: {}", ipAddress);
        Server server = serverRepo.findByIpAddress(ipAddress);
        InetAddress address = InetAddress.getByName(ipAddress);
        server.setStatus(address.isReachable(10000) ? SERVER_UP : SERVER_DOWN);
        return serverRepo.save(server);
    }

    @Override
    public Collection<Server> list(int limit) {
        log.info("Fetching all servers");
        return serverRepo.findAll(Pageable.ofSize(limit)).toList();
    }

    @Override
    public Server get(Long id) {
        log.info("Fetching server by id: {}", id);
        return serverRepo.findById(id).get();
    }

    @Override
    public Server update(Server server) {
        log.info("Updating a server {}", server.getName());
        return serverRepo.save(server);
    }

    @Override
    public Boolean delete(Long id) {
        log.info("Deleting a server by id: {}", id);
        serverRepo.deleteById(id);
        return TRUE;
    }

    private String setImageUrl() {
        String image = "server.png";
        return ServletUriComponentsBuilder.
                fromCurrentContextPath().path("/server/image/" + image).toUriString();
    }
}
