package com.tpo.prisma.model.modelNeo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Tag")
public class TagNode {
    @Id
    private String nombre; // nombre como id

    public TagNode() {}
    public TagNode(String nombre) { this.nombre = nombre; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
