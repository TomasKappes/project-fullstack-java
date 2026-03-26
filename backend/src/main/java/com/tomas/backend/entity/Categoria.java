package com.tomas.backend.entity;
import com.tomas.backend.enums.TipoCategoria;
import jakarta.persistence.*;

@Entity
@Table(name = "categorias")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;

    @Column (unique = true, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Boolean activo = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCategoria nombreCategoria;

    public Categoria() {
    }

    public Long getId() {
        return idCategoria;
    }

    public void setId(Long id) {
        this.idCategoria = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean isActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public TipoCategoria getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(TipoCategoria nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }
}

