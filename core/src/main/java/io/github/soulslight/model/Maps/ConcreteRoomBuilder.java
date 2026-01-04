package io.github.soulslight.model.Maps;

public class ConcreteRoomBuilder implements RoomBuilder {
  private Room result;

  public ConcreteRoomBuilder() {
    this.reset();
  }

  @Override
  public void reset() {
    this.result = new Room();
  }

  @Override
  public void loadTemplate(String templateName) {
    // Logica per settare il nome del template nel prodotto
    this.result.setTemplateName(templateName);
    // qui si caricheranno i template in .json
  }

  @Override
  public Room getResult() {
    return this.result;
  }

  @Override
  public void addEnemies(String type) {
    if (this.result == null) reset();
    this.result.addEnemy(type);
  }
}
