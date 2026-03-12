package com.mycompany.iaeval.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.iaeval.domain.enumeration.FormatDocument;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DocumentJoint.
 */
@Entity
@Table(name = "document_joint")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DocumentJoint implements Serializable {

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
    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false)
    private FormatDocument format;

    @Column(name = "url")
    private String url;

    @Lob
    @Column(name = "contenu_ocr")
    private String contenuOcr;

    @Column(name = "id_externe")
    private String idExterne;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "evaluation", "documents", "appelOffre", "candidat" }, allowSetters = true)
    private Soumission soumission;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DocumentJoint id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public DocumentJoint nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public FormatDocument getFormat() {
        return this.format;
    }

    public DocumentJoint format(FormatDocument format) {
        this.setFormat(format);
        return this;
    }

    public void setFormat(FormatDocument format) {
        this.format = format;
    }

    public String getUrl() {
        return this.url;
    }

    public DocumentJoint url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContenuOcr() {
        return this.contenuOcr;
    }

    public DocumentJoint contenuOcr(String contenuOcr) {
        this.setContenuOcr(contenuOcr);
        return this;
    }

    public void setContenuOcr(String contenuOcr) {
        this.contenuOcr = contenuOcr;
    }

    public String getIdExterne() {
        return this.idExterne;
    }

    public DocumentJoint idExterne(String idExterne) {
        this.setIdExterne(idExterne);
        return this;
    }

    public void setIdExterne(String idExterne) {
        this.idExterne = idExterne;
    }

    public Soumission getSoumission() {
        return this.soumission;
    }

    public void setSoumission(Soumission soumission) {
        this.soumission = soumission;
    }

    public DocumentJoint soumission(Soumission soumission) {
        this.setSoumission(soumission);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentJoint)) {
            return false;
        }
        return getId() != null && getId().equals(((DocumentJoint) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DocumentJoint{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", format='" + getFormat() + "'" +
            ", url='" + getUrl() + "'" +
            ", contenuOcr='" + getContenuOcr() + "'" +
            ", idExterne='" + getIdExterne() + "'" +
            "}";
    }
}
