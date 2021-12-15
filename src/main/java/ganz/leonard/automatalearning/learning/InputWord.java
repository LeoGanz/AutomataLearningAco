package ganz.leonard.automatalearning.learning;

import java.util.List;

public record InputWord<T>(List<T> word, boolean inLang) {}
