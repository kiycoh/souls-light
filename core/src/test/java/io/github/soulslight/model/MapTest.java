package io.github.soulslight.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import io.github.soulslight.model.Maps.RoomBuilder;
import io.github.soulslight.model.Maps.ConcreteRoomBuilder;
import io.github.soulslight.model.Maps.Room;

public class MapTest {
    @Test
    //incude anche testCorruptTemplate()
    public void testLoadTemplate() {
        // Cocreete builder
        RoomBuilder builder = new ConcreteRoomBuilder();
        String expectedTemplate = "StanzaTest";

        // Caricamento Template
        builder.reset();
        builder.loadTemplate(expectedTemplate);
        Room room = builder.getResult();

        // Controllo stanza non nulla
        assertNotNull(room, "La stanza non deve essere nulla dopo il caricamento");

        // Verifica template richiesto
        assertEquals(expectedTemplate, room.getTemplateName(),
            "Il nome del template nella stanza deve corrispondere a quello caricato");
    }

    @Test
    public void testAddEnemies() {

        ConcreteRoomBuilder builder = new ConcreteRoomBuilder();
        String enemyType = "Chaser";


        builder.reset();
        builder.addEnemies(enemyType);
        builder.addEnemies(enemyType); // Provo a caricare 2 "Chaser"
        Room room = builder.getResult();


        assertNotNull(room, "La stanza deve essere creata");
        assertEquals(2, room.getEnemies().size(), "La stanza dovrebbe contenere 2 nemici");
        assertEquals(enemyType, room.getEnemies().get(0), "Il tipo di nemico deve corrispondere");
    }
}
