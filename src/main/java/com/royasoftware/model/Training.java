package com.royasoftware.model;

import java.io.Serializable;
import java.util.List;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"id"})
@lombok.ToString
@lombok.NoArgsConstructor

@Entity
@NamedQuery(query = "SELECT tr FROM Training tr WHERE tr.id = :trainingid", name = "query_find_training_by_id")
public class Training implements Serializable{
	@Transient
	static private Logger logger = LoggerFactory.getLogger(Training.class);
	
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String secondaryTitle;

    @NotNull
    @Column(columnDefinition="varchar(511)")
    private String shortDescription;

    @NotNull
    @Column(columnDefinition="TEXT")
    private String longDescription;

    @NotNull
    private Integer duration;

	@Version
    @Column(name = "VERSION")
    private Integer version;

    public Long getId() {
//        logger.info("id="+id);     	
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany(fetch = FetchType.EAGER,mappedBy="training",cascade = CascadeType.ALL,orphanRemoval=true)
//    @JsonBackReference
	@JsonManagedReference
    private List<Event> events;

	
}
