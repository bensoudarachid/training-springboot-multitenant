package com.royasoftware.school.model;

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLogopath() {
		return logopath;
	}

	public void setLogopath(String logopath) {
		this.logopath = logopath;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
}
