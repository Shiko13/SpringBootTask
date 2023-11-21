package com.epam.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapsId;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trainees")
@NamedEntityGraph(name = "trainee-with-trainers-graph", attributeNodes = {@NamedAttributeNode("trainers")})
@NamedEntityGraph(name = "trainee-with-users-and-trainers-graph",
                  attributeNodes = {@NamedAttributeNode("user"), @NamedAttributeNode("trainers")})
public class Trainee {

    @Id
    private Long id;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address")
    private String address;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id", updatable = false)
    private User user;

    @ManyToMany
    @JoinTable(name = "trainee_trainer", joinColumns = @JoinColumn(name = "trainee_id"),
               inverseJoinColumns = @JoinColumn(name = "trainer_id"))
    private List<Trainer> trainers;
}
