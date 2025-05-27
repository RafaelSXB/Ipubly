package com.project.Ipubly.Model;

import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "USERS")
public class usersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @UuidGenerator
    private UUID ID;
    private String username;
    @Nullable
    private String API;
    @Nullable
    private String APITOKEN;
    @Nullable
    private String APIREFRESHTOKEN;
    @Nullable
    private Long EXPIRE;

    
}
