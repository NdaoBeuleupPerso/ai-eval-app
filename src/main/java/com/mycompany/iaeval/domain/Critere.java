package com.mycompany.iaeval.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.iaeval.domain.enumeration.StatutCritere;
import com.mycompany.iaeval.domain.enumeration.TypeCritere;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Critere.
 */
@Entity
@Table(name = "critere")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Critere implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotNull
    @Column(name = "ponderation", nullable = false)
    private Double ponderation;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "categorie", nullable = false)
    private TypeCritere categorie;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "criteres", "soumissions" }, allowSetters = true)
    private AppelOffre appelOffre;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutCritere statut = StatutCritere.VALIDE; // Par défaut VALIDE pour les saisies manuelles

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Critere() {}

    public Critere(StatutCritere statut) {
        this.statut = statut;
    }

    public StatutCritere getStatut() {
        return this.statut;
    }

    public void setStatut(StatutCritere statut) {
        this.statut = statut;
    }

    public Long getId() {
        return this.id;
    }

    public Critere id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Critere nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Double getPonderation() {
        return this.ponderation;
    }

    public Critere ponderation(Double ponderation) {
        this.setPonderation(ponderation);
        return this;
    }

    public void setPonderation(Double ponderation) {
        this.ponderation = ponderation;
    }

    public TypeCritere getCategorie() {
        return this.categorie;
    }

    public Critere categorie(TypeCritere categorie) {
        this.setCategorie(categorie);
        return this;
    }

    public void setCategorie(TypeCritere categorie) {
        this.categorie = categorie;
    }

    public String getDescription() {
        return this.description;
    }

    public Critere description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AppelOffre getAppelOffre() {
        return this.appelOffre;
    }

    public void setAppelOffre(AppelOffre appelOffre) {
        this.appelOffre = appelOffre;
    }

    public Critere appelOffre(AppelOffre appelOffre) {
        this.setAppelOffre(appelOffre);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Critere)) {
            return false;
        }
        return getId() != null && getId().equals(((Critere) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Critere{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", ponderation=" + getPonderation() +
            ", categorie='" + getCategorie() + "'" +
            ", description='" + getDescription() + "'" +
            ", statut='" + getStatut() + "'" +
            "}";
    }
}
