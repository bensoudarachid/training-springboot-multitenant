package com.royasoftware.school.model;

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
@lombok.ToString
@lombok.NoArgsConstructor

public class Tenant {
	static private Logger logger = LoggerFactory.getLogger(Tenant.class);
    private String name1;
    private String name2;
	public static Logger getLogger() {
		return logger;
	}
	public static void setLogger(Logger logger) {
		Tenant.logger = logger;
	}
	public String getName1() {
		return name1;
	}
	public void setName1(String name1) {
		this.name1 = name1;
	}
	public String getName2() {
		return name2;
	}
	public void setName2(String name2) {
		this.name2 = name2;
	}
	
}
