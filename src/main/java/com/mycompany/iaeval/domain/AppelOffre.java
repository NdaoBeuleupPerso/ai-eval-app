package com.mycompany.iaeval.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.iaeval.domain.enumeration.StatutAppel;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AppelOffre.
 */
@Entity
@Table(name = "appel_offre")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppelOffre implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @NotNull
    @Column(name = "titre", nullable = false)
    private String titre;

    @Lob
    @Column(name = "description")
    private byte[] description;

    @Column(name = "description_content_type")
    private String descriptionContentType;

    @Column(name = "date_cloture")
    private Instant dateCloture;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutAppel statut;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appelOffre")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "appelOffre" }, allowSetters = true)
    private Set<Critere> criteres = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appelOffre")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "evaluation", "documents", "appelOffre", "candidat" }, allowSetters = true)
    private Set<Soumission> soumissions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AppelOffre id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public AppelOffre reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTitre() {
        return this.titre;
    }

    public AppelOffre titre(String titre) {
        this.setTitre(titre);
        return this;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public byte[] getDescription() {
        return this.description;
    }

    public AppelOffre description(byte[] description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(byte[] description) {
        this.description = description;
    }

    public String getDescriptionContentType() {
        return this.descriptionContentType;
    }

    public AppelOffre descriptionContentType(String descriptionContentType) {
        this.descriptionContentType = descriptionContentType;
        return this;
    }

    public void setDescriptionContentType(String descriptionContentType) {
        this.descriptionContentType = descriptionContentType;
    }

    public Instant getDateCloture() {
        return this.dateCloture;
    }

    public AppelOffre dateCloture(Instant dateCloture) {
        this.setDateCloture(dateCloture);
        return this;
    }

    public void setDateCloture(Instant dateCloture) {
        this.dateCloture = dateCloture;
    }

    public StatutAppel getStatut() {
        return this.statut;
    }

    public AppelOffre statut(StatutAppel statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutAppel statut) {
        this.statut = statut;
    }

    public Set<Critere> getCriteres() {
        return this.criteres;
    }

    public void setCriteres(Set<Critere> criteres) {
        if (this.criteres != null) {
            this.criteres.forEach(i -> i.setAppelOffre(null));
        }
        if (criteres != null) {
            criteres.forEach(i -> i.setAppelOffre(this));
        }
        this.criteres = criteres;
    }

    public AppelOffre criteres(Set<Critere> criteres) {
        this.setCriteres(criteres);
        return this;
    }

    public AppelOffre addCriteres(Critere critere) {
        this.criteres.add(critere);
        critere.setAppelOffre(this);
        return this;
    }

    public AppelOffre removeCriteres(Critere critere) {
        this.criteres.remove(critere);
        critere.setAppelOffre(null);
        return this;
    }

    public Set<Soumission> getSoumissions() {
        return this.soumissions;
    }

    public void setSoumissions(Set<Soumission> soumissions) {
        if (this.soumissions != null) {
            this.soumissions.forEach(i -> i.setAppelOffre(null));
        }
        if (soumissions != null) {
            soumissions.forEach(i -> i.setAppelOffre(this));
        }
        this.soumissions = soumissions;
    }

    public AppelOffre soumissions(Set<Soumission> soumissions) {
        this.setSoumissions(soumissions);
        return this;
    }

    public AppelOffre addSoumissions(Soumission soumission) {
        this.soumissions.add(soumission);
        soumission.setAppelOffre(this);
        return this;
    }

    public AppelOffre removeSoumissions(Soumission soumission) {
        this.soumissions.remove(soumission);
        soumission.setAppelOffre(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppelOffre)) {
            return false;
        }
        return getId() != null && getId().equals(((AppelOffre) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppelOffre{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", titre='" + getTitre() + "'" +
            ", description='" + getDescription() + "'" +
            ", descriptionContentType='" + getDescriptionContentType() + "'" +
            ", dateCloture='" + getDateCloture() + "'" +
            ", statut='" + getStatut() + "'" +
            "}";
    }
}
