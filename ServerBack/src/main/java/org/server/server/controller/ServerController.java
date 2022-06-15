package org.server.server.controller;

import lombok.RequiredArgsConstructor;
import org.server.server.model.Response;
import org.server.server.model.Server;
import org.server.server.server.ServerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;

import static org.server.server.enumeration.Status.SERVER_UP;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("/server")
@RequiredArgsConstructor
public class ServerController {
    private final ServerService serverService;

    @GetMapping("/list")
    public ResponseEntity<Response> getServers(){
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(new HashMap<String, Collection<Server>>(){{
                            put("servers", serverService.list(30));
                        }})
                        .message("Servers retrieved")
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping("/ping/{ipAddress}")
    public ResponseEntity<Response> pingServer(@PathVariable("ipAddress") String ipAddress) throws IOException {
        Server server = serverService.ping(ipAddress);
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(new HashMap<String, Server>(){{
                            put("server", server);
                        }})
                        .message(server.getStatus() == SERVER_UP ? "ping success" : "ping failed")
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @PostMapping("/save")
    public ResponseEntity<Response> save(@RequestBody @Valid Server server) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(new HashMap<String, Server>(){{
                            put("server", serverService.create(server));
                        }})
                        .message("Server created")
                        .httpStatus(CREATED)
                        .statusCode(CREATED.value())
                        .build()
        );
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Response> getServer(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(new HashMap<String, Server>(){{
                            put("server", serverService.get(id));
                        }})
                        .message("Server retrieved")
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteServer(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(new HashMap<String, Boolean>(){{
                            put("deleted", serverService.delete(id));
                        }})
                        .message("Server deleted")
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping(path = "/image/{imageFile}", produces = IMAGE_PNG_VALUE)
    public byte[] getImage(@PathVariable("imageFile") String imageFile) throws IOException {
        return Files.readAllBytes(Paths.get("/Users/raiymbekmerekeyev/Desktop/server.png"));
    }
}
