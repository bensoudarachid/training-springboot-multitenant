package com.royasoftware.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"id"})
@lombok.ToString
@lombok.NoArgsConstructor

@Entity
public class Training {
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
	
}
