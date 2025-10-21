package com.tpo.prisma.model.modelNeo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Usuario")
public class UsuarioNode {
    @Id
    private String id; // mismo id que en MongoDB

    public UsuarioNode() {}
    public UsuarioNode(String id) { this.id = id; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
