package com.royasoftware.school.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"id"})
//@lombok.ToString
@lombok.NoArgsConstructor

@Entity
public class Event {
	@Transient
	static private Logger logger = LoggerFactory.getLogger(Event.class);
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private short number;

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date start;


    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date end;

	@Version
    @Column(name = "VERSION")
    private Integer version;

	@ManyToOne(fetch=FetchType.EAGER)
    @JsonBackReference
//	@JsonManagedReference
//    @JsonIgnore
	private Training training;	

	public String toString(){
		return "event: id ="+id+", start="+start+", end="+end;
	}
}
