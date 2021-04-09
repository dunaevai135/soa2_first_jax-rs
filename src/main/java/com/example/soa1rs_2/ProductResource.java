package com.example.soa1rs_2;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.connection_config.DBConfiguration;
import org.javalite.common.Convert;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.util.*;

@Path("/")
public class ProductResource {
    private static final String SERVLET_PATH_PRODUCT = "/";

    private static final String SERVLET_PATH_SUM_PRICE = "/sum_price";
    private static final String SERVLET_PATH_AVG_MANUFACTURE_COST = "/avg_manufacture_cost";
    private static final String SERVLET_PATH_NAME_STARTS = "/name_starts";

    static final ArrayList<String> PRODUCT_FIELDS = new ArrayList<>(Arrays.asList("name", "x", "y", "price", "manufactureCost", "unitOfMeasure",
            "owner_x", "owner_y", "location_name", "creationDate", "owner_name", "eyeColor", "hairColor", "nationality"));
    static final ArrayList<String> PRODUCT_FIELDS_REQUIRED = new ArrayList<>(Arrays.asList("name", "x", "y", "price"));
    static final ArrayList<String> PRODUCT_FIELDS_WITH_ID_AND_CREATION_DATE =
            new ArrayList<>(Arrays.asList("id", "name", "x", "y", "creationDate"));

    private String getPath(HttpServletRequest request) {
        String path = request.getPathInfo();
        return (path == null) ? "/" : path;
    }


    @PATCH
    @Path("/{id}")
    @Produces("text/xml;charset=UTF-8")
    public Response doPatchById(@PathParam("id") int id,String body, @Context HttpServletRequest request) {
        DBConfiguration.loadConfiguration("/database.properties");
        Base.open();

        Response response;
        try {
            Product product = ProductManager.getWorkerById(id);
//            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Map<String, String[]> params = ProductManager.paramsFromXml(body);
            if (product == null) {
                response = Response.status(404).build(); // no content
            } else {
                if (hasRedundantParameters(params.keySet()) ||
                        !validatePostPutFields(params)) {
                    response = Response.status(422).build(); // no content
                } else {
                    ProductManager.updateProduct(product, params);
                    response = Response.ok(ProductManager.toXml(product)).build();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(400).build();
        }
        Base.close();
        return response;
    }

    @POST
    @Path(SERVLET_PATH_SUM_PRICE)
    @Produces("text/xml;charset=UTF-8")
    public Response sumPrice(@Context HttpServletRequest request) {
        DBConfiguration.loadConfiguration("/database.properties");
        Base.open();

        Response response;
        try {
            LazyList<Product> products = ProductManager.getAllWorkers(request.getParameterMap());
            int sum = 0;
            for (Product i : products)
                if (i.get("price") != null)
                    sum = sum + Convert.toInteger(i.get("price"));

            response = Response.ok(ProductManager.toXml(Collections.singleton(sum))).build();
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(400).build();
        }
        Base.close();
        return response;


    }

    @POST
    @Path(SERVLET_PATH_AVG_MANUFACTURE_COST)
    @Produces("text/xml;charset=UTF-8")
    public Response avgCost(@Context HttpServletRequest request) {
        String path = getPath(request);

        DBConfiguration.loadConfiguration("/database.properties");
        Base.open();

        Response response;
        try {
            LazyList<Product> products = ProductManager.getAllWorkers(request.getParameterMap());
            double sum = 0;
            for (Product i : products)
                if (i.get("manufactureCost") != null)
                    sum = sum + Convert.toDouble(i.get("manufactureCost"));

            response = Response.ok(ProductManager.toXml(Collections.singleton(sum / products.size()))).build();
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(400).build();
        }
        Base.close();
        return response;


    }

    @POST
    @Path(SERVLET_PATH_NAME_STARTS + "/{prefix}")
    @Produces("text/xml;charset=UTF-8")
    public Response nameStarts(@PathParam("prefix") String prefix, @Context HttpServletRequest request) {
        String path = getPath(request);

        DBConfiguration.loadConfiguration("/database.properties");
        Base.open();

        Response response;
        try {
            LazyList<Product> products = ProductManager.getWorkersWithPrefix(prefix);
            if (products == null) {
                response = Response.status(404).build(); // no content
            } else {
                response = Response.ok(ProductManager.toXml(products.toMaps())).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(400).build();
        }
        Base.close();
        return response;
    }

    @POST
    @Produces("text/xml;charset=UTF-8")
    public Response create(String body, @Context HttpServletRequest request) {
        DBConfiguration.loadConfiguration("/database.properties");
        Base.open();

        Response response;
        try {
            Map<String, String[]> params = ProductManager.paramsFromXml(body);
//                System.out.println(ProductManager.paramsFromXml(body));
            if (hasRedundantParameters(params.keySet()) ||
                    !hasAllRequiredParameters(params.keySet()) ||
                    !validatePostPutFields(params)) {
                response = Response.status(422).build();
            } else {
                Product product = ProductManager.makeProductFromParams(params);
                response = Response.ok(ProductManager.toXml(product)).build();
            }

        } catch (NumberFormatException | ParseException e) {
            response = Response.status(422).build();
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(424).build();
        }
        Base.close();
        return response;
    }

    @GET
    @Produces("text/xml;charset=UTF-8")
    public Response getAll(@Context HttpServletRequest request) {
        String path = getPath(request);
        DBConfiguration.loadConfiguration("/database.properties");
        Base.open();

        Response response;
        try {
            LazyList<Product> products = ProductManager.getAllWorkers(request.getParameterMap());
            if (products == null) {
                response = Response.status(400).build(); // bad request
            } else {
                response = Response.ok(ProductManager.toXml(products.toMaps())).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(400).build();
        }
        Base.close();
        return response;
    }

    @GET
    @Path("/{id}")
    @Produces("text/xml;charset=UTF-8")
    public Response getById(@PathParam("id") int id, @Context HttpServletRequest request) {
        String path = getPath(request);

        DBConfiguration.loadConfiguration("/database.properties");
        Base.open();

        Response response;
        try {
            Product product = ProductManager.getWorkerById(id);
            if (product == null) {
                response = Response.status(404).build(); // no content
            } else {
                response = Response.ok(ProductManager.toXml(product)).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(400).build();
        }
        Base.close();
        return response;
    }

    @DELETE
    @Path("/{id}")
    @Produces("text/xml;charset=UTF-8")
    public Response delete(@PathParam("id") int id, @Context HttpServletRequest request) {
        String path = getPath(request);

        DBConfiguration.loadConfiguration("/database.properties");
        Base.open();

        Response response;
        try {
            Product product = ProductManager.getWorkerById(id);
            if (product == null) {
                response = Response.status(404).build(); // no content
            } else {
                product.deleteCascade();
                response = Response.status(200).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(400).build();
        }
        Base.close();
        return response;
    }

    private static boolean hasRedundantParameters(Set<String> params) {
        return params.stream().anyMatch(x -> PRODUCT_FIELDS.stream()
                .noneMatch(x::equals));
    }

    private static boolean hasAllRequiredParameters(Set<String> params) {
        return PRODUCT_FIELDS_REQUIRED.stream().filter(params::contains).count() == PRODUCT_FIELDS_REQUIRED.size();
    }

    private static boolean validatePostPutFields(Map<String, String[]> params) {
        try {
            String validName = "1";
            String name = params.get("name") == null ? validName : params.get("name")[0];
            Double x = (params.get("x") == null) ? null : Double.parseDouble(params.get("x")[0]);
            Double y = (params.get("y") == null) ? null : Double.parseDouble(params.get("y")[0]);

            Long price = (params.get("price") == null) ? null : Long.parseLong(params.get("price")[0]);

            UnitOfMeasure unitOfMeasure = (params.get("unitOfMeasure") == null) ? null : (params.get("unitOfMeasure")[0].equals("")) ? null : UnitOfMeasure.valueOf(params.get("unitOfMeasure")[0]);

            boolean res = name != null;


            res = res && !name.equals("") && (price == null || price > 0);
            String owner_name = params.get("owner_name") == null ? validName : params.get("owner_name")[0];
            String eyeColorString = (params.get("eyeColor") == null) ? null : params.get("eyeColor")[0];
            if (!Objects.equals(eyeColorString, "")) {
                Color eyeColor = (eyeColorString == null) ? null : Color.valueOf(eyeColorString);
            }
            String hairColorString = (params.get("hairColor") == null) ? null : params.get("hairColor")[0];
            if (!Objects.equals(hairColorString, "")) {
                Color hairColor = hairColorString == null ? null : Color.valueOf(hairColorString);
            }
            String nationalityString = (params.get("nationality") == null) ? null : params.get("nationality")[0];
            if (!Objects.equals(nationalityString, "")) {
                Country nationality = nationalityString == null ? null : Country.valueOf(nationalityString);
            }
//            Country nationality = params.get("nationality") == null ? null : Country.valueOf(params.get("nationality")[0]);
            res = res && !owner_name.equals("");

            Long loc_x = (params.get("owner_x") == null) ? null : Long.parseLong(params.get("owner_x")[0]);
            Double loc_y = (params.get("owner_y") == null) ? null : Double.parseDouble(params.get("owner_y")[0]);


            return res;
        } catch (Exception e) {
            return false;
        }
    }
}
