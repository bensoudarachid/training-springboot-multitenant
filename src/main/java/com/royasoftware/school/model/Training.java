package com.royasoftware.school.model;

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
import javax.validation.constraints.Size;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = { "id" })
@lombok.ToString
@lombok.NoArgsConstructor

@Entity
@NamedQuery(query = "SELECT tr FROM Training tr WHERE tr.id = :trainingid", name = "query_find_training_by_id")
public class Training implements Serializable {

	@Transient
//	static private Logger logger = LoggerFactory.getLogger(Training.class);
	private static final Logger logger = LogManager.getLogger(Training.class);
	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	@Required
	@Size(min = 1, max = 30)
	private String title;

	@NotNull
	private String secondaryTitle;

	@NotNull
	@Column(columnDefinition = "varchar(511)")
	@Required
	@Size(min = 1, max = 200)
	private String shortDescription;

	@NotNull
	@Column(columnDefinition = "TEXT")
	private String longDescription;

	@NotNull
	private Integer duration;

	@Version
	@Column(name = "VERSION")
	private Integer version;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "training", cascade = CascadeType.ALL, orphanRemoval = true)
	// @JsonBackReference
	@JsonManagedReference
	private List<Event> events;

	public Long getId() {
		// logger.info("id="+id);
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getDuration() {
		// logger.info("id="+id);
		if( duration == null) return 0;
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSecondaryTitle() {
		return secondaryTitle;
	}

	public void setSecondaryTitle(String secondaryTitle) {
		this.secondaryTitle = secondaryTitle;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
	
}
