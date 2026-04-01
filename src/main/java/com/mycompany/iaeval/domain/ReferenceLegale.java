package com.mycompany.iaeval.domain;

import com.mycompany.iaeval.domain.enumeration.TypeSource;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ReferenceLegale.
 */
@Entity
@Table(name = "reference_legale")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReferenceLegale implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "titre", nullable = false)
    private String titre;

    @Lob
    @Column(name = "contenu", nullable = false)
    private String contenu;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_source", nullable = false)
    private TypeSource typeSource;

    @Column(name = "version")
    private String version;

    @Column(name = "qdrant_uuid")
    private String qdrantUuid;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReferenceLegale id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return this.titre;
    }

    public ReferenceLegale titre(String titre) {
        this.setTitre(titre);
        return this;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return this.contenu;
    }

    public ReferenceLegale contenu(String contenu) {
        this.setContenu(contenu);
        return this;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public TypeSource getTypeSource() {
        return this.typeSource;
    }

    public ReferenceLegale typeSource(TypeSource typeSource) {
        this.setTypeSource(typeSource);
        return this;
    }

    public void setTypeSource(TypeSource typeSource) {
        this.typeSource = typeSource;
    }

    public String getVersion() {
        return this.version;
    }

    public ReferenceLegale version(String version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getQdrantUuid() {
        return this.qdrantUuid;
    }

    public ReferenceLegale qdrantUuid(String qdrantUuid) {
        this.setQdrantUuid(qdrantUuid);
        return this;
    }

    public void setQdrantUuid(String qdrantUuid) {
        this.qdrantUuid = qdrantUuid;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReferenceLegale)) {
            return false;
        }
        return getId() != null && getId().equals(((ReferenceLegale) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReferenceLegale{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", contenu='" + getContenu() + "'" +
            ", typeSource='" + getTypeSource() + "'" +
            ", version='" + getVersion() + "'" +
            ", qdrantUuid='" + getQdrantUuid() + "'" +
            "}";
    }
}
