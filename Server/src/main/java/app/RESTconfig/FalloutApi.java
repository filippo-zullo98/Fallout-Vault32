/*
 * Copyright (C) 2020 pierpaolo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package app.RESTconfig;

import app.GameDescription;
import app.JDBC;
import app.parser.Parser;
import app.parser.ParserOutput;
import app.type.*;
import org.json.JSONObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * questa classe ha il compito di gestire tutte le richieste REST inerenti alla partita
 */
@Path("api/")
public class FalloutApi {
    private final GameDescription game;
    private final Parser parser;


    /**
     *
     * @param game --> esso fa riferimento al game
     */
    public  FalloutApi(GameDescription game){
        this.game = game;
        this.parser = new Parser();
    }

    /**
     * Interfaccia che gestisce i comandi inseriti nella textbox
     * @param command --> stringa inserita in input
     */
    @GET
    @Path("parsercommand/")
    public Response parserCommand(@DefaultValue("-1") @QueryParam("command") String command){
        ParserOutput p = parser.parse(command, game.getCommands());
        JSONObject jsonResp = game.parseCommand(command);
        return Response.ok(jsonResp.toString(),MediaType.APPLICATION_JSON).build();
    }

    /**
     * Interfaccia con lo scopo di inviare l'inventario del match corrente
     */
    @GET
    @Path("inventario/")
    @Produces("application/json")
    public Response getInventario(){
        ParserOutput p = parser.parse("inventario", game.getCommands());
        String result = game.notifyObservers(p);
        return Response.ok(result,MediaType.APPLICATION_JSON).build();
    }

    /**
     * Funzione con lo scopo di salvare il match in uno specifico slot con il nome passato come paramentro
     */
    @GET
    @Path("savemetch/")
    @Produces("application/json")
    public Response saveMetch(@DefaultValue("-1") @QueryParam("idmetch") int idMatch, @DefaultValue("-1") @QueryParam("namemetch") String nameMetch){
        JDBC jdbc = JDBC.getDbInstance();
        assert jdbc != null;
        boolean results = jdbc.saveMatch(idMatch, nameMetch, game.getCurrentRoom().getId(),game.getRoomById(game.getCurrentRoom().getId()).getName(), game.getInventory(), game.getUnLockRoom());
        if (results){
            return Response.ok(MediaType.APPLICATION_JSON).build();
        }else{
            return Response.serverError().build();
        }
    }

    /**
     *  questa funzione viene chiamata nel momento in cui si fa riferimento all'api
     *  direzione
     *
     * @param direzione --> essa contiene la direzione sotto forma di stringa
     * @return Response.OK --> nel caso in cui la stringa passata come PathParam è valida
     *           Response.serverError() --> nel caso in cui la stringa passata non viene riconosciuta dal parser
     */
    @GET
    @Path("direzione/{direzione}")
    @Produces("application/json")
    public Response direzione(@PathParam("direzione") String direzione) {
        //la stringa viene passata al parser
        ParserOutput p = parser.parse(direzione, game.getCommands());
        boolean move;
        if (p == null || p.getCommand() == null) {
            return Response.serverError().build();//stringa non riconosciuta
        } else {
            //il parser accetta la stringa, ma non è detto che sia valida per eseguire un movimento
            move = game.nextMove(p); //esegue il movimeno
        }
        if (move){ //se il movimento è andato a buon fine
            JSONObject describe = Room.describeRoom(game.getCurrentRoom());
            describe = game.updateDescribeRoom(describe);
            return Response.ok(describe.toString(),MediaType.APPLICATION_JSON).build();
        }else{ // se il movimento non è andato a buon fine
            JSONObject jsonResp = new JSONObject().put("descrizione",game.getMessages());
            return Response.ok(jsonResp.toString(),MediaType.APPLICATION_JSON).build();
        }
    }

    /**
     * Funzione con lo scopo di restituire la descrizione della stanza corrente
     */
    @GET
    @Path("getcurrentroom/")
    @Produces("application/json")
    public Response getCurrentRoom(){
        JSONObject describe = Room.describeRoom(game.getCurrentRoom());
        describe = game.updateDescribeRoom(describe);
        return Response.ok(describe.toString(),MediaType.APPLICATION_JSON).build();
    }

}