package ganz.leonard.automatalearning.paramtests.conversion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ganz.leonard.automatalearning.automata.probability.function.PheromoneFunction;
import java.lang.reflect.Type;

public class PheromoneFunctionSerializer implements JsonSerializer<PheromoneFunction> {
  @Override
  public JsonElement serialize(
      PheromoneFunction src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject result = new JsonObject();
    result.addProperty("string_representationSUP", src.stringRep());
    return result;
  }
}
