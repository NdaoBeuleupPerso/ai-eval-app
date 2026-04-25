package com.mycompany.iaeval.domain;

import com.mycompany.iaeval.domain.enumeration.TypeSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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
    @Column(name = "contenu", nullable = true)
    private String contenu;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_source", nullable = false)
    private TypeSource typeSource;

    @Column(name = "version")
    private String version;

    @Column(name = "qdrant_uuid")
    private String qdrantUuid;

    @Column(name = "source")
    private String source;

    @Lob
    @Column(name = "document")
    private byte[] document;

    @Column(name = "document_content_type")
    private String documentContentType;

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

    public byte[] getDocument() {
        return this.document;
    }

    public void setDocument(byte[] document) {
        this.document = document;
    }

    public String getDocumentContentType() {
        return this.documentContentType;
    }

    public void setDocumentContentType(String documentContentType) {
        this.documentContentType = documentContentType;
    }

    public ReferenceLegale qdrantUuid(String qdrantUuid) {
        this.setQdrantUuid(qdrantUuid);
        return this;
    }

    public void setQdrantUuid(String qdrantUuid) {
        this.qdrantUuid = qdrantUuid;
    }

    public String getSource() {
        return this.source;
    }

    public ReferenceLegale source(String source) {
        this.setSource(source);
        return this;
    }

    public void setSource(String source) {
        this.source = source;
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
            ", source='" + getSource() + "'" +
            "}";
    }
}
