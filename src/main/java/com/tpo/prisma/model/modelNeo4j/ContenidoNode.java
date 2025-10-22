package com.tpo.prisma.model.modelNeo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Contenido")
public class ContenidoNode {
    @Id
    @Property("id")
    private String id;

    public ContenidoNode() {}
    public ContenidoNode(String id) { this.id = id; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
