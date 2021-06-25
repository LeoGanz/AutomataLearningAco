package ganz.leonard.al_ants.language;

import java.util.List;

public record Language<T>(Expression<T> expression) {

  public static void main(String[] args) {
    Language<Character> testLang = new Language<>(new Leaf<>('a').rep().seq(new Leaf<>('b'))); //a*b
    System.out.println(testLang);
  }

  @Override
  public String toString() {
    return expression.toString();
  }

  public List<T> generateSample() {
    // TODO
    return List.of();
  }
}
