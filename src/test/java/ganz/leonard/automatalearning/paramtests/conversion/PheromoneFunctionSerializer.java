package ganz.leonard.automatalearning.paramtests.conversion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ganz.leonard.automatalearning.automata.probability.PheromoneFunction;
import ganz.leonard.automatalearning.util.StringifyableFunction;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class PheromoneFunctionSerializer implements JsonSerializer<PheromoneFunction> {
  @Override
  public JsonElement serialize(
      PheromoneFunction src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject result = new JsonObject();
    result.addProperty("string_representation", src.stringRep());

    List<Field> declaredFields =
        Arrays.stream(PheromoneFunction.class.getDeclaredFields()).toList();
    for (Field field : declaredFields) {
      field.setAccessible(true);
      try {
        Object fieldValue = field.get(src);
        JsonElement fieldValueJson;
        if (fieldValue instanceof StringifyableFunction) {
          fieldValueJson = context.serialize(fieldValue, StringifyableFunction.class);
        } else {
          fieldValueJson = context.serialize(fieldValue);
        }
        result.add(field.getName(), fieldValueJson);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(
            "Could not access fields of pheromone function through reflection", e);
      }
    }
    return result;
  }
}
