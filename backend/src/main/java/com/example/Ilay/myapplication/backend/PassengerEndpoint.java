package com.example.Ilay.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "trempitApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.Ilay.example.com",
                ownerName = "backend.myapplication.Ilay.example.com",
                packagePath = ""
        )
)
public class PassengerEndpoint {

    private static final Logger logger = Logger.getLogger(PassengerEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Passenger.class);
    }

    /**
     * Returns the {@link Passenger} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Passenger} with the provided ID.
     */
    @ApiMethod(
            name = "getPassenger",
            path = "passenger/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Passenger get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Passenger with ID: " + id);
        Passenger passenger = ofy().load().type(Passenger.class).id(id).now();
        if (passenger == null) {
            throw new NotFoundException("Could not find Passenger with ID: " + id);
        }
        return passenger;
    }

    /**
     * Inserts a new {@code Passenger}.
     */
    @ApiMethod(
            name = "insertPassenger",
            path = "passenger",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Passenger insert(Passenger passenger) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that passenger.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.

        try {
            Thread.sleep(1000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        ofy().save().entity(passenger).now();
        logger.info("Created Passenger with ID: " + passenger.getId());

        return ofy().load().entity(passenger).now();
    }

    /**
     * Updates an existing {@code Passenger}.
     *
     * @param id        the ID of the entity to be updated
     * @param passenger the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Passenger}
     */
    @ApiMethod(
            name = "updatePassenger",
            path = "passenger/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Passenger update(@Named("id") Long id, Passenger passenger) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(passenger).now();
        logger.info("Updated Passenger: " + passenger);
        return ofy().load().entity(passenger).now();
    }

    /**
     * Deletes the specified {@code Passenger}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Passenger}
     */
    @ApiMethod(
            name = "removePassenger",
            path = "passenger/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Passenger.class).id(id).now();
        logger.info("Deleted Passenger with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "listPassengers",
            path = "passenger",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Passenger> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Passenger> query = ofy().load().type(Passenger.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Passenger> queryIterator = query.iterator();
        List<Passenger> passengerList = new ArrayList<Passenger>(limit);
        while (queryIterator.hasNext()) {
            passengerList.add(queryIterator.next());
        }
        return CollectionResponse.<Passenger>builder().setItems(passengerList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Passenger.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Passenger with ID: " + id);
        }
    }
}