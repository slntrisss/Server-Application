package org.server.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.server.server.enumeration.Status;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Server {
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;
    @Column(unique = true)
    @NotEmpty(message = "IP address should not be empty or null")
    private String ipAddress;
    private String name;
    private String memory;
    private String type;
    private String imageUrl;
    private Status status;
}
