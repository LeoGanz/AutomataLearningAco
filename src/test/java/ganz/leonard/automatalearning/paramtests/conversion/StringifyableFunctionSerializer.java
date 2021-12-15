package ganz.leonard.automatalearning.paramtests.conversion;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ganz.leonard.automatalearning.util.StringifyableFunction;
import java.lang.reflect.Type;

public class StringifyableFunctionSerializer<T, R>
    implements JsonSerializer<StringifyableFunction<T, R>> {

  @Override
  public JsonElement serialize(
      StringifyableFunction src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.stringRep());
  }
}

//        "No function parser implemented! Use default PheromoneFunction.");
