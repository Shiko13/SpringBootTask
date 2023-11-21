package com.epam.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trainers")
@NamedEntityGraph(name = "trainer-only-graph")
@NamedEntityGraph(name = "trainer-with-users-training-type-and-trainees-graph",
                  attributeNodes = {@NamedAttributeNode("user"), @NamedAttributeNode("trainingType"),
                          @NamedAttributeNode("trainees")})
public class Trainer {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "specialization")
    private TrainingType trainingType;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id", updatable = false)
    private User user;

    @ManyToMany(mappedBy = "trainers",
                cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Trainee> trainees;
}
