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
public class TenantProfile {
    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    private String description;
	@NotNull
	private String logopath;

	@Version
    @Column(name = "VERSION")
    private Integer version;
	
}
