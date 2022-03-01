package org.cycles.resteasyjackson;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.cycles.entites.User;
import org.cycles.entites.Product;
import org.cycles.repositories.UserRepository;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class UserApi {
 @Inject
 UserRepository pr;
 @Inject
 Vertx vertx;

 private WebClient webClient;

    @PostConstruct
    void initialize() {
        this.webClient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost("localhost")
                        .setDefaultPort(8081).setSsl(false).setTrustAll(true));
    }

    @GET
    @Blocking
    public List<User> list() {
        return pr.listUser();
    }

    @GET
    @Path("/{Id}")
    @Blocking
    public User getById(@PathParam("Id") Long Id) {
        return pr.findUser(Id);
    }

    @GET
    @Path("/{Id}/product")
    @Blocking
    public Uni<User> getByIdProduct(@PathParam("Id") Long Id) {
       return Uni.combine().all().unis(getUserReactive(Id),getAllProducts())
                .combinedWith((v1,v2) -> {
                    v1.getProducts().forEach(product -> {
                       v2.forEach(p -> {
                           if(product.getId().equals(p.getId())){
                               product.setName(p.getName());
                               product.setDescription(p.getDescription());
;                           }
                       });
                    });
                    return v1;
                });
    }

    @POST
    @Blocking
    public Response add(User c) {
        //c.getProducts().forEach(p-> p.setUser(c));
        pr.createdUser(c);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{Id}")
    @Blocking
    public Response delete(@PathParam("Id") Long Id) {
        User user = pr.findUser(Id);
        pr.deleteUser(user);
        return Response.ok().build();
    }

    @PUT
    @Blocking
    public Response update(User p) {
        User user = pr.findUser(p.getId());
        user.setUsername(p.getUsername());
        user.setPassword(p.getPassword());
        user.setSurname(p.getSurname());
        user.setPhone(p.getPhone());
        user.setAddress(p.getAddress());
        user.setProducts(p.getProducts());
        pr.updateUser(user);
        return Response.ok().build();
    }


private Uni<User> getUserReactive(Long Id){
    User user = pr.findUser(Id);
    Uni<User> item = Uni.createFrom().item(user);
    return item;
}

private Uni<List<Product>> getAllProducts(){
    return webClient.get(8081, "localhost", "/product").send()
            .onFailure().invoke(res -> log.error("Error recuperando productos ", res))
            .onItem().transform(res -> {
                List<Product> lista = new ArrayList<>();
                JsonArray objects = res.bodyAsJsonArray();
                objects.forEach(p -> {
                    log.info("See Objects: " + objects);
                    ObjectMapper objectMapper = new ObjectMapper();
                    // Pass JSON string and the POJO class
                    Product product = null;
                    try {
                        product = objectMapper.readValue(p.toString(), Product.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    lista.add(product);
                });
                return lista;
            });
    }

}
